package buffalo.suny.software.atmedicine.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.database.DatabaseConnection;
import buffalo.suny.software.atmedicine.utility.Globals;
import buffalo.suny.software.atmedicine.utility.Utility;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private Typeface customTypeface, customBold;

    private TextInputLayout inputLayoutEmail;
    private EditText txtInputEmail;
    private Button btnResetPassword;

    private String userEmailId = "";

    private DatabaseConnection dbConn;
    private Resources res;

    private ProgressDialog ringProgressDialog;
    private TextView txtLogoForgotPasswordTitle, txtLogoForgotPasswordSubtitle, linkCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        dbConn = DatabaseConnection.getInstance();
        res = getResources();

        customTypeface = Typeface.createFromAsset(getAssets(), Globals.FONT_ROBOTO_THIN);
        customBold = Typeface.create(customTypeface, Typeface.BOLD);

        txtLogoForgotPasswordTitle = (TextView) findViewById(R.id.txt_logo_forgot_password_title);
        txtLogoForgotPasswordTitle.setTypeface(customBold);

        txtLogoForgotPasswordSubtitle = (TextView) findViewById(R.id.txt_logo_forgot_password_subtitle);
        txtLogoForgotPasswordSubtitle.setTypeface(customTypeface);

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
        txtInputEmail.addTextChangedListener(new ResetPasswordTextWatcher(txtInputEmail));

        btnResetPassword.setOnClickListener(this);
        linkCreateAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset_password:
                submitForgotPasswordForm();
                break;

            case R.id.link_create_account:
                Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(intent);
                finish();
                break;

        }
    }

    private void submitForgotPasswordForm() {
        if (!validateEmail(userEmailId)) {
            return;
        }

        if (!Utility.isNetworkAvailable(this)) {
            errorDialog(res.getString(R.string.connection_error_title), res.getString(R.string.connection_error_msg));
            return;
        }

        hideSoftKeyboard();
        runResetPasswordTask();
    }

    private Boolean success = false;

    private void runResetPasswordTask() {
        AsyncTask<String, Boolean, Boolean> mResetPasswordTask;
        mResetPasswordTask = new AsyncTask<String, Boolean, Boolean>() {
            @Override
            protected void onPreExecute() {
                launchRingDialog("Sending new password...");
            }

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    success = dbConn.resetPassword(userEmailId);
                } catch (Exception e) {
                    success = false;
                    e.printStackTrace();
                }

                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                dismissProgressDialog();
                if (success) {
                    showToast(res.getString(R.string.reset_password_success), Toast.LENGTH_SHORT);
                    dismissProgressDialog();
                    finish();
                }
            }

            @Override
            protected void onCancelled() {
                dismissProgressDialog();

            }
        };
        mResetPasswordTask.execute();
    }

    private void showToast(String msg, int time) {
        Toast.makeText(this, msg, time).show();
    }

    private void errorDialog(String title, String msg) {
        final Dialog errorDialog = new Dialog(this);
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        errorDialog.setContentView(R.layout.dialog_error);
        errorDialog.setCancelable(false);
        errorDialog.show();

        Button btnOK = (Button) errorDialog.findViewById(R.id.btn_close);
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

    private boolean validateEmail(String userEmailId) {
        if (userEmailId.isEmpty() || !Utility.isValidEmail(userEmailId)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(txtInputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class ResetPasswordTextWatcher implements TextWatcher {
        private View view;

        private ResetPasswordTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.txt_input_email:
                    userEmailId = txtInputEmail.getText().toString().trim();
                    validateEmail(userEmailId);
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


    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
