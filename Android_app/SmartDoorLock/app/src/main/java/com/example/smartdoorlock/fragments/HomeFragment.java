package com.example.smartdoorlock.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.smartdoorlock.R;
import com.example.smartdoorlock.services.DoorControlService;
import com.example.smartdoorlock.utils.NotificationHelper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private DoorControlService doorControlService;
    private Button btnOpenDoor;
    private Button btnCloseDoor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        doorControlService = new DoorControlService();

        btnOpenDoor = view.findViewById(R.id.btn_open_door);
        btnCloseDoor = view.findViewById(R.id.btn_close_door);

        btnOpenDoor.setOnClickListener(v -> openDoor());
        btnCloseDoor.setOnClickListener(v -> closeDoor());

        return view;
    }

    private void openDoor() {
        doorControlService.controlDoor(true, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String jsonResponse = response.body().string();
                        NotificationHelper.showNotification(getContext(), "Door Opened", jsonResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        NotificationHelper.showNotification(getContext(), "Error", "Failed to parse response.");
                    }
                } else {
                    NotificationHelper.showNotification(getContext(), "Error", "Failed to open the door. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                NotificationHelper.showNotification(getContext(), "Error", "Network error: " + t.getMessage());
            }
        });
    }

    private void closeDoor() {
        doorControlService.controlDoor(false, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String jsonResponse = response.body().string();
                        NotificationHelper.showNotification(getContext(), "Door Closed", jsonResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        NotificationHelper.showNotification(getContext(), "Error", "Failed to parse response.");
                    }
                } else {
                    NotificationHelper.showNotification(getContext(), "Error", "Failed to close the door. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                NotificationHelper.showNotification(getContext(), "Error", "Network error: " + t.getMessage());
            }
        });
    }

}