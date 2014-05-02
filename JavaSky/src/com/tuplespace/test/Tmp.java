package com.tuplespace.test;


import com.tuplespace.EnvItem;
import com.tuplespace.json.JSONObject;
import com.tuplespace.json.JSONStringer;
import com.tuplespace.util.ObjectProxy;

public class Tmp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EnvItem e1 = new EnvItem(
				new ObjectProxy("FOR testing-NO 1"),
				EnvItem.TYPE_SUBSCRIBALE|EnvItem.TYPE_ACQUIRABLE,
				"test,hello,jason",
				60000);
		String s = new JSONStringer().object()
				.key("EnvItem").value(e1.pack())
				.endObject().toString();
		System.out.println(s);
		JSONObject jo = new JSONObject(s);
		System.out.println(jo.get("EnvItem"));
//		JSONObject j = new JSONObject(jo.get("EnvItem"));
		EnvItem e = new EnvItem(jo.get("EnvItem").toString());
	}

}
