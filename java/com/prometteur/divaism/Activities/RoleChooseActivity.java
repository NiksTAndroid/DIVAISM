package com.prometteur.divaism.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.prometteur.divaism.R;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.databinding.ActivityRoleChooseBinding;

public class RoleChooseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RoleChooseActivity";
    ActivityRoleChooseBinding binding;
    Context nContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoleChooseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        nContext=this;
        binding.btnClient.setOnClickListener(this);
        binding.btnStylist.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btnClient:
                ConstantStrings.usersavedtype=ConstantStrings.client;
                 intent=new Intent(this,SignUpActivity.class);
                intent.putExtra(ConstantStrings.usertype,ConstantStrings.client);
                startActivity(intent);
                finish();
                break;
            case R.id.btnStylist:
                ConstantStrings.usersavedtype=ConstantStrings.stylist;
                 intent=new Intent(this,SignUpActivity.class);
                intent.putExtra(ConstantStrings.usertype,ConstantStrings.stylist);
                startActivity(intent);
                finish();
                break;
        }
    }
}