package org.secfirst.umbrella;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.secfirst.umbrella.util.UmbrellaUtil;

public class LoginActivity extends BaseActivity {

    private EditText inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if (!global.hasPasswordSet()) {
            goToMain();
        }

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    tryLogin();
                }
                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryLogin();
            }
        });
        LinearLayout startLayout = (LinearLayout) findViewById(R.id.start_layout);
        UmbrellaUtil.setupUItoHideKeyboard(startLayout, LoginActivity.this);

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_start;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemResetPw = menu.findItem(R.id.action_reset_password);
        MenuItem itemSetPw = menu.findItem(R.id.action_set_password);
        itemSetPw.setVisible(!global.hasPasswordSet());
        itemResetPw.setVisible(global.hasPasswordSet());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reset_password) {
            global.resetPassword(this);
            return true;
        } else if (id == R.id.action_set_password) {
            global.setPassword(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void tryLogin() {
        boolean isPasswordOk = global.checkPassword(inputPassword.getText().toString().trim());
        if (isPasswordOk) {
            goToMain();
        } else {
            inputPassword.setText("");
            Toast.makeText(this, getString(R.string.incorrect_password), Toast.LENGTH_SHORT).show();
        }
    }

    private void goToMain() {
        Intent toMain = new Intent(LoginActivity.this, (global.getTermsAccepted() ? MainActivity.class : TourActivity.class));
        if (global.getTermsAccepted()) {
            Log.i("to", "main");
            toMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            Log.i("to", "tour");
        }
        startActivity(toMain);
    }
}
