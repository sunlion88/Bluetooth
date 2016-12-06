package com.sunlion.bluetooth.database;

import com.sunlion.bluetooth.bean.BluetoothMessage;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2016/12/3 0003.
 */

public class DBManager {

    private static DbManager.DaoConfig daoconfig;

    public static DbManager.DaoConfig getDaoConfig(){
            if(daoconfig==null){
                daoconfig = new DbManager.DaoConfig();
                daoconfig.setDbVersion(2);
            }
        return daoconfig;
    }

    public static void save(Object obj){
        try{
            x.getDb(DBManager.getDaoConfig()).save(obj);
        }catch (DbException e){
            e.printStackTrace();
        }
    }

    public static List<BluetoothMessage> findAll(String remoteAddress){
        try {
            Selector<BluetoothMessage> selector = x.getDb(getDaoConfig()).selector(BluetoothMessage.class);
            selector.where("sender","=",remoteAddress);
            selector.or("receiver","=",remoteAddress);
            return selector.findAll();
        }catch (DbException e){
            e.printStackTrace();
        }
        return null;
    }

}
