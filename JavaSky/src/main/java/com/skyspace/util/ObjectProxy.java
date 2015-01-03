package com.skyspace.util;

import com.skyspace.Sky;

public class ObjectProxy {
    protected String ip;
    protected int port;
    protected String name;

    public ObjectProxy() {
    }

    public ObjectProxy(String name) {
        this.name = name;
        this.ip = NetWorker.getLocalIP();
        this.port = NetWorker.getCommPort();
    }

    public ObjectProxy(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public static ObjectProxy buildByString(String str) {
        String[] info1 = str.split("@");
        if (info1.length == 2) {
            String[] info2 = info1[1].split(":");
            if (info2.length == 2) {
                try {
                    int p = Integer.parseInt(info2[1]);
                    return new ObjectProxy(info2[0], p, info1[0]);
                } catch (NumberFormatException e) {

                }
            }
        }
        Sky.logger.warning("build ObjectProxy from String error:" + str);
        return null;
    }

    @Override
    public String toString() {
        return name + "@" + ip + ":" + port;
    }

    /**
     * get Object's IP
     * TODO use the real one.
     *
     * @return
     */
    public String getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ObjectProxy) {
            ObjectProxy op = (ObjectProxy) obj;
            return port == op.port && ip.equals(op.ip) && name.equals(op.name);
        }
        return false;
    }
}
