package com.example.smartdoorlock.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdoorlock.R;
import com.example.smartdoorlock.models.AccessLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccessLogAdapter extends RecyclerView.Adapter<AccessLogAdapter.ViewHolder> {

    private static SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
    private static SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
    private List<AccessLog> accessLogs;

    public AccessLogAdapter(List<AccessLog> accessLogs) {
        this.accessLogs = accessLogs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_access_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccessLog log = accessLogs.get(position);
        holder.bind(log);
    }

    @Override
    public int getItemCount() {
        return accessLogs.size();
    }

    public void updateData(List<AccessLog> newLogs) {
        this.accessLogs = newLogs;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAccessId, tvAccessType, tvDoorStatus, tvAccessResult, tvAccessTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAccessId = itemView.findViewById(R.id.tv_access_id);
            tvAccessType = itemView.findViewById(R.id.tv_access_type);
            tvDoorStatus = itemView.findViewById(R.id.tv_door_status);
            tvAccessResult = itemView.findViewById(R.id.tv_access_result);
            tvAccessTime = itemView.findViewById(R.id.tv_access_time);
        }

        public void bind(AccessLog log) {
            tvAccessId.setText(String.valueOf(log.getId()));
            tvAccessType.setText(log.getAccessType().toString());
            tvDoorStatus.setText(log.getDoorStatus().toString());
            tvAccessResult.setText(log.getAccessResult());
            String accessTime = log.getAccessTime();
            if (accessTime != null && !accessTime.isEmpty()) {
                try {
                    Date date = apiFormat.parse(accessTime);
                    String formattedDate = displayFormat.format(date);
                    tvAccessTime.setText(formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    tvAccessTime.setText(accessTime);
                }
            } else {
                tvAccessTime.setText("N/A");
            }
        }
    }
}