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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.database.DatabaseConnection;
import buffalo.suny.software.atmedicine.model.User;
import buffalo.suny.software.atmedicine.utility.Globals;
import buffalo.suny.software.atmedicine.utility.Utility;


public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Typeface customTypeface, customBold;
    private TextView txtLogoUpdateProfile;
    private TextInputLayout inputLayoutLastName, inputLayoutFirstName, inputLayoutPhoneNumber, inputLayoutConfirmPassword;
    private EditText txtInputLastName, txtInputFirstName, txtInputPhoneNumber, txtInputConfirmPassword;
    private Button btnUpdateProfile;

    private ArrayAdapter<String> insuranceProviderAdapter;
    private Spinner spinnerInsuranceProviders;
    private DatePicker pickerDOB;
    private NumberPicker pickerHeightFeet, pickerHeightInch, pickerWeight;

    private List<String> insuranceProviders;
    private String insuranceProvider, lastName = "", firstName = "", phoneNumber = "", userPassword = "";
    private int year, month, day, heightFeet, heightInch, weightLbs;
    private boolean isDataChanged = false;

    private User user;
    private DatabaseConnection dbConn;
    private Resources res;

    private ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        user = User.getCurrentUser();
        dbConn = DatabaseConnection.getInstance();
        res = getResources();

        customTypeface = Typeface.createFromAsset(getAssets(), Globals.FONT_ROBOTO_THIN);
        customBold = Typeface.create(customTypeface, Typeface.BOLD);

        txtLogoUpdateProfile = (TextView) findViewById(R.id.txt_logo_update_profile_title);
        txtLogoUpdateProfile.setTypeface(customBold);

        inputLayoutLastName = (TextInputLayout) findViewById(R.id.input_layout_last_name);
        txtInputLastName = (EditText) findViewById(R.id.txt_input_last_name);
        txtInputLastName.setTypeface(customBold);

        inputLayoutFirstName = (TextInputLayout) findViewById(R.id.input_layout_first_name);
        txtInputFirstName = (EditText) findViewById(R.id.txt_input_first_name);
        txtInputFirstName.setTypeface(customBold);

        inputLayoutPhoneNumber = (TextInputLayout) findViewById(R.id.input_layout_phone_number);
        txtInputPhoneNumber = (EditText) findViewById(R.id.txt_input_phone_number);
        txtInputPhoneNumber.setTypeface(customBold);

        inputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.input_layout_confirm_password);
        txtInputConfirmPassword = (EditText) findViewById(R.id.txt_input_confirm_password);
        txtInputConfirmPassword.setTypeface(customBold);

        spinnerInsuranceProviders = (Spinner) findViewById(R.id.spinner_insurance_providers);
        spinnerInsuranceProviders.setOnItemSelectedListener(this);

        pickerDOB = (DatePicker) findViewById(R.id.picker_dob);
        pickerHeightFeet = (NumberPicker) findViewById(R.id.picker_height_feet);
        pickerHeightInch = (NumberPicker) findViewById(R.id.picker_height_inch);
        pickerWeight = (NumberPicker) findViewById(R.id.picker_weight);

        btnUpdateProfile = (Button) findViewById(R.id.btn_update);
        btnUpdateProfile.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        loadInsuranceProvider();
        loadUserDOB();
        loadUserHeight();
        loadUserWeight();

        lastName = user.getLastName();
        firstName = user.getFirstName();
        phoneNumber = user.getPhoneNumber();

        if (null != lastName) {
            txtInputLastName.setText(lastName);
        }

        if (null != firstName) {
            txtInputFirstName.setText(firstName);
        }

        if (null != phoneNumber) {
            txtInputPhoneNumber.setText(phoneNumber);
        }

        txtInputLastName.addTextChangedListener(new UpdateProfileFormTextWatcher(txtInputLastName));
        txtInputFirstName.addTextChangedListener(new UpdateProfileFormTextWatcher(txtInputFirstName));
        txtInputPhoneNumber.addTextChangedListener(new UpdateProfileFormTextWatcher(txtInputPhoneNumber));
        txtInputConfirmPassword.addTextChangedListener(new UpdateProfileFormTextWatcher(txtInputConfirmPassword));

    }

    private void loadInsuranceProvider() {
        insuranceProviders = new ArrayList<String>();
        insuranceProviders.add("HTC Worldwide");
        insuranceProviders.add("BlueCross BlueShield");
        insuranceProviders.add("Walsh Duffield");

        insuranceProviderAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, insuranceProviders);
        insuranceProviderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInsuranceProviders.setAdapter(insuranceProviderAdapter);

        int pos = indexOfUserInsuranceProvider();
        if (pos >= 0) {
            spinnerInsuranceProviders.setSelection(pos);
        }
    }

    private int indexOfUserInsuranceProvider() {
        insuranceProvider = user.getInsuranceProvider();

        if (null != insuranceProvider) {
            return insuranceProviders.indexOf(insuranceProvider);
        }

        return -1;

    }


    private void loadUserDOB() {
        DatePicker.OnDateChangedListener onDateChanged =
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(
                            DatePicker view,
                            int year,
                            int monthOfYear,
                            int dayOfMonth) {
                        Log.v(Globals.TAG, "year : " + year + ", monthOfYear : " + monthOfYear + ", dayOfMonth : " + dayOfMonth);
                    }
                };

        if (null != user.getDateOfBirth()) {
            String dob[] = user.getDateOfBirth().split("-");
            year = Integer.parseInt(dob[0]);
            month = Integer.parseInt(dob[1]);
            day = Integer.parseInt(dob[2]);

        } else {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        pickerDOB.init(year, month, day, onDateChanged);
        pickerDOB.setCalendarViewShown(false);
        pickerDOB.setSpinnersShown(true);
    }

    private void loadUserHeight() {
        if (null != user.getHeight()) {
            String height[] = user.getHeight().split("-");
            heightFeet = Integer.parseInt(height[0]);
            heightInch = Integer.parseInt(height[1]);
        } else {
            heightFeet = 5;
            heightInch = 10;
        }

        pickerHeightFeet.setMinValue(3);
        pickerHeightFeet.setMaxValue(10);
        pickerHeightFeet.setValue(heightFeet);
        pickerHeightFeet.setWrapSelectorWheel(false);

        pickerHeightFeet.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                isDataChanged = true;
                Log.v(Globals.TAG, "picker : " + picker + ", oldVal : " + oldVal + ", newVal : " + newVal);

            }
        });

        pickerHeightInch.setMinValue(0);
        pickerHeightInch.setMaxValue(11);
        pickerHeightInch.setValue(heightInch);
        pickerHeightInch.setWrapSelectorWheel(false);

        pickerHeightInch.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                isDataChanged = true;
                Log.v(Globals.TAG, "picker : " + picker + ", oldVal : " + oldVal + ", newVal : " + newVal);

            }
        });
    }

    private void loadUserWeight() {
        weightLbs = user.getWeightLbs();
        if (weightLbs <= 0) {
            weightLbs = 170;
        }

        pickerWeight.setMinValue(3);
        pickerWeight.setMaxValue(1500);
        pickerWeight.setValue(weightLbs);
        pickerWeight.setWrapSelectorWheel(false);

        pickerWeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                isDataChanged = true;
                Log.v(Globals.TAG, "picker : " + picker + ", oldVal : " + oldVal + ", newVal : " + newVal);

            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        parent.setSelection(pos);

        insuranceProvider = insuranceProviders.get(pos);

        int lastPos = indexOfUserInsuranceProvider();
        if (lastPos != pos)
            isDataChanged = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:

                if (!validateLastName(lastName)) {
                    return;
                }

                if (!validateFirstName(firstName)) {
                    return;
                }

                if (!Utility.isNetworkAvailable(this)) {
                    showDialog(res.getString(R.string.connection_error_title), res.getString(R.string.connection_error_msg));
                    return;
                }

                if(isDataChanged) {
                    hideSoftKeyboard();
                    runUpdateProfileTask();
                }

                break;
        }
    }

    private Boolean success = false;
    private void runUpdateProfileTask() {
        AsyncTask<String, Boolean, Boolean> mUpdateProfileTask;

        mUpdateProfileTask = new AsyncTask<String, Boolean, Boolean>() {
            @Override
            protected void onPreExecute() {
                launchRingDialog("Updating Info...");
            }

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    success = dbConn.updateProfileInfo(user.getLastName(), user.getFirstName(), user.getPhoneNumber(), user.getInsuranceProvider(), user.getDateOfBirth(), user.getHeight(), user.getWeightLbs(), userPassword);
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
                    saveUserData();
                    showDialog(res.getString(R.string.updated), res.getString(R.string.profile_updated));
                } else {
                    showDialog(res.getString(R.string.wrong_password), res.getString(R.string.wrong_password_msg));
                    txtInputConfirmPassword.setText("");
                }
            }

            @Override
            protected void onCancelled() {
                dismissProgressDialog();

            }
        };
        mUpdateProfileTask.execute();
    }

    private void saveUserData() {
        user.setLastName(lastName);
        user.setFirstName(firstName);
        user.setInsuranceProvider(insuranceProvider);
        user.setDateOfBirth(year + "-" + month + "-" + day);
        user.setHeight(heightFeet + "-" + heightInch);
        user.setWeightLbs(weightLbs);
        user.setPhoneNumber(phoneNumber);
    }

    private void showDialog(String title, String msg) {
        final Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mDialog.setContentView(R.layout.dialog_error);
        mDialog.setCancelable(false);
        mDialog.show();

        Button btnOK = (Button) mDialog.findViewById(R.id.btn_close);
        TextView dialogTitle = (TextView) mDialog.findViewById(R.id.dialog_toolbar_title);
        TextView dialogMessage = (TextView) mDialog.findViewById(R.id.dialog_msg);

        dialogTitle.setText(title);
        dialogMessage.setText(msg);

        btnOK.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         mDialog.dismiss();
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

    private class UpdateProfileFormTextWatcher implements TextWatcher {
        private View view;

        private UpdateProfileFormTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.txt_input_last_name:
                    isDataChanged = true;
                    lastName = txtInputLastName.getText().toString().trim();
                    validateLastName(lastName);
                    break;
                case R.id.txt_input_first_name:
                    isDataChanged = true;
                    firstName = txtInputFirstName.getText().toString().trim();
                    validateFirstName(firstName);
                    break;
                case R.id.txt_input_phone_number:
                    isDataChanged = true;
                    break;
                case R.id.txt_input_password:
                    userPassword = txtInputConfirmPassword.getText().toString().trim();
                    validatePassword(userPassword);
                    break;
            }
        }

    }

    private boolean validateLastName(String lastName) {
        if (lastName.isEmpty()) {
            inputLayoutLastName.setError(getString(R.string.err_msg_last_name));
            showSoftKeyboard(txtInputLastName);
            return false;
        } else {
            inputLayoutLastName.setError(null);
            inputLayoutLastName.setErrorEnabled(false);

        }

        return true;
    }

    private boolean validateFirstName(String firstName) {
        if (firstName.isEmpty()) {
            inputLayoutFirstName.setError(getString(R.string.err_msg_first_name));
            showSoftKeyboard(txtInputFirstName);
            return false;
        } else {
            inputLayoutLastName.setError(null);
            inputLayoutFirstName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword(String userPassword) {
        if (userPassword.isEmpty()) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_password));
            showSoftKeyboard(txtInputConfirmPassword);
            return false;
        } else {
            inputLayoutConfirmPassword.setError(null);
            inputLayoutConfirmPassword.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
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
