package buffalo.suny.software.atmedicine.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
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
import java.util.List;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.activities.HealthcareCentreActivity;
import buffalo.suny.software.atmedicine.database.DatabaseConnection;
import buffalo.suny.software.atmedicine.model.User;
import buffalo.suny.software.atmedicine.utility.Globals;
import buffalo.suny.software.atmedicine.utility.Utility;


public class LocateHealthcareCentreFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Typeface customTypeface, customBold;

    private TextView txtLogoHealthcareTitle, txtLogoHealthcareSubtitle;
    private Button btnLocateHealthCare;
    private ArrayAdapter<String> insuranceProviderAdapter;
    private Spinner spinnerInsuranceProviders;

    private User user;
    private DatabaseConnection dbConn;
    private MyLocationListener locationListener;
    private LocationManager locationManager;
    private Resources res;

    private ProgressDialog ringProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locate_healthcare_centre, container, false);

        user = User.getCurrentUser();
        dbConn = DatabaseConnection.getInstance();
        res = getResources();

        customTypeface = Typeface.createFromAsset(getActivity().getAssets(), Globals.FONT_ROBOTO_THIN);
        customBold = Typeface.create(customTypeface, Typeface.BOLD);

        txtLogoHealthcareTitle = (TextView) view.findViewById(R.id.txt_logo_healthcare_title);
        txtLogoHealthcareTitle.setTypeface(customBold);

        txtLogoHealthcareSubtitle = (TextView) view.findViewById(R.id.txt_logo_healthcare_subtitle);
        txtLogoHealthcareSubtitle.setTypeface(customBold);

        spinnerInsuranceProviders = (Spinner) view.findViewById(R.id.spinner_insurance_providers);
        spinnerInsuranceProviders.setOnItemSelectedListener(this);

        loadInsuranceProviders();

        btnLocateHealthCare = (Button) view.findViewById(R.id.btn_locate_healthcare_centre);
        btnLocateHealthCare.setOnClickListener(this);


        return view;
    }

    private void getLocation() {
        Location location = null;
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setCostAllowed(true);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        String provider = locationManager.getBestProvider(criteria, true);

        if (Utility.checkPermission(getActivity())) {
            location = locationManager.getLastKnownLocation(provider);
        }

        if (location != null) {
            locationListener.onLocationChanged(location);
        } else {
            showEnableGpsDialog();
        }

        locationManager.requestLocationUpdates(provider, 5000, 5, locationListener);
    }

    private void showEnableGpsDialog() {
        final Dialog errorDialog = new Dialog(getActivity());
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        errorDialog.setContentView(R.layout.dialog_error);
        errorDialog.setCancelable(false);
        errorDialog.show();

        Button btnOK = (Button) errorDialog.findViewById(R.id.btn_close);
        TextView dialogTitle = (TextView) errorDialog.findViewById(R.id.dialog_toolbar_title);
        TextView dialogMessage = (TextView) errorDialog.findViewById(R.id.dialog_msg);

        dialogTitle.setText(res.getString(R.string.enable_gps_title));
        dialogMessage.setText(res.getString(R.string.enable_gps_msg));
        btnOK.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         errorDialog.dismiss();
                                         Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                         startActivity(intent);
                                         launchRingDialog("Fetching current location...");
                                     }

                                 }

        );
    }

    private void loadInsuranceProviders() {
        List<String> insuranceProvider = new ArrayList<String>();
        insuranceProvider.add("HTC Worldwide");
        insuranceProvider.add("BlueCross BlueShield");
        insuranceProvider.add("Walsh Duffield");

        insuranceProviderAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, insuranceProvider);
        insuranceProviderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInsuranceProviders.setAdapter(insuranceProviderAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        parent.setSelection(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_locate_healthcare_centre:
                getLocation();
                break;
        }
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

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.v(Globals.TAG, "latitude : " + location.getLatitude() + ", longitude : " + location.getLongitude());

            dismissProgressDialog();

            user.setLatitude(location.getLatitude());
            user.setLongitude(location.getLongitude());

            if (Utility.checkPermission(getActivity()))
                locationManager.removeUpdates(locationListener);

            if (dbConn.fetchHealthcareCentres(spinnerInsuranceProviders.getSelectedItem().toString(), user.getLatitude(), user.getLongitude())) {
                Intent intent = new Intent(getActivity(), HealthcareCentreActivity.class);
                startActivity(intent);
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.v(Globals.TAG, "status changed to " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.v(Globals.TAG, "Provider " + provider + " enabled");

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.v(Globals.TAG, "Provider " + provider + " desabled");
        }
    }

}
