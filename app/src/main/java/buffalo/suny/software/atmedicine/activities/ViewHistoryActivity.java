package buffalo.suny.software.atmedicine.activities;

import android.graphics.Typeface;
import android.os.Bundle;
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

import buffalo.suny.software.atmedicine.model.MedicalHistory;
import buffalo.suny.software.atmedicine.model.User;
import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.adapter.GridMedicalHistoryAdapter;
import buffalo.suny.software.atmedicine.database.DatabaseConnection;
import buffalo.suny.software.atmedicine.utility.Globals;


public class ViewHistoryActivity extends AppCompatActivity {
    private Typeface customTypeface, customBold;
    private TextView txtLogoViewHistoryHistory;
    private Button btnLocateHealthCare;
    private DatabaseConnection dbConn;
    private Spinner spinnerInsuranceProviders;
    private ArrayAdapter<String> insuranceProviderAdapter;
    double latitude, longitude;
    private User user;
    private RelativeLayout recyclerMedicalHistoryLayout;
    private RecyclerView recyclerMedicalHistoryView;
    private GridMedicalHistoryAdapter medicalHistoryGridAdapter;
    private MedicalHistory[] mMedicalHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        dbConn = DatabaseConnection.getInstance();
        user = User.getCurrentUser();

        customTypeface = Typeface.createFromAsset(getAssets(), Globals.FONT_ROBOTO_THIN);
        customBold = Typeface.create(customTypeface, Typeface.BOLD);

        txtLogoViewHistoryHistory = (TextView) findViewById(R.id.txt_logo_view_history_title);
        txtLogoViewHistoryHistory.setTypeface(customBold);

        recyclerMedicalHistoryLayout = (RelativeLayout) findViewById(R.id.recycler_medical_history_layout);
        recyclerMedicalHistoryView = (RecyclerView) findViewById(R.id.recycler_medical_history_view);
        recyclerMedicalHistoryView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchUserHistory(user.getEmailId());
        drawMedicalHistoryGrids();

    }

    private void fetchUserHistory(String userEmailId) {
        int medicalHistoryCount = 4;
        mMedicalHistory = new MedicalHistory[medicalHistoryCount];

        mMedicalHistory[0] = new MedicalHistory();
        mMedicalHistory[0].setSymptom("Cough & Cold");
        mMedicalHistory[0].setLastHappen("2 days ago");
        mMedicalHistory[0].setDate("October 8, 2015");

        mMedicalHistory[1] = new MedicalHistory();
        mMedicalHistory[1].setSymptom("Skin Rash");
        mMedicalHistory[1].setLastHappen("5 days ago");
        mMedicalHistory[1].setDate("October 5, 2015");

        mMedicalHistory[2] = new MedicalHistory();
        mMedicalHistory[2].setSymptom("Constipation");
        mMedicalHistory[2].setLastHappen("15 days ago");
        mMedicalHistory[2].setDate("September 25, 2015");

        mMedicalHistory[3] = new MedicalHistory();
        mMedicalHistory[3].setSymptom("Fever");
        mMedicalHistory[3].setLastHappen("20 days ago");
        mMedicalHistory[3].setDate("September 20, 2015");

    }

    public void drawMedicalHistoryGrids() {
        medicalHistoryGridAdapter = new GridMedicalHistoryAdapter(mMedicalHistory);

        recyclerMedicalHistoryView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerMedicalHistoryView.setAdapter(medicalHistoryGridAdapter);
        medicalHistoryGridAdapter.setOnItemClickListener(new GridMedicalHistoryAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View itemView, int position, MedicalHistory mMedicalHistory) {
                Log.v(Globals.TAG, "Touched item : " + position + ", Name : " + mMedicalHistory.getSymptom());

            }

        });
    }


}
