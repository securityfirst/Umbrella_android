package org.secfirst.umbrella.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import org.apache.commons.collections4.CollectionUtils;
import org.secfirst.umbrella.data.InitialData;
import org.secfirst.umbrella.data.SegmentsDataSource;
import org.secfirst.umbrella.models.Segment;

import java.util.ArrayList;
import java.util.List;

public class UmbrellaUtil {

    public static void hideSoftKeyboard(Activity activity) {
        if (activity!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view!=null) inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void setupUItoHideKeyboard(View view, final Activity activity) {
        if (activity!=null) {
            if(!(view instanceof EditText) && !(view instanceof ImageButton)) {
                view.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        UmbrellaUtil.hideSoftKeyboard(activity);
                        return false;
                    }
                });
            }

            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    View innerView = ((ViewGroup) view).getChildAt(i);
                    setupUItoHideKeyboard(innerView, activity);
                }
            }
        }
    }

    public static void migrateDataOnStartup(Context context) {
        ArrayList<Segment> segments = InitialData.getSegmentList();
        Log.i("class segment count", String.valueOf(segments.size()));
        SegmentsDataSource dataSource = new SegmentsDataSource(context);
        dataSource.open();
        List<Segment> fromDB = dataSource.getAllSegments();
        Log.i("db segment count", String.valueOf(fromDB.size()));

        Log.i("commonlist", String.valueOf(CollectionUtils.containsAll(segments, fromDB)));
        if (!CollectionUtils.containsAll(segments, fromDB)) {
            dataSource.deleteAllSegments();
            for (Segment segment : segments) {
                dataSource.insertSegment(segment);
            }
        }
    }

}
