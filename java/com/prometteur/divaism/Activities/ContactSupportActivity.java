package com.prometteur.divaism.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.prometteur.divaism.PojoModels.CommonResponse;
import com.prometteur.divaism.PojoModels.FormAndReviewResponse;
import com.prometteur.divaism.R;
import com.prometteur.divaism.Retrofit.ApiConfing;
import com.prometteur.divaism.Utils.CommonMethods;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.Utils.LoadingDialog;
import com.prometteur.divaism.databinding.ActivityContactSupportBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactSupportActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityContactSupportBinding binding;
    Context nContext;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityContactSupportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        nContext=this;
        loadingDialog= LoadingDialog.getLoadingDialog(nContext);
        binding.btnSend.setOnClickListener(this);

    }


    private void sendMessageSupportApi(){
        loadingDialog.show();
        ApiConfing.getApiClient().sendSupportMsg(ConstantStrings.SavedUserID,binding.edtComments.getText().toString())
                .enqueue(new Callback<FormAndReviewResponse>() {
                    @Override
                    public void onResponse(Call<FormAndReviewResponse> call, Response<FormAndReviewResponse> response) {
                        FormAndReviewResponse nResponse=response.body();
                        if (nResponse.getStatus()==1){
                            Toast.makeText(nContext, "Message Sent", Toast.LENGTH_SHORT).show();
                            binding.edtComments.setText("");
                        }
                        else if(nResponse.getStatus()==0){
                            Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                            CommonMethods.logoutDueToSession(nContext);
                        }

                        loadingDialog.cancel();
                    }

                    @Override
                    public void onFailure(Call<FormAndReviewResponse> call, Throwable t) {
                        Toast.makeText(nContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingDialog.cancel();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSend:
                sendMessageSupportApi();
                break;
        }
    }
}