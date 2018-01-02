package org.secfirst.umbrella.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.ContentFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.secfirst.umbrella.CalcActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.util.UmbrellaUtil;

/**
 *
 */
public class InfoHandsShakeDialog extends DialogFragment implements View.OnClickListener {


    public static InfoHandsShakeDialog newInstance() {
        Bundle args = new Bundle();
        InfoHandsShakeDialog fragment = new InfoHandsShakeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_info_hands_shake_dialog, container, false);
        ContentFrameLayout mButtonOk = (ContentFrameLayout) view.findViewById(R.id.hands_shake_ok);
        ContentFrameLayout mButtonCancel = (ContentFrameLayout) view.findViewById(R.id.hands_shake_cancel);

        mButtonOk.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.hands_shake_cancel:
                dismiss();
                break;
            case R.id.hands_shake_ok:
                UmbrellaUtil.setMaskMode(getActivity(), true);
                Intent i = new Intent(getActivity(), CalcActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                dismiss();
                getActivity().finish();
                break;
        }
    }
}
