package buffalo.suny.software.atmedicine.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

import buffalo.suny.software.atmedicine.R;
import buffalo.suny.software.atmedicine.model.HealthcareCentre;

public class GridHealthcareCentresAdapter extends RecyclerView.Adapter<GridHealthcareCentresAdapter.ViewHolder> {
    private HealthcareCentre[] mHealthcareCentre;
    private Resources res;

    public GridHealthcareCentresAdapter(HealthcareCentre[] mHealthcareCentre) {
        super();

        this.mHealthcareCentre = mHealthcareCentre;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.healthcare_centre_list_card, viewGroup, false);

        res = viewGroup.getResources();

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        DecimalFormat distanceFormatter = new DecimalFormat();
        distanceFormatter.setMinimumFractionDigits(2);
        distanceFormatter.setMaximumFractionDigits(2);

        viewHolder.txtName.setText(mHealthcareCentre[i].getName());
        viewHolder.txtAddress.setText(mHealthcareCentre[i].getAddress());
        viewHolder.txtPhoneNumber.setText(res.getString(R.string.txt_healthcare_phone_number, mHealthcareCentre[i].getPhoneNumber()));
        viewHolder.txtEmail.setText(mHealthcareCentre[i].getEmailId());
        viewHolder.txtDistanceFromUser.setText(res.getString(R.string.txt_healthcare_distance, distanceFormatter.format(mHealthcareCentre[i].getDistanceFromUser())));
    }


    @Override
    public int getItemCount() {
        return mHealthcareCentre.length;
    }

    private OnItemClickListener mItemClickListener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, HealthcareCentre mProduct);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtName, txtAddress, txtPhoneNumber, txtEmail, txtDistanceFromUser;
        private ImageView imgDrivingDirection;

        public ViewHolder(final View itemView) {
            super(itemView);

            this.txtName = (TextView) itemView.findViewById(R.id.txt_name);
            this.txtAddress = (TextView) itemView.findViewById(R.id.txt_address);
            this.txtPhoneNumber = (TextView) itemView.findViewById(R.id.txt_phone);
            this.txtEmail = (TextView) itemView.findViewById(R.id.txt_email);
            this.txtDistanceFromUser = (TextView) itemView.findViewById(R.id.txt_distance);
            this.imgDrivingDirection = (ImageView) itemView.findViewById(R.id.img_driving_direction);

            imgDrivingDirection.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getAdapterPosition(), mHealthcareCentre[getAdapterPosition()]);
            }
        }
    }

}