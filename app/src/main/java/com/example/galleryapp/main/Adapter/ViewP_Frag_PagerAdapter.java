package com.example.galleryapp.main.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.galleryapp.main.Fragment.FolderFragment;
import com.example.galleryapp.main.Fragment.MainFragment;
import com.example.galleryapp.main.Fragment.VideoFragment;

public class ViewP_Frag_PagerAdapter extends FragmentPagerAdapter {

    public ViewP_Frag_PagerAdapter(@NonNull FragmentManager fm) {
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
