package org.secfirst.umbrella.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
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
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.Difficulty;
import org.secfirst.umbrella.models.DrawerChildItem;
import org.secfirst.umbrella.models.Favourite;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.InitialData;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.models.Relief.Countries.RWCountries;
import org.secfirst.umbrella.models.Relief.Data;
import org.secfirst.umbrella.models.Relief.Response;
import org.secfirst.umbrella.models.Segment;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static void resetDataToInitial() {
        CheckItem.deleteAll(CheckItem.class);
        Category.deleteAll(Category.class);
        Segment.deleteAll(Segment.class);
        Difficulty.deleteAll(Difficulty.class);
        Favourite.deleteAll(Favourite.class);
        for (Segment segment : InitialData.getSegmentList()) {
            segment.save();
        }
        for (CheckItem checkItem : InitialData.getCheckList()) {
            checkItem.save();
        }
        for (Category category : InitialData.getCategoryList()) {
            category.save();
        }
    }

    public static void migrateData(Global global) {

        ArrayList<Segment> segments = InitialData.getSegmentList();
        Dao<Segment, String> segmentDao = global.getDaoSegment();

        try {
            List<Segment> fromDB = segmentDao.queryForAll();
            if (fromDB.size() == 0) {
                for (Segment segment : segments) {
                    segmentDao.create(segment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<CheckItem> checkList = InitialData.getCheckList();
        Dao<CheckItem, String> checkItemDao = global.getDaoCheckItem();
        try {
            List<CheckItem> listsFromDB = checkItemDao.queryForAll();
            if (listsFromDB.size() == 0) {
                for (CheckItem checkItem : checkList) {
                    checkItemDao.create(checkItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<Category> categoryList = InitialData.getCategoryList();
        Dao<Category, String> categoryDao = global.getDaoCategory();
        try {
            List<Category> catFromDB = categoryDao.queryForAll();
            if (catFromDB.size() == 0) {
                for (Category category : categoryList) {
                    categoryDao.create(category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Registry selRefresh = new Registry("refresh_value", String.valueOf(TimeUnit.MINUTES.toMillis(30)));
        try {
            global.getDaoRegistry().create(selRefresh);
        } catch (SQLException e) {
            e.printStackTrace();
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

    public static ArrayList<Category> getParentCategories() {
        List<Category> categories = Category.listAll(Category.class);
        ArrayList<Category> parentCategories = new ArrayList<Category>();
        for (Category category : categories) {
            if (category.getParent()==0) {
                parentCategories.add(category);
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
        List<Registry> selCountry = Registry.find(Registry.class, "name = ?", "country");
        if (selCountry.size()>0) {
            UmbrellaRestClient.getFeed("http://api.rwlabs.org/v1/countries/?query[value]=" + selCountry.get(0).getValue(), null, context, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Gson gson = new GsonBuilder().create();
                    Type listType = new TypeToken<RWCountries>() {
                    }.getType();
                    RWCountries receivedSegments = gson.fromJson(response.toString(), listType);
                    if (receivedSegments != null) {
                        ArrayList<org.secfirst.umbrella.models.Relief.Countries.Data> results = receivedSegments.getData();
                        if (results.size() > 0) {
                            getReports(results.get(0).getId(), context, global);
                        }
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    public static void getReports(String countryId, final Context context, final Global global) {
        UmbrellaRestClient.getFeed("http://api.rwlabs.org/v1/countries/" + countryId, null, context, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<Response>() {
                }.getType();
                Response receivedResponse = gson.fromJson(response.toString(), listType);
                if (receivedResponse != null) {
                    List<org.secfirst.umbrella.models.Relief.Data> dataList = Arrays.asList(receivedResponse.getData());
                    for (Data data : dataList) {
                        if (data.getFields().getDescriptionhtml()!=null) {
                            Document document = Jsoup.parse(data.getFields().getDescriptionhtml());
                            Element ul = document.select("ul").get(0);
                            global.setFeedItems(new ArrayList<FeedItem>());
                            for(Element li : ul.select("li")) {
                                FeedItem toAdd = new FeedItem(li.text(), "Loading...", li.select("a").get(0).attr("href"));
                                global.getFeedItems().add(toAdd);
                                new GetRWBody(global.getFeedItems().size()-1, toAdd.getUrl(), global).execute();
                            }
                        }
                    }
                }
            }
        });
    }

    private static class GetRWBody extends AsyncTask<String, Void, String> {
        int index;
        String url;
        Global global;

        GetRWBody(int index, String url, Global global) {
            this.index = index;
            this.url = url;
            this.global = global;
        }

        @Override
        protected String doInBackground(String... params) {
            String body ="";
            Document doc;
            try {
                doc = Jsoup.connect(url).get();
                Elements forBody = doc.select("div.body.field");
                if (!forBody.isEmpty()) {
                    global.getFeedItems().get(index).setBody(forBody.get(0).text());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return body;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            global.getFeedItems().get(index).setBody("");
        }

        @Override
        protected void onPostExecute(String result) {
        }
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

    public static int getRefreshValue() {
        int retInterval = 0;
        List<Registry> selInterval = Registry.find(Registry.class, "name = ?", "refresh_interval");
        if (selInterval.size() > 0) {
            try {
                retInterval = Integer.parseInt(selInterval.get(0).getValue());
            } catch(NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
        return retInterval;
    }

    public static void setRefreshValue(int refreshValue) {
        List<Registry> selInterval = Registry.find(Registry.class, "name = ?", "refresh_interval");
        if (selInterval.size() > 0) {
            selInterval.get(0).setValue(String.valueOf(refreshValue));
            selInterval.get(0).save();
        } else {
             new Registry("refresh_interval", String.valueOf(refreshValue)).save();
        }
    }

}
