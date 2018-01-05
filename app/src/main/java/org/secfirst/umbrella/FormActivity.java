package org.secfirst.umbrella;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import org.secfirst.umbrella.adapters.FragmentStepAdapter;
import org.secfirst.umbrella.models.Form;
import org.secfirst.umbrella.util.OnNavigationBarListener;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.sql.SQLException;

public class FormActivity extends BaseActivity implements StepperLayout.StepperListener,
        OnNavigationBarListener {

    private static final String CURRENT_STEP_POSITION_KEY = "position";
    public static final int REQUEST_ID = 4414;

    protected StepperLayout mStepperLayout;
    protected  FragmentStepAdapter adapter;
    private Form f;
    private Long sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UmbrellaUtil.setStatusBarColor(this, getResources().getColor(R.color.umbrella_purple_dark));

        int formId = getIntent().getExtras().getInt("form_id", -1);
        if (formId < 0) {
            Toast.makeText(this, "Not a valid form", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        try {
            f = global.getDaoForm().queryForId(String.valueOf(formId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (f==null) {
            Toast.makeText(this, "Not a valid form", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        sessionId = getIntent().getExtras().getLong("session_id", -1);
        if (sessionId<0) {
            sessionId = System.currentTimeMillis()/1000;
        }
        setTitle(f.getTitle());

        int startingStepPosition = savedInstanceState != null ? savedInstanceState.getInt(CURRENT_STEP_POSITION_KEY) : 0;
        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);

        adapter = new FragmentStepAdapter(getSupportFragmentManager(), this, f, sessionId);
        mStepperLayout.setAdapter(adapter, startingStepPosition);

        mStepperLayout.setListener(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_form;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_STEP_POSITION_KEY, mStepperLayout.getCurrentStepPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        final int currentStepPosition = mStepperLayout.getCurrentStepPosition();
        if (currentStepPosition > 0) {
            mStepperLayout.onBackClicked();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompleted(View completeButton) {
        finish();
    }

    @Override
    public void onError(VerificationError verificationError) {}

    @Override
    public void onStepSelected(int newStepPosition) {}

    public Form getForm() {
        return f;
    }

    @Override
    public void onReturn() {
        finish();
    }

    @Override
    public void onChangeEndButtonsEnabled(boolean enabled) {
        mStepperLayout.setNextButtonVerificationFailed(!enabled);
        mStepperLayout.setCompleteButtonVerificationFailed(!enabled);
    }
}
