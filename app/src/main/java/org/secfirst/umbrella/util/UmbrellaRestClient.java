package org.secfirst.umbrella.util;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.secfirst.umbrella.R;
import org.thoughtcrime.ssl.pinning.PinningSSLSocketFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Locale;

import timber.log.Timber;

public class UmbrellaRestClient {

    private static final String BASE_URL = "https://api.secfirst.org";
    private static final String VERSION = "v3";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static AsyncHttpClient getClientForApiUpdates(Context context) {
        String[] pins = new String[] {
                "da9b52a8771169d31318a567e1dc9b1f44b5b35c"
        };
        try {
            client.setSSLSocketFactory(new PinningSSLSocketFactory(context ,pins, 0));
            client.addHeader("Accept-Language", Locale.getDefault().toString());
            client.addHeader("X-Tent-Language", Locale.getDefault().toString());
        } catch (UnrecoverableKeyException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
            Timber.e(e);
        }
        return client;
    }

    public static void get(String url, RequestParams params, String token, Context context, AsyncHttpResponseHandler responseHandler) {
        client = getClientForApiUpdates(context);
        if (isRequestReady(context, token)) client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static boolean isRequestReady(Context context) {
        return isRequestReady(context, null);
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

    public static String getAbsoluteUrl(String url) {
        return BASE_URL + "/" + VERSION + "/"+url;
    }

}
