package moser.jens.bluetoothgraph;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import moser.jens.bluetoothgraph.util.NavigationUtil;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter blueToothAdapter;

    private ListView listView;
    private ArrayList<BluetoothDevice> pairedDevices;
    private ArrayList<String> pairedDeviceNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        blueToothAdapter = bluetoothManager.getAdapter();

        if (blueToothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, bluetoothFilter);

        if (blueToothAdapter.isEnabled()) {
            list();
        }

        enableBlueTooth();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void findViews() {
        listView = (ListView) findViewById(R.id.listview_lv);
    }

    private void enableBlueTooth() {
        if (!blueToothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
        }
    }

    public void list() {
        Set<BluetoothDevice> pairedDevices = blueToothAdapter.getBondedDevices();
        this.pairedDevices = new ArrayList<>();
        this.pairedDeviceNames = new ArrayList<>();

        for (BluetoothDevice bt : pairedDevices)
            this.pairedDevices.add(bt);

        for (BluetoothDevice bt : pairedDevices)
            this.pairedDeviceNames.add(bt.getName());

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pairedDeviceNames);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                BluetoothDevice bluetoothDevice = MainActivity.this.pairedDevices.get(arg2);
                NavigationUtil.startGraphActivity(MainActivity.this, bluetoothDevice);
            }
        });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                list();
            }
        }
    };
}
