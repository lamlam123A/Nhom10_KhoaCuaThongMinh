package com.example.smartdoorlock.services;

import com.example.smartdoorlock.models.AccessLog;
import com.example.smartdoorlock.models.DefaultKeyRequest;
import com.example.smartdoorlock.models.DefaultKeyResponse;
import com.example.smartdoorlock.models.DoorControlRequest;
import com.example.smartdoorlock.models.ValidCardIdRequest;
import com.example.smartdoorlock.models.ValidCardIdResponse;
import com.example.smartdoorlock.retrofit.ApiService;
import com.example.smartdoorlock.retrofit.RetrofitClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class DoorControlService {
    private ApiService apiService;

    public DoorControlService() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public void getAccessLogs(Callback<List<AccessLog>> callback) {
        Call<List<AccessLog>> call = apiService.getAccessLogs();
        call.enqueue(callback);
    }

    public void updateDefaultKey(String newKey, Callback<DefaultKeyResponse> callback) {
        DefaultKeyRequest request = new DefaultKeyRequest(1, newKey);
        Call<DefaultKeyResponse> call = apiService.updateDefaultKey(request);
        call.enqueue(callback);
    }

    public void updateValidCardId(String newCardId, Callback<ValidCardIdResponse> callback) {
        ValidCardIdRequest request = new ValidCardIdRequest(newCardId);
        Call<ValidCardIdResponse> call = apiService.updateValidCardId(request);
        call.enqueue(callback);
    }

    public void getDefaultKey(Callback<DefaultKeyResponse> callback) {
        Call<DefaultKeyResponse> call = apiService.getDefaultKey();
        call.enqueue(callback);
    }

    public void getValidCardId(Callback<ValidCardIdResponse> callback) {
        Call<ValidCardIdResponse> call = apiService.getValidCardId();
        call.enqueue(callback);
    }

    public void controlDoor(boolean isOpen, Callback<ResponseBody> callback) {
        DoorControlRequest request = new DoorControlRequest();
        request.setOpen(isOpen);
        Call<ResponseBody> call = apiService.controlDoor(request);
        call.enqueue(callback);
    }
    
}