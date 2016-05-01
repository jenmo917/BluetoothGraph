package moser.jens.bluetoothgraph.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import moser.jens.bluetoothgraph.R;

/**
 * View Layout for showing progress icon and give the user a retry button
 */
public class LoadingLayout extends FrameLayout {

    private FrameLayout layoutContent;
    private FrameLayout layoutLoadingInfo;
    private boolean inflatingInProgress = true;
    private View progressLoader;
    private Button buttonRetry;
    private TextView textViewLoadingInfo;
    private LoadingListener loadingListener;
    private boolean isLoading;

    public boolean isLoading() {
        return isLoading;
    }

    public interface LoadingListener {
        void OnRetryPressed();
    }

    public LoadingLayout(final Context context) {
        super(context);
        init(context);
    }

    public LoadingLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_loading_layout, this, true);

        initGui();
        initListener();

        inflatingInProgress = false;
    }

    private void initListener() {
        buttonRetry.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (loadingListener != null)
                    loadingListener.OnRetryPressed();
            }
        });
    }

    private void initGui() {
        layoutContent = (FrameLayout) findViewById(R.id.layoutContent);
        layoutLoadingInfo = (FrameLayout) findViewById(R.id.layoutLoadingInfo);
        progressLoader = findViewById(R.id.progressLoader);
        buttonRetry = (Button) findViewById(R.id.buttonRetry);
        textViewLoadingInfo = (TextView) findViewById(R.id.textViewLoadingInfo);
    }

    public void loadingStart() {
        isLoading = true;
        progressLoader.setVisibility(View.VISIBLE);
        buttonRetry.setVisibility(View.GONE);
        textViewLoadingInfo.setVisibility(View.GONE);
        layoutLoadingInfo.setVisibility(View.GONE);
        layoutContent.setVisibility(View.INVISIBLE);
    }

    public void loadingSuccesssfull() {
        isLoading = false;
        progressLoader.setVisibility(View.GONE);
        buttonRetry.setVisibility(View.GONE);
        textViewLoadingInfo.setVisibility(View.GONE);
        layoutLoadingInfo.setVisibility(View.GONE);
        layoutContent.setVisibility(View.VISIBLE);
    }

    public void loadingFailed(String failMessage) {
        isLoading = false;
        if (failMessage != null) {
            textViewLoadingInfo.setText(failMessage);
        } else {
            textViewLoadingInfo.setText(R.string.retry_message);
        }

        progressLoader.setVisibility(View.GONE);
        textViewLoadingInfo.setVisibility(View.VISIBLE);
        buttonRetry.setVisibility(View.VISIBLE);
        buttonRetry.requestFocus();
        layoutLoadingInfo.setVisibility(View.INVISIBLE);
        layoutContent.setVisibility(View.GONE);
    }

    public void setLoadingListener(LoadingListener loadingListener) {
        this.loadingListener = loadingListener;
    }

    @Override
    public void addView(@NonNull final View child, final android.view.ViewGroup.LayoutParams params) {
        if (inflatingInProgress) {
            super.addView(child, params);
        } else {
            layoutContent.addView(child, params);
        }
    }

    @Override
    public void addView(@NonNull final View child, final int index, final android.view.ViewGroup.LayoutParams params) {
        if (inflatingInProgress) {
            super.addView(child, index, params);
        } else {
            layoutContent.addView(child, index, params);
        }
    }

    @Override
    public void addView(@NonNull final View child, final int width, final int height) {
        if (inflatingInProgress) {
            super.addView(child, width, height);
        } else {
            layoutContent.addView(child, width, height);
        }
    }

    @Override
    public void addView(@NonNull final View child) {
        if (inflatingInProgress) {
            super.addView(child);
        } else {
            layoutContent.addView(child);
        }
    }

    @Override
    public void addView(@NonNull final View child, final int index) {
        if (inflatingInProgress) {
            super.addView(child, index);
        } else {
            layoutContent.addView(child, index);
        }
    }
}
