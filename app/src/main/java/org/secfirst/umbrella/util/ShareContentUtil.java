package org.secfirst.umbrella.util;

import android.content.Context;
import android.content.Intent;

import org.secfirst.umbrella.R;

/**
 * Created by dougl on 22/01/2018.
 */

public class ShareContentUtil {


    public static void shareLinkContent(Context context, String html) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, html);
        sendIntent.setType("text/html");
        context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.send_to)));
    }

    public static void shareTextContent(Context context, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }
}
