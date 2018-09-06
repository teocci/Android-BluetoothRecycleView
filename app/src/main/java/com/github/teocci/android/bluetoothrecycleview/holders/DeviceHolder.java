package com.github.teocci.android.bluetoothrecycleview.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.teocci.android.bluetoothrecycleview.R;
import com.github.teocci.android.bluetoothrecycleview.interfaces.BluetoothRequestListener;
import com.github.teocci.android.bluetoothrecycleview.model.Device;
import com.github.teocci.android.bluetoothrecycleview.utils.LogHelper;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Sep-05
 */
public class DeviceHolder extends RecyclerView.ViewHolder
{
    private final String TAG = LogHelper.makeLogTag(DeviceHolder.class);

    private TextView textDeviceName;
    private TextView textDeviceAddress;
    private TextView textDeviceSignal;
    private TextView textDevicePaired;

    private BluetoothRequestListener listener;
    private Device device;

    public DeviceHolder(View itemView, final BluetoothRequestListener listener)
    {
        super(itemView);
        this.listener = listener;

        textDeviceName = (TextView) itemView.findViewById(R.id.text_name);
        textDeviceAddress = (TextView) itemView.findViewById(R.id.text_address);
        textDeviceSignal = (TextView) itemView.findViewById(R.id.text_signal);
        textDevicePaired = (TextView) itemView.findViewById(R.id.text_paired);

        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConnectRequest(device);
            }
        });
    }

    public TextView getTextDeviceName()
    {
        return textDeviceName;
    }

    public TextView getTextDeviceAddress()
    {
        return textDeviceAddress;
    }

    public TextView getTextDeviceSignal()
    {
        return textDeviceSignal;
    }

    public TextView getTextDevicePaired()
    {
        return textDevicePaired;
    }

    public void setDevice(Device device)
    {
        this.device = device;
    }
}
