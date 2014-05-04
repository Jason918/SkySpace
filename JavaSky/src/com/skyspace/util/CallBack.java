package com.skyspace.util;

import java.util.ArrayList;

import com.skyspace.Template;
import com.skyspace.Item;

public interface CallBack {
	void handle(Template eg,Item ei);
	void handleMany(Template eg,ArrayList<Item> eiList);
}
