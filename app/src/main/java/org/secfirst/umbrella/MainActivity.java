package org.secfirst.umbrella;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.secfirst.umbrella.adapters.DrawerAdapter;
import org.secfirst.umbrella.fragments.DashboardFragment;
import org.secfirst.umbrella.fragments.DifficultyFragment;
import org.secfirst.umbrella.fragments.TabbedFragment;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.Difficulty;
import org.secfirst.umbrella.models.DrawerChildItem;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class MainActivity extends BaseActivity implements DifficultyFragment.OnDifficultySelected, DrawerLayout.DrawerListener, OnShowcaseEventListener   {

    public DrawerLayout drawer;
    public ExpandableListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public int groupItem, navItem;
    public long drawerItem;
    public Spinner titleSpinner;
    private DrawerChildItem childItem;
    private int fragType = 0;
    public MenuItem favouriteItem;
    public boolean shownCoachmark = false;
    private TextView loginHeader;
    private View header;
    private boolean ran = false;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UmbrellaUtil.setStatusBarColor(this, getResources().getColor(R.color.umbrella_purple_dark));

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ExpandableListView) findViewById(R.id.left_drawer);
        header = View.inflate(this, R.layout.drawer_header, null);
        loginHeader = (TextView) header.findViewById(R.id.login_header);
        drawerList.addHeaderView(header);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UmbrellaUtil.isAppMasked(MainActivity.this)) {
            Intent i = new Intent(this, CalcActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            return;
        }
        boolean hasOpened = global.initializeSQLCipher("");
        if (!hasOpened) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (!global.getTermsAccepted()) {
            startActivity(new Intent(this, TourActivity.class));
        } else {
            if (titleSpinner!=null) {
                return;
            }
            titleSpinner = (Spinner) findViewById(R.id.spinner_nav);
            titleSpinner.setTag(0);
            navItem = 0;
            groupItem = -1;
            titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    List<Difficulty> hasDifficulty = null;
                    try {
                        hasDifficulty = global.getDaoDifficulty().queryForEq(Difficulty.FIELD_CATEGORY, String.valueOf(childItem.getPosition()));
                    } catch (SQLException e) {
                        Timber.e(e);
                    }
                    if (hasDifficulty != null && !hasDifficulty.isEmpty()) {
                        Category childCategory;
                        try {
                            childCategory = global.getDaoCategory().queryForId(String.valueOf(childItem.getPosition()));
                            if (!childCategory.getDifficultyAdvanced() && position > 0) {
                                position++;
                            }
                            if (!childCategory.getDifficultyBeginner()) {
                                position++;
                            }
                        } catch (SQLException e) {
                            if (BuildConfig.BUILD_TYPE.equals("debug"))
                                Log.getStackTraceString(e.getCause());
                        }
                        hasDifficulty.get(0).setSelected(position);
                        try {
                            global.getDaoDifficulty().update(hasDifficulty.get(0));
                        } catch (SQLException e) {
                            Timber.e(e);
                        }
                    }
                    if (!ran || ((Integer) titleSpinner.getTag()) == position) {
                        ran = true;
                        return;
                    }
                    titleSpinner.setTag(position);
                    setFragment(1, childItem.getTitle(), false);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            DrawerAdapter adapter = new DrawerAdapter(this);
            drawerList.setAdapter(adapter);
            drawerList.setOnChildClickListener(adapter);
            drawerList.setOnGroupClickListener(adapter);

            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, this.toolbar, R.string.open_drawer, R.string.close_drawer);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            actionBarDrawerToggle.syncState();
            loginHeader.setText(global.isLoggedIn() ? R.string.log_out : R.string.log_in);
            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (global.isLoggedIn() && global.hasPasswordSet(false) && !global.getSkipPassword()) {
                        global.logout(MainActivity.this, true);
                    } else {
                        global.setPassword(MainActivity.this, null);
                    }
                    loginHeader.setText(global.isLoggedIn() ? R.string.log_out : R.string.log_in);
                }
            });

            drawer.setDrawerListener(this);
            if (getIntent() != null && getIntent().getData() != null && !getIntent().getData().getPathSegments().isEmpty()) {
                for (ArrayList<DrawerChildItem> groupItem : UmbrellaUtil.getChildItems(MainActivity.this)) {
                    for (DrawerChildItem childItem : groupItem) {
                        if (childItem.getTitle().equalsIgnoreCase(getIntent().getData().getPathSegments().get(0).replace('-', ' ').replace('_', '-'))) {
                            this.childItem = childItem;
                        }
                    }
                }
                if (childItem != null) {
                    try {
                        Category category = global.getDaoCategory().queryForId(String.valueOf(childItem.getPosition()));
                        if (category.hasDifficulty()) {
                            setFragment(1, "", true);
                        } else {
                            drawerItem = childItem.getPosition();
                            setFragment(2, category.getCategory(), true);
                        }
                    } catch (SQLException e) {
                        Timber.e(e);
                    }
                }
            } else {
                setFragment(0, getString(R.string.my_security), true);
                if (!global.hasShownNavAlready()) {
                    drawer.openDrawer(Gravity.LEFT);
                    global.navShown();
                }
            }
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    public void setNavItems(String title) {
        ArrayList<String> navArray = new ArrayList<>();
        try {
            Category childCategory = global.getDaoCategory().queryForId(String.valueOf(childItem.getPosition()));
            if (childCategory.getDifficultyBeginner()) {
                navArray.add(title +" "+getString(R.string.beginner));
            }
            if (childCategory.getDifficultyAdvanced()) {
                navArray.add(title +" "+getString(R.string.advanced));
            }
            if (childCategory.getDifficultyExpert()) {
                navArray.add(title +" "+getString(R.string.expert));
            }
        } catch (SQLException e) {
            Timber.e(e);
        }
        ArrayAdapter<String> navAdapter = new ArrayAdapter<>(this, R.layout.spinner_nav_item, android.R.id.text1, navArray);
        titleSpinner.setVisibility(View.VISIBLE);
        titleSpinner.setAdapter(navAdapter);
    }

    public void setFragment(int fragType, String groupName, boolean isFirst) {
        this.fragType = fragType;
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        drawer.closeDrawer(drawerList);
        if (fragType == 0 || fragType == -1) {
            setTitle(groupName);
            android.support.v4.app.FragmentTransaction trans = fragmentTransaction.replace(R.id.container, DashboardFragment.newInstance(global, fragType == -1));
            if (!isFirst) {
                trans.addToBackStack(null);
            }
            trans.commit();
            titleSpinner.setVisibility(View.GONE);
        } else if (fragType == 1) {
            List<Difficulty> hasDifficulty = null;
            try {
                hasDifficulty = global.getDaoDifficulty().queryForEq(Difficulty.FIELD_CATEGORY, String.valueOf(childItem.getPosition()));
            } catch (SQLException e) {
                Timber.e(e);
            }
            if (hasDifficulty != null && !hasDifficulty.isEmpty() && getIntent() != null && getIntent().getData() != null && getIntent().getData().getPathSegments() != null && getIntent().getData().getPathSegments().size() > 1) {
                hasDifficulty.get(0).setSelected(Integer.valueOf(getIntent().getData().getPathSegments().get(1)));
                try {
                    global.getDaoDifficulty().update(hasDifficulty.get(0));
                } catch (SQLException e) {
                    Timber.e(e);
                }
            } else if (getIntent() != null && getIntent().getData() != null && getIntent().getData().getPathSegments() != null && getIntent().getData().getPathSegments().size() > 1) {
                try {
                    global.getDaoDifficulty().create(new Difficulty(childItem.getPosition(), Integer.valueOf(getIntent().getData().getPathSegments().get(1))));
                } catch (SQLException e) {
                    Timber.e(e);
                }
            }
            try {
                hasDifficulty = global.getDaoDifficulty().queryForEq(Difficulty.FIELD_CATEGORY, String.valueOf(childItem.getPosition()));
            } catch (SQLException e) {
                Timber.e(e);
            }
            drawerItem = childItem.getPosition();
            setNavItems(childItem.getTitle());
            if (hasDifficulty != null) {
                int spinnerNumber = 0;
                if (!hasDifficulty.isEmpty()) spinnerNumber = hasDifficulty.get(0).getSelected();
                setTitle("");
                boolean checklist = false;
                if (getIntent() != null && getIntent().getData() != null && getIntent().getData().getHost() != null) {
                    if (getIntent().getData().getHost().equalsIgnoreCase("checklist"))
                        checklist = true;
                    else if (getIntent().getData().getHost().equalsIgnoreCase("lesson") && getIntent().getData().getPathSegments() != null && getIntent().getData().getPathSegments().size() > 2)
                        page = Integer.valueOf(getIntent().getData().getPathSegments().get(2));
                }
                setIntent(null);
                android.support.v4.app.FragmentTransaction trans = fragmentTransaction.replace(R.id.container, TabbedFragment.newInstance(childItem.getPosition(), spinnerNumber, checklist, page), "tabbed");
                if (!isFirst) {
                    trans.addToBackStack(null);
                }
                trans.commit();
                if (spinnerNumber >= titleSpinner.getAdapter().getCount()) {
                    titleSpinner.setSelection(titleSpinner.getAdapter().getCount() - 1);
                } else {
                    titleSpinner.setSelection(spinnerNumber);
                }
            }
        } else if (fragType == 2) {
            setTitle(groupName);
            android.support.v4.app.FragmentTransaction trans;
            if (drawerItem == 58) { // index into pageviewer
                trans = fragmentTransaction.replace(R.id.container, TabbedFragment.newInstance(drawerItem, 0, false, 0), "tabbed");
            } else {
                trans = fragmentTransaction.replace(R.id.container, new TabbedFragment.TabbedSegmentFragment());
            }
            if (!isFirst) {
                trans.addToBackStack(null);
            }
            trans.commit();
            titleSpinner.setVisibility(View.GONE);
        } else if (fragType == 3) {
            setTitle(childItem.getTitle());
            titleSpinner.setVisibility(View.GONE);
            android.support.v4.app.FragmentTransaction trans = fragmentTransaction.replace(R.id.container, DifficultyFragment.newInstance(childItem.getPosition()), childItem.getTitle());
            if (!isFirst) {
                trans.addToBackStack(null);
            }
            trans.commit();
        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (actionBarDrawerToggle!=null) actionBarDrawerToggle.syncState();
    }

    public void onNavigationDrawerItemSelected(DrawerChildItem selectedItem) {
        Category category;
        try {
            category = global.getDaoCategory().queryForId(String.valueOf(selectedItem.getPosition()));
            if (category.hasDifficulty()) {
                childItem = selectedItem;
                setFragment(3, "", false);
            } else {
                drawerItem = selectedItem.getPosition();
                setFragment(2, category.getCategory(), false);
            }
        } catch (SQLException e) {
            Timber.e(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem _searchMenuItem = menu.findItem(R.id.action_search_view);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(_searchMenuItem);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.length() > 2) {
                    Intent i = new Intent(MainActivity.this, SearchActivity.class);
                    i.setAction(Intent.ACTION_SEARCH);
                    i.putExtra(SearchManager.QUERY, s);
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this, R.string.search_query_needs_to_be_at_least, Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        MenuItemCompat.setOnActionExpandListener(_searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemResetPw = menu.findItem(R.id.action_reset_password);
        MenuItem itemChangePw = menu.findItem(R.id.action_change_password);
        MenuItem itemSetPw = menu.findItem(R.id.action_set_password);
        MenuItem itemLogout = menu.findItem(R.id.action_logout);
        MenuItem itemExport = menu.findItem(R.id.export_checklist);
        itemSetPw.setVisible(!global.hasPasswordSet(true));
        itemResetPw.setVisible(global.hasPasswordSet(false) && !global.getSkipPassword());
        itemChangePw.setVisible(global.hasPasswordSet(false) && !global.getSkipPassword());
        itemLogout.setVisible(global.hasPasswordSet(false) && !global.getSkipPassword());
        itemExport.setVisible(false);
        if (childItem != null) {
            List<Difficulty> hasDifficulty = null;
            try {
                hasDifficulty = global.getDaoDifficulty().queryForEq(Difficulty.FIELD_CATEGORY, String.valueOf(childItem.getPosition()));
            } catch (SQLException e) {
                Timber.e(e);
            }
            itemExport.setVisible(hasDifficulty!=null && !hasDifficulty.isEmpty());
        }
        favouriteItem = menu.findItem(R.id.favourite);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        int id = item.getItemId();
        if (sdk >= android.os.Build.VERSION_CODES.HONEYCOMB && id==android.R.id.home) {
            if (drawer.isDrawerOpen(drawerList))
                drawer.closeDrawer(drawerList);
            else
                drawer.openDrawer(drawerList);
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_mask) {
            new MaterialDialog.Builder(this)
                    .title(R.string.masking_mode_title)
                    .content(getString(R.string.masking_mode_body, getString(R.string.app_calc)))
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            UmbrellaUtil.setMaskMode(MainActivity.this, true);
                            if (global.hasPasswordSet(false)) global.logout(MainActivity.this, false);
                            Intent i = new Intent(MainActivity.this, CalcActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        }
                    })
                    .show();
        }
        if (id == R.id.action_logout) {
            global.logout(this, true);
            return true;
        }
        if (id == R.id.action_set_password) {
            global.setPassword(this, null);
            return true;
        }
        if (id == R.id.action_reset_password) {
            global.resetPassword(this);
            return true;
        }
        if (id == R.id.action_change_password) {
            global.setPassword(this, null, global.hasPasswordSet(true));
            return true;
        }
        if (id == R.id.export_checklist) {
            List<Difficulty> hasDifficulty = null;
            try {
                hasDifficulty = global.getDaoDifficulty().queryForEq(Difficulty.FIELD_CATEGORY, String.valueOf(childItem.getPosition()));
            } catch (SQLException e) {
                Timber.e(e);
            }
            if (hasDifficulty!=null && !hasDifficulty.isEmpty()) {
                String body = "";
                List<CheckItem> items = null;
                QueryBuilder<CheckItem, String> queryBuilder = global.getDaoCheckItem().queryBuilder();
                Where<CheckItem, String> where = queryBuilder.where();
                try {
                    where.eq(CheckItem.FIELD_CATEGORY, String.valueOf(childItem.getPosition())).and().eq(CheckItem.FIELD_DIFFICULTY, String.valueOf(hasDifficulty.get(0).getSelected() + 1));
                    items = queryBuilder.query();
                } catch (SQLException e) {
                    if (BuildConfig.BUILD_TYPE.equals("debug"))
                        Log.getStackTraceString(e.getCause());
                }
                if (items!=null) {
                    for (CheckItem checkItem : items) {
                        body += "\n" + (checkItem.getParent()==0 ? "" : "   ") + (checkItem.getValue() ? "\u2713" : "\u2717") + " " + ((checkItem.getParent()==0) ? checkItem.getTitle() : checkItem.getText());
                    }
                }
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:?subject=Checklist&body=" + Uri.encode(body)));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDifficultySelected(int difficulty) {
        setNavItems(childItem.getTitle());
        if (difficulty >= titleSpinner.getAdapter().getCount()) {
            titleSpinner.setSelection(titleSpinner.getAdapter().getCount() - 1);
        } else {
            titleSpinner.setSelection(difficulty);
        }
        setFragment(1, "", false);
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin * 5, margin * 5);
        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(R.id.spinner_nav, this))
                .setContentText(getString(R.string.click_here_to_change_level))
                .setStyle(R.style.CustomShowcaseTheme4)
                .hideOnTouchOutside()
                .singleShot(2)
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
                        lps.setMargins(margin, margin, margin * 5, margin * 5);
                        new ShowcaseView.Builder(MainActivity.this)
                                .setTarget(new ViewTarget(R.id.pager_title_strip, MainActivity.this))
                                .setContentText(getString(R.string.swipe_left_to_read))
                                .setStyle(R.style.CustomShowcaseTheme4)
                                .hideOnTouchOutside()
                                .singleShot(3)
                                .build()
                                .setButtonPosition(lps);
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                    }
                })
                .build()
                .setButtonPosition(lps);
    }

    public void onNavigationDrawerGroupItemSelected(Category category) {
        drawerItem = category.getId();
        setFragment(2, category.getCategory(), false);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        actionBarDrawerToggle.onDrawerSlide(drawerView, slideOffset);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        actionBarDrawerToggle.onDrawerOpened(drawerView);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        actionBarDrawerToggle.onDrawerClosed(drawerView);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
            lps.setMargins(margin, margin, margin * 5, margin * 5);
            new ShowcaseView.Builder(this)
                    .setTarget(new PointTarget(0, 0))
                    .setContentText(getString(R.string.swipe_to_view_menu))
                    .setStyle(R.style.CustomShowcaseTheme4)
                    .hideOnTouchOutside()
                    .singleShot(1)
                    .setShowcaseEventListener(this)
                    .build()
                    .setButtonPosition(lps);
            if (!shownCoachmark)
               onShowcaseViewHide(null);
        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        actionBarDrawerToggle.onDrawerStateChanged(newState);
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        if (fragType == 0) {
            RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
            lps.setMargins(margin, margin, margin * 5, margin * 5);
            new ShowcaseView.Builder(MainActivity.this)
                    .setTarget(new ViewTarget(R.id.pager_title_strip, this))
                    .setContentText(getString(R.string.view_all_checlists))
                    .setStyle(R.style.CustomShowcaseTheme4)
                    .hideOnTouchOutside()
                    .singleShot(7)
                    .build()
                    .setButtonPosition(lps);
        }
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {
        shownCoachmark = true;
    }

}
