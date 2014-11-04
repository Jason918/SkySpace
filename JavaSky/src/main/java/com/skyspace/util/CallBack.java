package com.skyspace.util;

import com.skyspace.element.Item;
import com.skyspace.element.Template;

import java.util.ArrayList;

public interface CallBack {
    void handle(Template tmpl, Item it);

    void handleMany(Template tmpl, ArrayList<Item> itList);
}
