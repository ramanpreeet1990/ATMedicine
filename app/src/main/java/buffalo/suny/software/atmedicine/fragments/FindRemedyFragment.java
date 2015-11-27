package buffalo.suny.software.atmedicine.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.database.DatabaseConnection;
import buffalo.suny.software.atmedicine.utility.Globals;


public class FindRemedyFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Typeface customTypeface, customBold;
    private TextView txtLogoRemedy1, txtLogoRemedy2;
    private Button btnFindRemedy;
    private DatabaseConnection dbConn;
    private Spinner spinnerBodyPart, spinnerSymptom;
    private ArrayAdapter<String> bodyPartsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_remedy, container, false);

        dbConn = DatabaseConnection.getInstance();

        customTypeface = Typeface.createFromAsset(getActivity().getAssets(), Globals.FONT_ROBOTO_THIN);
        customBold = Typeface.create(customTypeface, Typeface.BOLD);

        txtLogoRemedy1 = (TextView) view.findViewById(R.id.txt_logo_remedy_title);
        txtLogoRemedy1.setTypeface(customBold);

        txtLogoRemedy2 = (TextView) view.findViewById(R.id.txt_logo_remedy_subtitle);
        txtLogoRemedy2.setTypeface(customBold);

        spinnerBodyPart = (Spinner) view.findViewById(R.id.spinner_body_part);
        spinnerBodyPart.setOnItemSelectedListener(this);

        spinnerSymptom = (Spinner) view.findViewById(R.id.spinner_symptom);
        spinnerSymptom.setOnItemSelectedListener(this);

        loadBodyParts();

        btnFindRemedy = (Button) view.findViewById(R.id.btn_find_remedy);
        btnFindRemedy.setOnClickListener(this);

        return view;

    }

    private void loadBodyParts() {
        List<String> bodyParts = new ArrayList<String>();
        bodyParts.add("Upper");
        bodyParts.add("Middle");
        bodyParts.add("Lower");

        bodyPartsAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, bodyParts);
        bodyPartsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBodyPart.setAdapter(bodyPartsAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_find_remedy:
                dbConn.findRemedy(spinnerBodyPart.getSelectedItem().toString(), spinnerSymptom.getSelectedItem().toString());
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (parent.getAdapter() == bodyPartsAdapter) {
            parent.getItemAtPosition(pos);

            if (pos == 0) {
                loadUpperBodySymptoms();
            } else if (pos == 1) {
                loadMiddleBodySymptoms();
            } else if (pos == 2) {
                loadLowerBodySymptoms();
            }
        } else {
            parent.setSelection(pos);
        }
    }

    private void loadUpperBodySymptoms() {
        List<String> upperBodySymptoms = new ArrayList<String>();
        upperBodySymptoms.add("Headache");
        upperBodySymptoms.add("Nausea");
        upperBodySymptoms.add("Ear problem");

        ArrayAdapter<String> upperBodySymptomAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, upperBodySymptoms);
        upperBodySymptomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSymptom.setAdapter(upperBodySymptomAdapter);
    }

    private void loadMiddleBodySymptoms() {
        List<String> middleBodySymptoms = new ArrayList<String>();
        middleBodySymptoms.add("Stomach ache");
        middleBodySymptoms.add("Skin burn");
        middleBodySymptoms.add("Shoulder pain");

        ArrayAdapter<String> middleBodySymptomAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, middleBodySymptoms);
        middleBodySymptomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSymptom.setAdapter(middleBodySymptomAdapter);
    }

    private void loadLowerBodySymptoms() {
        List<String> lowerBodySymptoms = new ArrayList<String>();
        lowerBodySymptoms.add("Leg rash");
        lowerBodySymptoms.add("Toe problem");
        lowerBodySymptoms.add("Back problem");

        ArrayAdapter<String> lowerBodySymptomAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, lowerBodySymptoms);
        lowerBodySymptomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSymptom.setAdapter(lowerBodySymptomAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
