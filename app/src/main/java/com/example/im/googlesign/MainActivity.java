package com.example.im.googlesign;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import static java.lang.System.exit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
        Databasehelper dbhelp= new Databasehelper(this);
    Contact c=new Contact();
private static final String TAG = MainActivity.class.getSimpleName();
private static final int RC_SIGN_IN = 007;
     CallbackManager callbackManager;
private GoogleApiClient mGoogleApiClient;
private ProgressDialog mProgressDialog;
private Button gmailSignInButton , gmail_Sigoutbutton;
private TextView loginStatus,email,userName;
private ImageView userPic;
        Button fb_LoginButton, fb_LogoutButton;
int loginApp=0;
    @Override
protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this.getApplicationContext());


       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//checks network state
        isNetworkAvailable();
        // check login in DB
        //// UserData userData = dbheelper.checkLogin();
        // if existing login
        // goto info
       // gotoInfoscreen()
        //else show login
        //Read mvc and model



// initialize login UI

            gmailSignInButton = (Button) findViewById(R.id.gmail_signinbutton);
            gmail_Sigoutbutton = (Button) findViewById(R.id.gmail_Sigoutbutton);
            loginStatus = (TextView) findViewById(R.id.login_status);
            userName = (TextView) findViewById(R.id.UserName);
            email = (TextView) findViewById(R.id.UserEmail);
            userPic = (ImageView) findViewById(R.id.ProfilePic);

            fb_LoginButton = (Button) findViewById(R.id.fb_LoginButton);
            fb_LoginButton.setOnClickListener(this);
            fb_LogoutButton = (Button) findViewById(R.id.fb_LogoutButton);
            fb_LogoutButton.setOnClickListener(this);
//applying onclick listener on the google signin and sign out buttons

            gmail_Sigoutbutton.setOnClickListener(this);



            gmailSignInButton.setOnClickListener(this);
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this).addOnConnectionFailedListener(this)
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
                            loginStatus.setText(" FB Login Cancelled");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            loginStatus.setText(exception.toString());
                       }

                    });
        c=dbhelp.checkLogin();
        if(c.getApp().equals("Google"))
        {
            dbhelp.delete();
            signIn();

        }
        else if(c.getApp().equals("Facebook"))
        {
            dbhelp.delete();
            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends","email"));

        }


}


//google sign in function
private void signIn() {
boolean connected=isNetworkAvailable();
    if (connected == true) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    else {
  Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
    }
}//sign out  function for google
private void signOut() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                                updateUI(false);
                        }
                });

}



//on click function set the view according to the button being pressed
@Override
public void onClick(View v) {
        int id = v.getId();

        switch (id) {
                case R.id.gmail_signinbutton:
                        signIn();
                        break;
                case R.id.gmail_Sigoutbutton:
                {
                        dbhelp.delete();
                  signOut();

                        break;
                 }
                case R.id.fb_LoginButton: {
                        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends","email"));

                        break;
                }                case R.id.fb_LogoutButton: {
                        LoginManager.getInstance().logOut();
                        loginStatus.setText("Status");
                        userPic.setVisibility(View.GONE);
                        fb_LogoutButton.setVisibility(View.GONE);
                        gmailSignInButton.setVisibility(View.VISIBLE);
                        fb_LoginButton.setVisibility(View.VISIBLE);
                        dbhelp.delete();
                        break;
                }
        }
        }
    ConnectionResult connectionResult;
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
//        gmailSignInButton.setEnabled(false);
                Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();
//          loginStatus.setText(connectionResult.toString());
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
//function to fetch the google log in data(Name , Email and profile) for current profile logged in
public void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
    if(!result.isSuccess()){
        // Signed out, show unauthenticated UI.
        updateUI(false);
//        loginStatus.setText("No Internet Connection...");
//        onConnectionFailed(connectionResult);


    }
        else{
        // Signed in successfully, show authenticated UI.
        GoogleSignInAccount acct = result.getSignInAccount();
        Log.e(TAG, "display name: " + acct.getDisplayName());

        String personName = acct.getDisplayName();
        String emails = acct.getEmail();

                String Pic;
     userName.setText(personName);
        email.setText(emails);
//fetching profile photo with the help of glide
if(acct.getPhotoUrl()==null) {
        userPic.setImageResource(R.drawable.noic1);
}
else
        {
                Pic = acct.getPhotoUrl().toString();
                Glide.with(getApplicationContext()).load(Pic)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(userPic);
}
                Log.e(TAG, "Name: " + personName + ", email: " + emails);

//Inserting details into database.
                  c.setDate(DateFormat.getDateTimeInstance().format(new Date()).toString());
                  c.setApp("Google");
                  c.setName(personName);
                  c.setEmail(emails);
                  dbhelp.insert(c);
// in case of successful login change the UI to the present porfile pic, name, email.
        updateUI(true);
        }
//        else {
//        // Signed out, show unauthenticated UI.
//            onConnectionFailed(connectionResult);
//            updateUI(false);
//        }

        }

public void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
        gmailSignInButton.setVisibility(View.GONE);
        loginStatus.setText("Login Success");
                gmail_Sigoutbutton.setVisibility(View.VISIBLE);
                userPic.setVisibility(View.VISIBLE);
                 fb_LoginButton.setVisibility(View.GONE);
                fb_LogoutButton.setVisibility(View.GONE);

        } else {

        gmailSignInButton.setVisibility(View.VISIBLE);
                gmail_Sigoutbutton.setVisibility(View.GONE);
                loginStatus.setText("Status");
                        email.setText("");
                userName.setText("");
                userPic.setVisibility(View.GONE);
                fb_LoginButton.setVisibility(View.VISIBLE);
        }
        }



    //facebook function to fetch data from current logged in profile.
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
                                                gmailSignInButton.setVisibility(View.GONE);
                                                gmail_Sigoutbutton.setVisibility(View.GONE);
                                                fb_LoginButton.setVisibility(View.GONE);
                                                fb_LogoutButton.setVisibility(View.VISIBLE);
                                                String email = response.getJSONObject().getString("email");
                                                Profile profile = Profile.getCurrentProfile();
                                                String id = profile.getId();
                                                String link = profile.getLinkUri().toString();
                                                String name = profile.getName();
                                                String time = DateFormat.getDateTimeInstance().format(new Date());
                                           //INserting values in the database
                                            c.setDate(time);
                                            c.setApp("Facebook");
                                            c.setName(name);
                                            c.setEmail(email);
                                            dbhelp.insert(c);

                                            loginStatus.setText("Login Success : " + name + "\n"+ email);
                                                userPic.setVisibility(View.VISIBLE);
                                                Log.i("Link",link);
                                     //fetching profile picusing glide
                                                if (Profile.getCurrentProfile()!=null)
                                                {
                                                        if(Profile.getCurrentProfile().getProfilePictureUri(200,200)==null) {
                                                                userPic.setImageResource(R.drawable.noic1);
                                                        }
                                                        else {
                                                                Glide.with(getApplicationContext()).load(Profile.getCurrentProfile().getProfilePictureUri(200, 200))
                                                                        .thumbnail(0.5f)
                                                                        .crossFade()
                                                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                                        .into(userPic);
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

//receives result from the function being called in mainactivity
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
              // check if the request code is same as what is passed
                        if(requestCode == RC_SIGN_IN) {
                                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                                handleSignInResult(result);
                        }
                if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
                        return;
                }
        }

        boolean connected=false;
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo== null)
        {
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
//            startActivity(intent);
//
          connected=false;
          }
else {
            connected = true;
        }
      return connected;
     }

}

