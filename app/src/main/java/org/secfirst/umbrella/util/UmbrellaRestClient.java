package org.secfirst.umbrella.util;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.secfirst.umbrella.R;

public class UmbrellaRestClient {

    private static final String BASE_URL = "https://api.secfirst.org";
    private static final String VERSION = "v1";

    private static AsyncHttpClient client;

    public UmbrellaRestClient() {
        client = getTolerantClient();
    }

    public static AsyncHttpClient getTolerantClient() {
        AsyncHttpClient client = new AsyncHttpClient();
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) client.getHttpClient().getConnectionManager().getSchemeRegistry().getScheme("https")
                .getSocketFactory();
        final X509HostnameVerifier delegate = sslSocketFactory.getHostnameVerifier();
        if(!(delegate instanceof WildCardSSLVerifier)) {
            sslSocketFactory.setHostnameVerifier(new WildCardSSLVerifier(delegate));
        }
        return client;
    }

    public static void get(String url, RequestParams params, String token, Context context, AsyncHttpResponseHandler responseHandler) {
        if (isRequestReady(context, token)) client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getFeed(String url, RequestParams params, Context context, AsyncHttpResponseHandler responseHandler) {
        if (client==null) client = getTolerantClient();
        if (UmbrellaUtil.isNetworkAvailable(context)) client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, String token, Context context, AsyncHttpResponseHandler responseHandler) {
        if (isRequestReady(context, token)) client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void put(String url, RequestParams params, String token, Context context, AsyncHttpResponseHandler responseHandler) {
        if (isRequestReady(context, token)) client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(String url, String token, Context context, AsyncHttpResponseHandler responseHandler) {
        if (isRequestReady(context, token)) client.delete(getAbsoluteUrl(url), responseHandler);
    }

    private static boolean isRequestReady(Context context, String token) {
        boolean isReady = UmbrellaUtil.isNetworkAvailable(context);
        if (isReady) {
            if (token!=null) client.addHeader("token", token);
        } else {
            Toast.makeText(context, context.getString(R.string.no_network_message), Toast.LENGTH_LONG).show();
        }
        return isReady;
    }

    private static String getAbsoluteUrl(String url) {
        return BASE_URL + "/" + VERSION + "/"+url;
    }

}