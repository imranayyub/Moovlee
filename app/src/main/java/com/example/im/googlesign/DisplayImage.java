package com.example.im.googlesign;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Im on 09-11-2017.
 */

public class DisplayImage extends AppCompatActivity {
    int req = 1;
//    FragmentActivity fragmentActivity=new FragmentActivity();
//    FragmentManager manager=getFragmentManager();
ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayimage);
//        FragmentTransaction transaction=manager.beginTransaction();
//        transaction.add(R.id.activity_DisplayImage, fragmentActivity).commit();
//        transaction.show(fragmentActivity);
        image=(ImageView)findViewById(R.id.image);
        openFileManager();

    }


    void openFileManager() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, req);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == req && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
//
//
            Uri imageUri = data.getData();
            try {

                InputStream inputStream = this.getContentResolver().openInputStream(imageUri);

                Glide.with(getApplicationContext()).load(imageUri.toString())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(image);

            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Unable to open image  ", Toast.LENGTH_LONG).show();
                e.printStackTrace();

            }

            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }
}