package buffalo.suny.software.atmedicine.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.adapter.GridHealthcareCentresAdapter;
import buffalo.suny.software.atmedicine.database.DatabaseConnection;
import buffalo.suny.software.atmedicine.model.HealthcareCentre;
import buffalo.suny.software.atmedicine.utility.Globals;


public class HealthcareCentreActivity extends AppCompatActivity {
    private Typeface customTypeface, customBold;
    private TextView txtHealthcareCount;

    private RecyclerView recyclerHealthcareCentreView;
    private GridHealthcareCentresAdapter healthcareCentresGridAdapter;
    private HealthcareCentre[] mHealthcareCentre;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthcare_centres);

        mContext = this;

        customTypeface = Typeface.createFromAsset(getAssets(), Globals.FONT_ROBOTO_THIN);
        customBold = Typeface.create(customTypeface, Typeface.BOLD);

        txtHealthcareCount = (TextView) findViewById(R.id.txt_healthcare_centres_count);
        txtHealthcareCount.setTypeface(customBold);

        recyclerHealthcareCentreView = (RecyclerView) findViewById(R.id.recycler_healthcare_centre_view);
        recyclerHealthcareCentreView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchHealthcareCentresFromDB();
        drawHealthCareCentresGrids();
    }

    private void fetchHealthcareCentresFromDB() {
        int healthcareCount = 6;
        mHealthcareCentre = new HealthcareCentre[healthcareCount];

        mHealthcareCentre[0] = new HealthcareCentre();
        mHealthcareCentre[0].setName("Student Health Centre");
        mHealthcareCentre[0].setAddress("2668 Tonawada Street\nBuffalo NY-14214");
        mHealthcareCentre[0].setPhoneNumber("Ph : (716) 848 1132");
        mHealthcareCentre[0].setLatitude(42.9008912);
        mHealthcareCentre[0].setLongitude(-78.8655908);


        mHealthcareCentre[1] = new HealthcareCentre();
        mHealthcareCentre[1].setName("HTH Medical Care");
        mHealthcareCentre[1].setAddress("1440 Niagara Blvd Road\nBuffalo NY-14214");
        mHealthcareCentre[1].setPhoneNumber("Ph : (716) 801 1010");

        mHealthcareCentre[2] = new HealthcareCentre();
        mHealthcareCentre[2].setName("Student Health Centre");
        mHealthcareCentre[2].setAddress("2668 Tonawada Street\nBuffalo NY-14214");
        mHealthcareCentre[2].setPhoneNumber("Ph : (716) 848 1132");

        mHealthcareCentre[3] = new HealthcareCentre();
        mHealthcareCentre[3].setName("HTH Medical Care");
        mHealthcareCentre[3].setAddress("1440 Niagara Blvd Road\nBuffalo NY-14214");
        mHealthcareCentre[3].setPhoneNumber("Ph : (716) 801 1010");

        mHealthcareCentre[4] = new HealthcareCentre();
        mHealthcareCentre[4].setName("Student Health Centre");
        mHealthcareCentre[4].setAddress("2668 Tonawada Street\nBuffalo NY-14214");
        mHealthcareCentre[4].setPhoneNumber("Ph : (716) 848 1132");

        mHealthcareCentre[5] = new HealthcareCentre();
        mHealthcareCentre[5].setName("HTH Medical Care");
        mHealthcareCentre[5].setAddress("1440 Niagara Blvd Road\nBuffalo NY-14214");
        mHealthcareCentre[5].setPhoneNumber("Ph : (716) 801 1010");

        Resources res = getResources();
        String text = res.getQuantityString(R.plurals.txt_count_healthcare_centres,
                healthcareCount, healthcareCount);
        txtHealthcareCount.setText(text);
    }

    public void drawHealthCareCentresGrids() {
        healthcareCentresGridAdapter = new GridHealthcareCentresAdapter(mHealthcareCentre);

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
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(unrestrictedIntent);
                    } catch (ActivityNotFoundException innerEx) {
                        Toast.makeText(mContext, "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }

            }

        });
    }

}
