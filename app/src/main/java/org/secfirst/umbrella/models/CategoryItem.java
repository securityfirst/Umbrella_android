package org.secfirst.umbrella.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.PreparedQuery;

import org.secfirst.umbrella.util.Global;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CategoryItem {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PARENT = "parent";
    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int id;
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    @DatabaseField(columnName = FIELD_PARENT)
    private String parent;
    @DatabaseField(persisted = false)
    private List<CategoryItem> subcategories;
    @DatabaseField(persisted = false)
    private List<ChecksItem> checks;
    @DatabaseField(persisted = false)
    private List<ItemsItem> items;

    public CategoryItem() {}

    public CategoryItem(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean hasDifficulty(Global global) {
        List<ItemsItem> items = new ArrayList<>();
        try {
            PreparedQuery<ItemsItem> queryBuilder = global.getDaoItemsItem().queryBuilder()
                    .groupBy(ItemsItem.FIELD_DIFFICULTY)
                    .where().eq(ItemsItem.FIELD_CATEGORY, name).prepare();
            items = global.getDaoItemsItem().query(queryBuilder);
            for (ItemsItem item : items) {
                Timber.d("itit %s", item);
            }

        } catch (SQLException e) {
            Timber.e(e);
        }
        return items.size()>1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CategoryItem> getSubcategories() {
        return subcategories;
    }

    public List<ChecksItem> getChecks() {
        return checks;
    }

    public List<ItemsItem> getItems() {
        return items==null ? new ArrayList<ItemsItem>() : items;
    }

    public boolean hasDifficulty(Global global, String difficulty) {
        try {
            PreparedQuery<ItemsItem> queryBuilder = global.getDaoItemsItem().queryBuilder().setCountOf(true)
                    .where().eq(ItemsItem.FIELD_CATEGORY, name).and().like(ItemsItem.FIELD_DIFFICULTY, difficulty).prepare();
            long items = global.getDaoItemsItem().countOf(queryBuilder);

            return items>0;
        } catch (SQLException e) {
            Timber.e(e);
        }
        return false;
    }

    public Difficulty getSelectedDifficulty(Global global) {
        Difficulty item = null;
        try {
            PreparedQuery<Difficulty> queryBuilder = global.getDaoDifficulty().queryBuilder().where().eq(ItemsItem.FIELD_CATEGORY, name).prepare();
            item = global.getDaoDifficulty().queryForFirst(queryBuilder);
        } catch (SQLException e) {
            Timber.e(e);
        }
        return item;
    }
}