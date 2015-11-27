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
import buffalo.suny.software.atmedicine.model.User;
import buffalo.suny.software.atmedicine.utility.Globals;
import buffalo.suny.software.atmedicine.utility.Utility;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private Typeface customTypeface, customBold;

    private TextInputLayout inputLayoutEmail, inputLayoutPassword, inputLayoutConfirmPassword;
    private TextView txtLogoCreateAccountTitle, txtLogoCreateAccountSubtitle, linkLogin;
    private EditText txtInputEmail, txtInputPassword, txtInputConfirmPassword;
    private Button btnCreateAccount;

    private String newUserEmailId = "", newUserPassword = "", newUserConfirmPassword = "";

    private User user;
    private DatabaseConnection dbConn;
    private Resources res;

    private ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        dbConn = DatabaseConnection.getInstance();
        res = getResources();

        customTypeface = Typeface.createFromAsset(getAssets(), Globals.FONT_ROBOTO_THIN);
        customBold = Typeface.create(customTypeface, Typeface.BOLD);

        txtLogoCreateAccountTitle = (TextView) findViewById(R.id.txt_logo_create_account_title);
        txtLogoCreateAccountTitle.setTypeface(customBold);

        txtLogoCreateAccountSubtitle = (TextView) findViewById(R.id.txt_logo_create_account_subtitle);
        txtLogoCreateAccountSubtitle.setTypeface(customTypeface);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        txtInputEmail.setTypeface(customTypeface);

        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        txtInputPassword.setTypeface(customTypeface);

        inputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.input_layout_confirm_password);
        txtInputConfirmPassword.setTypeface(customTypeface);

        txtInputEmail = (EditText) findViewById(R.id.txt_input_email);
        txtInputPassword = (EditText) findViewById(R.id.txt_input_password);
        txtInputConfirmPassword = (EditText) findViewById(R.id.txt_input_confirm_password);

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
            showErrorDialog(res.getString(R.string.password_unmatch_title), res.getString(R.string.password_criteria_unmatch_subtitle));
            return;
        }

        hideSoftKeyboard();
        runCreateAccountTask();
    }

    private Boolean success = false;

    private void runCreateAccountTask() {
        AsyncTask<String, Boolean, Boolean> mCreateAccountTask;
        mCreateAccountTask = new AsyncTask<String, Boolean, Boolean>() {
            @Override
            protected void onPreExecute() {
                launchRingDialog("Creating Account...");
            }

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    success = dbConn.registerUser(newUserEmailId, newUserPassword);
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
                    showToast(res.getString(R.string.success_registration), Toast.LENGTH_SHORT);
                    user = User.getCurrentUser();
                    user.setEmailId(newUserEmailId);
                    startApp();
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

    private boolean validateEmail(String newUserEmailId) {
        if (newUserEmailId.isEmpty() || !Utility.isValidEmail(newUserEmailId)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(txtInputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword(String newUserPassword) {
        if (newUserPassword.isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(txtInputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateConfirmPassword(String newUserConfirmPassword) {
        if (newUserConfirmPassword.isEmpty()) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_password));
            requestFocus(txtInputConfirmPassword);
            return false;
        } else {
            inputLayoutConfirmPassword.setErrorEnabled(false);
        }

        return true;
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
