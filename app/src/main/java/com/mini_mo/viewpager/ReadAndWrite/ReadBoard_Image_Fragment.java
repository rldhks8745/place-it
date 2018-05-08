package com.mini_mo.viewpager.ReadAndWrite;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mini_mo.viewpager.R;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by sasor on 2018-04-29.
 */

public class ReadBoard_Image_Fragment extends Fragment {

    PhotoViewAttacher mAttacher;


    public static ReadBoard_Image_Fragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt("position",position);

        ReadBoard_Image_Fragment fragment = new ReadBoard_Image_Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rnw_activity_imageselect,container,false);
        ImageView imageView = (ImageView)view.findViewById(R.id.select_img);

        imageView.setImageResource(getArguments().getInt("position"));

        mAttacher = new PhotoViewAttacher(imageView);

        return view;
    }
}
