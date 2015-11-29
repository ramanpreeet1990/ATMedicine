package buffalo.suny.software.atmedicine.adapter;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import buffalo.suny.software.atmedicine.model.MedicalHistory;
import buffalo.suny.software.atmedicine.R;

/********************************************************************************************************************************
 * Developer : Ramanpreet Singh Khinda
 * <p/>
 * This class is responsible for showing data on each grid
 ********************************************************************************************************************************/
public class GridMedicalHistoryAdapter extends RecyclerView.Adapter<GridMedicalHistoryAdapter.ViewHolder> {
    private MedicalHistory[] mMedicalHistory;
    private AsyncTask<String, Void, Bitmap> mAsyncTask;

    public GridMedicalHistoryAdapter(MedicalHistory[] mMedicalHistory) {
        super();

        this.mMedicalHistory = mMedicalHistory;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.medical_history_list_card, viewGroup, false);


        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.txtSymptom.setText(mMedicalHistory[i].getSymptom());
        viewHolder.txtLastHappen.setText(mMedicalHistory[i].getBodyPart());
        viewHolder.txtDate.setText(mMedicalHistory[i].getDate());
    }

    @Override
    public int getItemCount() {
        return mMedicalHistory.length;
    }

    private OnItemClickListener mItemClickListener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, MedicalHistory mMedicalHistory);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtSymptom, txtLastHappen, txtDate;

        public ViewHolder(final View itemView) {
            super(itemView);

            this.txtSymptom = (TextView) itemView.findViewById(R.id.txt_symptom);
            this.txtLastHappen = (TextView) itemView.findViewById(R.id.txt_bodypart);
            this.txtDate = (TextView) itemView.findViewById(R.id.txt_date);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getPosition(), mMedicalHistory[getPosition()]);
            }
        }
    }

}