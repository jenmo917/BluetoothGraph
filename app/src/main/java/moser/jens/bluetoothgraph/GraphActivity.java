package moser.jens.bluetoothgraph;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;

public class GraphActivity extends AppCompatActivity {

    private double graph2LastXValue = 5d;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket mmSocket;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private boolean stopWorker;
    private int readBufferPosition;
    private byte[] readBuffer;
    private Thread workerThread;
    private GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        graph = (GraphView) findViewById(R.id.graph);

        bluetoothDevice = getIntent().getParcelableExtra(MainActivity.BLUETOOTH_DEVICE);

        Log.d("#######", bluetoothDevice.getName());
    }

    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);

                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                    readBufferPosition = 0;

                                    if (encodedBytes.length != 3) {
                                        continue;
                                    }

                                    final int i1 = encodedBytes[0];
                                    final int i2 = encodedBytes[1];
                                    final int i3 = encodedBytes[2];

                                    handler.post(new Runnable() {
                                        public void run() {
                                            Log.d("Data", i1 + ", " + i2 + ", " + i3);
                                            graph2LastXValue += 1d;
                                            DataPoint dataPointA = new DataPoint(graph2LastXValue, i1);
                                            DataPoint dataPointB = new DataPoint(graph2LastXValue, i2);
                                            DataPoint dataPointC = new DataPoint(graph2LastXValue, i3);
                                            graph.appendData(dataPointA, dataPointB, dataPointC);

                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            openBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeBT();
    }

    void closeBT() {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
        } catch (Exception e) {

        }
    }
}
