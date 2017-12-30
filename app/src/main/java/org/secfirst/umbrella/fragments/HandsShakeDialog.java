package org.secfirst.umbrella.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.ContentFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.secfirst.umbrella.R;

/**
 * Created by dougl on 29/12/2017.
 */

public class HandsShakeDialog extends DialogFragment implements View.OnClickListener {


    private ContentFrameLayout mNextButton;


    public static HandsShakeDialog newInstance() {

        Bundle args = new Bundle();

        HandsShakeDialog fragment = new HandsShakeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hands_shake_enter_dialog, container, false);

        mNextButton = (ContentFrameLayout) view.findViewById(R.id.hands_shake_next_button);
        mNextButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

    }
}
