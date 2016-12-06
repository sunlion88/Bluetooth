package com.sunlion.bluetooth.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/12/3 0003.
 */

public class SharedPreferenceUtil {
    private static String SHARED_NAME = "bid_yangjing_bluetooth";

    private static SharedPreferenceUtil instance;

    private static SharedPreferences sharedPreferences;

    private Context mContext;

    public SharedPreferenceUtil(Context context) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferenceUtil getInstance(Context context){
        if(instance == null){
            instance = new SharedPreferenceUtil(context);
        }
        return instance;
    }

    public void putString(String key,String value){
        sharedPreferences.edit().putString(key,value).apply();
    }

    public String getString(String key){
        return sharedPreferences.getString(key,"");
    }
}

