package com.example.smartdoorlock.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smartdoorlock.R;
import com.example.smartdoorlock.models.DefaultKeyResponse;
import com.example.smartdoorlock.services.DoorControlService;
import com.example.smartdoorlock.utils.NotificationHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {
    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private boolean isCurrentPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private Button btnChangePassword;
    private DoorControlService doorControlService;
    private String keyValue;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(v -> changePassword());

        doorControlService = new DoorControlService();
        getOldPassword();

        // Set up touch listeners for password visibility toggles
        etCurrentPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etCurrentPassword.getRight() - etCurrentPassword.getCompoundDrawablesRelative()[2].getBounds().width())) {
                    isCurrentPasswordVisible = !isCurrentPasswordVisible;
                    togglePasswordVisibility(etCurrentPassword, isCurrentPasswordVisible);
                    return true;
                }
            }
            return false;
        });

        etNewPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etNewPassword.getRight() - etNewPassword.getCompoundDrawablesRelative()[2].getBounds().width())) {
                    isNewPasswordVisible = !isNewPasswordVisible;
                    togglePasswordVisibility(etNewPassword, isNewPasswordVisible);
                    return true;
                }
            }
            return false;
        });

        etConfirmPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etConfirmPassword.getRight() - etConfirmPassword.getCompoundDrawablesRelative()[2].getBounds().width())) {
                    isConfirmPasswordVisible = !isConfirmPasswordVisible;
                    togglePasswordVisibility(etConfirmPassword, isConfirmPasswordVisible);
                    return true;
                }
            }
            return false;
        });

        return view;
    }

    private void togglePasswordVisibility(EditText et, boolean isVisible) {
        if (isVisible) {
            et.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            Drawable[] drawables = et.getCompoundDrawablesRelative();
            Drawable newVisibilityIcon = ContextCompat.getDrawable(getContext(), R.drawable.baseline_visibility_off_24);
            et.setCompoundDrawablesRelativeWithIntrinsicBounds(drawables[0], drawables[1], newVisibilityIcon, drawables[3]);
        } else {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            Drawable[] drawables = et.getCompoundDrawablesRelative();
            Drawable newVisibilityIcon = ContextCompat.getDrawable(getContext(), R.drawable.baseline_visibility_24);
            et.setCompoundDrawablesRelativeWithIntrinsicBounds(drawables[0], drawables[1], newVisibilityIcon, drawables[3]);
        }
        et.setSelection(et.length());
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (currentPassword.isEmpty()) {
            etCurrentPassword.setError("Current password is required");
            etCurrentPassword.requestFocus();
            return;
        }

        if (!currentPassword.equals(keyValue)) {
            etCurrentPassword.setError("Current password is incorrect");
            etCurrentPassword.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Confirm password is required");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Password does not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Call API to change the password
        doorControlService.updateDefaultKey(newPassword, new Callback<DefaultKeyResponse>() {
            @Override
            public void onResponse(Call<DefaultKeyResponse> call, Response<DefaultKeyResponse> response) {
                if (response.isSuccessful()) {
                    gotoHomeFragment();
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

    private void getOldPassword() {
        doorControlService.getDefaultKey(new Callback<DefaultKeyResponse>() {
            @Override
            public void onResponse(Call<DefaultKeyResponse> call, Response<DefaultKeyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    keyValue = response.body().getKeyValue();
                } else {
                    NotificationHelper.showNotification(getContext(), "Error", "Failed to retrieve current password.");
                }
            }

            @Override
            public void onFailure(Call<DefaultKeyResponse> call, Throwable t) {
                NotificationHelper.showNotification(getContext(), "Error", "Network error. Please check your connection.");
            }
        });
    }

    private void gotoHomeFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
    }
}