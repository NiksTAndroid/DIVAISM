package com.prometteur.divaism.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.prometteur.divaism.PojoModels.LoginResponse;

public class Preference {

    public static boolean isLogin(Context context){
        SharedPreferences prefs=context.getSharedPreferences(ConstantStrings.prefName,
                Context.MODE_PRIVATE);
        if(prefs.getBoolean(ConstantStrings.isLogin,false)){
            return true;
        }else {
            return false;
        }
    }

    public static String getPrefData(Context nContext,String Key){
        SharedPreferences prefs=nContext.getSharedPreferences(ConstantStrings.prefName,
                Context.MODE_PRIVATE);
        if(prefs.getString(Key,"")!=null){
            return prefs.getString(Key,"");
        }else {
            return "";
        }

    }

    public static String userDetails(Context context, LoginResponse.Result response, boolean islogin, String userType, String signInType) {

        SharedPreferences prefs = context.getSharedPreferences(ConstantStrings.prefName,
                Context.MODE_PRIVATE);
        if (response == null) {
            return prefs.getString("USERDETAILS", null);
        } else {
            SharedPreferences.Editor edit = prefs.edit();
            Gson gson = new Gson();
            String st = gson.toJson(response);
            edit.putString(ConstantStrings.USERDETAILS, st);
            edit.putString(ConstantStrings.USERID, response.getUserId());
            edit.putString(ConstantStrings.USERSESSION, response.getUserSession());
            edit.putString(ConstantStrings.usertype, userType);
            edit.putString(ConstantStrings.SignInType, signInType);
            edit.putBoolean(ConstantStrings.isLogin,islogin);
            edit.apply();
            return null;
        }

    }

    public static String instaToken(Context context,String token){
        SharedPreferences prefs = context.getSharedPreferences(ConstantStrings.prefName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(ConstantStrings.INSTAGRAMTOKEN,token);
        edit.apply();
        return null;
    }

    public static void clearAllPref(Context context) {
        SharedPreferences preferences =context.getSharedPreferences(ConstantStrings.prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(ConstantStrings.USERDETAILS);
        editor.remove(ConstantStrings.usertype);
        editor.remove(ConstantStrings.USERSESSION);
        editor.remove(ConstantStrings.SignInType);
        editor.remove(ConstantStrings.isLogin);
        editor.remove(ConstantStrings.INSTAGRAMTOKEN);
        editor.commit();
    }
}
