package com.prometteur.divaism.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookButtonBase;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.prometteur.divaism.PojoModels.LoginResponse;
import com.prometteur.divaism.R;
import com.prometteur.divaism.Retrofit.ApiConfing;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.Utils.InstaAuthenticationDialog;
import com.prometteur.divaism.Utils.LoadingDialog;
import com.prometteur.divaism.Utils.Preference;
import com.prometteur.divaism.databinding.ActivitySignUpBinding;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AUTH;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

import static com.prometteur.divaism.Utils.ConstantStrings.stylist;
import static com.prometteur.divaism.Utils.ConstantStrings.usertype;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, InstaAuthenticationDialog.AuthenticationListener {
    private static final String TAG = "SignUpActivity";
    ActivitySignUpBinding binding;
    Bundle bundle;
    String userType;
    Context nContext;
    GoogleSignInClient mGoogleSignInClient;
    int Google_signin = 101;
    int FB_signin = 102;
    int Insta_signin = 103;
    public static int logintype = 0;
    GoogleSignInAccount account;
    LoginResult accountResult;

    CallbackManager callbackManager;
    Bundle facebookData;
    String signInType;
    String fbID;
    String fbName;
    String instaID;
    String instaName;
    String instaImage;
    InstaAuthenticationDialog authenticationDialog;
    Dialog loadingDialog;
    String prefSavedUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        nContext = this;
        loadingDialog = LoadingDialog.getLoadingDialog(nContext);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            userType = bundle.getString(usertype);
        }
        if (userType.equalsIgnoreCase(stylist)) {
            binding.btnGmail.setVisibility(View.GONE);
        }
        if (Preference.getPrefData(nContext, ConstantStrings.USERID) != null) {
            prefSavedUserId = Preference.getPrefData(nContext, ConstantStrings.USERID);
            ConstantStrings.SavedUserID=Preference.getPrefData(nContext, ConstantStrings.USERID);
            Log.e(TAG, "onCreate: " + prefSavedUserId);
        }
        binding.btnFaceBook.setOnClickListener(this);
        binding.btnInstagram.setOnClickListener(this);
        binding.btnGmail.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnFaceBook:
                logintype = 1;
                loginWithFaceBook();

                break;
            case R.id.btnInstagram:
                logintype = 2;
                loginWithInstagram();

                break;
            case R.id.btnGmail:
                logintype = 3;
                loginWithGmail();

                break;

        }
    }

    private void loginWithGmail() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Google_signin);

    }

    private void loginWithInstagram() {

        authenticationDialog = new InstaAuthenticationDialog(this, this);
        authenticationDialog.setCancelable(true);
        authenticationDialog.show();

        //signInAPICall();
    }

    private void loginWithFaceBook() {
        /*import com.facebook.FacebookSdk;
        import com.facebook.appevents.AppEventsLogger;*/

        callbackManager = CallbackManager.Factory.create();
        LoginButton button=new LoginButton(nContext);
        button.performClick();
        button.setReadPermissions(Arrays.asList(
                "public_profile", "email"));

        button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();

                // Facebook Email address
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {


                                try {
                                    fbName = object.getString("name");

                                    fbID=object.getString("id");
                                    signInAPICall();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                facebookData = new Bundle();
                facebookData.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(facebookData);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Toast.makeText(nContext, "FB Login canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "onError: " + error.getMessage());
            }
        });

        //startNewActivity();

    }

    private void startNewActivity() {
        Intent intent;
        if (userType.equalsIgnoreCase(stylist)) {
            if (prefSavedUserId.isEmpty() && !prefSavedUserId.equalsIgnoreCase(ConstantStrings.userDetails.getUserId())) {
                    intent = new Intent(this, StylistDetailsActivity.class)
                            .putExtra(ConstantStrings.usertype, userType)
                            .putExtra(ConstantStrings.fromHome, false);

                    startActivity(intent);
                    finish();
                }
             else {
                if(!prefSavedUserId.isEmpty() && !prefSavedUserId.equalsIgnoreCase(ConstantStrings.userDetails.getUserId())) {
                    intent = new Intent(this, StylistDetailsActivity.class)
                            .putExtra(ConstantStrings.usertype, userType)
                            .putExtra(ConstantStrings.fromHome, false);

                    startActivity(intent);
                    finish();
                }else{
                    intent = new Intent(this, HomePage.class)
                            .putExtra(ConstantStrings.usertype, userType)
                            .putExtra(ConstantStrings.fromHome, false);

                    startActivity(intent);
                    finish();
                }
            }

        } else {
            if (prefSavedUserId.isEmpty() && !prefSavedUserId.equalsIgnoreCase(ConstantStrings.userDetails.getUserId())) {
                intent = new Intent(this, RequestFormActivity.class)
                        .putExtra(ConstantStrings.usertype, userType)
                        .putExtra(ConstantStrings.NewUser, true)
                        .putExtra(ConstantStrings.toBeSaved, true);
                startActivity(intent);
                finish();
            } else {
                if(!prefSavedUserId.isEmpty() && !prefSavedUserId.equalsIgnoreCase(ConstantStrings.userDetails.getUserId())) {
                    intent = new Intent(this, RequestFormActivity.class)
                            .putExtra(ConstantStrings.usertype, userType)
                            .putExtra(ConstantStrings.NewUser, true)
                            .putExtra(ConstantStrings.toBeSaved, true);
                    startActivity(intent);
                    finish();
                }else{
                    intent = new Intent(this, HomePage.class)
                            .putExtra(ConstantStrings.usertype, userType)
                            .putExtra(ConstantStrings.fromHome, false);
                    startActivity(intent);
                    finish();
                }
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Google_signin) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }


    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            signInAPICall();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void signInAPICall() {
        loadingDialog.show();
        Call<LoginResponse> loginCall = null;
        String userVerified = "0";
        if (userType.equalsIgnoreCase("1")) {
            userVerified = "1";
        } else {
            userVerified = "0";
        }
        if (userType.equalsIgnoreCase("1")) {
            if (logintype == 1) {
                signInType = ConstantStrings.isFaceBook;

                loginCall=ApiConfing.getApiClient().faceBookLogin(fbID,"",fbName,"","",userType,userVerified);
            } else if (logintype == 2) {
                signInType = ConstantStrings.isInstagram;

                loginCall=ApiConfing.getApiClient().InstagramLogin(instaID,instaImage,instaName,"","",userType,"",userVerified);
            } else {
                signInType = ConstantStrings.isGoogle;
                String id = "";
                String photoUrl = "";
                String DisplayName = "";
                String email = "";

                if (account.getId() != null) {
                    id = account.getId();
                }
                if (account.getPhotoUrl() != null) {
                    photoUrl = account.getPhotoUrl().toString();
                }
                if (account.getDisplayName() != null) {
                    DisplayName = account.getDisplayName();
                }
                if (account.getEmail() != null) {
                    email = account.getEmail();
                }

                loginCall = ApiConfing.getApiClient().GoogleLogin(id, photoUrl, DisplayName,
                        "", email, userType, userVerified);
            }
        } else {
            if (logintype == 1) {
                signInType = ConstantStrings.isFaceBook;
                loginCall=ApiConfing.getApiClient().faceBookLogin(fbID,"",fbName,"","",userType,userVerified);
            } else if (logintype == 2) {
                signInType = ConstantStrings.isInstagram;
                /*loginCall = ApiConfing.getApiClient().InstagramLogin("123456789", "", "STYLISTNIKS",
                        "", "insta@in.com", "2", "", "1");*/
                loginCall=ApiConfing.getApiClient().InstagramLogin(instaID,instaImage,instaName,"","",userType,"",userVerified);
            }
        }
        if (loginCall != null) {
            loginCall.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.body().getStatus() == 1) {
                        LoginResponse loginResponse = response.body();

                        ConstantStrings.userDetails = loginResponse.getResult().get(0);
                        Log.e(TAG, "onResponse: " + response.body());

                        Preference.userDetails(nContext, response.body().getResult().get(0), true, userType, signInType);
                        ConstantStrings.SavedUserID=loginResponse.getResult().get(0).getUserId();
                        startNewActivity();
                    }else{
                        Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.cancel();
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    Toast.makeText(nContext, "failure " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.cancel();
                }
            });
        }

    }

    private String token = null;

    @Override
    public void onTokenReceived(String auth_token) {
        if (auth_token == null)
            return;
        token = auth_token;
        getUserInfoByAccessToken(auth_token);
    }


    private void getUserInfoByAccessToken(String token) {
        new RequestInstagramAPI().execute();
    }


    private class RequestInstagramAPI extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... params) {
            loadingDialog.show();
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(getResources().getString(R.string.get_user_info_url) + token);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                loadingDialog.cancel();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    if (jsonData.has("id")) {
                        instaID=jsonData.getString("id");
                        instaName=jsonData.getString("username");
                        instaImage= jsonData.getString("profile_picture");
                        signInAPICall();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingDialog.cancel();
                }
            } else {
                loadingDialog.cancel();
                Toast toast = Toast.makeText(getApplicationContext(),"Login error!",Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    /*private class AuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(CALLBACKURL)) {
                System.out.println(url);
                String parts[] = url.split("=");
                request_token = parts[1];  //This is your request token.
                InstagramLoginDialog.this.dismiss();
                return true;
            }
            return false;
        }
    }*/


}




   /* $ keytool -exportcert -alias DIVAISM -keystore /Users/nikhiltekawade/Desktop | openssl sha1 -binary |openssl base64

        Nikhils-macbook-pro-2:Divaism nikhiltekawade$ keytool -exportcert -alias Divaism -keystore /Users/nikhiltekawade/Desktop | openssl sha1 -binary | openssl base6
*/