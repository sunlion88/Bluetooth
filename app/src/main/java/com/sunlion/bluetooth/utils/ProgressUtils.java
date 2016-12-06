package com.sunlion.bluetooth.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Administrator on 2016/12/4 0004.
 */

public class ProgressUtils {

    private static ProgressDialog progressDialog;

    public static void showProgress(Context context,String msg){
        progressDialog = progressDialog.show(context,null,msg,false,true);
    }

    public static void dismissProgress(){
        if ((progressDialog != null && progressDialog.isShowing())){
            progressDialog.dismiss();
        }
    }

}
