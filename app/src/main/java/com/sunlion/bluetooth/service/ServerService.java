package com.sunlion.bluetooth.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.sunlion.bluetooth.manager.UserManager;
import com.sunlion.bluetooth.utils.BluetoothUtils;

/**
 * Created by Administrator on 2016/12/4 0004.
 */

public class ServerService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(BluetoothUtils.getInstance(this).startServerThread == null){
            BluetoothUtils.getInstance(this).initServer();
            UserManager.setMyuserId(this,BluetoothUtils.getInstance(this).getBluetoothAdapter().getAddress());

            String add = BluetoothUtils.getInstance(this).getBluetoothAdapter().getAddress();
            System.out.print(add);
        }
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        BluetoothUtils.getInstance(this).onDestroy();
        super.onDestroy();
    }
}
