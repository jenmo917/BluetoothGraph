package moser.jens.bluetoothgraph.util;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import moser.jens.bluetoothgraph.GraphActivity;

public class NavigationUtil {

    public static final String BLUETOOTH_DEVICE = "BLUETOOTH_DEVICE";

    public static void startGraphActivity(Activity activity, BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(activity, GraphActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(BLUETOOTH_DEVICE, bluetoothDevice);
        intent.putExtras(bundle);
        intent.putExtras(bundle);
        activity.startActivity(intent, bundle);
    }
}
