package org.secfirst.umbrella;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import org.secfirst.umbrella.models.Favourite;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements DifficultyFragment.OnDifficultySelected {

    public DrawerLayout drawer;
    public ExpandableListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public int groupItem, navItem;
    public long drawerItem;
    private Spinner titleSpinner;
    private DrawerChildItem childItem;
    private int fragType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        UmbrellaUtil.setStatusBarColor(this, getResources().getColor(R.color.umbrella_purple_dark));
        global.migrateData();
        if (global.hasPasswordSet() && !global.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (!global.getTermsAccepted()) {
            startActivity(new Intent(this, TourActivity.class));
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
                    e.printStackTrace();
                }
                if (hasDifficulty!=null && hasDifficulty.size() > 0) {
                    Category childCategory = null;
                    try {
                        childCategory = global.getDaoCategory().queryForId(String.valueOf(childItem.getPosition()));
                        if (!childCategory.getDifficultyAdvanced() && position > 0) {
                            position++;
                        }
                        if (!childCategory.getDifficultyBeginner()) {
                            position++;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    hasDifficulty.get(0).setSelected(position);
                    try {
                        global.getDaoDifficulty().update(hasDifficulty.get(0));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (((Integer) titleSpinner.getTag()) == position) {
                    return;
                }
                titleSpinner.setTag(position);
                setFragment(1, childItem.getTitle(), false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ExpandableListView) findViewById(R.id.left_drawer);
        DrawerAdapter adapter = new DrawerAdapter(this);
        View header = View.inflate(this, R.layout.drawer_header, null);
        final TextView loginHeader = (TextView) header.findViewById(R.id.login_header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.hasPasswordSet()) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    global.setPassword(MainActivity.this);
                }
                loginHeader.setText(global.isLoggedIn() ? R.string.log_out : R.string.log_in);
            }
        });
        loginHeader.setText(global.isLoggedIn() ? R.string.log_out : R.string.log_in);
        drawerList.addHeaderView(header);
        drawerList.setAdapter(adapter);
        drawerList.setOnChildClickListener(adapter);
        drawerList.setOnGroupClickListener(adapter);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer,
                R.drawable.ic_drawer, R.string.open_drawer,
                R.string.close_drawer) {
            public void onDrawerClosed(View view) {}

            public void onDrawerOpened(View drawerView) {}
        };

        drawer.setDrawerListener(actionBarDrawerToggle);
        if (getIntent() != null && getIntent().getData() != null && getIntent().getData().getPathSegments().size() > 0) {
            for (ArrayList<DrawerChildItem> groupItem : UmbrellaUtil.getChildItems(MainActivity.this)) {
                for (DrawerChildItem childItem : groupItem) {
                    if (childItem.getTitle().equalsIgnoreCase(getIntent().getData().getPathSegments().get(0).replace('-', ' '))) {
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
                    e.printStackTrace();
                }
            }
        } else {
            setFragment(0, "My Security", true);
            drawer.openDrawer(Gravity.LEFT);
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
                navArray.add(title +" Beginner");
            }
            if (childCategory.getDifficultyAdvanced()) {
                navArray.add(title +" Advanced");
            }
            if (childCategory.getDifficultyExpert()) {
                navArray.add(title +" Expert");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        if (fragType == 0) {
            setTitle(groupName);
            android.support.v4.app.FragmentTransaction trans = fragmentTransaction.replace(R.id.container, DashboardFragment.newInstance(global));
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
                e.printStackTrace();
            }
            if (hasDifficulty!=null && hasDifficulty.size() > 0 && getIntent() != null && getIntent().getData() != null && getIntent().getData().getPathSegments() != null && getIntent().getData().getPathSegments().size() > 1) {
                hasDifficulty.get(0).setSelected(Integer.valueOf(getIntent().getData().getPathSegments().get(1)));
                try {
                    global.getDaoDifficulty().update(hasDifficulty.get(0));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (getIntent() != null && getIntent().getData() != null && getIntent().getData().getPathSegments() != null && getIntent().getData().getPathSegments().size() > 1) {
                try {
                    global.getDaoDifficulty().create(new Difficulty(childItem.getPosition(), Integer.valueOf(getIntent().getData().getPathSegments().get(1))));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                hasDifficulty = global.getDaoDifficulty().queryForEq(Difficulty.FIELD_CATEGORY, String.valueOf(childItem.getPosition()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            drawerItem = childItem.getPosition();
            setNavItems(childItem.getTitle());
            if (hasDifficulty!=null && hasDifficulty.size() > 0) {
                setTitle("");
                boolean checklist = false;
                if (getIntent() != null && getIntent().getData() != null && getIntent().getData().getHost() != null && getIntent().getData().getHost().equalsIgnoreCase("checklist")) {
                    checklist = true;
                }
                setIntent(null);
                android.support.v4.app.FragmentTransaction trans = fragmentTransaction.replace(R.id.container, TabbedFragment.newInstance(childItem.getPosition(), hasDifficulty.get(0).getSelected(), checklist), "tabbed");
                if (!isFirst) {
                    trans.addToBackStack(null);
                }
                trans.commit();
                if (hasDifficulty.get(0).getSelected() >= titleSpinner.getAdapter().getCount()) {
                    titleSpinner.setSelection(titleSpinner.getAdapter().getCount()-1);
                } else {
                    titleSpinner.setSelection(hasDifficulty.get(0).getSelected());
                }
            } else {
                setTitle(childItem.getTitle());
                titleSpinner.setVisibility(View.GONE);
                android.support.v4.app.FragmentTransaction trans = fragmentTransaction.replace(R.id.container, DifficultyFragment.newInstance(childItem.getPosition()), childItem.getTitle());
                if (!isFirst) {
                    trans.addToBackStack(null);
                }
                trans.commit();
            }
        } else {
            setTitle(groupName);
            android.support.v4.app.FragmentTransaction trans = fragmentTransaction.replace(R.id.container, new TabbedFragment.TabbedSegmentFragment());
            if (!isFirst) {
                trans.addToBackStack(null);
            }
            trans.commit();
            titleSpinner.setVisibility(View.GONE);
        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public void onNavigationDrawerItemSelected(DrawerChildItem selectedItem) {
        Category category = null;
        try {
            category = global.getDaoCategory().queryForId(String.valueOf(selectedItem.getPosition()));
            if (category.hasDifficulty()) {
                childItem = selectedItem;
                setFragment(1, "", false);
            } else {
                drawerItem = selectedItem.getPosition();
                setFragment(2, category.getCategory(), false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem _searchMenuItem = menu.findItem(R.id.action_search_view);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(_searchMenuItem);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i("submit", s);
                if (s.length() > 2) {
                    Intent i = new Intent(MainActivity.this, SearchActivity.class);
                    i.setAction(Intent.ACTION_SEARCH);
                    i.putExtra(SearchManager.QUERY, s);
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this, "The search query needs to be at least 3 characters long", Toast.LENGTH_SHORT).show();
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
        MenuItem itemSetPw = menu.findItem(R.id.action_set_password);
        MenuItem itemLogout = menu.findItem(R.id.action_logout);
        MenuItem itemExport = menu.findItem(R.id.export_checklist);
        itemSetPw.setVisible(!global.hasPasswordSet());
        itemResetPw.setVisible(global.hasPasswordSet());
        itemLogout.setVisible(global.hasPasswordSet());
        itemExport.setVisible(false);
        if (childItem != null) {
            List<Difficulty> hasDifficulty = null;
            try {
                hasDifficulty = global.getDaoDifficulty().queryForEq(Difficulty.FIELD_CATEGORY, String.valueOf(childItem.getPosition()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            itemExport.setVisible(hasDifficulty!=null && hasDifficulty.size()>0);
        }
        MenuItem favouriteItem = menu.findItem(R.id.favourite);
        if (fragType == 0 || fragType == 2) {
            favouriteItem.setVisible(false);
        } else if (fragType == 1 && childItem != null) {
            List<Difficulty> hasDifficulty = null;
            try {
                hasDifficulty = global.getDaoDifficulty().queryForEq(Difficulty.FIELD_CATEGORY, String.valueOf(childItem.getPosition()));
                if (hasDifficulty.size() > 0) {
                    List<Favourite> favourites = null;
                    try {
                        QueryBuilder<Favourite, String> queryBuilder = global.getDaoFavourite().queryBuilder();
                        Where<Favourite, String> where = queryBuilder.where();
                        where.eq(Favourite.FIELD_CATEGORY, String.valueOf(childItem.getPosition())).and().eq(Favourite.FIELD_DIFFICULTY, String.valueOf(hasDifficulty.get(0).getSelected()));
                        favourites = queryBuilder.query();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    favouriteItem.setIcon(favourites!=null && favourites.size() > 0 ? R.drawable.abc_btn_rating_star_on_mtrl_alpha : R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            favouriteItem.setVisible(hasDifficulty!=null && hasDifficulty.size() > 0);
        }
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
        if (id == R.id.action_logout) {
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        if (id == R.id.action_set_password) {
            global.setPassword(this);
            return true;
        }
        if (id == R.id.action_reset_password) {
            global.resetPassword(this);
            return true;
        }
        if (id == R.id.export_checklist) {
            List<Difficulty> hasDifficulty = null;
            try {
                hasDifficulty = global.getDaoDifficulty().queryForEq(Difficulty.FIELD_CATEGORY, String.valueOf(childItem.getPosition()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (hasDifficulty!=null && hasDifficulty.size() > 0) {
                String body = "";
                List<CheckItem> items = null;
                QueryBuilder<CheckItem, String> queryBuilder = global.getDaoCheckItem().queryBuilder();
                Where<CheckItem, String> where = queryBuilder.where();
                try {
                    where.eq(CheckItem.FIELD_CATEGORY, String.valueOf(childItem.getPosition())).and().eq(CheckItem.FIELD_DIFFICULTY, String.valueOf(hasDifficulty.get(0).getSelected() + 1));
                    items = queryBuilder.query();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (items!=null) {
                    for (CheckItem checkItem : items) {
                        body += "\n" + (checkItem.getValue() ? "\u2713" : "\u2717") + " " + checkItem.getTitle();
                    }
                }
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:?subject=Checklist&body=" + Uri.encode(body)));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                return true;
            }
        }
        if (id == R.id.favourite) {
            List<Difficulty> hasDifficulty = null;
            try {
                hasDifficulty = global.getDaoDifficulty().queryForEq(Difficulty.FIELD_CATEGORY, String.valueOf(childItem.getPosition()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (hasDifficulty!=null && hasDifficulty.size() > 0) {
                List<Favourite> favourites = null;
                try {
                    QueryBuilder<Favourite, String> queryBuilder = global.getDaoFavourite().queryBuilder();
                    Where<Favourite, String> where = queryBuilder.where();
                    where.eq(Favourite.FIELD_CATEGORY, String.valueOf(childItem.getPosition())).and().eq(Favourite.FIELD_DIFFICULTY, String.valueOf(hasDifficulty.get(0).getSelected()));
                    favourites = queryBuilder.query();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (favourites!=null && favourites.size() > 0) {
                    for (Favourite favourite : favourites) {
                        try {
                            global.getDaoFavourite().delete(favourite);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        global.getDaoFavourite().create(new Favourite(childItem.getPosition(), hasDifficulty.get(0).getSelected()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDifficultySelected(int difficulty) {
        if (difficulty >= titleSpinner.getAdapter().getCount()) {
            titleSpinner.setSelection(titleSpinner.getAdapter().getCount()-1);
        } else {
            titleSpinner.setSelection(difficulty);
        }
        setFragment(1, "", false);
    }

    public void onNavigationDrawerGroupItemSelected(Category category) {
        drawerItem = category.getId();
        setFragment(2, category.getCategory(), false);
    }

}
