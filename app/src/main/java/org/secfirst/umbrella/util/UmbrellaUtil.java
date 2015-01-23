package org.secfirst.umbrella.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import org.secfirst.umbrella.data.CategoryDataSource;
import org.secfirst.umbrella.data.CheckListDataSource;
import org.secfirst.umbrella.data.InitialData;
import org.secfirst.umbrella.data.SegmentsDataSource;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.DrawerChildItem;
import org.secfirst.umbrella.models.Segment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

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
        SegmentsDataSource segmentDAO = new SegmentsDataSource(context);
        segmentDAO.open();
        List<Segment> fromDB = segmentDAO.getAllSegments();

        if (fromDB.size()==0) {
            syncSegments(segmentDAO, segments);
        }

        ArrayList<CheckItem> checkList = InitialData.getCheckList();
        CheckListDataSource checkListDataSource = new CheckListDataSource(context);
        checkListDataSource.open();
        List<CheckItem> listsFromDB = checkListDataSource.getAllItems();
        if (listsFromDB.size()==0) {
            syncCheckLists(checkListDataSource, checkList);
        }

        ArrayList<Category> categoryList = InitialData.getCategoryList();
        CategoryDataSource categoryDataSource = new CategoryDataSource(context);
        categoryDataSource.open();
        List<Category> catFromDB = categoryDataSource.getAllItems();
        if (catFromDB.size()==0) {
            syncCategories(categoryDataSource, categoryList);
        }
    }

    public static void syncSegments(SegmentsDataSource segmentDAO, ArrayList<Segment> segments) {
        segmentDAO.deleteAllSegments();
        for (Segment segment : segments) {
            segmentDAO.insertSegment(segment);
        }
        segmentDAO.close();
    }

    public static void syncCategories(CategoryDataSource categoryDAO, ArrayList<Category> categories) {
        categoryDAO.dropTable();
        categoryDAO.createTable();
        for (Category item : categories) {
            categoryDAO.insertItem(item);
        }
        categoryDAO.close();
    }

    public static void syncCheckLists(CheckListDataSource checkListDataSource, ArrayList<CheckItem> checkList) {
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
        checkListDataSource.close();
    }

    public static int dpToPix(int sizeInDp, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (sizeInDp*scale + 0.5f);
    }

    public static void dumpIntent(Intent i, Activity activity) {
        String LOG_TAG = activity.getClass().getSimpleName();
        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.e("dump intent", "Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                Log.e(LOG_TAG, "[" + key + "=" + bundle.get(key) + "]");
            }
            Log.e(LOG_TAG, "Dumping Intent end");
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static ProgressDialog launchRingDialogWithText(final Activity activity, String text) {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(activity, "Please wait ...", text, false);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30000);
                    if (!activity.isFinishing()) ringProgressDialog.dismiss();
                } catch (Exception e) { }
            }
        }).start();
        return ringProgressDialog;
    }

    public static ArrayList<String> getParentCategories(Context context) {
        CategoryDataSource catDAO = new CategoryDataSource(context);
        catDAO.open();
        ArrayList<Category> categories = catDAO.getAllItems();
        catDAO.close();
        ArrayList<String> parentCategories = new ArrayList<String>();
        for (Category category : categories) {
            if (category.getParent()==0) {
                parentCategories.add(category.getCategory());
            }
        }
        return parentCategories;
    }

    public static ArrayList<ArrayList<DrawerChildItem>> getChildItems(Context context) {
        CategoryDataSource catDAO = new CategoryDataSource(context);
        catDAO.open();
        ArrayList<Category> categories = catDAO.getAllItems();
        catDAO.close();
        ArrayList<Category> parentCategories = new ArrayList<Category>();
        for (Category category : categories) {
            if (category.getParent()==0) {
                parentCategories.add(category);
            }
        }

        ArrayList<ArrayList<DrawerChildItem>> childItem = new ArrayList<ArrayList<DrawerChildItem>>();
        ArrayList<DrawerChildItem> child = new ArrayList<DrawerChildItem>();
        for (Category parentCategory : parentCategories) {
            child = new ArrayList<DrawerChildItem>();
            for (Category category : categories) {
                if (category.getParent() == parentCategory.getId()) {
                    child.add(new DrawerChildItem(category.getCategory(), category.getId()));
                }
            }
            childItem.add(child);
        }

        return childItem;
    }

    public static String getDifficultyString(int difficulty) {
        switch (difficulty) {
            case 1:
                return "Beginner";
            case 2:
                return "Intermediate";
            case 3:
                return "Expert";
            default:
                return "Beginner";
        }
    }

    public static String checkPasswordStrength(String password) {
        if (password.length()<6) {
            return "Password too short";
        } else if(!Pattern.compile("\\d").matcher(password).find()) {
            return "Password must have at least one digit";
        } else if(!Pattern.compile("[A-Z]").matcher(password).find()) {
            return "Password must have at least one capital letter";
        } else if(!Pattern.compile("[A-Z]").matcher(password).find()) {
            return "Password must have at least one small letter";
        }
        return "";

    }

}
