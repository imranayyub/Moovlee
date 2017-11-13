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
    FragmentManager manager = getFragmentManager();
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
        viewPager = (ViewPager)findViewById(R.id.viewPager);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.SelectImageButton1:
                FragmentTransaction transaction = manager.beginTransaction();

                transaction.replace(R.id.fragmentActivity, fragmentActivity).commit();
                transaction.show(fragmentActivity);

                ImageDisplayFragment f1 = (ImageDisplayFragment) manager.findFragmentById(R.id.fragmentActivity);
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
//        ArrayList<String> marray= getAllShownImagesPath(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == req && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            ArrayList<String> imagearray = new ArrayList<>();
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();

                    imagearray.add(i, uri.toString());
                }
                ImageDisplayFragment f1 = (ImageDisplayFragment) manager.findFragmentById(R.id.fragmentActivity);
                f1.showImage(imagearray.get(1).toString());
                myCustomPagerAdapter = new MyCustomPagerAdapter(DisplayImage.this, imagearray);
                viewPager.setAdapter(myCustomPagerAdapter);

            }
        }
    }
}


//Dialogue fragment  to get more details from user