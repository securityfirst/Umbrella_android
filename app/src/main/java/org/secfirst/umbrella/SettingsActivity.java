package org.secfirst.umbrella;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.secfirst.umbrella.fragments.BackupDatabaseDialog;
import org.secfirst.umbrella.fragments.SettingsFragment;
import org.secfirst.umbrella.util.FolderChooserDialog;

import java.io.File;

public class SettingsActivity extends BaseActivity implements FolderChooserDialog.FolderCallback {

    Toolbar toolbar;
    private SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }

    @Override
    public void setLocale(String languageToLoad) {
        super.setLocale(languageToLoad);
        mSettingsFragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new SettingsFragment())
                .commit();

    }

    @Override
    public void onFolderSelection(FolderChooserDialog dialog, File folder) {
        BackupDatabaseDialog.mPath = folder.getPath();
    }

    @Override
    public void onFolderChooserDismissed(FolderChooserDialog dialog) {

    }
}