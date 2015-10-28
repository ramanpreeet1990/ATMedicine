package buffalo.suny.software.atmedicine.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.utility.Globals;
import buffalo.suny.software.atmedicine.utility.Utility;

public class LoginActivity extends AppCompatActivity {
    private EditText txtInputEmail, txtInputPassword;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    private Button btnLogin;
    private ProgressDialog ringProgressDialog;
    private Utility utility;
    private String currentUserEmailId;
    private Typeface customTypeface, customBold;
    private TextView txtLogoLogin1, txtLogoLogin2, linkForgotPassword, linkCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(checkUserSession())
            startApp(currentUserEmailId);

        utility = new Utility();

        customTypeface = Typeface.createFromAsset(getAssets(), Globals.FONT_ROBOTO_THIN);
        customBold = Typeface.create(customTypeface, Typeface.BOLD);

        txtLogoLogin1 = (TextView) findViewById(R.id.txt_logo_login_1);
        txtLogoLogin1.setTypeface(customBold);

        txtLogoLogin2 = (TextView) findViewById(R.id.txt_logo_login_2);
        txtLogoLogin2.setTypeface(customBold);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);

        txtInputEmail = (EditText) findViewById(R.id.txt_input_email);
        txtInputEmail.setTypeface(customTypeface);

        txtInputPassword = (EditText) findViewById(R.id.txt_input_password);
        txtInputPassword.setTypeface(customTypeface);

        btnLogin = (Button) findViewById(R.id.btn_login);
        linkForgotPassword = (TextView) findViewById(R.id.link_forgot_password);
        linkCreateAccount = (TextView) findViewById(R.id.link_create_account);

    }

    @Override
    protected void onResume() {
        super.onResume();

        txtInputEmail.setText(getAccount());
        txtInputEmail.addTextChangedListener(new LoginFormTextWatcher(txtInputEmail));
        txtInputPassword.addTextChangedListener(new LoginFormTextWatcher(txtInputPassword));

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitLoginForm();
            }
        });

        linkForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        linkCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkUserSession(){
        SharedPreferences prefs = getSharedPreferences(Globals.ATM_PREF, getApplicationContext().MODE_PRIVATE);
        currentUserEmailId = prefs.getString(Globals.CURRENT_USER_EMAIL_ID, "user@gmail.com");
        return prefs.getBoolean(Globals.IS_LOGGED_IN, false);
    }
    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    private void submitLoginForm() {
        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        if (utility.isNetworkAvailable(this)) {
            currentUserEmailId = ((EditText) findViewById(R.id.txt_input_email)).getText().toString();
            launchRingDialog("Logging in...");
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            dismissProgressDialog();
                            startApp(currentUserEmailId);
                        }
                    }, 3000);
        } else {
            showErrorDialog("Connection Error", "Can't connect to the network. Please check your internet connection");
        }
    }

    private void showToast(String msg, int time) {
        Toast.makeText(this, msg, time).show();
    }

    private void showErrorDialog(String title, String msg) {
        final Dialog errorDialog = new Dialog(this);
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        errorDialog.setContentView(R.layout.error_dialog);
        errorDialog.setCancelable(false);
        errorDialog.show();

        Button btnOK = (Button) errorDialog.findViewById(R.id.btnOK);
        TextView dialogTitle = (TextView) errorDialog.findViewById(R.id.dialog_toolbar_title);
        TextView dialogMessage = (TextView) errorDialog.findViewById(R.id.dialog_msg);

        dialogTitle.setText(title);
        dialogMessage.setText(msg);

        btnOK.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         errorDialog.dismiss();
                                     }

                                 }

        );
    }

    private boolean validateEmail() {
        String email = txtInputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(txtInputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (txtInputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(txtInputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class LoginFormTextWatcher implements TextWatcher {
        private View view;

        private LoginFormTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.txt_input_email:
                    validateEmail();
                    break;
                case R.id.txt_input_password:
                    validatePassword();
                    break;
            }
        }

    }

    public String getAccount() {
        Account[] accounts = AccountManager.get(this)
                .getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String[] accountName = new String[accounts.length];
        for (int i = 0; i < accountName.length; i++) {
            accountName[i] = accounts[i].name;
        }

        if (accountName != null) {
            return accountName[0];
        } else
            return "";
    }

    private void launchRingDialog(String displayMessage) {
        ringProgressDialog = ProgressDialog.show(this, "Please wait ...", displayMessage, true);
        ringProgressDialog.setIndeterminate(true);
        ringProgressDialog.setCancelable(false);
    }

    public void dismissProgressDialog() {
        if (ringProgressDialog != null && ringProgressDialog.isShowing()) {
            ringProgressDialog.dismiss();
        }
    }

    public void startApp(String currentUserEmailId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Globals.CURRENT_USER_EMAIL_ID, currentUserEmailId);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //prevent window leak exception
        dismissProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
