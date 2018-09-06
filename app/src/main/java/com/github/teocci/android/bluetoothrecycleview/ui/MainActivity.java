package com.github.teocci.android.bluetoothrecycleview.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.github.teocci.android.bluetoothrecycleview.R;
import com.github.teocci.android.bluetoothrecycleview.adapters.DeviceAdapter;
import com.github.teocci.android.bluetoothrecycleview.interfaces.BluetoothRequestListener;
import com.github.teocci.android.bluetoothrecycleview.model.Device;
import com.github.teocci.android.bluetoothrecycleview.utils.LogHelper;
import com.github.teocci.android.bluetoothrecycleview.views.DeviceRecycleView;

import java.util.ArrayList;
import java.util.List;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
    private static final String TAG = LogHelper.makeLogTag(MainActivity.class);

    private static final int ACCESS_COARSE_LOCATION_CODE = 1;

    private static final int REQUEST_ENABLE_BLUETOOTH = 2;
    private static final int SCAN_MODE_ERROR = 3;

    private BluetoothAdapter bluetoothAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private DeviceRecycleView recyclerView;

    private DeviceAdapter deviceAdapter;

    private boolean bluetoothReceiverRegistered;

    private List<Device> devices = new ArrayList<>();

    private final Handler handler = new Handler();

    private Runnable scanTask = new Runnable()
    {
        @Override
        public void run()
        {
            handler.postDelayed(this, 3000);
            scanBluetooth();
        }
    };

    private BluetoothRequestListener callback = new BluetoothRequestListener()
    {
        @Override
        public void onConnectRequest(Device device)
        {
            LogHelper.e(TAG, "onConnectRequest");
            bluetoothAdapter.cancelDiscovery();
            if (!device.isPaired()) {
                BluetoothDevice newDevice = bluetoothAdapter.getRemoteDevice(device.getAddress());
                newDevice.createBond();
            }
        }
    };

    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short deviceRSSI = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI, (short) 0);
                Device newDevice = processDevice(device, deviceRSSI);

                Device oldDevice = scannedDevice(newDevice);
                if (oldDevice != null) {
                    devices.set(devices.indexOf(oldDevice), newDevice);
                } else {
                    devices.add(newDevice);
                }

                deviceAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (devices.size() == 0) {
                    LogHelper.e(TAG, "onReceive: No device");
                }
            }
        }

        private Device scannedDevice(Device newDevice)
        {
            for (Device device : devices) {
                if (newDevice.getAddress().equals(device.getAddress())) {
                    return device;
                }
            }

            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Request Permission
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Request permissions", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_CODE);
            }
        }

        initView();
        initAdapter();

        initReceiver();

        handler.post(scanTask);
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(scanTask);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        handler.post(scanTask);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        handler.removeCallbacks(scanTask);

        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }

        if (bluetoothReceiverRegistered) {
            unregisterReceiver(bluetoothReceiver);
        }
    }

    @Override
    public void onRefresh()
    {
        runOnUiThread(() -> {
            if (bluetoothAdapter != null) {
                if (!bluetoothAdapter.isEnabled()) {
                    //mBluetoothAdapter.enable();
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                }
//                    handler.post(scanTask);
                scanBluetooth();
            }
            deviceAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private Device processDevice(BluetoothDevice device, short deviceRSSI)
    {
        if (device == null) return null;

        String deviceName = device.getName();
        boolean paired = device.getBondState() == BOND_BONDED;
        String deviceAddress = device.getAddress();
        return new Device(deviceName, paired, deviceAddress, deviceRSSI);
    }

    private void initView()
    {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (DeviceRecycleView) findViewById(R.id.recycler_view);

        deviceAdapter = new DeviceAdapter(devices, callback);
        recyclerView.setAdapter(deviceAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initAdapter()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void initReceiver()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothReceiver, filter);
        bluetoothReceiverRegistered = true;
    }

    private void scanBluetooth()
    {
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.bt_scanning);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
    }
}
