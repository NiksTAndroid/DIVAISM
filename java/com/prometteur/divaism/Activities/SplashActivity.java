package com.prometteur.divaism.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.prometteur.divaism.PojoModels.LoginResponse;
import com.prometteur.divaism.R;
import com.prometteur.divaism.Utils.CommonMethods;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.Utils.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static com.prometteur.divaism.Utils.ConstantStrings.USERDETAILS;
import static com.prometteur.divaism.Utils.ConstantStrings.prefName;
import static com.prometteur.divaism.Utils.ConstantStrings.stylist;

public class SplashActivity extends AppCompatActivity {

    String userType;
    Context nContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        nContext=this;
        String lang= Locale.getDefault().toString();
        Log.e("Splash", "onCreate: "+lang );
        if (lang.contains("fr")|| lang.contains("FR")){
            CommonMethods.setLocale(this,lang);
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(Preference.isLogin(nContext)){
                    userType=Preference.getPrefData(nContext,ConstantStrings.usertype);
                    Gson gson=new Gson();
                    String userdata=Preference.getPrefData(nContext,USERDETAILS);
                    ConstantStrings.userDetails=gson.fromJson(userdata,LoginResponse.Result.class);
                    ConstantStrings.SavedUserID=Preference.getPrefData(nContext, ConstantStrings.USERID);
                    ConstantStrings.SavedUserSession=Preference.getPrefData(nContext, ConstantStrings.USERSESSION);
                    startNewActivity();

                }else {

                    Intent intent = new Intent(SplashActivity.this, RoleChooseActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }

    private void startNewActivity(){
        if (userType.equalsIgnoreCase(stylist)){
            Intent intent = new Intent(this, HomePage.class)
                    .putExtra(ConstantStrings.usertype,userType)

                    .putExtra(ConstantStrings.fromHome,false);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(this, HomePage.class)
                    .putExtra(ConstantStrings.usertype,userType).putExtra(ConstantStrings.toBeSaved,true);
            startActivity(intent);
            finish();
        }

    }
}