package com.abroadbent.servicenowapi.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abroadbent.servicenowapi.R;
import com.abroadbent.servicenowapi.model.object.ApiResponseRecord;
import com.abroadbent.servicenowapi.model.object.ApprovalRecord;

import java.util.List;

/**
 *      Adapter for adding a list of API Records to the HomeActivity view
 *
 * @author      alexander.broadbent
 * @version     03/02/2015
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecordViewHolder> {

    protected List<? extends ApiResponseRecord> mRecordList;
    protected RecyclerView mListView;

    public RecyclerViewAdapter(List<? extends ApiResponseRecord> recordList) {
        this.mRecordList = recordList;
    }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_approval, parent, false);

        this.mListView = (RecyclerView) parent.findViewById(R.id.fragment_list_view_container);

        return new RecordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {
        ApiResponseRecord record = mRecordList.get(position);
        holder.tag = record.getSysId();
        holder.vTitle.setText(record.getSysId());
        holder.vMessage.setText(record.getUpdatedOn());

        if (record.getClass().equals(ApprovalRecord.class) || record instanceof ApprovalRecord) {
            ApprovalRecord approval = (ApprovalRecord) record;

            if (approval.getApproverUserRecord() != null)
                holder.vAdditional.setText(approval.getApproverUserRecord().getName());
        }
    }

    @Override
    public int getItemCount() {
        return mRecordList.size();
    }

    // Set the approver name once it has been loaded from the async task
    public void setItemAdditionalText(String sys_id, String additional_text) {

    }



    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        protected String tag;
        protected TextView vTitle;
        protected TextView vMessage;
        protected TextView vAdditional;

        public RecordViewHolder(View itemView) {
            super(itemView);
            vTitle = (TextView) itemView.findViewById(R.id.card_title);
            vMessage = (TextView) itemView.findViewById(R.id.card_message);
            vAdditional = (TextView) itemView.findViewById(R.id.card_additional);
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }
    }
}
