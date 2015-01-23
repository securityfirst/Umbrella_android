package org.secfirst.umbrella;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
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

public class StartActivity extends BaseActivity {

    private EditText inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        UmbrellaUtil.migrateDataOnStartup(this);
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
        UmbrellaUtil.setupUItoHideKeyboard(startLayout, StartActivity.this);

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
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StartActivity.this);
            alertDialogBuilder.setTitle("Confirm reset password");
            alertDialogBuilder.setMessage("Are you sure you want to reset your password? This also means losing any data you might have entered so far");
            alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    global.savePassword("");
                    Toast.makeText(StartActivity.this, "Password reset and all data removed.", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        } else if (id == R.id.action_set_password) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Set your password");
            alert.setMessage("Your password must be at least 4 characters long and must contain at least one digit and one capital letter");
            final EditText pwInput = new EditText(this);
            alert.setView(pwInput);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String pw = pwInput.getText().toString();
                    String checkError = UmbrellaUtil.checkPasswordStrength(pw);
                    if (checkError.equals("")) {
                        global.savePassword(pw);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(StartActivity.this, "You must choose a stronger password. "+checkError,Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });
            alert.show();
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
        Intent toMain = new Intent(StartActivity.this, (global.getTermsAccepted() ? MainActivity.class : TourActivity.class));
        if (global.getTermsAccepted()) {
            toMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        }
        startActivity(toMain);
    }
}
