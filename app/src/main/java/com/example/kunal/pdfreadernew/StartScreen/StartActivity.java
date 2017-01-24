package com.example.kunal.pdfreadernew.StartScreen;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.kunal.pdfreadernew.login.LoginActivity;
import com.example.kunal.pdfreadernew.R;

/**
 * Created by Kunal on 23-01-2017.
 */

public class StartActivity extends AppCompatActivity {

    ViewPager mImageViewPager;
    TabLayout tabLayout;

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_start_screen);

        getSupportActionBar().hide();

        prefs = getSharedPreferences("ThoughtCliff Reader", MODE_PRIVATE);

        StartScreenPageAdapter startScreenPageAdapter = new StartScreenPageAdapter(getSupportFragmentManager());
        mImageViewPager = (ViewPager) findViewById(R.id.pager);
        mImageViewPager.setAdapter(startScreenPageAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabDots);

        tabLayout.setupWithViewPager(mImageViewPager);



        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false


            prefs.edit().putBoolean("firstrun", false).commit();
        }
        else{
            gotoEBooks();
        }

    }

    public void gotoEBooks(){
        Intent i = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(i);
    }
}

class StartScreenPageAdapter extends FragmentPagerAdapter
{
    public StartScreenPageAdapter(FragmentManager fm)
    {
        super(fm);
    }
    @Override
    public int getCount()
    {
        return 2;
    }
    @Override
    public Fragment getItem(int position)
    {
        switch(position)
        {
            case 0: return new ThoghtcliffFragment();
            case 1: return new ReaderStartFragment();
            default : return new ThoghtcliffFragment();
        }
    }
//    @Override
//    public CharSequence getPageTitle(int position) {
//        switch (position) {
//            case 0:
//                return "Welcome";
//            case 1:
//                return "Begin";
//            default:
//                return "Welcome";
//        }
//    }
}
