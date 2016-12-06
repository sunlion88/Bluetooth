package com.sunlion.bluetooth.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sunlion.bluetooth.R;
import com.sunlion.bluetooth.adapter.DevicesAdapter;
import com.sunlion.bluetooth.bean.BluetoothMessage;
import com.sunlion.bluetooth.utils.BluetoothUtils;
import com.sunlion.bluetooth.utils.ProgressUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static String[] permissions = {
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.ACCESS_COARSE_LOCATION",
//            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.BLUETOOTH_PRIVILEGED"
    };

    private ListView listView;
    private DevicesAdapter adapter;
    private ArrayList<BluetoothDevice> list;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mContext = this;
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(permissions, 1);
        }
        initReceiver();

        list = new ArrayList<>();
        adapter = new DevicesAdapter(this, list);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);

        if (BluetoothUtils.getInstance(this).isEnable()) {
            BluetoothUtils.getInstance(this).scanDevice();
        } else {
            BluetoothUtils.getInstance(this).enableBluetooth();
        }

        list.addAll(BluetoothUtils.getInstance(mContext).getAvailableDevices());
        adapter.notifyDataSetChanged();
    }

    public void onClick(View view) {
        showToast("开始扫描");
        list.clear();
        list.addAll(BluetoothUtils.getInstance(mContext).getAvailableDevices());
        adapter.notifyDataSetChanged();
        BluetoothUtils.getInstance(this).scanDevice();
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        intentFilter.addAction(BluetoothMessage.ACTION_INIT_COMPLETE);
        intentFilter.addAction(BluetoothMessage.ACTION_CONNECTED_SERVER);
        intentFilter.addAction(BluetoothMessage.ACTION_CONNECT_ERROR);
        registerReceiver(receiver,intentFilter);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    for(int i=0;i<list.size();i++){
                        if(device.getAddress()==null || device.getAddress().equals(list.get(i).getAddress())){
                            return;
                        }
                    }
                    list.add(device);
                    adapter.notifyDataSetChanged();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    showToast("开始扫描");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    showToast("扫描完成");
                    break;
                case BluetoothMessage.ACTION_INIT_COMPLETE:
                    ProgressUtils.dismissProgress();
                    break;
                case BluetoothMessage.ACTION_CONNECTED_SERVER:
                    ProgressUtils.dismissProgress();
                    String remoteAddress = intent.getStringExtra(BluetoothUtils.EXTRA_REMOTE_ADDRESS);
                    openChatRoom(remoteAddress);
                    break;
                case BluetoothMessage.ACTION_CONNECT_ERROR:
                    ProgressUtils.dismissProgress();
                    showToast(intent.getStringExtra(BluetoothUtils.EXTRA_ERROR_MSG));
                    break;
            }
        }
    };

    private void openChatRoom(String remoteAddress) {
        Intent itent = new Intent(mContext,ChatActivity.class);
        itent.putExtra("remoteAddress",remoteAddress);
        startActivity(itent);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final BluetoothDevice device = list.get(i);
        if(BluetoothUtils.getInstance(this).isDiscoverying()){
            BluetoothUtils.getInstance(this).cancelScan();
        }

        BluetoothUtils.getInstance(mContext).connect(device.getAddress());

        ProgressUtils.showProgress(mContext,"正在连接,请稍后...");
    }
}
