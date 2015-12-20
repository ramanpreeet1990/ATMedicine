package buffalo.suny.software.atmedicine.activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.adapter.GridHealthcareCentresAdapter;
import buffalo.suny.software.atmedicine.database.DatabaseConnection;
import buffalo.suny.software.atmedicine.model.HealthcareCentre;
import buffalo.suny.software.atmedicine.model.User;
import buffalo.suny.software.atmedicine.utility.Globals;


public class HealthcareCentreActivity extends AppCompatActivity {
    private TextView txtHealthcareCount;

    private RecyclerView recyclerHealthcareCentreView;

    private Context mContext;
    private ProgressDialog ringProgressDialog;
    private DatabaseConnection dbConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthcare_centres);

        mContext = this;

        Typeface customTypeface = Typeface.createFromAsset(getAssets(), Globals.FONT_ROBOTO_THIN);
        Typeface customBold = Typeface.create(customTypeface, Typeface.BOLD);

        txtHealthcareCount = (TextView) findViewById(R.id.txt_healthcare_centres_count);
        txtHealthcareCount.setTypeface(customBold);

        recyclerHealthcareCentreView = (RecyclerView) findViewById(R.id.recycler_healthcare_centre_view);
        recyclerHealthcareCentreView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchHealthcareCentresTask();
    }

    private void fetchHealthcareCentresTask() {
        AsyncTask<String, ArrayList<ArrayList<String>>, ArrayList<ArrayList<String>>> mFetchHealthcareCentresTask;
        mFetchHealthcareCentresTask = new AsyncTask<String, ArrayList<ArrayList<String>>, ArrayList<ArrayList<String>>>() {
            @Override
            protected void onPreExecute() {
                dbConn = DatabaseConnection.getInstance();
                launchRingDialog("Loading Data...");
            }

            @Override
            protected ArrayList<ArrayList<String>> doInBackground(String... params) {
                ArrayList<ArrayList<String>> healthcareCentresList = new ArrayList<>();

                try {
                    healthcareCentresList = dbConn.fetchHealthcareCentres(User.getCurrentUser().getUserId(), User.getCurrentUser().getInsuranceProvider(), User.getCurrentUser().getLatitude(), User.getCurrentUser().getLongitude());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return healthcareCentresList;
            }

            @Override
            protected void onPostExecute(ArrayList<ArrayList<String>> healthcareCentresList) {
                super.onPostExecute(healthcareCentresList);
                dismissProgressDialog();

                drawHealthCareCentresGrids(healthcareCentresList);
            }

            @Override
            protected void onCancelled() {
                dismissProgressDialog();

            }
        };
        mFetchHealthcareCentresTask.execute();
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


    public void drawHealthCareCentresGrids(ArrayList<ArrayList<String>> healthcareCentresList) {
        int healthcareCount = healthcareCentresList.size();

        Resources res = getResources();
        String text = res.getQuantityString(R.plurals.txt_count_healthcare_centres,
                healthcareCount, healthcareCount);
        txtHealthcareCount.setText(text);

        HealthcareCentre[] mHealthcareCentre = new HealthcareCentre[healthcareCount];

        int counter = 0;
        while (counter < healthcareCount) {
            mHealthcareCentre[counter] = new HealthcareCentre();
            mHealthcareCentre[counter].setName(healthcareCentresList.get(counter).get(0));
            mHealthcareCentre[counter].setAddress(healthcareCentresList.get(counter).get(1));
            mHealthcareCentre[counter].setPhoneNumber(healthcareCentresList.get(counter).get(2));
            mHealthcareCentre[counter].setEmailId(healthcareCentresList.get(counter).get(3));
            mHealthcareCentre[counter].setLatitude(Double.valueOf(healthcareCentresList.get(counter).get(4)));
            mHealthcareCentre[counter].setLongitude(Double.valueOf(healthcareCentresList.get(counter).get(5)));
            mHealthcareCentre[counter].setDistanceFromUser(Double.valueOf(healthcareCentresList.get(counter).get(6)));

            ++counter;
        }


        GridHealthcareCentresAdapter healthcareCentresGridAdapter = new GridHealthcareCentresAdapter(mHealthcareCentre);

        recyclerHealthcareCentreView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerHealthcareCentreView.setAdapter(healthcareCentresGridAdapter);
        healthcareCentresGridAdapter.setOnItemClickListener(new GridHealthcareCentresAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View itemView, int position, HealthcareCentre mHealthcareCentre) {
                Log.v(Globals.TAG, "Touched item : " + position + ", Name : " + mHealthcareCentre.getName());

                double destinationLatitude = mHealthcareCentre.getLatitude();
                double destinationLongitude = mHealthcareCentre.getLongitude();

                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", destinationLatitude, destinationLongitude, mHealthcareCentre.getName());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    try {
                        Intent directionIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(directionIntent);
                    } catch (ActivityNotFoundException innerEx) {
                        Toast.makeText(mContext, "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }

            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
