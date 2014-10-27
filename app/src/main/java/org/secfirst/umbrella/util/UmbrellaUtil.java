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
import org.secfirst.umbrella.data.CheckListDataSource;
import org.secfirst.umbrella.data.InitialData;
import org.secfirst.umbrella.data.SegmentsDataSource;
import org.secfirst.umbrella.models.CheckItem;
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
        SegmentsDataSource segmentDAO = new SegmentsDataSource(context);
        segmentDAO.open();
        List<Segment> fromDB = segmentDAO.getAllSegments();
        Log.i("db segment count", String.valueOf(fromDB.size()));

        Log.i("commonlist", String.valueOf(CollectionUtils.containsAll(segments, fromDB)));
        if (true) {
            segmentDAO.deleteAllSegments();
            for (Segment segment : segments) {
                Segment inserted = segmentDAO.insertSegment(segment);
            }
        }

        ArrayList<CheckItem> checkList = InitialData.getCheckList();
        Log.i("class segment count", String.valueOf(checkList.size()));
        CheckListDataSource checkListDataSource = new CheckListDataSource(context);
        checkListDataSource.open();
        List<CheckItem> listsFromDB = checkListDataSource.getAllItems();
        Log.i("db check item count", String.valueOf(listsFromDB.size()));

        Log.i("commonlist checkItems", String.valueOf(CollectionUtils.containsAll(checkList, listsFromDB)));
        if (true) {
            checkListDataSource.deleteAllItems();
            CheckItem previousItem = null;
            for (CheckItem checkItem : checkList) {
                if (previousItem!=null && checkItem.getTitle().equals(previousItem.getTitle())&& checkItem.getParent()!=0) {
                    checkItem.setParent(previousItem.getId());
                    checkListDataSource.insertItem(checkItem);
                } else {
                    previousItem = checkListDataSource.insertItem(checkItem);
                }
            }
        }
    }

    public static int dpToPix(int sizeInDp, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (sizeInDp*scale + 0.5f);
    }

}
