package org.secfirst.umbrella;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaUtil;


public class StartActivity extends ActionBarActivity {

    private EditText inputPassword;
    private Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        global = (Global) getApplicationContext();

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        Button btnSkip = (Button) findViewById(R.id.btn_skip);
        EditText inputPassword = (EditText) findViewById(R.id.input_password);
        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    tryLogin();
                }
                return false;
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (global.hasShownTour()) {

                } else {

                }
                startActivity(new Intent(StartActivity.this, (global.hasShownTour() ? MainActivity.class : TourActivity.class) ));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryLogin();
            }
        });
        LinearLayout startLayout = (LinearLayout) findViewById(R.id.start_layout);
        UmbrellaUtil.setupUItoHideKeyboard(startLayout, StartActivity.this);

//        UmbrellaUtil.migrateDataOnStartup(this);
    }

    private void tryLogin() {
        Log.i("to", "login");
    }
}
