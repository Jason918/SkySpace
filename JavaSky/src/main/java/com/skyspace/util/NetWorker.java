package com.skyspace.util;

import com.skyspace.Sky;
import com.skyspace.element.Item;
import com.skyspace.element.Template;
import com.skyspace.json.JSONException;
import com.skyspace.json.JSONObject;
import com.skyspace.json.JSONStringer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Enumeration;
import java.util.Random;
import java.util.Scanner;

public class NetWorker {
    private static final String DEFAULT_GROUP_IP = "224.0.0.251";
    private static final int DEFAULT_GROUP_PORT = 63860;
    private static final int DEFAULT_SOCKET_PORT = 63861;
    private static final int BUFFER_SIZE = 1024;
    public static String ipv4 = null;
    private static InetAddress group = null;
//	public static final int TIME_SPAN = 200;

    //	private final String node_ip;
    private static int socket_port = getSocketPort();
    MulticastSocket sender;
    Thread request_listener;
    boolean request_listener_stop;
    Thread result_listener;
    boolean result_listener_stop;

    /**
     * 做初始化工作.
     */
    public NetWorker() {
        /**
         * init sender...
         */
        try {
            group = InetAddress.getByName(DEFAULT_GROUP_IP);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        if (!group.isMulticastAddress()) {
            Sky.logger.severe("group is not multicast address");
            return;
        }
        sender = null;
        try {
            sender = new MulticastSocket(DEFAULT_GROUP_PORT);
            sender.setTimeToLive(100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get a free socket_port.
     */
    private static int getSocketPort() {
        if (!isPortAvilable(DEFAULT_SOCKET_PORT)) {
            int max_cnt = 10;
            Random rand = new Random();
            while (max_cnt-- > 0) {

                int new_port = rand.nextInt(65536 - 1024) + 1024;
                if (isPortAvilable(new_port)) {
                    Sky.logger.fine("get new free socket port:" + new_port);
                    return new_port;
                }
            }
            Sky.logger.warning("get new free socket port failed");
            return 0;
        } else {
            Sky.logger.fine("use default socket port:" + DEFAULT_SOCKET_PORT);
            return DEFAULT_SOCKET_PORT;
        }

    }

    private static void bindPort(String host, int port) throws Exception {
        Socket s = new Socket();
        s.bind(new InetSocketAddress(host, port));
        s.close();
    }

    private static boolean isPortAvilable(int port) {
//		System.out.println("testing port "+port);
        try {
            bindPort("0.0.0.0", port);
            bindPort(getLocalIP(), port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getLocalIP() {
        if (ipv4 == null) {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface
                        .getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf
                            .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            ipv4 = inetAddress.getHostAddress().toString();
                            if (ipv4.matches("\\d+.\\d+.\\d+.\\d+")) {
                                Sky.logger.info("local ip:" + ipv4);
                                return ipv4;
                            }
//                            if (ipv4.startsWith("192")) {
//                                Sky.logger.info("local ip:" + ipv4);
//                                return ipv4;
//                            } else {
//                                Sky.logger.info("false ip" + ipv4);
//                                ipv4 = null;
//                            }
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        if (ipv4 == null)
            return "127.0.0.1";
        else
            return ipv4;
    }

    public static int getCommPort() {
        return socket_port;
    }

    /**
     * 开启接受其他节点请求的工作线程
     * 使用多播...
     *
     * @param tupleSpace
     */
    public void startRequestListener(final Sky tupleSpace) {
        request_listener = new Thread(new Runnable() {
            public void run() {
                InetAddress group = null;
                try {
                    group = InetAddress.getByName(DEFAULT_GROUP_IP);
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
                if (!group.isMulticastAddress()) {
                    Sky.logger.severe("group is not multicast address");
                    return;
                }
                MulticastSocket ms = null;
                try {
                    ms = new MulticastSocket(DEFAULT_GROUP_PORT);
                    ms.joinGroup(group);

                    byte[] buffer = new byte[BUFFER_SIZE];
                    Sky.logger.fine("Request listener is working");
                    while (true && !request_listener_stop) {
                        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                        ms.receive(dp);//TODO 可能会有丢包???
                        String s = new String(dp.getData(), 0, dp.getLength());
                        Sky.logger.info("M-listener GOT packet:" + s);
                        if (request_listener_stop) break;
                        try {
                            JSONObject jo = new JSONObject(s);
                            tupleSpace.handleRequest(new Template(jo));
                        } catch (JSONException e) {
                            Sky.logger.warning("Not JSON format:" + s);
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (ms != null) {
                        try {
                            ms.leaveGroup(group);
                            ms.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        request_listener.setName("Multicast Listener Thread");
        request_listener.start();
    }

    public void stopRequestListener() {
        request_listener_stop = true;
        //sendDataToEnviroment("stop your self");
        //request_listener.stop();
    }

    /**
     * 开启接受响应的工作线程
     * 使用TCP
     * 接受的数据情况：
     * 如果无法解析将返回的roger将为false，
     * 1.	获取本节点发出tmpl请求的响应，对方节点发送：
     * format: {op = "send result",Template tmpl,Item it,boolean isFromOwner}.
     * 如果isFromOwner==false，则再向source节点直接发出获取节点的请求。
     * 返回结果：{boolean roger,bool accept,String message}
     * 如果接收这个Item，则accept = true，无message。
     * 如果拒绝这个Item，则accept = false，message为具体原因。
     * <p/>
     * 2.	接受到别的节点发来的acquire元组的请求:
     * format: {op = "acquire Item",Item it,Template tmpl}
     * *此时要考虑 跟多播线程的数据同步问题。由TuplePool来保证线程安全。
     * 返回：{boolean roger, boolean result,Item it,String message}
     * 如果result为true，表示允许对方acquire Item，对方需要返回是否接受该Item。格式同上。
     * 如果result为false，则可以在message中说明原因。
     *
     * @param tupleSpace
     */
    public void startResponseListener(final Sky tupleSpace) {
        result_listener = new Thread(new Runnable() {

            @Override
            public void run() {
                ServerSocket ss;
                try {
                    ss = new ServerSocket(socket_port);
                    Socket socket;
                    while (!result_listener_stop) {
                        socket = ss.accept();
                        Scanner scanner = new Scanner(socket.getInputStream());
                        PrintStream ps = new PrintStream(socket.getOutputStream(), true);

                        String data = scanner.nextLine();

                        Sky.logger.info("S-listener received:" + data);

                        JSONObject obj;
                        try {
                            obj = new JSONObject(data);
                        } catch (JSONException e) {
                            Sky.logger.warning("creat json fail:" + e);
                            ps.println("{\"roger\":false}");
                            socket.close();
//							scanner.close();
                            continue;
                        }
                        if (obj.getString("op").equals("send result")) {//Operation 1.send result
                            if (obj.has("Item") && obj.has("Template") && obj.has("isFromOwner")) {
                                Item it = new Item(obj.get("Item").toString());
                                Template tmpl = new Template(obj.get("Template").toString());
                                if (tmpl.isAcquire() && !obj.getBoolean("isFromOwner")) {
                                    tupleSpace.handleCacheAcquireResult(it, tmpl);
                                    ps.println(new JSONStringer().object()
                                            .key("roger").value(true)
                                            .key("accept").value(true)
                                            .endObject()
                                            .toString());
                                    socket.close();
                                    continue;
                                } else {
                                    boolean accept = tupleSpace.handleResult(it, tmpl);
                                    ps.println(new JSONStringer().object()
                                            .key("roger").value(true)
                                            .key("accept").value(accept)
                                            .endObject()
                                            .toString());
                                    socket.close();
                                    continue;
                                }

                            } else {//error...
                                Sky.logger.warning("json member error:" + obj.toString());
                                ps.println(new JSONStringer().object()
                                        .key("roger").value(true)
                                        .key("accept").value(false)
                                        .key("message").value("missing proper key.")
                                        .endObject()
                                        .toString());
                                socket.close();
//								scanner.close();
                                continue;
                            }
                        } else if (obj.getString("op").equals("acquire Item")) {//Operation 2.acquire Item
                            if (obj.has("Item") && obj.has("Template")) {
                                Item it = new Item(obj.get("Item").toString());
                                Template tmpl = new Template(obj.get("Template").toString());
                                Item new_it = tupleSpace.acquireFromPool(it, tmpl);
                                if (new_it == null) {
                                    ps.println(new JSONStringer().object()
                                            .key("roger").value(true)
                                            .key("result").value(false)
                                                    //.key("message").value("missing proper key.")
                                            .endObject()
                                            .toString());
                                    socket.close();
//									scanner.close();
                                    continue;
                                } else {
                                    ps.println(new JSONStringer().object()
                                            .key("roger").value(true)
                                            .key("result").value(true)
                                            .key("Item").value(new_it.pack())
                                            .endObject()
                                            .toString());
                                    String rcv = scanner.nextLine();
                                    JSONObject jo = null;
                                    try {
                                        jo = new JSONObject(rcv);
                                    } catch (JSONException e) {
                                        Sky.logger.warning("can not parse received data into json:" + rcv);
                                    }
                                    if (jo.has("roger") && jo.getBoolean("roger")) {
                                        tupleSpace.confirmAcquire(new_it, jo.getBoolean("accept"));
                                    } else {
                                        tupleSpace.confirmAcquire(new_it, false);
                                        Sky.logger.warning("target did not roger that message:" + data);
                                    }
                                    socket.close();
//									scanner.close();
                                    continue;
                                }
                            } else {//error handle...
                                Sky.logger.warning("json member error:" + obj.toString(4));
                                ps.println(new JSONStringer().object()
                                        .key("roger").value(true)
                                        .key("result").value(false)
                                        .key("message").value("missing proper key.")
                                        .endObject()
                                        .toString());
                                socket.close();
//								scanner.close();
                                continue;
                            }
                        } else {
                            Sky.logger.warning("Unknown message:" + obj.toString(4));
                        }
                    }
                    ss.close();
                } catch (IOException e) {
//					System.out.println("get IO exception");
                    e.printStackTrace();
                }
            }
        });
        result_listener.setName("TCP Listener Thread");
        result_listener.start();
    }

    public void stopResponseListener() {
        result_listener_stop = true;
        sendDataToNode("STOP YOURSELF!", getLocalIP(), socket_port);
    }

    /**
     * send request into tuple space
     *
     * @param data
     * @return
     */
    public boolean sendDataToEnviroment(final String data) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                byte[] buff = data.getBytes();
                final DatagramPacket dp = new DatagramPacket(buff, buff.length, group, DEFAULT_GROUP_PORT);
                try {
                    sender.send(dp);
                } catch (Exception e) {
                    System.out.println("Template:" + group.toString());
                    e.printStackTrace();
                }
            }
        }).start();
        return true;
    }

    /**
     * send $data to a Node at $address.
     *
     * @param data
     * @param address,对方的IP地址,暂定为IPV4,格式为的DDD.DDD.DDD.DDD
     * @return
     */
    public boolean sendDataToNode(String data, String address, int port) {
        if (address.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            try {
                Socket socket = new Socket(address, port);
                PrintStream ps = new PrintStream(socket.getOutputStream());

                ps.println(data);
                Sky.logger.info("sending data done:->" + data);
                socket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            Sky.logger.warning("IP format not right");
            return false;
        }
    }

    /**
     * 往tmpl发送it，
     *
     * @param it
     * @param tmpl
     * @param isFromOwner
     * @return
     */
    public boolean sendResult(Item it, Template tmpl, boolean isFromOwner) {
        String data = new JSONStringer()
                .object()
                .key("op").value("send result")
                .key("Item").value(it.pack())
                .key("Template").value(tmpl.pack())
                .key("isFromOwner").value(isFromOwner)
                .endObject()
                .toString();
        String address = tmpl.getOwner().getIP();
        int port = tmpl.getOwner().getPort();
        boolean rtn = false;
        if (address.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            try {
                Socket socket = new Socket(address, port);
                Scanner scanner = new Scanner(socket.getInputStream());
                PrintStream ps = new PrintStream(socket.getOutputStream());

                ps.println(data);

                String rcv = scanner.nextLine();
                JSONObject jo = null;
                try {
                    jo = new JSONObject(rcv);
                } catch (JSONException e) {
                    Sky.logger.warning("can not parse received data into json:" + rcv);
                }
                if (jo.has("roger") && jo.getBoolean("roger")) {
                    if (jo.getBoolean("accept")) {
                        rtn = true;
                        Sky.logger.info("sending data accept:->" + data);
                    } else {
                        Sky.logger.info("send data refused:->" + data);
                    }
                } else {
                    Sky.logger.warning("target did not roger that message:" + data);
                }
//				scanner.close();
                socket.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Sky.logger.warning("IP format not right");
        }
        return rtn;
    }

    /**
     * 接受到别的节点发来的acquire元组的请求:
     * format: {op = "acquire Item",Item it,Template tmpl}
     * *此时要考虑 跟多播线程的数据同步问题。由TuplePool来保证线程安全。
     * 返回：{boolean roger, boolean result,Item it,String message}
     * 如果result为true，表示允许对方acquire Item，对方需要返回是否接受该Item。格式同上。
     * 如果result为false，则可以在message中说明原因。
     *
     * @param it
     * @param tmpl
     */
    public Item acquireItem(Item it, Template tmpl) {
        String address = tmpl.getOwner().getIP();
        int port = tmpl.getOwner().getPort();
        if (address.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            try {
                Socket socket = new Socket(address, port);
                Scanner scanner = new Scanner(socket.getInputStream());
                PrintStream ps = new PrintStream(socket.getOutputStream());
                String data = new JSONStringer().object()
                        .key("op").value("acquire Item")
                        .key("Item").value(it.pack())
                        .key("Template").value(tmpl.pack())
                        .endObject().toString();

                ps.println(data);

                String rcv = scanner.nextLine();

                JSONObject jo = null;
                try {
                    jo = new JSONObject(rcv);
                } catch (JSONException e) {
                    Sky.logger.warning("can not parse received data into json:" + rcv);
                }
                if (jo.has("roger") && jo.getBoolean("roger")) {
                    if (jo.getBoolean("result")) {
                        Item new_it = new Item(jo.getString("Item"));
                        Sky.logger.info("result:" + new_it);
                        socket.close();
//						scanner.close();
                        return new_it;
                    } else {
                        Sky.logger.info("result false");
                    }
                } else {
                    Sky.logger.warning("target did not roger that message:" + data);
                }
                socket.close();
//				scanner.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Sky.logger.warning("IP format not right");
        }
        return null;
    }
}
