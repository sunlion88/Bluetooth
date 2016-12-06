package com.sunlion.bluetooth.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunlion.bluetooth.R;
import com.sunlion.bluetooth.bean.BluetoothMessage;

import java.util.List;

/**
 * Created by Administrator on 2016/12/4 0004.
 */

public class MessageListAdapter extends BaseAdapter {

    private Context mContext;
    private List<BluetoothMessage> list;

    public MessageListAdapter(Context context,List list) {
        this.list = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_msg_list,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        viewHolder =(ViewHolder)view.getTag();

        BluetoothMessage message = list.get(i);

        if(0==message.getIsMe()){
            viewHolder.head.setText(message.getSenderNick());
            viewHolder.content.setText(message.getContent());
            viewHolder.myLayout.setVisibility(View.GONE);
            viewHolder.layout.setVisibility(View.VISIBLE);
        }else {
            viewHolder.myHead.setText("我");
            viewHolder.myContent.setText(message.getContent());

            viewHolder.myLayout.setVisibility(View.VISIBLE);
            viewHolder.layout.setVisibility(View.GONE);
        }
        return view;
    }

    public static class ViewHolder{
        public TextView content;
        public TextView head;

        public TextView myContent;
        public TextView myHead;

        public RelativeLayout layout;
        public RelativeLayout myLayout;

        public ViewHolder(View view){
            head = (TextView)view.findViewById(R.id.head);
            content = (TextView)view.findViewById(R.id.msg_content);

            myContent = (TextView)view.findViewById(R.id.my_msg_content);
            myHead = (TextView)view.findViewById(R.id.my_head);

            layout = (RelativeLayout)view.findViewById(R.id.layout);
            myLayout = (RelativeLayout)view.findViewById(R.id.my_layout);
        }
    }
}
