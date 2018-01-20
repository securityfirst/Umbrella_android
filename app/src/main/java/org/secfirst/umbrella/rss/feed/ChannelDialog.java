package org.secfirst.umbrella.rss.feed;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.secfirst.umbrella.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChannelDialog extends DialogFragment implements View.OnClickListener {

    private AppCompatEditText mUrl;
    private OnChannelDialog onChannelDialog;

    public static ChannelDialog newInstance(OnChannelDialog onChannelDialog) {
        Bundle args = new Bundle();
        ChannelDialog fragment = new ChannelDialog();
        fragment.setArguments(args);
        fragment.onChannelDialog = onChannelDialog;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rssdialog, container, false);
        AppCompatTextView mOkButton = view.findViewById(R.id.channel_dialog_ok);
        AppCompatTextView mCancelButton = view.findViewById(R.id.channel_dialog_cancel);
        mOkButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mUrl = view.findViewById(R.id.channel_dialog_source);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.channel_dialog_ok:
                onChannelDialog.getCustomChannel(mUrl.getText().toString());
                dismiss();
                break;
            case R.id.channel_dialog_cancel:
                dismiss();
                break;
        }
    }

    interface OnChannelDialog {
        void getCustomChannel(String url);
    }
}
