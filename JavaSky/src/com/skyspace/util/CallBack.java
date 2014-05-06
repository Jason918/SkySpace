package com.skyspace.util;

import java.util.ArrayList;

import com.skyspace.Template;
import com.skyspace.Item;

public interface CallBack {
	void handle(Template tmpl,Item it);
	void handleMany(Template tmpl,ArrayList<Item> itList);
}
