package com.prometteur.divaism.Retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prometteur.divaism.Utils.ConstantStrings;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiConfing {





    /*http://sairoses.com/phpmyadmin/
mydelivery-us
mydelivery@123*/


    private static String userSession = "";
    private static String userID = "";

    public static ApiCalls getApiClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        try {
            userSession = ConstantStrings.SavedUserSession;
            userID = ConstantStrings.SavedUserID;
        }
        catch (Exception e) {
            Log.e("Divaism-Error", e.getMessage());
        }



        Interceptor interceptor2 = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("user_session", userSession)
                        .addHeader("user_id", userID).build();
                return chain.proceed(request);
            }
        };


        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(120, TimeUnit.SECONDS)
                .callTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().setLenient().create();

        return new Retrofit.Builder()
                .baseUrl(ConstantStrings.baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build().create(ApiCalls.class);
    }

}