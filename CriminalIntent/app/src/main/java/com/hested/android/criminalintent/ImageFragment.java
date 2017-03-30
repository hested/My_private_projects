package com.hested.android.criminalintent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Project:  CriminalIntent
 * Package:  com.hested.android.criminalintent
 * Date:     25-03-2017
 * Time:     02:06
 * Author:   Johnni Hested
 */

public class ImageFragment extends AppCompatDialogFragment {
    private static final String EXTRA_IMAGE_PATH = "com.hested.android.criminalintent.image_path";

    private ImageView mImageView;

    public static ImageFragment newInstance(String imagePath) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
        fragment.setArguments(args);
        //fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
        Bitmap image = PictureUtils.getScaledBitmap(path, getActivity());
        mImageView.setImageBitmap(image);

        return mImageView;
    }

    /**
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //PictureUtils.cleanImageView(mImageView);
    }
    **/

}
