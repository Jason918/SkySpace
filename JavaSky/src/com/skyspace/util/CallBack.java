package com.skyspace.util;

import java.util.ArrayList;

import com.skyspace.Template;
import com.skyspace.Item;

public interface CallBack {
	void callback(Template eg,Item ei);
	void callbackMany(Template eg,ArrayList<Item> eiList);
}
