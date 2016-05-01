package moser.jens.bluetoothgraph.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphView extends com.jjoe64.graphview.GraphView {

    private static final int MAX_DATA_POINTS = 200;
    private static final boolean SCROLL_TO_END = true;
    private LineGraphSeries<DataPoint> seriesA;
    private LineGraphSeries<DataPoint> seriesB;
    private LineGraphSeries<DataPoint> seriesC;

    public GraphView(Context context) {
        super(context);
        initView();
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public GraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        seriesA = new LineGraphSeries<>();
        seriesB = new LineGraphSeries<>();
        seriesC = new LineGraphSeries<>();

        seriesA.setColor(Color.RED);
        seriesB.setColor(Color.GREEN);
        seriesC.setColor(Color.BLUE);

        seriesA.setDrawDataPoints(true);
        seriesB.setDrawDataPoints(true);
        seriesC.setDrawDataPoints(true);

        seriesA.setDataPointsRadius(10);
        seriesB.setDataPointsRadius(10);
        seriesC.setDataPointsRadius(10);

        seriesA.setTitle("Sensor A");
        seriesB.setTitle("Sensor B");
        seriesC.setTitle("Sensor C");

        getViewport().setXAxisBoundsManual(true);
        getViewport().setMinX(0);
        getViewport().setMaxX(100);


        getViewport().setYAxisBoundsManual(true);
        getViewport().setMinY(0);
        getViewport().setMaxY(80);

        getViewport().setScalable(true);
        getViewport().setScrollable(true);

        getLegendRenderer().setVisible(true);
        getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        addSeries(seriesA);
        addSeries(seriesB);
        addSeries(seriesC);
    }

    public void appendData(DataPoint dataPointA, DataPoint dataPointB, DataPoint dataPointC) {
        seriesA.appendData(dataPointA, SCROLL_TO_END, MAX_DATA_POINTS);
        seriesB.appendData(dataPointB, SCROLL_TO_END, MAX_DATA_POINTS);
        seriesC.appendData(dataPointC, SCROLL_TO_END, MAX_DATA_POINTS);
    }
}
