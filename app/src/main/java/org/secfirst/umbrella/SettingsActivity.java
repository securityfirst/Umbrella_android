package org.secfirst.umbrella;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.secfirst.umbrella.fragments.DatabaseBackupDialog;
import org.secfirst.umbrella.fragments.SettingsFragment;
import org.secfirst.umbrella.util.FileChooserDialog;
import org.secfirst.umbrella.util.FolderChooserDialog;
import org.secfirst.umbrella.util.OrmHelper;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.File;
import java.io.IOException;

public class SettingsActivity extends BaseActivity implements FolderChooserDialog.FolderCallback, FileChooserDialog.FileCallback {

    Toolbar toolbar;
    private SettingsFragment mSettingsFragment;
    public static File mImportFile;


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
        DatabaseBackupDialog.mPath = folder.getPath();
    }

    @Override
    public void onFolderChooserDismissed(FolderChooserDialog dialog) {

    }

    @Override
    public void onFileSelection(FileChooserDialog dialog, File file) {
        File dst = getApplicationContext().getDatabasePath(OrmHelper.DATABASE_NAME);
        try {
            UmbrellaUtil.copyFile(file, dst);
        } catch (IOException e) {
            e.printStackTrace();
        }
        UmbrellaUtil.doRestartApplication(this);
    }

    @Override
    public void onFileChooserDismissed(FileChooserDialog dialog) {

    }
}