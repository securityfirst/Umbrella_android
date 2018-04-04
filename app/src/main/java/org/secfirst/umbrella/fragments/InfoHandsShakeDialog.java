package org.secfirst.umbrella.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.ContentFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.secfirst.umbrella.CalcActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.util.UmbrellaUtil;

import static android.content.Context.MODE_PRIVATE;

/**
 *
 */
public class InfoHandsShakeDialog extends DialogFragment implements View.OnClickListener {

    public static String MASK_APP_KEY = "mask_app_key";
    public static String MASK_APP_PREFS = "mask_prefs";

    public static InfoHandsShakeDialog newInstance() {
        Bundle args = new Bundle();
        InfoHandsShakeDialog fragment = new InfoHandsShakeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_info_hands_shake_dialog, container, false);
        ContentFrameLayout mButtonOk = view.findViewById(R.id.hands_shake_ok);
        ContentFrameLayout mButtonCancel = view.findViewById(R.id.hands_shake_cancel);

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
                storeMaskAppState(true, getContext());
                break;
        }
    }

    public static void storeMaskAppState(boolean hasMasked, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MASK_APP_PREFS, MODE_PRIVATE).edit();
        editor.putBoolean(MASK_APP_KEY, hasMasked);
        editor.apply();
    }

    public static boolean isMaskModeEnable(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(InfoHandsShakeDialog.MASK_APP_PREFS, MODE_PRIVATE);
        return prefs.getBoolean(InfoHandsShakeDialog.MASK_APP_KEY, false);
    }
}
