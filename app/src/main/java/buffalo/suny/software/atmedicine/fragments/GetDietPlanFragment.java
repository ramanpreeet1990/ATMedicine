package buffalo.suny.software.atmedicine.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.database.DatabaseConnection;
import buffalo.suny.software.atmedicine.model.User;
import buffalo.suny.software.atmedicine.utility.Globals;


public class GetDietPlanFragment extends Fragment implements View.OnClickListener {
    private Typeface customTypeface, customBold;

    private TextView txtLogoGetDietPlanTitle, txtLogoGetDietPlanSubtitle;
    private Button btnGetDietPlan;

    private NumberPicker pickerAge, pickerHeightFeet, pickerHeightInch, pickerWeight;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonMale, radioButtonFemale;

    private String interpretedBMI;
    private int userAge, year, month, day, heightFeet, heightInch, weightLbs;
    private float BMI;
    private boolean isMale;

    private User user;
    private DatabaseConnection dbConn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_diet_plan, container, false);

        dbConn = DatabaseConnection.getInstance();
        user = User.getCurrentUser();

        customTypeface = Typeface.createFromAsset(getActivity().getAssets(), Globals.FONT_ROBOTO_THIN);
        customBold = Typeface.create(customTypeface, Typeface.BOLD);

        txtLogoGetDietPlanTitle = (TextView) view.findViewById(R.id.txt_logo_get_diet_plan_title);
        txtLogoGetDietPlanTitle.setTypeface(customBold);

        txtLogoGetDietPlanSubtitle = (TextView) view.findViewById(R.id.txt_logo_get_diet_plan_subtitle);
        txtLogoGetDietPlanSubtitle.setTypeface(customBold);

        radioGroupGender = (RadioGroup) view.findViewById(R.id.radio_group_gender);
        radioButtonMale = (RadioButton) view.findViewById(R.id.radio_button_male);
        radioButtonFemale = (RadioButton) view.findViewById(R.id.radio_button_female);

        pickerAge = (NumberPicker) view.findViewById(R.id.picker_age);
        pickerHeightFeet = (NumberPicker) view.findViewById(R.id.picker_height_feet);
        pickerHeightInch = (NumberPicker) view.findViewById(R.id.picker_height_inch);
        pickerWeight = (NumberPicker) view.findViewById(R.id.picker_weight);

        btnGetDietPlan = (Button) view.findViewById(R.id.btn_get_diet_plan);
        btnGetDietPlan.setOnClickListener(this);

        loadUserGender();
        loadUserAge();
        loadUserHeight();
        loadUserWeight();
        return view;
    }

    private void loadUserGender() {
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_button_male) {
                    Log.v(Globals.TAG, "user is male");
                    isMale = true;
                } else if (checkedId == R.id.radio_button_female) {
                    Log.v(Globals.TAG, "user is female");
                    isMale = false;
                }
            }
        });
    }

    private void loadUserAge() {
        String userDob = user.getDateOfBirth();

        if (null != userDob) {
            String dob[] = user.getDateOfBirth().split("-");
            year = Integer.parseInt(dob[0]);
            month = Integer.parseInt(dob[1]);
            day = Integer.parseInt(dob[2]);


            GregorianCalendar cal = new GregorianCalendar();
            int y, m, d, a;

            y = cal.get(Calendar.YEAR);
            m = cal.get(Calendar.MONTH);
            d = cal.get(Calendar.DAY_OF_MONTH);
            cal.set(year, month, day);
            a = y - cal.get(Calendar.YEAR);
            if ((m < cal.get(Calendar.MONTH))
                    || ((m == cal.get(Calendar.MONTH)) && (d < cal
                    .get(Calendar.DAY_OF_MONTH)))) {
                --a;
            }

            if (a > 0)
                userAge = a;
        } else {
            userAge = Globals.DEFAULT_AGE;
        }

        pickerAge.setMinValue(10);
        pickerAge.setMaxValue(120);
        pickerAge.setValue(userAge);
        pickerAge.setWrapSelectorWheel(false);

        pickerHeightFeet.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.v(Globals.TAG, "picker : " + picker + ", oldVal : " + oldVal + ", newVal : " + newVal);

                userAge = newVal;
            }
        });


    }

    private void loadUserHeight() {
        if (null != user.getHeight()) {
            String height[] = user.getHeight().split("-");
            heightFeet = Integer.parseInt(height[0]);
            heightInch = Integer.parseInt(height[1]);
        } else {
            heightFeet = Globals.DEFAULT_HEIGHT_FEET;
            heightInch = Globals.DEFAULT_HEIGHT_INCH;
        }

        pickerHeightFeet.setMinValue(3);
        pickerHeightFeet.setMaxValue(10);
        pickerHeightFeet.setValue(heightFeet);
        pickerHeightFeet.setWrapSelectorWheel(false);

        pickerHeightFeet.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.v(Globals.TAG, "picker : " + picker + ", oldVal : " + oldVal + ", newVal : " + newVal);

                heightFeet = newVal;
            }
        });

        pickerHeightInch.setMinValue(0);
        pickerHeightInch.setMaxValue(11);
        pickerHeightInch.setValue(heightInch);
        pickerHeightInch.setWrapSelectorWheel(false);

        pickerHeightInch.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.v(Globals.TAG, "picker : " + picker + ", oldVal : " + oldVal + ", newVal : " + newVal);

                heightInch = newVal;
            }
        });
    }

    private void loadUserWeight() {
        weightLbs = user.getWeightLbs();
        if (weightLbs <= 0) {
            weightLbs = Globals.DEFAULT_WEIGHT;
        }

        pickerWeight.setMinValue(3);
        pickerWeight.setMaxValue(1500);
        pickerWeight.setValue(weightLbs);
        pickerWeight.setWrapSelectorWheel(false);

        pickerWeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.v(Globals.TAG, "picker : " + picker + ", oldVal : " + oldVal + ", newVal : " + newVal);

                weightLbs = newVal;
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_diet_plan:
                saveUserData();
                float height = heightFeet + (heightInch / 100f);

                Log.v(Globals.TAG, "height : " + height);

                BMI = calculateBMI(weightLbs, height);
                interpretedBMI = interpretBMI(BMI);
                user.setInterpretedBMI(interpretedBMI);

                DecimalFormat bmiFormatter = new DecimalFormat("###.##");
                bmiFormatter.setMinimumFractionDigits(2);
                bmiFormatter.setMaximumFractionDigits(2);

                showDietPlan(Float.parseFloat(bmiFormatter.format(BMI)), isMale);
                break;

        }
    }

    private void saveUserData() {
        user.setMale(isMale);
        user.setHeight(heightFeet + "-" + heightInch);
        user.setWeightLbs(weightLbs);
    }

    private float calculateBMI(float weight, float height) {
        return (float) (weight * 4.88 / (height * height));
    }

    private String interpretBMI(float bmiValue) {
        if (bmiValue < 16) {
            return "Severely underweight";
        } else if (bmiValue < 18.5) {
            return "Underweight";
        } else if (bmiValue < 25) {
            return "Normal";
        } else if (bmiValue < 30) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    private void showDietPlan(Float bmi, boolean isMale) {
        final Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mDialog.setContentView(R.layout.dialog_diet_plan);
        mDialog.setCancelable(false);
        mDialog.show();

        TextView dialogTitle = (TextView) mDialog.findViewById(R.id.dialog_toolbar_title);
        TextView dialogMessage = (TextView) mDialog.findViewById(R.id.dialog_msg);

        ImageView dietPlan = (ImageView) mDialog.findViewById(R.id.img_diet_plan);

        if (bmi < 18.5) {
            if (isMale)
                dietPlan.setImageResource(R.drawable.diet_plan_underweight_male);
            else
                dietPlan.setImageResource(R.drawable.diet_plan_underweight_female);
        } else if (bmi < 25) {
            if (isMale)
                dietPlan.setImageResource(R.drawable.diet_plan_normal_male);
            else
                dietPlan.setImageResource(R.drawable.diet_plan_normal_female);
        } else if (bmi < 30) {
            if (isMale)
                dietPlan.setImageResource(R.drawable.diet_plan_overweight_male);
            else
                dietPlan.setImageResource(R.drawable.diet_plan_overweight_female);
        } else {
            if (isMale)
                dietPlan.setImageResource(R.drawable.diet_plan_obese_male);
            else
                dietPlan.setImageResource(R.drawable.diet_plan_obese_female);
        }

        Button btnOK = (Button) mDialog.findViewById(R.id.btn_close);

        dialogTitle.setText("BMI " + bmi);
        dialogMessage.setText("You are " + interpretedBMI + " follow the below diet plan");

        btnOK.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         mDialog.dismiss();
                                     }

                                 }

        );
    }
}
