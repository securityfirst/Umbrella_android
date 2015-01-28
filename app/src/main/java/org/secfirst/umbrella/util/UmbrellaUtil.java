package org.secfirst.umbrella.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.DrawerChildItem;
import org.secfirst.umbrella.models.InitialData;
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

    public static void migrateData(Context context) {
        ArrayList<Segment> segments = InitialData.getSegmentList();
        List<Segment> fromDB = Segment.listAll(Segment.class);
        if (fromDB.size()==0) {
            for (Segment segment : segments) {
                segment.save();
            }
        }

        ArrayList<CheckItem> checkList = InitialData.getCheckList();
        List<CheckItem> listsFromDB = CheckItem.listAll(CheckItem.class);
        if (listsFromDB.size()==0) {
            for (CheckItem checkItem : checkList) {
                checkItem.save();
            }
        }

        ArrayList<Category> categoryList = InitialData.getCategoryList();
        List<Category> catFromDB = Category.listAll(Category.class);
        if (catFromDB.size()==0) {
            for (Category category : categoryList) {
                category.save();
            }
        }
    }

    public static void syncSegments(ArrayList<Segment> segments) {
        Segment.deleteAll(Segment.class);
        for (Segment segment : segments) {
            segment.save();
        }
    }

    public static void syncCategories(ArrayList<Category> categories) {
        Category.deleteAll(Category.class);
        for (Category item : categories) {
            item.save();
        }
    }

    public static void syncCheckLists(ArrayList<CheckItem> checkList) {
        CheckItem.deleteAll(CheckItem.class);
        CheckItem previousItem = null;
        for (CheckItem checkItem : checkList) {
            if (previousItem!=null && checkItem.getTitle().equals(previousItem.getTitle())&& checkItem.getParent()!=0) {
                checkItem.setParent(previousItem.getId());
                checkItem.save();
            } else {
                previousItem = checkItem;
            }
        }
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

    public static ArrayList<String> getParentCategories() {
        List<Category> categories = Category.listAll(Category.class);
        ArrayList<String> parentCategories = new ArrayList<String>();
        for (Category category : categories) {
            if (category.getParent()==0) {
                parentCategories.add(category.getCategory());
            }
        }
        return parentCategories;
    }

    public static ArrayList<ArrayList<DrawerChildItem>> getChildItems() {
        List<Category> categories = Category.listAll(Category.class);
        ArrayList<Category> parentCategories = new ArrayList<Category>();
        for (Category category : categories) {
            if (category.getParent()==0) {
                parentCategories.add(category);
            }
        }

        ArrayList<ArrayList<DrawerChildItem>> childItem = new ArrayList<ArrayList<DrawerChildItem>>();
        for (Category parentCategory : parentCategories) {
            ArrayList<DrawerChildItem> child = new ArrayList<>();
            for (Category category : categories) {
                if (category.getParent() == parentCategory.getId()) {
                    child.add(new DrawerChildItem(category.getCategory(), category.getMId()));
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

    public static void setStatusBarColor(Activity activity, int colorResource) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(colorResource);
        }
    }

}
