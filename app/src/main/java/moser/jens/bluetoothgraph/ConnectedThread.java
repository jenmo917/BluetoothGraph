package moser.jens.bluetoothgraph;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final ConnectedListener connectedListener;
    private int readBufferPosition;

    public interface ConnectedListener {
        void obtainMessage(byte[] message);
    }

    public ConnectedThread(BluetoothSocket socket, ConnectedListener connectedListener) {
        mmSocket = socket;
        this.connectedListener = connectedListener;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        final byte delimiter = 10; //This is the ASCII code for a newline character
        readBufferPosition = 0;
        byte[] readBuffer = new byte[1024];
        while (true) {
            try {
                    int bytesAvailable = mmInStream.available();
                    if (bytesAvailable > 0) {
                        byte[] packetBytes = new byte[bytesAvailable];
                        mmInStream.read(packetBytes);

                        for (int i = 0; i < bytesAvailable; i++) {
                            byte b = packetBytes[i];
                            if (b == delimiter) {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                readBufferPosition = 0;

                                if (encodedBytes.length < 3) {
                                    continue;
                                }
                                // Send the obtained bytes to the UI activity
                                connectedListener.obtainMessage(encodedBytes);
                            } else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
            } catch (IOException e) {
                break;
            }
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmOutStream.close();
            mmInStream.close();
            mmSocket.close();
        } catch (IOException e) { }
    }
}