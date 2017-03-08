package org.secfirst.umbrella.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
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
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.commons.collections4.ListUtils;
import org.apache.http.Header;
import org.json.JSONArray;
import org.jsoup.helper.StringUtil;
import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.BuildConfig;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.CategoryItem;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.Registry;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import timber.log.Timber;

public class UmbrellaUtil {

    private UmbrellaUtil() {
        throw new AssertionError("Instantiating utility class.");
    }

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

    static boolean isNetworkAvailable(Context context) {
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

    public static ArrayList<CategoryItem> getParentCategories(Context context) {
        Global global = (Global) context.getApplicationContext();
        ArrayList<CategoryItem> parentCategories = new ArrayList<>();
        try {
            List<CategoryItem> categories = global.getDaoCategoryItem().queryForAll();
            Timber.d("categories1 %d", categories.size());
            for (CategoryItem category : categories) {
                if (StringUtil.isBlank(category.getParent())) {
                    parentCategories.add(category);
                }
            }
        } catch (SQLException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
        Timber.d("categories2 %d", parentCategories.size());
        return parentCategories;
    }

    public static List<ArrayList<CategoryItem>> getChildItems(Context context) {
        Global global = (Global) context.getApplicationContext();
        List<CategoryItem> parentCategories = new ArrayList<>();
        List<ArrayList<CategoryItem>> childItem = new ArrayList<>();
        try {
            List<CategoryItem> categories = ListUtils.emptyIfNull(global.getDaoCategoryItem().queryForAll());
            for (CategoryItem category : categories) {
                if (StringUtil.isBlank(category.getParent())) {
                    parentCategories.add(category);
                }
            }
            for (CategoryItem parentCategory : parentCategories) {
                ArrayList<CategoryItem> child = new ArrayList<>();
                for (CategoryItem category : categories) {
                    if (category.getParent()!=null && category.getParent().equals(parentCategory.getName())) {
                        child.add(category);
                    }
                }
                if (parentCategory.getId() == 1) {
                    child.add(new CategoryItem(context.getString(R.string.my_checklists)));
                    child.add(new CategoryItem(context.getString(R.string.dashboard)));
                }
                childItem.add(child);
            }
        } catch (SQLException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
        return childItem;
    }

    static String checkPasswordStrength(String password, Context context) {
        if (password.length()<8) {
            return context.getString(R.string.password_too_short);
        } else if(!Pattern.compile("\\d").matcher(password).find()) {
            return context.getString(R.string.password_one_digit);
        } else if(!Pattern.compile("[A-Z]").matcher(password).find()) {
            return context.getString(R.string.password_one_capital);
        } else if(!Pattern.compile("[A-Z]").matcher(password).find()) {
            return context.getString(R.string.password_one_small);
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
        Registry selISO2 = global.getRegistry("iso2");
        if (selISO2!=null) {
            List<Registry> selections;
            try {
                selections = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
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
                    String mUrl = "feed?country=" + selISO2.getValue() + "&sources=" + sources + "&since="+global.getFeedItemsRefreshed();
                    UmbrellaRestClient.get(mUrl, null, "", context, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            super.onSuccess(statusCode, headers, response);
                            Gson gson = new GsonBuilder().create();
                            Type listType = new TypeToken<ArrayList<FeedItem>>() {
                            }.getType();
                            ArrayList<FeedItem> receivedItems = gson.fromJson(response.toString(), listType);
                            if (receivedItems != null && !receivedItems.isEmpty()) {
                                List<FeedItem> oldList = global.getFeedItems();
                                List<FeedItem> notificationItems = new ArrayList<>();
                                if(global.getNotificationsEnabled()) {
                                    for (FeedItem feedItem : receivedItems) {
                                        if (!oldList.contains(feedItem)) {
                                            try {
                                                global.getDaoFeedItem().create(feedItem);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                            notificationItems.add(feedItem);
                                        }
                                    }
                                    if (notificationItems.size() != 0) {
                                        context.sendOrderedBroadcast(BaseActivity.getNotificationIntent(notificationItems), null);
                                    }
                                }
                            }
                        }
                    });
                } else {
                    return false;
                }
                return true;
            } catch (SQLException e) {
                Timber.e(e);
            }
        }
        return false;
    }

    public static CharSequence[] getLanguageEntries() {
        List<String> languageLabels = new ArrayList<>();
        languageLabels.add("English");
        languageLabels.add("Español");
        return languageLabels.toArray(new CharSequence[languageLabels.size()]);
    }

    public static CharSequence[] getLanguageEntryValues() {
        List<String> languageLabels = new ArrayList<>();
        languageLabels.add("en");
        languageLabels.add("es");
        return languageLabels.toArray(new CharSequence[languageLabels.size()]);
    }

    public static HashMap<String, Integer> getRefreshValues(Context context) {
        LinkedHashMap<String, Integer> refreshInterval =new LinkedHashMap<>();
        refreshInterval.put(context.getString(R.string.half_hour), (int) TimeUnit.MINUTES.toMillis(30));
        refreshInterval.put("1 "+context.getString(R.string.hour),  (int) TimeUnit.HOURS.toMillis(1));
        refreshInterval.put("2 "+context.getString(R.string.hours), (int) TimeUnit.HOURS.toMillis(2));
        refreshInterval.put("4 "+context.getString(R.string.hours), (int) TimeUnit.HOURS.toMillis(4));
        refreshInterval.put("6 "+context.getString(R.string.hours), (int) TimeUnit.HOURS.toMillis(6));
        refreshInterval.put("12 "+context.getString(R.string.hours), (int) TimeUnit.HOURS.toMillis(12));
        refreshInterval.put("24 "+context.getString(R.string.hours), (int) TimeUnit.HOURS.toMillis(24));
        refreshInterval.put(context.getString(R.string.manually), 0);
        return refreshInterval;
    }

    public static CharSequence[] getRefreshEntries(Context context) {
        List<String> listItems = new ArrayList<>();
        listItems.add(context.getString(R.string.half_hour));
        listItems.add("1 "+context.getString(R.string.hour));
        listItems.add("2 "+context.getString(R.string.hours));
        listItems.add("4 "+context.getString(R.string.hours));
        listItems.add("6 "+context.getString(R.string.hours));
        listItems.add("12 "+context.getString(R.string.hours));
        listItems.add("24 "+context.getString(R.string.hours));
        listItems.add(context.getString(R.string.manually));
        return listItems.toArray(new CharSequence[listItems.size()]);
    }

    public static CharSequence[] getRefreshEntryValues() {
        List<String> listItems = new ArrayList<>();
        listItems.add(String.valueOf(TimeUnit.MINUTES.toMillis(30)));
        listItems.add(String.valueOf(TimeUnit.HOURS.toMillis(1)));
        listItems.add(String.valueOf(TimeUnit.HOURS.toMillis(2)));
        listItems.add(String.valueOf(TimeUnit.HOURS.toMillis(4)));
        listItems.add(String.valueOf(TimeUnit.HOURS.toMillis(6)));
        listItems.add(String.valueOf(TimeUnit.HOURS.toMillis(12)));
        listItems.add(String.valueOf(TimeUnit.HOURS.toMillis(24)));
        listItems.add(String.valueOf(0));
        return listItems.toArray(new CharSequence[listItems.size()]);
    }

    public static String getStringFromAssetFile(Context context, String fileName) {
        String str = "";
        InputStream is;
        try {
            is = context.getAssets().open(fileName);
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            str = new String(buffer);
        } catch (IOException e) {
            Timber.e(e);
        }
        return str;
    }

    public static void setMaskMode(Activity activity, boolean masked) {
        String packageName = BuildConfig.APPLICATION_ID;
        List<String> disableNames = new ArrayList<>();
        disableNames.add("org.secfirst.umbrella.MainActivity-normal");
        disableNames.add("org.secfirst.umbrella.MainActivity-calculator");
        String activeName = disableNames.remove(masked ? 1 :0);

        activity.getPackageManager().setComponentEnabledSetting(
                new ComponentName(packageName, activeName),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        for (int i = 0; i < disableNames.size(); i++) {
            try {
                activity.getPackageManager().setComponentEnabledSetting(
                        new ComponentName(packageName, disableNames.get(i)),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isAppMasked(Activity activity) {
        String packageName = BuildConfig.APPLICATION_ID;
        List<String> disableNames = new ArrayList<>();
        disableNames.add("org.secfirst.umbrella.MainActivity-normal");
        disableNames.add("org.secfirst.umbrella.MainActivity-calculator");
        for (int i = 0; i < disableNames.size(); i++) {
            try {
                int flag = activity.getPackageManager().getComponentEnabledSetting(new ComponentName(packageName, disableNames.get(i)));
                return flag > 1 && i == 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static int fromDifficulty(Context context, String difficulty) {
        ArrayList<String> diffs = new ArrayList<>();
        diffs.add(context.getString(R.string.beginner));
        diffs.add(context.getString(R.string.advanced));
        diffs.add(context.getString(R.string.expert));
        for (int i = 0; i < diffs.size(); i ++) {
            if (diffs.get(i).toLowerCase().equals(difficulty.toLowerCase())) {
                return i;
            }
        }
        return 0;
    }

    public static String fromDifficultyInt(Context context, int difficulty) {
        ArrayList<String> diffs = new ArrayList<>();
        diffs.add(context.getString(R.string.beginner));
        diffs.add(context.getString(R.string.advanced));
        diffs.add(context.getString(R.string.expert));
        return diffs.get((difficulty>0 && diffs.size()>difficulty) ? difficulty : 0);
    }

}
