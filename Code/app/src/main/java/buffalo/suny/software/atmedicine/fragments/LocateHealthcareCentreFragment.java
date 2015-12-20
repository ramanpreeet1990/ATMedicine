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
import buffalo.suny.software.atmedicine.model.User;
import buffalo.suny.software.atmedicine.utility.Globals;
import buffalo.suny.software.atmedicine.utility.Utility;


public class LocateHealthcareCentreFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Spinner spinnerInsuranceProviders;

    private User user;
    private Resources res;
    private ProgressDialog ringProgressDialog;
    private LocationManager locationManager;
    private boolean isNetworkEnabled;
    private boolean isGpsEnabled;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locate_healthcare_centre, container, false);

        user = User.getCurrentUser();
        res = getResources();

        Typeface customTypeface = Typeface.createFromAsset(getActivity().getAssets(), Globals.FONT_ROBOTO_THIN);
        Typeface customBold = Typeface.create(customTypeface, Typeface.BOLD);

        TextView txtLogoHealthcareTitle = (TextView) view.findViewById(R.id.txt_logo_healthcare_title);
        txtLogoHealthcareTitle.setTypeface(customBold);

        TextView txtLogoHealthcareSubtitle = (TextView) view.findViewById(R.id.txt_logo_healthcare_subtitle);
        txtLogoHealthcareSubtitle.setTypeface(customBold);

        spinnerInsuranceProviders = (Spinner) view.findViewById(R.id.spinner_insurance_providers);
        spinnerInsuranceProviders.setOnItemSelectedListener(this);

        loadInsuranceProviders();

        Button btnLocateHealthCare = (Button) view.findViewById(R.id.btn_locate_healthcare_centre);
        btnLocateHealthCare.setOnClickListener(this);

        return view;
    }


    private void showEnableGpsDialog() {
        final Dialog errorDialog = new Dialog(getActivity());
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        errorDialog.setContentView(R.layout.dialog_error);
        errorDialog.setCancelable(true);
        errorDialog.show();

        Button btnOK = (Button) errorDialog.findViewById(R.id.btn_ok);
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
                                     }

                                 }

        );
    }

    private void loadInsuranceProviders() {
        List<String> insuranceProvider = new ArrayList<>();
        insuranceProvider.add("HTH WORLDWIDE INSURANCE SERVICES");
        insuranceProvider.add("BLUE CROSS BLUE SHIELD");
        insuranceProvider.add("INDEPENDENT HEALTH");

        ArrayAdapter<String> insuranceProviderAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, insuranceProvider);
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
                if (null != getContext()) {
                    locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                    isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


                    if (!(isNetworkEnabled || isGpsEnabled)) {
                        showEnableGpsDialog();
                    } else {
                        launchRingDialog("Searching nearest healthcare centre...");
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        FetchCurrentUserLocation loc = new FetchCurrentUserLocation();
                                        loc.getLocation(getContext(),
                                                new FetchCurrentUserLocation.LocationCallback() {
                                                    @Override
                                                    public void onLocationAvailable(double latitude, double longitude) {
                                                        dismissProgressDialog();
                                                        fetchCentres(latitude, longitude);
                                                    }
                                                });

                                    }
                                }, 1000);
                    }
                }
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

    public static class FetchCurrentUserLocation {

        public interface LocationCallback {
            public void onLocationAvailable(double latitude, double longitude);
        }

        public void getLocation(final Context context, final LocationCallback callback) {
            final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.v(Globals.TAG, "isNetworkEnabled : " + isNetworkEnabled);
            Log.v(Globals.TAG, "isGPSEnabled : " + isGPSEnabled);

            if (Utility.checkPermission(context)) {
                if (isNetworkEnabled) {
                    Log.v(Globals.TAG, "Fetching Location from Network Provider");

                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);

                    locationManager.requestSingleUpdate(criteria, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.v(Globals.TAG, "latitude : " + location.getLatitude() + ", longitude : " + location.getLongitude());
                            callback.onLocationAvailable(location.getLatitude(), location.getLongitude());
                        }


                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    }, null);
                } else if (isGPSEnabled) {
                    Log.v(Globals.TAG, "Fetching Location from GPS Provider");

                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    locationManager.requestSingleUpdate(criteria, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.v(Globals.TAG, "latitude : " + location.getLatitude() + ", longitude : " + location.getLongitude());
                            callback.onLocationAvailable(location.getLatitude(), location.getLongitude());
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    }, null);

                }
            }
        }

    }

    private void fetchCentres(double latitude, double longitude) {
        user.setLatitude(latitude);
        user.setLongitude(longitude);

        //user.setLatitude(43.000904);
        //user.setLongitude(-78.789567);
        user.setInsuranceProvider(spinnerInsuranceProviders.getSelectedItem().toString());


        dismissProgressDialog();
        Intent intent = new Intent(getActivity(), HealthcareCentreActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}
