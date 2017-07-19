package com.seoultechappsoftlab.wireloc.activity.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.seoultechappsoftlab.wireloc.activity.R;
import com.seoultechappsoftlab.wireloc.entities.Fingerprint;
import com.seoultechappsoftlab.wireloc.entities.Stage;
import com.seoultechappsoftlab.wireloc.infrastructures.ListViewAdapterBase;
import com.seoultechappsoftlab.wireloc.services.StageService;

/**
 * Fingerprint Manager List View Adapter
 *
 * @author farissyariati
 */
@SuppressLint("InflateParams")
public class FingerprintManagerListViewAdapter extends ListViewAdapterBase<Fingerprint> {

    //Region Private Variables

    private StageService stageService;
    private Context context;

    //End Region Private Variables

    /**
     * Constructor
     *
     * @param context
     */
    public FingerprintManagerListViewAdapter(Context context) {
        super(context);
        this.stageService = new StageService(context);
        this.context = context;
    }

    @Override
    public Object getItem(int position) {
        return this.dataCollection.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return (this.dataCollection == null ? 0 : this.dataCollection.size());
    }

    @Override
    public void setData(List<Fingerprint> dataCollection) {
        this.dataCollection = dataCollection;
    }

    @Override
    public List<Fingerprint> getDataCollection() {
        return this.dataCollection;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = this.layoutInflater.inflate(R.layout.row_list_fingerprint_manager, null);
            holder = new ViewHolder();
            holder.textViewPosition = (TextView) convertView.findViewById(R.id.tvFingerprintPosition);
            holder.textViewStageInformation = (TextView) convertView.findViewById(R.id.tvStageLocation);
            holder.textViewFingerprintStatus = (TextView) convertView.findViewById(R.id.tvFingerprintStatus);
            holder.enableDistableFingerprintCheckbox = (CheckBox) convertView.findViewById(R.id.enableDisableCheckBox);
            holder.enableDistableFingerprintCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = (Integer) buttonView.getTag();
                    getData(position).setIsSelected(buttonView.isChecked());
                }
            });
            convertView.setTag(R.id.enableDisableCheckBox, holder.enableDistableFingerprintCheckbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //maintain & lock, checked or unchecked
        holder.enableDistableFingerprintCheckbox.setTag(position);
        holder.enableDistableFingerprintCheckbox.setChecked(this.getData(position).isSelected());

        String positionString = this.context.getString(R.string.step_recording_coordinate_format);
        positionString = positionString.replace("[x]", Double.toString(this.getData(position).getPointX()));
        positionString = positionString.replace("[y]", Double.toString(this.getData(position).getPointY()));
        holder.textViewPosition.setText(positionString);
        Stage currentStage = this.stageService.getById(this.getData(position).getStageId());
        holder.textViewStageInformation.setText(currentStage.getStageName());

        String fingerprintingSatus = this.context.getString(R.string.fingerprint_status);
        fingerprintingSatus = fingerprintingSatus.replace("[s]", this.getData(position).isActive() ? this.context.getString(R.string.fingerprint_status_enable) : this.context.getString(R.string.fingerprint_status_disable));
        holder.textViewFingerprintStatus.setText(fingerprintingSatus);
        return convertView;
    }

    @Override
    public Fingerprint getData(int position) {
        return this.dataCollection.get(position);
    }

    static class ViewHolder {
        TextView textViewPosition;
        TextView textViewStageInformation;
        TextView textViewFingerprintStatus;
        CheckBox enableDistableFingerprintCheckbox;
    }

}
