package com.example.smartdoorlock.retrofit;

import com.example.smartdoorlock.models.AccessLog;
import com.example.smartdoorlock.models.DefaultKeyRequest;
import com.example.smartdoorlock.models.DefaultKeyResponse;
import com.example.smartdoorlock.models.DoorControlRequest;
import com.example.smartdoorlock.models.ValidCardIdRequest;
import com.example.smartdoorlock.models.ValidCardIdResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {

//    @POST("/api/door/open")
//    Call<Void> openDoor();
//
//    @POST("/api/door/close")
//    Call<Void> closeDoor();

    @POST("/api/control-door")
//    Call<String> controlDoor(@Body DoorControlRequest request);
    Call<ResponseBody> controlDoor(@Body DoorControlRequest request);

    @GET("/api/logs")
    Call<List<AccessLog>> getAccessLogs();

    @PUT("/api/default_key")
    Call<DefaultKeyResponse> updateDefaultKey(@Body DefaultKeyRequest request);

    @PUT("/api/valid_card_id")
    Call<ValidCardIdResponse> updateValidCardId(@Body ValidCardIdRequest request);

    @GET("/api/default_key")
    Call<DefaultKeyResponse> getDefaultKey();

    @GET("/api/valid_card_id")
    Call<ValidCardIdResponse> getValidCardId();

//    @GET("logs")
//    Call<List<AccessLog>> getAllLogs();
//
//    @GET("logs/{id}")
//    Call<AccessLog> getLogById(@Path("id") Long id);
//
//    @POST("logs")
//    Call<AccessLog> addLog(@Body AccessLog log);
//
//    @DELETE("logs/{id}")
//    Call<Void> deleteLog(@Path("id") Long id);
//
//    @PUT("default_key")
//    Call<DefaultKeyResponse> updateDefaultKey(@Body DefaultKeyRequest request);
//
//    @PUT("valid_card_id")
//    Call<ValidCardIdResponse> updateValidCardId(@Body ValidCardIdRequest request);
//
//    @GET("default_key")
//    Call<DefaultKeyResponse> getDefaultKey();
//
//    @GET("valid_card_id")
//    Call<ValidCardIdResponse> getValidCardId();
//
//    @POST("open_door")
//    Call<Void> openDoor();
//
//    @POST("close_door")
//    Call<Void> closeDoor();
}