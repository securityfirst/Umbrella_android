package org.secfirst.umbrella;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewDialog extends DialogFragment implements View.OnClickListener {


    private WebView mWebView;
    private TextView mClose;
    private TextView mOpen;
    private String mUrl;

    public WebViewDialog() {
        // Required empty public constructor
    }

    public static WebViewDialog newInstance(String url) {
        Bundle args = new Bundle();
        WebViewDialog fragment = new WebViewDialog();
        fragment.mUrl = url;
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_web_view_dialog, container, false);
        mWebView = view.findViewById(R.id.web_view_dialog);
        mClose = view.findViewById(R.id.web_view_close);
        mOpen = view.findViewById(R.id.web_view_open);
        mClose.setOnClickListener(this);
        mOpen.setOnClickListener(this);

        return view;
    }

    private void setUpWebView() {
        mWebView.loadUrl(mUrl);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpWebView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.web_view_open:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mUrl));
                startActivity(intent);
                break;

            case R.id.web_view_close:
                mWebView.clearHistory();
                mWebView.clearCache(true);
                dismiss();
                break;
        }

    }
}
