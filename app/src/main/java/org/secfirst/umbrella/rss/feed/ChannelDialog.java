package org.secfirst.umbrella.rss.feed;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.secfirst.umbrella.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChannelDialog extends DialogFragment {


    public static ChannelDialog newInstance() {
        Bundle args = new Bundle();
        ChannelDialog fragment = new ChannelDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rssdialog, container, false);
    }

}
