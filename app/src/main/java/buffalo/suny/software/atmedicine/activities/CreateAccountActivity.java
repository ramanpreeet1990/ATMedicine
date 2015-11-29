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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;

import java.security.MessageDigest;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.database.DatabaseConnection;
import buffalo.suny.software.atmedicine.model.User;
import buffalo.suny.software.atmedicine.utility.Globals;
import buffalo.suny.software.atmedicine.utility.Utility;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private Typeface customTypeface, customBold;

    private TextInputLayout inputLayoutEmail, inputLayoutPassword, inputLayoutConfirmPassword;
    private TextView txtLogoCreateAccountTitle, txtLogoCreateAccountSubtitle, linkLogin;
    private EditText txtInputEmail, txtInputPassword, txtInputConfirmPassword;
    private Button btnCreateAccount;

    private String newUserEmailId, newUserPassword, newUserConfirmPassword;

    private User user;
    private DatabaseConnection dbConn;
    private Resources res;

    private ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        res = getResources();

        customTypeface = Typeface.createFromAsset(getAssets(), Globals.FONT_ROBOTO_THIN);
        customBold = Typeface.create(customTypeface, Typeface.BOLD);

        txtLogoCreateAccountTitle = (TextView) findViewById(R.id.txt_logo_create_account_title);
        txtLogoCreateAccountTitle.setTypeface(customBold);

        txtLogoCreateAccountSubtitle = (TextView) findViewById(R.id.txt_logo_create_account_subtitle);
        txtLogoCreateAccountSubtitle.setTypeface(customTypeface);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        txtInputEmail = (EditText) findViewById(R.id.txt_input_email);
        txtInputEmail.setTypeface(customTypeface);

        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        txtInputPassword = (EditText) findViewById(R.id.txt_input_password);
        txtInputPassword.setTypeface(customTypeface);

        inputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.input_layout_confirm_password);
        txtInputConfirmPassword = (EditText) findViewById(R.id.txt_input_confirm_password);
        txtInputConfirmPassword.setTypeface(customTypeface);

        btnCreateAccount = (Button) findViewById(R.id.btn_create_account);
        linkLogin = (TextView) findViewById(R.id.link_login);
    }

    @Override
    protected void onResume() {
        super.onResume();

        txtInputEmail.setText(getAccount());
        txtInputEmail.addTextChangedListener(new LoginFormTextWatcher(txtInputEmail));
        txtInputPassword.addTextChangedListener(new LoginFormTextWatcher(txtInputPassword));

        btnCreateAccount.setOnClickListener(this);
        linkLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create_account:
                submitCreateAccountForm();
                break;

            case R.id.link_login:
                finish();
                break;

        }
    }

    private void submitCreateAccountForm() {
        newUserEmailId = txtInputEmail.getText().toString().trim();
        newUserPassword = txtInputPassword.getText().toString().trim();
        newUserConfirmPassword = txtInputConfirmPassword.getText().toString().trim();

        if (!validateEmail(newUserEmailId)) {
            return;
        }

        if (!validatePassword(newUserPassword)) {
            return;
        }

        if (!validateConfirmPassword(newUserConfirmPassword)) {
            return;
        }

        if (!Utility.isNetworkAvailable(this)) {
            showErrorDialog(res.getString(R.string.connection_error_title), res.getString(R.string.connection_error_msg));
            return;
        }

        if (!Utility.passwordCriteriaMatches(newUserPassword)) {
            showErrorDialog(res.getString(R.string.password_criteria_unmatch_title), res.getString(R.string.password_criteria_unmatch_subtitle));
            return;
        }

        if (!Utility.passwordMatches(newUserPassword, newUserConfirmPassword)) {
            showErrorDialog(res.getString(R.string.password_unmatch_title), res.getString(R.string.password_unmatch_subtitle));
            return;
        }

        hideSoftKeyboard();
        runCreateAccountTask();
    }

    private int result = -1;
    private int uniqueUserId = -1;

    private void runCreateAccountTask() {
        AsyncTask<String, Integer, Integer> mCreateAccountTask;
        mCreateAccountTask = new AsyncTask<String, Integer, Integer>() {
            @Override
            protected void onPreExecute() {
                dbConn = DatabaseConnection.getInstance();
                launchRingDialog("Creating Account...");
            }

            @Override
            protected Integer doInBackground(String... params) {
                try {
                    result = dbConn.registerUser(newUserEmailId, Utility.generateMD5(newUserPassword));
                    uniqueUserId = dbConn.isRegisteredUser(newUserEmailId, Utility.generateMD5(newUserPassword));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return uniqueUserId;
            }

            @Override
            protected void onPostExecute(Integer uniqueUserId) {
                super.onPostExecute(uniqueUserId);
                dismissProgressDialog();

                Log.v(Globals.TAG, "result : " + result + ", uniqueUserId : " + uniqueUserId);
                if (result == 1 && uniqueUserId >= 0 ) {
                    showToast(res.getString(R.string.success_registration), Toast.LENGTH_SHORT);
                    user = User.getCurrentUser();
                    user.setEmailId(newUserEmailId);
                    user.setUserId(uniqueUserId);

                    startApp();
                } else if(result == 0) {
                    showToast(res.getString(R.string.user_already_registered), Toast.LENGTH_SHORT);
                } else {
                    showToast(res.getString(R.string.connection_error_msg), Toast.LENGTH_SHORT);
                }
            }

            @Override
            protected void onCancelled() {
                dismissProgressDialog();

            }
        };
        mCreateAccountTask.execute();
    }

    private void showToast(String msg, int time) {
        Toast.makeText(this, msg, time).show();
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

    private boolean validateEmail(String newUserEmailId) {
        if (null == newUserEmailId || newUserEmailId.isEmpty() || !Utility.isValidEmail(newUserEmailId)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            showSoftKeyboard(txtInputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword(String newUserPassword) {
        if (null == newUserPassword || newUserPassword.isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            showSoftKeyboard(txtInputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateConfirmPassword(String newUserConfirmPassword) {
        if (null == newUserConfirmPassword || newUserConfirmPassword.isEmpty()) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_password));
            showSoftKeyboard(txtInputConfirmPassword);
            return false;
        } else {
            inputLayoutConfirmPassword.setErrorEnabled(false);
        }

        return true;
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
                    newUserEmailId = txtInputEmail.getText().toString().trim();
                    validateEmail(newUserEmailId);
                    break;

                case R.id.txt_input_password:
                    newUserPassword = txtInputPassword.getText().toString().trim();
                    validatePassword(newUserPassword);
                    break;

                case R.id.txt_input_confirm_password:
                    newUserConfirmPassword = txtInputConfirmPassword.getText().toString().trim();
                    validateConfirmPassword(newUserConfirmPassword);
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
