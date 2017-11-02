package com.example.im.googlesign;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
        Databasehelper dbhelp= new Databasehelper(this);
private static final String TAG = MainActivity.class.getSimpleName();
private static final int RC_SIGN_IN = 007;

        CallbackManager callbackManager;

private GoogleApiClient mGoogleApiClient;
private ProgressDialog mProgressDialog;
private Button GmailSignInButton , gmail_sigoutbutton;
private TextView LoginStatus,Email,UserName;
private ImageView UserPic;
        Button bnfb, bnfbl;

@Override
protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        GmailSignInButton = (Button) findViewById(R.id.gmail_signinbutton);
        gmail_sigoutbutton = (Button) findViewById(R.id.gmail_sigoutbutton);
        LoginStatus = (TextView) findViewById(R.id.login_status);
        UserName=(TextView)findViewById(R.id.UserName);
        Email=(TextView)findViewById(R.id.UserEmail);
        UserPic=(ImageView)findViewById(R.id.ProfilePic);

        bnfb=(Button)findViewById(R.id.bnfb);
        bnfb.setOnClickListener(this);
        bnfbl=(Button)findViewById(R.id.bnfbl);
        bnfbl.setOnClickListener(this);

        gmail_sigoutbutton.setOnClickListener(this);

        GmailSignInButton.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
        .enableAutoManage(this, this)
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
        .build();


        // fb begins
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {

//                        Status_view.setText("Login Success " + loginResult.getAccessToken().getUserId().toString() + "\n" + loginResult.getAccessToken().getToken().toString());

                                setFacebookData(loginResult);
                        }

                        @Override
                        public void onCancel() {
                                LoginStatus.setText("Login Cancelled");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                                LoginStatus.setText("An Error Occured");
                        }
                });


}

private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        }

private void signOut() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                                updateUI(false);
                        }
                });

}




@Override
public void onClick(View v) {
        int id = v.getId();

        switch (id) {
                case R.id.gmail_signinbutton:
                        signIn();
                        break;
                case R.id.gmail_sigoutbutton:
                {
                        dbhelp.delete();
                  signOut();

                        break;
                 }
                case R.id.bnfb: {
                        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends","email"));

                        break;
                }                case R.id.bnfbl: {
                        LoginManager.getInstance().logOut();
                        LoginStatus.setText("Status");
                        UserPic.setVisibility(View.GONE);
                        bnfbl.setVisibility(View.GONE);
                        GmailSignInButton.setVisibility(View.VISIBLE);
                        bnfb.setVisibility(View.VISIBLE);
                        dbhelp.delete();
                        break;
                }
        }
        }


private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
        // Signed in successfully, show authenticated UI.
        GoogleSignInAccount acct = result.getSignInAccount();
        Log.e(TAG, "display name: " + acct.getDisplayName());

        String personName = acct.getDisplayName();
        String email = acct.getEmail();

                String Pic;
     UserName.setText(personName);
        Email.setText(email);

if(acct.getPhotoUrl()==null) {
        UserPic.setImageResource(R.drawable.noic1);
}
else
        {
                Pic = acct.getPhotoUrl().toString();
                Glide.with(getApplicationContext()).load(Pic)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(UserPic);
}
                Log.e(TAG, "Name: " + personName + ", email: " + email);

                String time = DateFormat.getDateTimeInstance().format(new Date());
                dbhelp.insert(personName,email,"Google",time);
        updateUI(true);
        } else {
        // Signed out, show unauthenticated UI.
        updateUI(false);
        }
        }

private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
        GmailSignInButton.setVisibility(View.GONE);
        LoginStatus.setText("Login Success");
                gmail_sigoutbutton.setVisibility(View.VISIBLE);
                UserPic.setVisibility(View.VISIBLE);
                 bnfb.setVisibility(View.GONE);
                bnfbl.setVisibility(View.GONE);

        } else {

        GmailSignInButton.setVisibility(View.VISIBLE);
                gmail_sigoutbutton.setVisibility(View.GONE);
                LoginStatus.setText("Status");
                        Email.setText("");

                UserName.setText("");
UserPic.setVisibility(View.GONE);
                bnfb.setVisibility(View.VISIBLE);
        }
        }
@Override
public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        }



        private void setFacebookData(LoginResult loginResult)
        {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                        // Application code
                                        try {
                                                Log.i("Response",response.toString());
                                                GmailSignInButton.setVisibility(View.GONE);
                                                gmail_sigoutbutton.setVisibility(View.GONE);
                                                bnfb.setVisibility(View.GONE);
                                                bnfbl.setVisibility(View.VISIBLE);
                                                String email = response.getJSONObject().getString("email");
                                                Profile profile = Profile.getCurrentProfile();
                                                String id = profile.getId();
                                                String link = profile.getLinkUri().toString();
                                                String name = profile.getName();
                                                String time = DateFormat.getDateTimeInstance().format(new Date());
                                                dbhelp.insert(name,email,"Facebook",time);
                                                LoginStatus.setText("Login Success : " + name + "\n"+ email);
                                                UserPic.setVisibility(View.VISIBLE);
                                                Log.i("Link",link);
                                                if (Profile.getCurrentProfile()!=null)
                                                {
                                                        if(Profile.getCurrentProfile().getProfilePictureUri(200,200)==null) {
                                                                UserPic.setImageResource(R.drawable.noic1);
                                                        }
                                                        else {
                                                                Glide.with(getApplicationContext()).load(Profile.getCurrentProfile().getProfilePictureUri(200, 200))
                                                                        .thumbnail(0.5f)
                                                                        .crossFade()
                                                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                                        .into(UserPic);
                                                        }
                                                }



                                        } catch (JSONException e) {
                                                e.printStackTrace();
                                        }
                                }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,first_name,last_name,gender");
                request.setParameters(parameters);
                request.executeAsync();
        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);

                        if(requestCode == RC_SIGN_IN) {
                                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                                handleSignInResult(result);
                        }
                if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
                        return;
                }
        }

}



