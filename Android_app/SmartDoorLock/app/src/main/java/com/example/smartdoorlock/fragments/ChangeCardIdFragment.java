package com.example.smartdoorlock.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smartdoorlock.R;
import com.example.smartdoorlock.models.ValidCardIdResponse;
import com.example.smartdoorlock.services.DoorControlService;
import com.example.smartdoorlock.utils.NotificationHelper;

import java.util.function.Consumer;
import java.util.function.Supplier;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeCardIdFragment extends Fragment {
    private EditText etCurrentCardId, etNewCardId, etConfirmCardId;
    private boolean isCurrentCardIdVisible = false;
    private boolean isNewCardIdVisible = false;
    private boolean isConfirmCardIdVisible = false;
    private Button btnChangeCardId;
    private DoorControlService doorControlService;
    private String cardId;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_card_id, container, false);

        etCurrentCardId = view.findViewById(R.id.etCurrentCardId);
        etNewCardId = view.findViewById(R.id.etNewCardId);
        etConfirmCardId = view.findViewById(R.id.etConfirmCardId);
        btnChangeCardId = view.findViewById(R.id.btnChangeCardId);

        btnChangeCardId.setOnClickListener(v -> changeCardId());

        doorControlService = new DoorControlService();
        getOldCardId();

        // Set up touch listeners for card ID visibility toggles
        setupVisibilityToggle(etCurrentCardId, () -> isCurrentCardIdVisible, b -> isCurrentCardIdVisible = b);
        setupVisibilityToggle(etNewCardId, () -> isNewCardIdVisible, b -> isNewCardIdVisible = b);
        setupVisibilityToggle(etConfirmCardId, () -> isConfirmCardIdVisible, b -> isConfirmCardIdVisible = b);

        // Apply CardIdTextWatcher to all EditText fields
        etCurrentCardId.addTextChangedListener(createCardIdTextWatcher(etCurrentCardId));
        etNewCardId.addTextChangedListener(createCardIdTextWatcher(etNewCardId));
        etConfirmCardId.addTextChangedListener(createCardIdTextWatcher(etConfirmCardId));

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupVisibilityToggle(EditText editText, Supplier<Boolean> isVisible, Consumer<Boolean> setVisible) {
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawablesRelative()[2].getBounds().width())) {
                    boolean newVisibility = !isVisible.get();
                    setVisible.accept(newVisibility);
                    toggleCardIdVisibility(editText, newVisibility);
                    return true;
                }
            }
            return false;
        });
    }

    private void toggleCardIdVisibility(EditText et, boolean isVisible) {
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

    private void changeCardId() {
        String currentCardId = etCurrentCardId.getText().toString().trim();
        String newCardId = etNewCardId.getText().toString().trim();
        String confirmCardId = etConfirmCardId.getText().toString().trim();

        if (currentCardId.isEmpty()) {
            etCurrentCardId.setError("Current card ID is required");
            etCurrentCardId.requestFocus();
            return;
        }

        if (!currentCardId.equals(cardId)) {
            etCurrentCardId.setError("Current card ID is incorrect");
            etCurrentCardId.requestFocus();
            return;
        }

        if (newCardId.isEmpty()) {
            etNewCardId.setError("New card ID is required");
            etNewCardId.requestFocus();
            return;
        }

        if (confirmCardId.isEmpty()) {
            etConfirmCardId.setError("Confirm card ID is required");
            etConfirmCardId.requestFocus();
            return;
        }

        if (!newCardId.equals(confirmCardId)) {
            etConfirmCardId.setError("Card ID does not match");
            etConfirmCardId.requestFocus();
            return;
        }

        // Call API to change the card ID
        doorControlService.updateValidCardId(newCardId, new Callback<ValidCardIdResponse>() {
            @Override
            public void onResponse(Call<ValidCardIdResponse> call, Response<ValidCardIdResponse> response) {
                if (response.isSuccessful()) {
                    gotoHomeFragment();
                    NotificationHelper.showNotification(getContext(), "Card ID updated successfully", "The card ID has been updated successfully.");
                } else {
                    NotificationHelper.showNotification(getContext(), "Error", "Failed to update card ID. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<ValidCardIdResponse> call, Throwable t) {
                NotificationHelper.showNotification(getContext(), "Error", "Network error. Please check your connection.");
            }
        });
    }

    private void getOldCardId() {
        doorControlService.getValidCardId(new Callback<ValidCardIdResponse>() {
            @Override
            public void onResponse(Call<ValidCardIdResponse> call, Response<ValidCardIdResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cardId = response.body().getCardId();
                } else {
                    NotificationHelper.showNotification(getContext(), "Error", "Failed to retrieve current card ID.");
                }
            }

            @Override
            public void onFailure(Call<ValidCardIdResponse> call, Throwable t) {
                NotificationHelper.showNotification(getContext(), "Error", "Network error. Please check your connection.");
            }
        });
    }

    private void gotoHomeFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
    }

    private CardIdTextWatcher createCardIdTextWatcher(EditText editText) {
        return new CardIdTextWatcher(editText);
    }

    private class CardIdTextWatcher implements TextWatcher {
        private boolean isFormatting;
        private int prevLength;
        private EditText editText;

        public CardIdTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!isFormatting) {
                prevLength = s.length();
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!isFormatting) {
                isFormatting = true;
                String formatted = formatCardId(s.toString());
                if (!formatted.equals(s.toString())) {
                    int cursorPosition = start + count;
                    if (formatted.length() > prevLength) {
                        cursorPosition++;
                    }
                    editText.setText(formatted);
                    editText.setSelection(Math.min(cursorPosition, formatted.length()));
                }
                isFormatting = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // No action needed
        }

        private String formatCardId(String cardId) {
            String cleanCardId = cardId.replaceAll("[^0-9A-Fa-f]", "").toUpperCase();
            if (cleanCardId.length() > 8) {
                cleanCardId = cleanCardId.substring(0, 8);
            }
            return cleanCardId.replaceAll("(.{2})(?!$)", "$1 ");
        }
    }
}