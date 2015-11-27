package buffalo.suny.software.atmedicine.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.activities.ChangePasswordActivity;
import buffalo.suny.software.atmedicine.activities.UpdateProfileActivity;
import buffalo.suny.software.atmedicine.activities.ViewHistoryActivity;
import buffalo.suny.software.atmedicine.utility.Globals;


public class SettingsFragment extends Fragment implements View.OnClickListener {
    private Typeface customTypeface, customBold;
    private Button btnUpdateProfile, btnChangePwd, btnViewHistory;
    private TextView txtLogoSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        customTypeface = Typeface.createFromAsset(getActivity().getAssets(), Globals.FONT_ROBOTO_THIN);
        customBold = Typeface.create(customTypeface, Typeface.BOLD);

        txtLogoSettings = (TextView) view.findViewById(R.id.txt_logo_settings_title);
        txtLogoSettings.setTypeface(customBold);

        btnUpdateProfile = (Button) view.findViewById(R.id.btn_update_profile);
        btnUpdateProfile.setOnClickListener(this);

        btnChangePwd = (Button) view.findViewById(R.id.btn_change_password);
        btnChangePwd.setOnClickListener(this);

        btnViewHistory = (Button) view.findViewById(R.id.btn_view_history);
        btnViewHistory.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.btn_update_profile:
                intent = new Intent(getActivity(), UpdateProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_change_password:
                intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_view_history:
                intent = new Intent(getActivity(), ViewHistoryActivity.class);
                startActivity(intent);
                break;
        }
    }
}