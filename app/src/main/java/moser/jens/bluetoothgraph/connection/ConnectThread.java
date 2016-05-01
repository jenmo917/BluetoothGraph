package moser.jens.bluetoothgraph.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {

    private final BluetoothSocket socket;
    private final BluetoothAdapter bluetoothAdapter;
    private final ConnectListener connectListener;

    public interface ConnectListener {
        void onConnected(BluetoothSocket socket);
        void onConnectFailure(Exception e);
    }

    public ConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, UUID uuid, ConnectThread.ConnectListener connectListener) {

        // Use a temporary object that is later assigned to socket,
        // because socket is final
        BluetoothSocket tmp = null;

        this.connectListener = connectListener;
        this.bluetoothAdapter = bluetoothAdapter;

        try {
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            connectListener.onConnectFailure(e);
        }
        socket = tmp;
    }

    @Override
    public void run() {
        // Cancel discovery because it will slow down the connection
        bluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            socket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            connectListener.onConnectFailure(connectException);
            try {
                socket.close();
            } catch (IOException e) {}
            return;
        }

        // Do work to manage the connection (in a separate thread)
        connectListener.onConnected(socket);
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            connectListener.onConnectFailure(e);
        }
    }
}
