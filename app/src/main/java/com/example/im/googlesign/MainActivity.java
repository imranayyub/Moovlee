package com.example.im.googlesign;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
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
        Button fb_LoginButton, fb_LogoutButton,SelectImageButton;


    FragmentManager manager=getFragmentManager();
    dialogFragment dialog = new dialogFragment();
    Button infoButton;
    String fname="",address="";
    TextView text;
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

        infoButton= (Button) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(this);
        text= (TextView) findViewById(R.id.text);

// initialize login UI
        SelectImageButton  = (Button) findViewById(R.id.SelectImageButton);
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


        SelectImageButton.setOnClickListener(this);

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
            if(!c.getAddress().isEmpty())
            {
                fname=c.getFname();
                address=c.getAddress();
                text.setText("Father's Name : "+ fname+"\n Address : "+address);

            }
            dbhelp.delete();
            signIn();
            infoButton.setVisibility(View.GONE);

        }
        else if(c.getApp().equals("Facebook"))
        {
            if(!c.getAddress().isEmpty())
            {
                fname=c.getFname();
                address=c.getAddress();
                text.setText("Father's Name : "+ fname+"\n Address : "+address);

            }
            dbhelp.delete();
            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends","email"));
            infoButton.setVisibility(View.GONE);
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
                    infoButton.setVisibility(View.GONE);
                  signOut();

                        break;
                 }
                case R.id.fb_LoginButton: {
                        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends","email"));

                        break;
                }
                case R.id.fb_LogoutButton: {
                        LoginManager.getInstance().logOut();
                        loginStatus.setText("Status");
                    text.setText("");
                    userName.setText("");
                    email.setText("");
                        userPic.setVisibility(View.GONE);
                        fb_LogoutButton.setVisibility(View.GONE);
                        gmailSignInButton.setVisibility(View.VISIBLE);
                    SelectImageButton.setVisibility(View.GONE);
                    infoButton.setVisibility(View.INVISIBLE);

                    fb_LoginButton.setVisibility(View.VISIBLE);
                        dbhelp.delete();
                        break;
                }
            case R.id.SelectImageButton:
            {
                Intent intent = new Intent(MainActivity.this, DisplayImage.class);
                startActivity(intent);
//                displayImage.openFileManager();
                break;
            }
            case R.id.infoButton: {
                dialog.show(manager, "YourDialog");
//                showtext();
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
            SelectImageButton.setVisibility(View.VISIBLE);
            infoButton.setVisibility(View.VISIBLE);
        } else {

        gmailSignInButton.setVisibility(View.VISIBLE);
                gmail_Sigoutbutton.setVisibility(View.GONE);
                loginStatus.setText("Status");
                        email.setText("");
                userName.setText("");
                userPic.setVisibility(View.GONE);
            text.setText("");
            SelectImageButton.setVisibility(View.GONE);
            fb_LoginButton.setVisibility(View.VISIBLE);
            infoButton.setVisibility(View.GONE);
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
                                            infoButton.setVisibility(View.VISIBLE);
                                                String emails = response.getJSONObject().getString("email");
                                                Profile profile = Profile.getCurrentProfile();
                                                String id = profile.getId();
                                                String link = profile.getLinkUri().toString();
                                                String name = profile.getName();
                                                String time = DateFormat.getDateTimeInstance().format(new Date());
                                           //INserting values in the database
                                            c.setDate(time);
                                            c.setApp("Facebook");
                                            c.setName(name);
                                            c.setEmail(emails);
                                            dbhelp.insert(c);
                                            SelectImageButton.setVisibility(View.VISIBLE);

                                            loginStatus.setText("Login Success : " );
                                            userName.setText(name);
                                            email.setText(emails);
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
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
            if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
                return;
            }
            //for file manager's result

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
//Accesses the file explorer of phone to select image

//    public static String encodeTobase64(Bitmap image)
//    {
//        Bitmap immagex=image;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] b = baos.toByteArray();
//        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
//
//        Log.e("LOOK", imageEncoded);
//        return imageEncoded;
//    }
//    public static Bitmap decodeBase64(String input)
//    {
//        byte[] decodedByte = Base64.decode(input, 0);
//        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
//
//    }


//    READ XML STORAGE ANDWRITE XML STORAGE PERMISSIONS IN MANIFEST

    public void showtext(String fname, String address)
    {
        text.setText("Father's Name : "+ fname+"\n Address : "+address);
//        dbhelp.delete();
        c.setAddress(address);
        c.setFname(fname);
        dbhelp.insert(c);
        infoButton.setVisibility(View.GONE);
    }
}

