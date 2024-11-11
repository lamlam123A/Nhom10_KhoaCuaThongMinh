package com.example.smartdoorlock.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.smartdoorlock.R;
import com.example.smartdoorlock.models.DefaultKeyResponse;
import com.example.smartdoorlock.models.ValidCardIdResponse;
import com.example.smartdoorlock.services.DoorControlService;
import com.example.smartdoorlock.utils.NotificationHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {
    private CardView btnUpdateDefaultKey;
    private CardView btnUpdateValidCardId;
    private DoorControlService doorControlService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        doorControlService = new DoorControlService();

        btnUpdateDefaultKey = view.findViewById(R.id.btnUpdateDefaultKey);
        btnUpdateValidCardId = view.findViewById(R.id.btnUpdateValidCardId);


//        btnUpdateDefaultKey.setOnClickListener(v -> updateDefaultKey(etDefaultKey.getText().toString()));
//        btnUpdateValidCardId.setOnClickListener(v -> updateValidCardId(etValidCardId.getText().toString()));


        btnUpdateDefaultKey.setOnClickListener(v -> {
            Fragment changePasswordFragment = new ChangePasswordFragment();
            replaceFragment(changePasswordFragment);
        });
        btnUpdateValidCardId.setOnClickListener(v -> {
            Fragment changeCardIdFragment = new ChangeCardIdFragment();
            replaceFragment(changeCardIdFragment);
        });

        return view;
    }

    private void updateDefaultKey(String newKey) {
        doorControlService.updateDefaultKey(newKey, new Callback<DefaultKeyResponse>() {
            @Override
            public void onResponse(Call<DefaultKeyResponse> call, Response<DefaultKeyResponse> response) {
                if (response.isSuccessful()) {
                    NotificationHelper.showNotification(getContext(), "Default key updated successfully", "The default key has been updated successfully.");
                } else {
                    NotificationHelper.showNotification(getContext(), "Error", "Failed to update default key. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<DefaultKeyResponse> call, Throwable t) {
                NotificationHelper.showNotification(getContext(), "Error", "Network error. Please check your connection.");
            }
        });
    }

    private void updateValidCardId(String newCardId) {
        doorControlService.updateValidCardId(newCardId, new Callback<ValidCardIdResponse>() {
            @Override
            public void onResponse(Call<ValidCardIdResponse> call, Response<ValidCardIdResponse> response) {
                if (response.isSuccessful()) {
                    NotificationHelper.showNotification(getContext(), "Valid card ID updated successfully", "The valid card ID has been updated successfully.");
                } else {
                    NotificationHelper.showNotification(getContext(), "Error", "Failed to update valid card ID. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<ValidCardIdResponse> call, Throwable t) {
                NotificationHelper.showNotification(getContext(), "Error", "Network error. Please check your connection.");
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}