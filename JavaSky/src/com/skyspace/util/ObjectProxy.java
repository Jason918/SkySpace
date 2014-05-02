package com.skyspace.util;

import com.skyspace.NetWorker;

public class ObjectProxy {
	protected String NodeID; //node's ip address.format:ip:port
	protected String ObjectID;//app's action/URI,format:edu.pku.sei.act.....
	
	
	public ObjectProxy(String ownerInfo) {
		String info_list[] = ownerInfo.split("#");
		if (info_list.length == 1) {
			NodeID = NetWorker.getLocalIP() + ":" +NetWorker.SOCKET_PORT;
			ObjectID = info_list[0];
		} else {
			NodeID = info_list[0];
			ObjectID = info_list[1];
		}
	}

	public ObjectProxy(String ip,int port,String action) {
		NodeID = ip + ":" + port;
		ObjectID = action;
	}
	
	@Override
	public String toString() {
		return NodeID+"#"+ObjectID;
	}
	
	/**
	 * get Object's IP
	 * TODO use the real one.
	 * @return
	 */
	public String getIP() {
		int index = NodeID.indexOf(':');
		return NodeID.substring(0,index);
	}
	public int getPort() {
		int index = NodeID.indexOf(':');
		return Integer.parseInt(NodeID.substring(index+1));
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ObjectProxy) {
			ObjectProxy op = (ObjectProxy) obj;
			return NodeID.equals(op.NodeID) && ObjectID.equals(op.ObjectID);
		}
		return false;
	}
	public String getAction() {
		return ObjectID;
	}
}
