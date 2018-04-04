package org.secfirst.umbrella;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.secfirst.umbrella.util.UmbrellaUtil;

public class WebViewActivity extends BaseActivity {

    public static String URL_KEY;
    private ProgressBar mProgress;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private WebView mWebView;
    private boolean mRefreshEnable;
    private final static int MAX_TIME = 30000;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UmbrellaUtil.setStatusBarColor(this, getResources().getColor(R.color.umbrella_purple_dark));
        mWebView = findViewById(R.id.article_web_view);
        mProgress = findViewById(R.id.web_view_load);
        mSwipeRefreshLayout = findViewById(R.id.web_view_swipe_refresh);
        enableActionBar();
        setUpWebView();
        setUpSwipeRefresh();
        mWebView.loadUrl(getLink());
    }

    private void setUpSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
    }

    private void doRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        mProgress.setVisibility(View.INVISIBLE);
        mRefreshEnable = true;
        mWebView.loadUrl(getLink());
    }


    private void setUpWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.clearHistory();
        mWebView.clearFormData();
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.clearCache(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(WebViewActivity.this, description, Toast.LENGTH_SHORT).show();
                mProgress.setVisibility(View.INVISIBLE);
                mSwipeRefreshLayout.setEnabled(false);
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                mProgress.setVisibility(View.INVISIBLE);
                mSwipeRefreshLayout.setEnabled(false);
                if (mRefreshEnable) mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!mRefreshEnable) mProgress.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.setVisibility(View.INVISIBLE);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, MAX_TIME);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgress.setVisibility(View.INVISIBLE);
                if (mRefreshEnable) mSwipeRefreshLayout.setRefreshing(false);

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.web_view, menu);
        //mShareIcon = menu.findItem(R.id.action_share);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.web_view_menu_refresh:
                doRefresh();
                break;

            case R.id.web_view_action_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getLink()));
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_web_view;
    }

    private void enableActionBar() {
        toolbar = findViewById(R.id.web_view_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private String getLink() {
        if (getIntent() != null)
            return getIntent().getStringExtra(URL_KEY);
        return "";
    }
}
