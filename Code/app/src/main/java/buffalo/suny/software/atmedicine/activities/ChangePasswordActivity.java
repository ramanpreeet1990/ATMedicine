package buffalo.suny.software.atmedicine.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.database.DatabaseConnection;
import buffalo.suny.software.atmedicine.model.User;
import buffalo.suny.software.atmedicine.utility.Globals;
import buffalo.suny.software.atmedicine.utility.Utility;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout inputLayoutCurrentPassword, inputLayoutNewPassword, inputLayoutConfirmPassword;
    private EditText txtInputCurrentPassword, txtInputNewPassword, txtInputConfirmPassword;
    private Button btnUpdatePassword;

    private String currentPassword, newPassword, confirmPassword;

    private User user;
    private DatabaseConnection dbConn;
    private Resources res;

    private ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        dbConn = DatabaseConnection.getInstance();
        user = User.getCurrentUser();
        res = getResources();

        Typeface customTypeface = Typeface.createFromAsset(getAssets(), Globals.FONT_ROBOTO_THIN);
        Typeface customBold = Typeface.create(customTypeface, Typeface.BOLD);

        TextView txtLogoChangePasswordTitle = (TextView) findViewById(R.id.txt_logo_change_password_title);
        txtLogoChangePasswordTitle.setTypeface(customBold);

        inputLayoutCurrentPassword = (TextInputLayout) findViewById(R.id.input_layout_current_password);
        txtInputCurrentPassword = (EditText) findViewById(R.id.txt_input_current_password);
        txtInputCurrentPassword.setTypeface(customBold);

        inputLayoutNewPassword = (TextInputLayout) findViewById(R.id.input_layout_new_password);
        txtInputNewPassword = (EditText) findViewById(R.id.txt_input_new_password);
        txtInputNewPassword.setTypeface(customBold);

        inputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.input_layout_confirm_password);
        txtInputConfirmPassword = (EditText) findViewById(R.id.txt_input_confirm_password);
        txtInputConfirmPassword.setTypeface(customBold);

        btnUpdatePassword = (Button) findViewById(R.id.btn_update);
    }

    @Override
    protected void onResume() {
        super.onResume();

        txtInputCurrentPassword.addTextChangedListener(new UpdatePasswordTextWatcher(txtInputCurrentPassword));
        txtInputNewPassword.addTextChangedListener(new UpdatePasswordTextWatcher(txtInputNewPassword));
        txtInputConfirmPassword.addTextChangedListener(new UpdatePasswordTextWatcher(txtInputConfirmPassword));

        btnUpdatePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                submitChangePasswordForm();
                break;
        }
    }

    private void submitChangePasswordForm() {
        currentPassword = txtInputCurrentPassword.getText().toString().trim();
        newPassword = txtInputNewPassword.getText().toString().trim();
        confirmPassword = txtInputConfirmPassword.getText().toString().trim();

        if (!validateCurrentPassword(currentPassword) || !validateNewPassword(newPassword)
                || !validateConfirmPassword(confirmPassword)) {
            return;
        }

        if (!Utility.isNetworkAvailable(this)) {
            showDialog(res.getString(R.string.connection_error_title), res.getString(R.string.connection_error_msg), false);
            return;
        }

        if (!Utility.passwordCriteriaMatches(newPassword)) {
            showDialog(res.getString(R.string.password_criteria_unmatch_title), res.getString(R.string.password_criteria_unmatch_subtitle), false);
            return;
        }

        if (!Utility.passwordMatches(newPassword, confirmPassword)) {
            showDialog(res.getString(R.string.password_unmatch_title), res.getString(R.string.password_unmatch_subtitle), false);
            return;
        }

        hideSoftKeyboard();
        runUpdatePasswordTask();
    }

    private Boolean success = false;

    private void runUpdatePasswordTask() {
        AsyncTask<String, Boolean, Boolean> mUpdatePasswordTask;
        mUpdatePasswordTask = new AsyncTask<String, Boolean, Boolean>() {
            @Override
            protected void onPreExecute() {
                launchRingDialog("Updating Password...");
            }

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    success = dbConn.updatePassword(user.getUserId(), Utility.generateMD5(currentPassword));
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
                    showDialog(res.getString(R.string.updated), res.getString(R.string.password_updated), true);
                } else {
                    showDialog(res.getString(R.string.wrong_password), res.getString(R.string.wrong_password_msg), false);
                    txtInputCurrentPassword.setText("");
                }
            }

            @Override
            protected void onCancelled() {
                dismissProgressDialog();

            }
        };
        mUpdatePasswordTask.execute();
    }

    private void showDialog(String title, String msg, final boolean success) {
        final Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mDialog.setContentView(R.layout.dialog_error);
        mDialog.setCancelable(false);
        mDialog.show();

        Button btnOk = (Button) mDialog.findViewById(R.id.btn_ok);
        TextView dialogTitle = (TextView) mDialog.findViewById(R.id.dialog_toolbar_title);
        TextView dialogMessage = (TextView) mDialog.findViewById(R.id.dialog_msg);

        dialogTitle.setText(title);
        dialogMessage.setText(msg);

        btnOk.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         mDialog.dismiss();

                                         if (success)
                                             finish();
                                     }

                                 }

        );
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

    private class UpdatePasswordTextWatcher implements TextWatcher {
        private View view;

        private UpdatePasswordTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.txt_input_current_password:
                    currentPassword = txtInputCurrentPassword.getText().toString().trim();
                    validateCurrentPassword(currentPassword);
                    break;

                case R.id.txt_input_new_password:
                    newPassword = txtInputNewPassword.getText().toString().trim();
                    validateNewPassword(newPassword);
                    break;

                case R.id.txt_input_confirm_password:
                    confirmPassword = txtInputConfirmPassword.getText().toString().trim();
                    validateConfirmPassword(confirmPassword);
                    break;
            }
        }

    }

    private boolean validateCurrentPassword(String currentPassword) {
        if (null == currentPassword || currentPassword.isEmpty()) {
            inputLayoutCurrentPassword.setError(getString(R.string.err_msg_password));
            showSoftKeyboard(txtInputCurrentPassword);
            return false;
        } else {
            inputLayoutCurrentPassword.setError(null);
            inputLayoutCurrentPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateNewPassword(String newPassword) {
        if (null == newPassword || newPassword.isEmpty()) {
            inputLayoutNewPassword.setError(getString(R.string.err_msg_password));
            showSoftKeyboard(txtInputNewPassword);
            return false;
        } else {
            inputLayoutNewPassword.setError(null);
            inputLayoutNewPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateConfirmPassword(String confirmPassword) {
        if (null == confirmPassword || confirmPassword.isEmpty()) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_password));
            showSoftKeyboard(txtInputConfirmPassword);
            return false;
        } else {
            inputLayoutConfirmPassword.setError(null);
            inputLayoutConfirmPassword.setErrorEnabled(false);
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

    @Override
    protected void onPause() {
        super.onPause();

        //prevent window leak exception
        dismissProgressDialog();
    }
}
