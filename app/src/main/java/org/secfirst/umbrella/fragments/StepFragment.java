/*
Copyright 2016 StepStone Services

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package org.secfirst.umbrella.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.j256.ormlite.stmt.PreparedQuery;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.secfirst.umbrella.FormActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.Form;
import org.secfirst.umbrella.models.FormItem;
import org.secfirst.umbrella.models.FormOption;
import org.secfirst.umbrella.models.FormScreen;
import org.secfirst.umbrella.models.FormValue;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.OnNavigationBarListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

public class StepFragment extends Fragment implements Step {

    private static final String SCREEN_INDEX_KEY = "screen_index";
    private static final String SESSION_INDEX_KEY = "session_index";

    private int screenId;
    private Long sessionId;
    private Form form;

    ProgressBar progressBar;
    LinearLayout formHolder;

    @Nullable
    private OnNavigationBarListener onNavigationBarListener;

    public static StepFragment newInstance(int position, Long sessionId) {
        Bundle args = new Bundle();
        args.putInt(SCREEN_INDEX_KEY, position);
        args.putLong(SESSION_INDEX_KEY, sessionId);
        StepFragment fragment = new StepFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationBarListener) {
            onNavigationBarListener = (OnNavigationBarListener) context;
        }
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        form = ((FormActivity) getContext()).getForm();
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        screenId = getArguments().getInt(SCREEN_INDEX_KEY);
        sessionId = getArguments().getLong(SESSION_INDEX_KEY);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setMax(100);
        int progress = (int) Math.round((screenId+1) * 100.0 / (form.getScreens().size()));
        progressBar.setProgress(progress);

        formHolder = (LinearLayout) view.findViewById(R.id.form_holder);
        FormScreen fsc = new ArrayList<>(form.getScreens()).get(screenId);
        TextView holderTitle = new TextView(getContext());
        holderTitle.setText(fsc.getTitle());
        holderTitle.setTextSize(18);
        holderTitle.setGravity(Gravity.CENTER);
        holderTitle.setVisibility(View.GONE);
        formHolder.addView(holderTitle);
        View separator = new View(getContext());
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        viewParams.setMargins(0, 20, 0, 20);
        separator.setBackgroundColor(getResources().getColor(R.color.black));
        separator.setLayoutParams(viewParams);
        formHolder.addView(separator);
        for (int i = 0; i < fsc.getItems().size(); i++) {
            FormItem formItem = new ArrayList<>(fsc.getItems()).get(i);
            try {
                PreparedQuery<FormValue> queryBuilder = Global.INSTANCE.getDaoFormValue().queryBuilder().where().eq(FormValue.FIELD_SESSION, sessionId).and().eq(FormValue.FIELD_FORM_ITEM_ID, formItem.get_id()).prepare();
                formItem.setValues(Global.INSTANCE.getDaoFormValue().query(queryBuilder));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            switch (formItem.getType()) {
                case "label":
                    if (!formItem.getTitle().equals("")) {
                        TextView tvTextInput = new TextView(getContext());
                        tvTextInput.setText(Html.fromHtml(Jsoup.clean(formItem.getTitle(), Whitelist.simpleText())));
                        formHolder.addView(tvTextInput);
                    }
                    break;
                case "text_input":
                    if (!formItem.getTitle().equals("")) {
                        TextView tvTextInput = new TextView(getContext());
                        tvTextInput.setText(formItem.getTitle());
                        formHolder.addView(tvTextInput);
                    }

                    EditText etTextInput = new EditText(getContext());
                    etTextInput.setHint(formItem.getHint());
                    etTextInput.setTag(i);
                    if (formItem.getValues()!=null && formItem.getValues().size()>0) etTextInput.setText(new ArrayList<>(formItem.getValues()).get(0).getValue());

                    formHolder.addView(etTextInput);
                    break;
                case "text_area":
                    if (!formItem.getTitle().equals("")) {
                        TextView taTextInput = new TextView(getContext());
                        taTextInput.setText(formItem.getTitle());
                        formHolder.addView(taTextInput);
                    }

                    EditText taTextInput = new EditText(getContext());
                    taTextInput.setHint(formItem.getHint());
                    taTextInput.setTag(i);
                    taTextInput.setLines(4);
                    if (formItem.getValues()!=null && formItem.getValues().size()>0) taTextInput.setText(new ArrayList<>(formItem.getValues()).get(0).getValue());
                    formHolder.addView(taTextInput);
                    break;
                case "multiple_choice":
                    if (!formItem.getTitle().equals("")) {
                        TextView mcTextInput = new TextView(getContext());
                        mcTextInput.setText(formItem.getTitle());
                        formHolder.addView(mcTextInput);
                    }
                    List<String> mcValList = new ArrayList<>();
                    if (formItem.getValues()!=null && formItem.getValues().size()>0 && !formItem.getValues().get(0).getValue().equals("")) {
                        mcValList = new LinkedList<>(Arrays.asList(formItem.getValues().get(0).getValue().split(",")));
                    }
                    for (FormOption formOption : formItem.getOptions()) {
                        CheckBox mcCheck = new CheckBox(getContext());
                        mcCheck.setText(formOption.getOption());
                        if (mcValList.size()>0) {
                            mcCheck.setChecked(Boolean.parseBoolean(mcValList.get(0)));
                            mcValList.remove(0);
                        }
                        mcCheck.setTag(i);
                        formHolder.addView(mcCheck);
                    }
                    break;
                case "single_choice":
                    if (!formItem.getTitle().equals("")) {
                        TextView scTextInput = new TextView(getContext());
                        scTextInput.setText(formItem.getTitle());
                        formHolder.addView(scTextInput);
                    }
                    List<String> scValList = new ArrayList<>();
                    if (formItem.getValues()!=null && formItem.getValues().size()>0 && !formItem.getValues().get(0).getValue().equals("")) {
                        scValList = new LinkedList<>(Arrays.asList(formItem.getValues().get(0).getValue().split(",")));
                    }
                    for (FormOption formOption : formItem.getOptions()) {
                        RadioButton scCheck = new RadioButton(getContext());
                        scCheck.setTag(i);
                        scCheck.setText(formOption.getOption());
                        if (scValList.size()>0) {
                            scCheck.setChecked(Boolean.parseBoolean(scValList.get(0)));
                            scValList.remove(0);
                        }
                        formHolder.addView(scCheck);
                    }
                    break;
                case "toggle_button":
                    if (!formItem.getTitle().equals("")) {
                        TextView toTextInput = new TextView(getContext());
                        toTextInput.setText(formItem.getTitle());
                        formHolder.addView(toTextInput);
                    }
                    for (FormOption formOption : formItem.getOptions()) {
                        ToggleButton toCheck = new ToggleButton(getContext());
                        toCheck.setText(formOption.getOption());
                        toCheck.setTag(i);
                        if (formItem.getValues()!=null && formItem.getValues().size()>0 && !formItem.getValues().get(0).getValue().equals("")) {
                            toCheck.setChecked(Boolean.parseBoolean(formItem.getValues().get(0).getValue()));
                        }
                        formHolder.addView(toCheck);
                    }
                    break;
                default:
            }
        }
        return view;
    }


    @Override
    public void onStop() {
        FormScreen fsc = new ArrayList<>(form.getScreens()).get(screenId);
        for (int i = 0; i < fsc.getItems().size(); i++) {
            FormItem formItem = new ArrayList<>(fsc.getItems()).get(i);
            ArrayList<View> views = new ArrayList<>();
            final int childCount = formHolder.getChildCount();
            for (int j = 0; j < childCount; j++) {
                View v = formHolder.getChildAt(j);
                if (v.getTag()!=null && v.getTag().equals(i)) {
                    views.add(v);
                }
            }
            if (views.size()<1) {
                super.onStop();
                return;
            }
            switch (formItem.getType()) {
                case "text_input":
                case "text_area":
                    EditText editText = (EditText) views.get(0);
                    upsertFormValue(new FormValue(editText.getText().toString(), formItem, sessionId), formItem);
                    break;
                case "multiple_choice":
                    StringBuilder checkedMC = new StringBuilder();
                    for (View view : views) {
                        CheckBox checkBox = (CheckBox) view;
                        checkedMC.append(String.valueOf(checkBox.isChecked()));
                        checkedMC.append(",");
                    }
                    String mcVal = checkedMC.toString();
                    mcVal = mcVal.length() > 0 ? mcVal.substring(0, mcVal.length() - 1): "";
                    upsertFormValue(new FormValue(mcVal, formItem, sessionId), formItem);
                    break;
                case "single_choice":
                    StringBuilder checkedSC = new StringBuilder();
                    for (View view : views) {
                        RadioButton radio = (RadioButton) view;
                        checkedSC.append(String.valueOf(radio.isChecked()));
                        checkedSC.append(",");
                    }
                    String scVal = checkedSC.toString();
                    scVal = scVal.length() > 0 ? scVal.substring(0, scVal.length() - 1): "";
                    upsertFormValue(new FormValue(scVal, formItem, sessionId), formItem);
                    break;
                case "toggle_button":
                    ToggleButton toggleButton = (ToggleButton) views.get(0);
                    upsertFormValue(new FormValue(String.valueOf(toggleButton.isChecked()), formItem, sessionId), formItem);
                    break;
                default:
            }
        }
        super.onStop();
    }

    private void upsertFormValue(FormValue formValue, FormItem formItem) {
        try {
            PreparedQuery<FormValue> queryBuilder = Global.INSTANCE.getDaoFormValue().queryBuilder().where().eq(FormValue.FIELD_SESSION, formValue.getSessionID()).and().eq(FormValue.FIELD_FORM_ITEM_ID, formValue.getFormId()).prepare();
            FormValue exists = Global.INSTANCE.getDaoFormValue().queryForFirst(queryBuilder);
            if (exists!=null && exists.get_id()>0) {
                exists.setValue(formValue.getValue());
                Global.INSTANCE.getDaoFormValue().update(exists);
            } else {
                Global.INSTANCE.getDaoFormValue().create(formValue);
                formItem.addValue(formValue);
            }
        } catch (SQLException e) {
            Timber.e(e);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateNavigationBar();
    }

    @Override
    public VerificationError verifyStep() {
//        return new VerificationError("A message");
        return null;
    }

    @Override
    public void onSelected() {
        updateNavigationBar();
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        formHolder.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake_error));
    }

    private void updateNavigationBar() {
        if (onNavigationBarListener != null) {
            onNavigationBarListener.onChangeEndButtonsEnabled(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
