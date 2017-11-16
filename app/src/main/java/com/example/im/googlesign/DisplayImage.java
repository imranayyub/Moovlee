package com.example.im.googlesign;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.assist.AssistContent;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
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

public class DisplayImage extends AppCompatActivity implements View.OnClickListener {
    int req = 1;
    FragmentManager manager = getFragmentManager();    //Initializing Fragment Manager
    ImageDisplayFragment fragmentActivity = new ImageDisplayFragment();
    Button SelectImageButton1;
    ViewPager viewPager;
    MyCustomPagerAdapter myCustomPagerAdapter;

    //ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayimage);
        SelectImageButton1 = (Button) findViewById(R.id.SelectImageButton1);
        SelectImageButton1.setOnClickListener(this);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.SelectImageButton1:
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragmentActivity, fragmentActivity).commit();
                transaction.show(fragmentActivity);          //Shows Fragment in the current Xml.
                ImageDisplayFragment f1 = (ImageDisplayFragment) manager.findFragmentById(R.id.fragmentActivity);   //Finds Fragment by id.
                f1.state("IMAGE!");
                openFileManager();

        }

    }

    //Opens File Manager of phone to Select Images from phone.
    void openFileManager() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, req);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Checks if the requestCode is same as req.
        if (requestCode == req && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            ArrayList<String> imagearray = new ArrayList<>();
            ClipData clipData = data.getClipData();//Clipdata is a Complex type and can store instances of more than one item.
           //In Case of Single Image
            if (clipData == null) {
                Uri img = data.getData();
                imagearray.add(0, img.toString());
            }
            //In Case of Multiple Images
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();       //Returns Item URI
                    imagearray.add(i, uri.toString());
                }
            }
            ImageDisplayFragment f1 = (ImageDisplayFragment) manager.findFragmentById(R.id.fragmentActivity);  //Finds Fragment By ID.
            f1.showImage(imagearray.get(0).toString()); // Calls ShowImage Function of ImageDisplayFragment.
            myCustomPagerAdapter = new MyCustomPagerAdapter(DisplayImage.this, imagearray);
            viewPager.setAdapter(myCustomPagerAdapter);


        }
    }
}
