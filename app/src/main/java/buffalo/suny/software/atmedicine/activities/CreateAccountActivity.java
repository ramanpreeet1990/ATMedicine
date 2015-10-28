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

public class CreateAccountActivity extends AppCompatActivity {
    private EditText txtInputEmail, txtInputPassword, txtInputConfirmPassword;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword, inputLayoutConfirmPassword;
    private Button btnCreateAccount;
    private ProgressDialog ringProgressDialog;
    private Utility utility;
    private Typeface customTypeface, customBold;
    private TextView txtLogoCreateAccount1, txtLogoCreateAccount2, linkLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        utility = new Utility();

        customTypeface = Typeface.createFromAsset(getAssets(), Globals.FONT_ROBOTO_THIN);
        customBold = Typeface.create(customTypeface, Typeface.BOLD);

        txtLogoCreateAccount1 = (TextView) findViewById(R.id.txt_logo_create_account_1);
        txtLogoCreateAccount1.setTypeface(customBold);

        txtLogoCreateAccount2 = (TextView) findViewById(R.id.txt_logo_create_account_2);
        txtLogoCreateAccount2.setTypeface(customTypeface);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.input_layout_confirm_password);

        txtInputEmail = (EditText) findViewById(R.id.txt_input_email);
        txtInputEmail.setTypeface(customTypeface);

        txtInputPassword = (EditText) findViewById(R.id.txt_input_password);
        txtInputPassword.setTypeface(customTypeface);

        txtInputConfirmPassword = (EditText) findViewById(R.id.txt_input_confirm_password);
        txtInputConfirmPassword.setTypeface(customTypeface);

        btnCreateAccount = (Button) findViewById(R.id.btn_login);
        linkLogin = (TextView) findViewById(R.id.link_login);
    }

    @Override
    protected void onResume() {
        super.onResume();

        txtInputEmail.setText(getAccount());
        txtInputEmail.addTextChangedListener(new LoginFormTextWatcher(txtInputEmail));
        txtInputPassword.addTextChangedListener(new LoginFormTextWatcher(txtInputPassword));

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitCreateAccountForm();
            }
        });

        linkLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void submitCreateAccountForm() {
        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        if (!validateConfirmPassword()) {
            return;
        }

        if (!utility.isNetworkAvailable(this)) {
            errorDialog("Connection Error", "Can't connect to the network. Please check your internet connection");
            return;
        }

        if (!passwordCriteriaMatches()) {
            errorDialog("Password doesn't match criteria", "Your entered password must be between 8 to 15 characters long containing both Alphabets and Numbers");
            return;
        }

        if (!passwordMatches()) {
            errorDialog("Password doesn't match", "Your entered password must match with each other. Please enter again");
            return;
        }

        launchRingDialog("Creating Account...");
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        showToast("You have been succesfully registered with ATM", Toast.LENGTH_SHORT);
                        dismissProgressDialog();
                        startApp();
                    }
                }, 3000);

    }

    private void showToast(String msg, int time) {
        Toast.makeText(this, msg, time).show();
    }

    private void errorDialog(String title, String msg) {
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

    private boolean passwordMatches() {
        String pwd1 = txtInputPassword.getText().toString().trim();
        String pwd2 = txtInputConfirmPassword.getText().toString().trim();

        return pwd1.equals(pwd2);
    }

    private boolean passwordCriteriaMatches() {
        String pwd1 = txtInputPassword.getText().toString().trim();

        String pattern = "^[a-zA-Z0-9]*$";
        if (pwd1.matches(pattern) && pwd1.length() > 7 && pwd1.length() < 16) {
            return true;
        }

        return false;
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

    private boolean validateConfirmPassword() {
        if (txtInputConfirmPassword.getText().toString().trim().isEmpty()) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_password));
            requestFocus(txtInputConfirmPassword);
            return false;
        } else {
            inputLayoutConfirmPassword.setErrorEnabled(false);
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

    public void startApp() {
        String userEmailId = ((EditText) findViewById(R.id.txt_input_email)).getText().toString();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Globals.CURRENT_USER_EMAIL_ID, userEmailId);
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
