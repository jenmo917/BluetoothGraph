package moser.jens.bluetoothgraph;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jjoe64.graphview.series.DataPoint;

import java.util.UUID;

import moser.jens.bluetoothgraph.connection.ConnectThread;
import moser.jens.bluetoothgraph.connection.ConnectedThread;
import moser.jens.bluetoothgraph.view.GraphView;
import moser.jens.bluetoothgraph.view.LoadingLayout;

public class GraphActivity extends AppCompatActivity {

    private static final String UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"; //Standard SerialPortService ID
    private double graph2LastXValue = 5d;
    private BluetoothDevice bluetoothDevice;
    private GraphView graph;
    private boolean pause;
    private BluetoothAdapter blueToothAdapter;
    private ConnectedThread connectedThread;
    private ConnectThread connectThread;
    private LoadingLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        graph = (GraphView) findViewById(R.id.graph);
        loadingLayout = (LoadingLayout) findViewById(R.id.loading_layout);

        loadingLayout.setLoadingListener(new LoadingLayout.LoadingListener() {
            @Override
            public void OnRetryPressed() {
                disconnectBT();
                connectBT();
            }
        });

        bluetoothDevice = getIntent().getParcelableExtra(MainActivity.BLUETOOTH_DEVICE);
        blueToothAdapter = BluetoothAdapter.getDefaultAdapter();

        loadingLayout.loadingStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectBT();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnectBT();
    }

    private void connectBT() {
        final Handler handler = new Handler();
        ConnectThread.ConnectListener connectListener = new ConnectThread.ConnectListener() {
            @Override
            public void onConnected(BluetoothSocket socket) {
                ConnectedThread.ConnectedListener connectedListener = new ConnectedThread.ConnectedListener() {
                    @Override
                    public void obtainMessage(byte[] encodedBytes) {

                        if (!pause) {
                            final int i1 = encodedBytes[0];
                            final int i2 = encodedBytes[1];
                            final int i3 = encodedBytes[2];

                            handler.post(new Runnable() {
                                public void run() {
                                    Log.d("Data", i1 + ", " + i2 + ", " + i3);
                                    loadingLayout.loadingSuccesssfull();
                                    if (!pause) {
                                        graph2LastXValue += 1d;
                                        DataPoint dataPointA = new DataPoint(graph2LastXValue, i1);
                                        DataPoint dataPointB = new DataPoint(graph2LastXValue, i2);
                                        DataPoint dataPointC = new DataPoint(graph2LastXValue, i3);
                                        graph.appendData(dataPointA, dataPointB, dataPointC);
                                    }

                                }
                            });

                        }
                    }

                    @Override
                    public void onConnectedFailure(final Exception e) {
                        handler.post(new Runnable() {
                            public void run() {
                                loadingLayout.loadingFailed(e.getCause().getMessage());
                            }
                        });
                    }
                };
                connectedThread = new ConnectedThread(socket, connectedListener);
                connectedThread.start();
            }

            @Override
            public void onConnectFailure(final Exception e) {
                handler.post(new Runnable() {
                    public void run() {
                        loadingLayout.loadingFailed(e.getCause().getMessage());
                    }
                });
            }
        };

        UUID uuid = UUID.fromString(UUID_STRING);
        connectThread = new ConnectThread(bluetoothDevice, blueToothAdapter, uuid, connectListener);
        connectThread.start();
    }

    private void disconnectBT() {
        if (connectThread != null)
            connectThread.cancel();
        if (connectedThread != null)
            connectedThread.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.graph_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pause:
                pause = !pause;

                if (pause) {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                } else {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
