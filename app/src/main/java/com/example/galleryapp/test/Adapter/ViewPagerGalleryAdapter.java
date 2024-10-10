package com.example.galleryapp.test.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.galleryapp.test.Fragment.FolderFragment;
import com.example.galleryapp.test.Fragment.MainFragment;
import com.example.galleryapp.test.Fragment.VideoFragment;

public class ViewPagerGalleryAdapter extends FragmentPagerAdapter {

    public ViewPagerGalleryAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new MainFragment();
        } else if (position == 1) {
            return new VideoFragment();
        } else {
            return new FolderFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Gallery";
        } else if (position == 1) {
            return "Video";
        } else {
            return "Folder";
        }
    }

}
