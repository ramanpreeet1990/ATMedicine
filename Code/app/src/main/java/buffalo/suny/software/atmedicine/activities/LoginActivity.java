package buffalo.suny.software.atmedicine.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthUtil;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.database.DatabaseConnection;
import buffalo.suny.software.atmedicine.model.User;
import buffalo.suny.software.atmedicine.utility.Globals;
import buffalo.suny.software.atmedicine.utility.Utility;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    private TextView linkForgotPassword, linkCreateAccount;
    private EditText txtInputEmail, txtInputPassword;
    private Button btnLogin;

    private String userEmailId, userPassword;
    private int userId;

    private User user;
    private DatabaseConnection dbConn;
    private Resources res;

    private ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = User.getCurrentUser();
        if (checkUserSession()) {
            user.setEmailId(userEmailId);
            user.setUserId(userId);
            startApp(userEmailId);
        }

        res = getResources();

        Typeface customTypeface = Typeface.createFromAsset(getAssets(), Globals.FONT_ROBOTO_THIN);
        Typeface customBold = Typeface.create(customTypeface, Typeface.BOLD);

        TextView txtLogoLoginTitle = (TextView) findViewById(R.id.txt_logo_login_title);
        txtLogoLoginTitle.setTypeface(customBold);

        TextView txtLogoLoginSubtitle = (TextView) findViewById(R.id.txt_logo_login_subtitle);
        txtLogoLoginSubtitle.setTypeface(customBold);

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

        btnLogin.setOnClickListener(this);
        linkForgotPassword.setOnClickListener(this);
        linkCreateAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.btn_login:
                submitLoginForm();
                break;

            case R.id.link_forgot_password:
                intent = new Intent(this, ForgotPasswordActivity.class);
                startActivity(intent);
                break;

            case R.id.link_create_account:
                intent = new Intent(this, CreateAccountActivity.class);
                startActivity(intent);
                break;
        }
    }

    private boolean checkUserSession() {
        SharedPreferences prefs = getSharedPreferences(Globals.ATM_PREF, Context.MODE_PRIVATE);
        userEmailId = prefs.getString(Globals.CURRENT_USER_EMAIL_ID, "user@gmail.com");
        userId = prefs.getInt(Globals.CURRENT_USER_ID, -1);
        return prefs.getBoolean(Globals.IS_LOGGED_IN, false);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    private void submitLoginForm() {
        userEmailId = txtInputEmail.getText().toString().trim();
        userPassword = txtInputPassword.getText().toString().trim();

        if (!validateEmail(userEmailId) || !validatePassword(userPassword)) {
            return;
        }

        if (!Utility.isNetworkAvailable(this)) {
            showErrorDialog(res.getString(R.string.connection_error_title), res.getString(R.string.connection_error_msg));
            return;
        }

        hideSoftKeyboard();
        runLoginTask();
    }

    private int uniqueUserId = -1;

    private void runLoginTask() {
        AsyncTask<String, Integer, Integer> mLoginTask;

        mLoginTask = new AsyncTask<String, Integer, Integer>() {
            @Override
            protected void onPreExecute() {
                dbConn = DatabaseConnection.getInstance();
                launchRingDialog("Logging in...");
            }

            @Override
            protected Integer doInBackground(String... params) {
                try {
                    uniqueUserId = dbConn.isRegisteredUser(userEmailId, Utility.generateMD5(userPassword));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return uniqueUserId;
            }

            @Override
            protected void onPostExecute(Integer uniqueUserId) {
                super.onPostExecute(uniqueUserId);
                dismissProgressDialog();

                Log.v(Globals.TAG, "uniqueUserId : " + uniqueUserId);

                if (uniqueUserId >= 0) {
                    user.setEmailId(userEmailId);
                    user.setUserId(uniqueUserId);
                    startApp(userEmailId);
                } else {
                    showErrorDialog(res.getString(R.string.login_error_title), res.getString(R.string.login_error_msg));
                    txtInputEmail.setText("");
                    txtInputPassword.setText("");
                }
            }

            @Override
            protected void onCancelled() {
                dismissProgressDialog();

            }
        };
        mLoginTask.execute();
    }

    private void showErrorDialog(String title, String msg) {
        final Dialog errorDialog = new Dialog(this);
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        errorDialog.setContentView(R.layout.dialog_error);
        errorDialog.setCancelable(false);
        errorDialog.show();

        Button btnOK = (Button) errorDialog.findViewById(R.id.btn_ok);
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
        if (null == userEmailId || userEmailId.isEmpty() || !Utility.isValidEmail(userEmailId)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            showSoftKeyboard(txtInputEmail);
            return false;
        } else {
            inputLayoutEmail.setError(null);
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword(String userPassword) {
        if (null == userPassword || userPassword.isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            showSoftKeyboard(txtInputPassword);
            return false;
        } else {
            inputLayoutPassword.setError(null);
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
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
                    userEmailId = txtInputEmail.getText().toString().trim();
                    validateEmail(userEmailId);
                    break;
                case R.id.txt_input_password:
                    userPassword = txtInputPassword.getText().toString().trim();
                    validatePassword(userPassword);
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

        return accountName[0];
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
