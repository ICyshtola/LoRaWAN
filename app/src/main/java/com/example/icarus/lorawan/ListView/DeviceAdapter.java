package com.example.icarus.lorawan.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.icarus.lorawan.ListView.Device;
import com.example.icarus.lorawan.R;

import java.util.List;

public class DeviceAdapter extends ArrayAdapter<Device> {
    private int resourceId;
    public DeviceAdapter(Context context,int textViewResourceId,List<Device> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Device device = getItem(position);
        View view ;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.deveui = (TextView)view.findViewById(R.id.deveui);
            viewHolder.devname = (TextView)view.findViewById(R.id.devname);
            viewHolder.status = (TextView)view.findViewById(R.id.status);
            viewHolder.lastruntime = (TextView)view.findViewById(R.id.lastruntime);
            viewHolder.type = (TextView)view.findViewById(R.id.devtype);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.deveui.setText(device.getDeveui());
        viewHolder.devname.setText(device.getDevname());
        viewHolder.status.setText(device.getStatus());
        viewHolder.lastruntime.setText(device.getLastruntime());
        viewHolder.type.setText(device.getType());
        return view;
    }

    class ViewHolder{
        TextView deveui;
        TextView devname;
        TextView status;
        TextView lastruntime;
        TextView type;
    }
}
