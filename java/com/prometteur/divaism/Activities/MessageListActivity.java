package com.prometteur.divaism.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.prometteur.divaism.Adapters.MessagerListAdapter;
import com.prometteur.divaism.Adapters.StylistListAdapter;
import com.prometteur.divaism.PojoModels.MessageResponse;
import com.prometteur.divaism.R;
import com.prometteur.divaism.Retrofit.ApiConfing;
import com.prometteur.divaism.Utils.CommonMethods;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.Utils.LoadingDialog;
import com.prometteur.divaism.databinding.ActivityMessageListBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.prometteur.divaism.Utils.ConstantStrings.usertype;

public class MessageListActivity extends AppCompatActivity {

    ActivityMessageListBinding binding;
    Context nContext;
    Bundle bundle;
    String userType;
    List<MessageResponse.Result> mResponse;
    Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMessageListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        nContext=this;
        loadingDialog=LoadingDialog.getLoadingDialog(nContext);
        bundle=getIntent().getExtras();
        if(bundle!=null){
            userType=bundle.getString(usertype);
        }else{
            userType= ConstantStrings.usersavedtype;
        }
        messageApiCall();


    }


    private void messageApiCall(){

        loadingDialog.show();
        if(ConstantStrings.userDetails!=null && ConstantStrings.userDetails.getUserId()==null || ConstantStrings.SavedUserID==null) {
            Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
            loadingDialog.cancel();
            return;
        }
        ApiConfing.getApiClient().getMessageList(ConstantStrings.SavedUserID,userType)
                .enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {

                        MessageResponse nResponse=response.body();
                        mResponse=new ArrayList<>();
                        if(nResponse.getStatus()==1) {
                            if (response.body() != null) {
                                if (response.isSuccessful() && nResponse.getStatus() == 1) {
                                    mResponse.addAll(nResponse.getResult());
                                    initAdapter();

                                }
                            } else {
                                Toast.makeText(nContext, "No data Found", Toast.LENGTH_SHORT).show();
                            }
                        }else if(nResponse.getStatus()==0){
                            Toast.makeText(nContext, "No data Found", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                            CommonMethods.logoutDueToSession(nContext);
                        }

                        loadingDialog.cancel();
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        Log.e("Message", "onFailure: "+t.getMessage() );
                        loadingDialog.cancel();
                    }
                });
    }

    private void initAdapter() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Collections.sort(mResponse, new Comparator<MessageResponse.Result>() {
            @Override
            public int compare(MessageResponse.Result result, MessageResponse.Result t1) {
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = dateFormat.parse(result.getReqCreateDate());
                    date2 = dateFormat.parse(t1.getReqCreateDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return (date1.getTime() > date2.getTime() ? -1 : 1);     //descending
                //return (date1.getTime() > date2.getTime() ? 1 : -1);
            }
        });
        MessagerListAdapter adapter=new MessagerListAdapter(nContext,userType,mResponse);
        binding.recMessagerList.setLayoutManager(new LinearLayoutManager(nContext));
        binding.recMessagerList.setAdapter(adapter);
    }


}