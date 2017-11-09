package com.example.im.googlesign;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Im on 09-11-2017.
 */

public class FragmentActivity  extends Fragment {
  ImageView image;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
     image.findViewById(R.id.image);
        return inflater.inflate(R.layout.activity_fragment,container,false);


    }
//    public void setImage(String imageUri){
//        Glide.with(getApplicationContext()).load(imageUri)
//                .thumbnail(0.5f)
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(image);
//    }
}


//support fragment manager
