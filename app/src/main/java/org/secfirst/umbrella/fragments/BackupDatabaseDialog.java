package org.secfirst.umbrella.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.secfirst.umbrella.BuildConfig;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.OrmHelper;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by dougl on 20/03/2018.
 */

public class BackupDatabaseDialog extends DialogFragment implements View.OnClickListener {

    public static final int EXPORTED_DB_REQUEST = 1;
    private Button mOk;
    private Button mCancel;
    private AppCompatEditText mFileName;
    private TextView mPath;
    private RadioGroup mBackupTypeGroup;
    private CheckBox mWipeData;


    public static BackupDatabaseDialog newInstance() {
        Bundle args = new Bundle();
        BackupDatabaseDialog fragment = new BackupDatabaseDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.backup_database_dialog, container, false);
        mBackupTypeGroup = view.findViewById(R.id.backup_type_group);
        mPath = view.findViewById(R.id.backup_path);
        mFileName = view.findViewById(R.id.backup_file_name);
        mCancel = view.findViewById(R.id.backup_cancel);
        mOk = view.findViewById(R.id.backup_ok);
        mWipeData = view.findViewById(R.id.backup_wipe_data);
        initObject();
        return view;
    }

    private void initObject() {
        mCancel.setOnClickListener(this);
        mOk.setOnClickListener(this);
        mBackupTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.share_type:
                    Toast.makeText(getContext(), "share", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.export_type:
                    Toast.makeText(getContext(), "export", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        mWipeData.setOnClickListener(this);
    }

    private void shareDbFile(String fileName) {
        File databaseFile = getContext().getDatabasePath(OrmHelper.DATABASE_NAME);
        try {
            File dstDatabase = new File(Global.INSTANCE.getCacheDir().getPath() + "/" + fileName + ".db");
            UmbrellaUtil.copyFile(databaseFile, dstDatabase);
            Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, dstDatabase);
            Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                    .setType(getActivity().getContentResolver().getType(uri))
                    .setStream(uri)
                    .getIntent();
            //Provide read access
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            PackageManager pm = getActivity().getPackageManager();

            if (shareIntent.resolveActivity(pm) != null) {
                getActivity().startActivityForResult
                        (Intent.createChooser(shareIntent, getString(R.string.share_form)),
                                EXPORTED_DB_REQUEST);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backup_ok:

                break;
            case R.id.backup_cancel:

                dismiss();
                break;
            case R.id.backup_wipe_data:

                break;
        }
    }
}
