package buffalo.suny.software.atmedicine.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText txtInputEmail;
    private TextInputLayout inputLayoutEmail;
    private Button btnResetPassword;
    private Utility utility;
    private ProgressDialog ringProgressDialog;
    private Typeface customTypeface, customBold;
    private TextView txtLogoForgotPassword1, txtLogoForgotPassword2, linkCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        utility = new Utility();

        customTypeface = Typeface.createFromAsset(getAssets(), Globals.FONT_ROBOTO_THIN);
        customBold = Typeface.create(customTypeface, Typeface.BOLD);

        txtLogoForgotPassword1 = (TextView) findViewById(R.id.txt_logo_forgot_password_1);
        txtLogoForgotPassword1.setTypeface(customBold);

        txtLogoForgotPassword2 = (TextView) findViewById(R.id.txt_logo_forgot_password_2);
        txtLogoForgotPassword2.setTypeface(customTypeface);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);

        txtInputEmail = (EditText) findViewById(R.id.txt_input_email);
        txtInputEmail.setTypeface(customTypeface);

        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        linkCreateAccount = (TextView) findViewById(R.id.link_create_account);
    }

    @Override
    protected void onResume() {
        super.onResume();

        txtInputEmail.setText(getAccount());
        txtInputEmail.addTextChangedListener(new LoginFormTextWatcher(txtInputEmail));

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForgotPasswordForm();
            }
        });

        linkCreateAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void submitForgotPasswordForm() {
        if (!validateEmail()) {
            return;
        }

        if (utility.isNetworkAvailable(this)) {
            launchRingDialog("Sending new password...");

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            showToast("A new password has been sent to your registered email id",Toast.LENGTH_SHORT);
                            dismissProgressDialog();
                            finish();
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
