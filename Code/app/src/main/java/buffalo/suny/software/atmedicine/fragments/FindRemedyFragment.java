package buffalo.suny.software.atmedicine.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.database.DatabaseConnection;
import buffalo.suny.software.atmedicine.model.User;
import buffalo.suny.software.atmedicine.utility.Globals;


public class FindRemedyFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Typeface customTypeface, customBold;
    private static DatabaseConnection dbConn;
    private Spinner spinnerBodyPart, spinnerSymptom;
    private ArrayAdapter<String> bodyPartsAdapter;

    private Context mContext;
    private ProgressDialog ringProgressDialog;
    private HashMap<String, ArrayList<String>> remedyDataMap = new HashMap<String, ArrayList<String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_remedy, container, false);

        dbConn = DatabaseConnection.getInstance();
        mContext = getActivity().getBaseContext();

        customTypeface = Typeface.createFromAsset(getActivity().getAssets(), Globals.FONT_ROBOTO_THIN);
        customBold = Typeface.create(customTypeface, Typeface.BOLD);

        TextView txtLogoRemedy1 = (TextView) view.findViewById(R.id.txt_logo_remedy_title);
        txtLogoRemedy1.setTypeface(customBold);

        TextView txtLogoRemedy2 = (TextView) view.findViewById(R.id.txt_logo_remedy_subtitle);
        txtLogoRemedy2.setTypeface(customBold);

        spinnerBodyPart = (Spinner) view.findViewById(R.id.spinner_body_part);
        spinnerBodyPart.setOnItemSelectedListener(this);

        spinnerSymptom = (Spinner) view.findViewById(R.id.spinner_symptom);
        spinnerSymptom.setOnItemSelectedListener(this);

        Button btnFindRemedy = (Button) view.findViewById(R.id.btn_find_remedy);
        btnFindRemedy.setOnClickListener(this);

        loadRemedyDataTask();

        return view;

    }

    private void loadRemedyDataTask() {
        AsyncTask<String, HashMap<String, ArrayList<String>>, HashMap<String, ArrayList<String>>> mRemedyDataTask;
        mRemedyDataTask = new AsyncTask<String, HashMap<String, ArrayList<String>>, HashMap<String, ArrayList<String>>>() {
            @Override
            protected void onPreExecute() {
                launchRingDialog("Loading Data...");
            }

            @Override
            protected HashMap<String, ArrayList<String>> doInBackground(String... params) {
                HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

                try {
                    map = dbConn.fetchRemedyData();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return map;
            }

            @Override
            protected void onPostExecute(HashMap<String, ArrayList<String>> map) {
                super.onPostExecute(map);
                dismissProgressDialog();

                remedyDataMap = map;
                loadRemedyData();

            }

            @Override
            protected void onCancelled() {
                dismissProgressDialog();

            }
        };
        mRemedyDataTask.execute();
    }

    private void launchRingDialog(String displayMessage) {
        ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", displayMessage, true);
        ringProgressDialog.setIndeterminate(true);
        ringProgressDialog.setCancelable(false);
    }

    public void dismissProgressDialog() {
        if (ringProgressDialog != null && ringProgressDialog.isShowing()) {
            ringProgressDialog.dismiss();
        }
    }

    public void loadRemedyData() {
        int size = remedyDataMap.size();

        if (size <= 0)
            return;

        ArrayList<String> bodyParts = new ArrayList<>();
        for (Map.Entry<String, ArrayList<String>> remedyData : remedyDataMap.entrySet()) {
            bodyParts.add(remedyData.getKey());
        }

        bodyPartsAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, bodyParts);
        bodyPartsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBodyPart.setAdapter(bodyPartsAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_find_remedy:
                findRemedyTask(spinnerBodyPart.getSelectedItem().toString(), spinnerSymptom.getSelectedItem().toString());
                break;
        }
    }

    public void findRemedyTask(final String bodyPart, final String symptom) {
        AsyncTask<String, ArrayList<String>, ArrayList<String>> mRemedyTask;
        mRemedyTask = new AsyncTask<String, ArrayList<String>, ArrayList<String>>() {
            @Override
            protected void onPreExecute() {
                launchRingDialog("Loading Data...");
            }

            @Override
            protected ArrayList<String> doInBackground(String... params) {
                ArrayList<String> remedyList = new ArrayList<>();

                try {
                    remedyList = dbConn.findRemedy(bodyPart, symptom);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return remedyList;
            }

            @Override
            protected void onPostExecute(ArrayList<String> remedyList) {
                super.onPostExecute(remedyList);
                dismissProgressDialog();

                showRemedyDialog(remedyList, symptom);
                saveUserMedicalHistoryTask(bodyPart, symptom);
            }

            @Override
            protected void onCancelled() {
                dismissProgressDialog();

            }
        };
        mRemedyTask.execute();
    }

    private boolean success;

    private void saveUserMedicalHistoryTask(final String bodyPart, final String symptom) {
        AsyncTask<String, Boolean, Boolean> mSaveMedicalHistoryTask;


        mSaveMedicalHistoryTask = new AsyncTask<String, Boolean, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    success = dbConn.saveUserMedicalHistory(User.getCurrentUser().getUserId(), bodyPart, symptom);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);

                if (success)
                    Log.v(Globals.TAG, "User history saved");
                else
                    Log.v(Globals.TAG, "User history could not saved");
            }

            @Override
            protected void onCancelled() {
                dismissProgressDialog();

            }
        };
        mSaveMedicalHistoryTask.execute();
    }

    private void showRemedyDialog(ArrayList<String> remedyList, String symptom) {
        final Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mDialog.setContentView(R.layout.dialog_remedy);
        mDialog.setCancelable(false);
        mDialog.show();

        //We are showing only suggestions of 6 remedies
        TextView remedy[] = new TextView[6];

        TextView txtOtcRemedyTitle = (TextView) mDialog.findViewById(R.id.txt_otc_remedy_title);
        remedy[0] = (TextView) mDialog.findViewById(R.id.txt_otc_remedy_1);
        remedy[1] = (TextView) mDialog.findViewById(R.id.txt_otc_remedy_2);
        remedy[2] = (TextView) mDialog.findViewById(R.id.txt_otc_remedy_3);

        TextView txtHomeRemedyTitle = (TextView) mDialog.findViewById(R.id.txt_home_remedy_title);
        remedy[3] = (TextView) mDialog.findViewById(R.id.txt_home_remedy_1);
        remedy[4] = (TextView) mDialog.findViewById(R.id.txt_home_remedy_2);
        remedy[5] = (TextView) mDialog.findViewById(R.id.txt_home_remedy_3);


        txtOtcRemedyTitle.setTypeface(customBold);
        remedy[0].setTypeface(customBold);
        remedy[1].setTypeface(customBold);
        remedy[2].setTypeface(customBold);

        txtHomeRemedyTitle.setTypeface(customBold);
        remedy[3].setTypeface(customBold);
        remedy[4].setTypeface(customBold);
        remedy[5].setTypeface(customBold);

        txtHomeRemedyTitle.setText(getActivity().getResources().getString(R.string.txt_home_remedy_title, symptom));
        txtOtcRemedyTitle.setText(getActivity().getResources().getString(R.string.txt_otc_remedy_title, symptom));

        int count = remedyList.size();

        while (count > 0) {
            --count;
            remedy[count].setText(remedyList.get(count));
        }

        Button btnOK = (Button) mDialog.findViewById(R.id.btn_close);

        btnOK.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         mDialog.dismiss();
                                     }

                                 }

        );
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (parent.getAdapter() == bodyPartsAdapter) {
            loadBodySymptoms((String) parent.getItemAtPosition(pos));
        } else {
            parent.setSelection(pos);
        }
    }

    private void loadBodySymptoms(String key) {
        ArrayAdapter<String> symptomAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, remedyDataMap.get(key));
        symptomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSymptom.setAdapter(symptomAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
