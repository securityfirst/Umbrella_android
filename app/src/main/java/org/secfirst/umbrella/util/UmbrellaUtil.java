package org.secfirst.umbrella.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.secfirst.umbrella.BuildConfig;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.DrawerChildItem;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.Registry;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

    public static int dpToPix(int sizeInDp, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (sizeInDp*scale + 0.5f);
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
                } catch (Exception e) {
                    if (BuildConfig.BUILD_TYPE.equals("debug"))
                        Log.getStackTraceString(e.getCause());
                }
            }
        }).start();
        return ringProgressDialog;
    }

    public static ArrayList<Category> getParentCategories(Context context) {
        Global global = (Global) context.getApplicationContext();
        ArrayList<Category> parentCategories = new ArrayList<>();
        try {
            List<Category> categories = global.getDaoCategory().queryForAll();
            for (Category category : categories) {
                if (category.getParent()==0) {
                    parentCategories.add(category);
                }
            }
        } catch (SQLException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
        return parentCategories;
    }

    public static ArrayList<ArrayList<DrawerChildItem>> getChildItems(Context context) {
        Global global = (Global) context.getApplicationContext();
        ArrayList<Category> parentCategories = new ArrayList<>();
        ArrayList<ArrayList<DrawerChildItem>> childItem = new ArrayList<>();
        List<Category> categories = null;
        try {
            categories = global.getDaoCategory().queryForAll();
        } catch (SQLException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
        if (categories!=null) {
            for (Category category : categories) {
                if (category.getParent()==0) {
                    parentCategories.add(category);
                }
            }
            for (Category parentCategory : parentCategories) {
                ArrayList<DrawerChildItem> child = new ArrayList<>();
                for (Category category : categories) {
                    if (category.getParent() == parentCategory.getId()) {
                        child.add(new DrawerChildItem(category.getCategory(), category.getId()));
                    }
                }
                if (parentCategory.getId() == 1) {
                    child.add(new DrawerChildItem("My Checklists", -1));
                    child.add(new DrawerChildItem("Dashboard", -2));
                }
                childItem.add(child);
            }
        }

        return childItem;
    }

    public static String checkPasswordStrength(String password) {
        if (password.length()<8) {
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

    public static boolean getFeeds(final Context context) {
        final Global global = (Global) context.getApplicationContext();
        global.setFeedItems(new ArrayList<FeedItem>());
        Dao<Registry, String> regDao = global.getDaoRegistry();
        List<Registry> selISO2 = null;
        try {
            selISO2 = regDao.queryForEq(Registry.FIELD_NAME, "iso2");
        } catch (SQLException e) {
            UmbrellaUtil.logIt(context, Log.getStackTraceString(e.getCause()));
        }
        if (selISO2!=null && selISO2.size()>0) {
            List<Registry> selections;
            try {
                selections = regDao.queryForEq(Registry.FIELD_NAME, "feed_sources");
                if (selections.size()>0) {
                    String separator = ",";
                    int total = selections.size() * separator.length();
                    for (Registry item : selections) {
                        total += item.getValue().length();
                    }
                    StringBuilder sb = new StringBuilder(total);
                    for (Registry item : selections) {
                        sb.append(separator).append(item.getValue());
                    }
                    String sources = sb.substring(separator.length());
                    String mUrl = "feed?country=" + selISO2.get(0).getValue() + "&sources=" + sources + "&since=0";
                    UmbrellaRestClient.get(mUrl, null, "", context, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            super.onSuccess(statusCode, headers, response);
                            Gson gson = new GsonBuilder().create();
                            Type listType = new TypeToken<ArrayList<FeedItem>>() {
                            }.getType();
                            ArrayList<FeedItem> receivedItems = gson.fromJson(response.toString(), listType);
                            if (receivedItems != null && receivedItems.size() > 0) {
                                global.setFeedItems(receivedItems);
                            }
                        }
                    });
                } else {
                    return false;
                }
                return true;
            } catch (SQLException e) {
                UmbrellaUtil.logIt(context, Log.getStackTraceString(e.getCause()));
            }
        }
        return false;
    }

    public static void logIt(Context context, String message) {
        if (BuildConfig.BUILD_TYPE.equals("debug") && message != null && message.length() > 0)
            Log.i(context.getClass().getSimpleName(), message);
    }

    public static HashMap<String, Integer> getRefreshValues() {
        LinkedHashMap<String, Integer> refreshInterval =new LinkedHashMap<>();
        refreshInterval.put("30 min", (int) TimeUnit.MINUTES.toMillis(30));
        refreshInterval.put("1 hour",  (int) TimeUnit.HOURS.toMillis(1));
        refreshInterval.put("2 hours", (int) TimeUnit.HOURS.toMillis(2));
        refreshInterval.put("4 hours", (int) TimeUnit.HOURS.toMillis(4));
        refreshInterval.put("6 hours", (int) TimeUnit.HOURS.toMillis(6));
        refreshInterval.put("12 hours", (int) TimeUnit.HOURS.toMillis(12));
        refreshInterval.put("24 hours", (int) TimeUnit.HOURS.toMillis(24));
        refreshInterval.put("Manually", 0);
        return refreshInterval;
    }

}
