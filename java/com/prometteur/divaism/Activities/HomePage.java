package com.prometteur.divaism.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.prometteur.divaism.Adapters.StylistListAdapter;
import com.prometteur.divaism.PojoModels.CommonResponse;
import com.prometteur.divaism.PojoModels.FormAndReviewResponse;
import com.prometteur.divaism.PojoModels.StylistListResponse;
import com.prometteur.divaism.R;
import com.prometteur.divaism.Retrofit.ApiConfing;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.Utils.CustomEditText;
import com.prometteur.divaism.Utils.LoadingDialog;
import com.prometteur.divaism.Utils.Preference;
import com.prometteur.divaism.databinding.ActivityHomePageBinding;
import com.prometteur.divaism.databinding.ActivityStylistDetailsBinding;
import com.prometteur.divaism.databinding.DialogDeleteAccountBinding;
import com.prometteur.divaism.databinding.DialogEntertextBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.prometteur.divaism.Utils.ConstantStrings.client;
import static com.prometteur.divaism.Utils.ConstantStrings.fromHome;
import static com.prometteur.divaism.Utils.ConstantStrings.usertype;


public class HomePage extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HomePage";
    ActivityHomePageBinding binding;
    Context nContext;
    Bundle bundle;
    String userType;
    List<StylistListResponse.Result> stylistList;
    List<StylistListResponse.Result> searchList;
    StylistListAdapter adapter;
    Dialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        nContext = this;
        loadingDialog = LoadingDialog.getLoadingDialog(nContext);
        bundle = getIntent().getExtras();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (bundle != null) {
            userType = bundle.getString(usertype);
            Log.e(TAG, "onCreate: " + userType);
        } else {
            userType = ConstantStrings.usersavedtype;
        }
        if (userType == null) {
            if(ConstantStrings.userDetails.getUserType()!=null) {
                userType = ConstantStrings.userDetails.getUserType();
            }
        }

        stylistListApiCall();
        searchList = new ArrayList<>();
        binding.edtSearch.setDrawableClickListener(new CustomEditText.DrawableClickListener() {


            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:
                        //Do something here
                        break;
                    case RIGHT:
                        binding.edtSearch.setText("");
                        //Do something here
                        break;
                    default:
                        break;
                }
            }

        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchList.clear();
                if(stylistList!=null) {
                    for (StylistListResponse.Result result : stylistList) {
                        if (result.getUserName().contains(charSequence) || result.getUserHashtag().contains(charSequence)) {
                            searchList.add(result);
                            initAdapter(searchList);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.ivMessage.setOnClickListener(this);
        binding.ivProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivMessage:
                startActivity(new Intent(nContext, MessageListActivity.class).putExtra(ConstantStrings.usertype, userType));
                break;

            case R.id.ivProfile:
                showMenu();
                break;
        }
    }

    private void showMenu() {
        PopupMenu popup = new PopupMenu(this, binding.ivProfile);

        if (userType.equalsIgnoreCase(client)) {
            popup.getMenuInflater().inflate(R.menu.homemenu, popup.getMenu());
        } else {
            popup.getMenuInflater().inflate(R.menu.stylistmenu, popup.getMenu());
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.myProfile:

                        intent = new Intent(nContext, StylistDetailsActivity.class)
                                .putExtra(usertype, userType)
                                .putExtra(fromHome, true);
                        startActivity(intent);
                        finish();


                        break;
                    case R.id.RequestForm:
                        intent = new Intent(nContext, RequestFormActivity.class)
                                .putExtra(ConstantStrings.toSend,false)
                                .putExtra(ConstantStrings.usertype, userType);
                        startActivity(intent);
                        break;
                    case R.id.logOut:

                        if (Preference.getPrefData(nContext, ConstantStrings.SignInType).equalsIgnoreCase(ConstantStrings.isFaceBook)) {

                        } else if (Preference.getPrefData(nContext, ConstantStrings.SignInType).equalsIgnoreCase(ConstantStrings.isInstagram)) {

                        } else if (Preference.getPrefData(nContext, ConstantStrings.SignInType).equalsIgnoreCase(ConstantStrings.isGoogle)) {
                            GoogleSignInOptions gso = new GoogleSignInOptions.
                                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                                    build();
                            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(nContext, gso);
                            googleSignInClient.signOut();
                        }
                        Preference.clearAllPref(nContext);
                        intent = new Intent(nContext, RoleChooseActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        break;
                    /*case R.id.BankAccount:

                        break;*/
                    case R.id.CloseAccount:
                            showDeleteAccountDialog();
                        break;
                    case R.id.ContactSupport:
                        intent = new Intent(nContext, ContactSupportActivity.class)
                                .putExtra(ConstantStrings.usertype, userType);
                        startActivity(intent);
                        break;

                }
                return true;
            }
        });
        popup.show();

    }

    private void stylistListApiCall() {
        loadingDialog.show();
        ApiConfing.getApiClient().getAllStylistList("2").enqueue(new Callback<StylistListResponse>() {
            @Override
            public void onResponse(Call<StylistListResponse> call, Response<StylistListResponse> response) {
                stylistList = new ArrayList<>();
                StylistListResponse listResponse = response.body();
                if (listResponse != null && listResponse.getStatus() == 1) {
                    /*for (StylistListResponse.Result result : listResponse.getResult())
                        if (!result.getUserId().equalsIgnoreCase(ConstantStrings.userDetails.getUserId())) {
                            stylistList.add(result);
                            initAdapter(stylistList);
                        }*/
                    stylistList.addAll(listResponse.getResult());
                    initAdapter(stylistList);
                }else if(listResponse.getStatus()==0){
                    Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                }
                loadingDialog.cancel();
            }

            @Override
            public void onFailure(Call<StylistListResponse> call, Throwable t) {
                Toast.makeText(nContext, "Error", Toast.LENGTH_SHORT).show();
                loadingDialog.cancel();
            }
        });
    }

    private void initAdapter(List<StylistListResponse.Result> List) {

        adapter = new StylistListAdapter(nContext, userType, List);
        binding.recycleStylistList.setLayoutManager(new LinearLayoutManager(nContext));
        binding.recycleStylistList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void showDeleteAccountDialog() {
        final Dialog dialog = new Dialog(nContext);
        final DialogDeleteAccountBinding dBinding = DialogDeleteAccountBinding.inflate(getLayoutInflater());
        dialog.setContentView(dBinding.getRoot());
        dBinding.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteApiCall();
                dialog.cancel();
            }
        });
        dBinding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();

    }

    private void deleteApiCall() {
        loadingDialog.show();
        ApiConfing.getApiClient().deleteUser(ConstantStrings.SavedUserID)
                .enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                        CommonResponse nResponse = response.body();
                        if (nResponse.getStatus() == 1) {
                            loadingDialog.cancel();
                            Toast.makeText(nContext, nContext.getResources().getString(R.string.DeleteSuccessfull), Toast.LENGTH_SHORT).show();
                            if (Preference.getPrefData(nContext, ConstantStrings.SignInType).equalsIgnoreCase(ConstantStrings.isFaceBook)) {

                            } else if (Preference.getPrefData(nContext, ConstantStrings.SignInType).equalsIgnoreCase(ConstantStrings.isInstagram)) {

                            } else if (Preference.getPrefData(nContext, ConstantStrings.SignInType).equalsIgnoreCase(ConstantStrings.isGoogle)) {
                                GoogleSignInOptions gso = new GoogleSignInOptions.
                                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                                        build();
                                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(nContext, gso);
                                googleSignInClient.signOut();
                            }
                            Preference.clearAllPref(nContext);
                            Intent intent = new Intent(nContext, RoleChooseActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }else if(nResponse.getStatus()==0){
                            Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonResponse> call, Throwable t) {
                        loadingDialog.cancel();
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }


}