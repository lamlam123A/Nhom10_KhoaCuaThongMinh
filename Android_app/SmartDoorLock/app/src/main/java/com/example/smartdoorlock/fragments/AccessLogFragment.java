package com.example.smartdoorlock.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdoorlock.R;
import com.example.smartdoorlock.adapters.AccessLogAdapter;
import com.example.smartdoorlock.models.AccessLog;
import com.example.smartdoorlock.services.DoorControlService;
import com.example.smartdoorlock.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccessLogFragment extends Fragment {
    private RecyclerView recyclerView;
    private AccessLogAdapter adapter;
    private List<AccessLog> accessLogs;
    private DoorControlService doorControlService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_access_log, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewAccessLogs);
        doorControlService = new DoorControlService();

        accessLogs = new ArrayList<>();
        adapter = new AccessLogAdapter(accessLogs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fetchAccessLogs();

        return view;
    }

    private void fetchAccessLogs() {
        doorControlService.getAccessLogs(new Callback<List<AccessLog>>() {
            @Override
            public void onResponse(Call<List<AccessLog>> call, Response<List<AccessLog>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    accessLogs.clear();
                    accessLogs.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    NotificationHelper.showNotification(getContext(), "Error", "Failed to load access logs. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<List<AccessLog>> call, Throwable t) {
                NotificationHelper.showNotification(getContext(), "Error", "Network error. Please check your connection.");
            }
        });
    }
}