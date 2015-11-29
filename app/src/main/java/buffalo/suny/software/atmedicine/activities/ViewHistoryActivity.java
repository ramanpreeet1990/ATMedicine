package buffalo.suny.software.atmedicine.activities;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
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

import java.util.ArrayList;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.adapter.GridMedicalHistoryAdapter;
import buffalo.suny.software.atmedicine.database.DatabaseConnection;
import buffalo.suny.software.atmedicine.fragments.FindRemedyFragment;
import buffalo.suny.software.atmedicine.model.MedicalHistory;
import buffalo.suny.software.atmedicine.model.User;
import buffalo.suny.software.atmedicine.utility.Globals;


public class ViewHistoryActivity extends AppCompatActivity {
    private Typeface customTypeface, customBold;
    private TextView txtLogoViewHistoryHistory;
    private Button btnLocateHealthCare;
    private DatabaseConnection dbConn;
    private Spinner spinnerInsuranceProviders;
    private ArrayAdapter<String> insuranceProviderAdapter;
    private double latitude, longitude;
    private User user;
    private RelativeLayout recyclerMedicalHistoryLayout;
    private ProgressDialog ringProgressDialog;
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

        fetchUserHistoryTask();
    }

    private void fetchUserHistoryTask() {
        AsyncTask<String, ArrayList<String>, ArrayList<String>> mUserHistoryTask;
        mUserHistoryTask = new AsyncTask<String, ArrayList<String>, ArrayList<String>>() {
            @Override
            protected void onPreExecute() {
                launchRingDialog("Loading Data...");
            }

            @Override
            protected ArrayList<String> doInBackground(String... params) {
                ArrayList<String> medicalHistoryList = new ArrayList<String>();

                try {
                    medicalHistoryList = dbConn.fetchUserMedicalHistory(User.getCurrentUser().getUserId());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return medicalHistoryList;
            }

            @Override
            protected void onPostExecute(ArrayList<String> medicalHistoryList) {
                super.onPostExecute(medicalHistoryList);
                dismissProgressDialog();

                drawMedicalHistoryGrids(medicalHistoryList);
            }

            @Override
            protected void onCancelled() {
                dismissProgressDialog();

            }
        };
        mUserHistoryTask.execute();
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

    public void drawMedicalHistoryGrids(ArrayList<String> medicalHistoryList) {
        int medicalHistoryCount = medicalHistoryList.size();
        mMedicalHistory = new MedicalHistory[medicalHistoryCount];

        int counter = 0;

        String split[];
        while (counter < medicalHistoryCount) {
            split = medicalHistoryList.get(counter).split("#");

            mMedicalHistory[counter] = new MedicalHistory();
            mMedicalHistory[counter].setDate(split[0]);
            mMedicalHistory[counter].setSymptom(split[2]);
            mMedicalHistory[counter].setBodyPart("in " + split[1]);

            ++counter;
        }



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
