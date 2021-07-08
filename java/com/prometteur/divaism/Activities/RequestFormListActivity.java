package com.prometteur.divaism.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.prometteur.divaism.Adapters.FormsListAdapter;
import com.prometteur.divaism.Adapters.MessagerListAdapter;
import com.prometteur.divaism.LocalDB.DatabaseHelper;
import com.prometteur.divaism.PojoModels.MessageResponse;
import com.prometteur.divaism.PojoModels.SavedFormDataPojo;
import com.prometteur.divaism.R;
import com.prometteur.divaism.Retrofit.ApiConfing;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.databinding.ActivityFilledRequestFormBinding;
import com.prometteur.divaism.databinding.ActivityMessageListBinding;
import com.prometteur.divaism.databinding.ActivityRequestFormListBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.prometteur.divaism.Utils.ConstantStrings.stylistId;
import static com.prometteur.divaism.Utils.ConstantStrings.usertype;

public class RequestFormListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RequestFormListActivity";
    ActivityRequestFormListBinding binding;
    Context nContext;
    List<SavedFormDataPojo> responseData=new ArrayList<>();
    DatabaseHelper dbHelper;
    Bundle bundle;
    String userType;
    String STYLISTID;
    boolean sendForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRequestFormListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        nContext=this;
        bundle=getIntent().getExtras();
        if(bundle!=null){
            if(bundle.containsKey(usertype)) {
                userType = bundle.getString(usertype);
                Log.e(TAG, "onCreate: "+userType );
            }
            if (bundle.containsKey(stylistId)){
                STYLISTID=bundle.getString(stylistId);
                Log.e(TAG, "onCreate: "+STYLISTID );
            }
        }
        if (STYLISTID==null){
            sendForm=true;
        }else{
            sendForm=false;
        }
        dbHelper=new DatabaseHelper(nContext);
        binding.fbCreateNewForm.setOnClickListener(this);
        getFormListFromDB();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fbCreateNewForm:
                startActivity(new Intent(nContext,RequestFormActivity.class)
                .putExtra(ConstantStrings.toBeSaved,sendForm)
                .putExtra(usertype,userType)
                .putExtra(stylistId,STYLISTID));
                break;
        }
    }

    private void getFormListFromDB(){
        for (SavedFormDataPojo pojo:dbHelper.getDataFromDatabase()) {
            if(pojo.getUserId().equalsIgnoreCase(ConstantStrings.SavedUserID)){
                responseData.add(pojo);
            }
        }

        intAdapter();
    }

    private void intAdapter(){
        FormsListAdapter adapter=new FormsListAdapter(nContext,responseData,userType,sendForm,STYLISTID);
        binding.recFormsList.setLayoutManager(new LinearLayoutManager(nContext));
        binding.recFormsList.setAdapter(adapter);
    }


}