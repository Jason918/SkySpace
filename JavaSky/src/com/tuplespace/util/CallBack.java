package com.tuplespace.util;

import java.util.ArrayList;

import com.tuplespace.EnvGroup;
import com.tuplespace.EnvItem;

public interface CallBack {
	void callback(EnvGroup eg,EnvItem ei);
	void callbackMany(EnvGroup eg,ArrayList<EnvItem> eiList);
}
