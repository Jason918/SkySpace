package com.skyspace.test;


import com.skyspace.element.Item;
import com.skyspace.json.JSONObject;
import com.skyspace.json.JSONStringer;
import com.skyspace.util.ObjectProxy;

public class Tmp {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Item e1 = new Item(
                new ObjectProxy("FOR testing-NO 1"),
                Item.TYPE_SUBSCRIBALE | Item.TYPE_ACQUIRABLE,
                "test,hello,jason",
                60000);
        String s = new JSONStringer().object()
                .key("Item").value(e1.pack())
                .endObject().toString();
        System.out.println(s);
        JSONObject jo = new JSONObject(s);
        System.out.println(jo.get("Item"));
//		JSONObject j = new JSONObject(jo.get("Item"));
        Item e = new Item(jo.get("Item").toString());
    }

}
