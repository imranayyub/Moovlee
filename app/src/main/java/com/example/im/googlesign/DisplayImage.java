package com.example.im.googlesign;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Im on 09-11-2017.
 */

public class DisplayImage extends AppCompatActivity implements View.OnClickListener{
    int req = 1;
    FragmentManager manager=getFragmentManager();
   ImageDisplayFragment fragmentActivity=new ImageDisplayFragment();
    Button SelectImageButton1;
    //ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayimage);
        SelectImageButton1=(Button)findViewById(R.id.SelectImageButton1);

        SelectImageButton1.setOnClickListener(this);
//        image=(ImageView)findViewById(R.id.image);

    }

    @Override
    public void onClick(View v) {
        int id =v.getId();
        switch (id){
            case R.id.SelectImageButton1 :
                FragmentTransaction transaction=manager.beginTransaction();

                transaction.replace(R.id.fragmentActivity, fragmentActivity).commit();
                transaction.show(fragmentActivity);

                ImageDisplayFragment f1= (ImageDisplayFragment) manager.findFragmentById(R.id.fragmentActivity);
                f1.state("IMAGE!");
                openFileManager();

        }

    }

    void openFileManager() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
//            String [] max={};
//            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
            Uri imageUri = data.getData();
            try {

                InputStream inputStream = this.getContentResolver().openInputStream(imageUri);
                ImageDisplayFragment f1= (ImageDisplayFragment) manager.findFragmentById(R.id.fragmentActivity);
                f1.showImage(imageUri.toString());




//
//                Glide.with(getApplicationContext()).load(imageUri.toString())
//                        .thumbnail(0.5f)
//                        .crossFade()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(image);

            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Unable to open image  ", Toast.LENGTH_LONG).show();
                e.printStackTrace();

            }

            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }
}