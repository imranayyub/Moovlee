package com.example.im.googlesign;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.InputStream;



import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Im on 09-11-2017.
 */

public class ImageDisplayFragment extends Fragment {
    ImageView image;
//DisplayImage displayImage=new DisplayImage();
TextView text;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_fragment,container,false);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        image= (ImageView)getActivity().findViewById(R.id.image1);
text= (TextView) getActivity().findViewById(R.id.text);
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
////        image= (ImageView)getActivity().findViewById(R.id.image1);
//
//    }

    public void showImage(String s)
    {

            Glide.with(getApplicationContext()).load(s)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into((ImageView)getActivity().findViewById(R.id.image1));

    }
    public void state(String s){
        text.setText(s);
    }
}
//support fragment manager
