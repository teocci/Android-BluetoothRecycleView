package com.github.teocci.android.bluetoothrecycleview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.teocci.android.bluetoothrecycleview.R;
import com.github.teocci.android.bluetoothrecycleview.holders.DeviceHolder;
import com.github.teocci.android.bluetoothrecycleview.interfaces.BluetoothRequestListener;
import com.github.teocci.android.bluetoothrecycleview.model.Device;

import java.util.List;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Sep-06
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder>
{
    public List<Device> deviceList;

    private BluetoothRequestListener listener;

    private Context context;

    public DeviceAdapter(List<Device> deviceList)
    {
        this.deviceList = deviceList;
    }

    public DeviceAdapter(List<Device> deviceList, BluetoothRequestListener listener)
    {
        this.deviceList = deviceList;
        this.listener = listener;
    }

    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        context = parent.getContext().getApplicationContext();

        View view = LayoutInflater.from(context).inflate(R.layout.bt_device_item, parent, false);
        return new DeviceHolder(view , listener);
    }

    @Override
    public void onBindViewHolder(DeviceHolder holder, int position)
    {
        Device device = deviceList.get(position);
        if (device != null) {
            holder.setDevice(device);
            holder.getTextDeviceName().setText((device.getName() == null || device.getName().isEmpty()) ? "Unknown Device" : device.getName());
            holder.getTextDeviceAddress().setText(device.getAddress());
            holder.getTextDevicePaired().setText(device.isPaired() ? "Paired" : "Not paired");
            holder.getTextDeviceSignal().setText(context.getString(R.string.bt_device_signal, device.getSignal()));
        }
    }

    @Override
    public int getItemCount()
    {
        return deviceList.size();
    }
}