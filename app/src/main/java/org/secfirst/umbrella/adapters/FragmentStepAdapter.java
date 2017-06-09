package org.secfirst.umbrella.adapters;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

import org.secfirst.umbrella.fragments.StepFragment;
import org.secfirst.umbrella.models.Form;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.util.ArrayList;

public class FragmentStepAdapter extends AbstractFragmentStepAdapter {
    private Form form;
    private Long sessionId;

    public FragmentStepAdapter(@NonNull FragmentManager fm, @NonNull Context context, Form form, Long sessionId) {
        super(fm, context);
        this.form = form;
        this.sessionId = sessionId;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        String title = "";
        if (form.getScreens()!=null) {
            title = UmbrellaUtil.ellipsis(new ArrayList<>(form.getScreens()).get(position).getTitle(), 50);

        }
        return new StepViewModel.Builder(context).setTitle(title).create();
    }

    @Override
    public Step createStep(int position) {
        return StepFragment.newInstance(position, sessionId);
    }

    @Override
    public int getCount() {
        return form.getScreens()!=null ? form.getScreens().size() : 0;
    }
}