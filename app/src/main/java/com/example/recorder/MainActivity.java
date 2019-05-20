package com.example.recorder;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements recordFragment.SendfileName{


    private SectionsPageAdapter sectionsPageAdapter;

    private ViewPager viewPager;

    private boolean permissionToRecordAccepted = false;

    private boolean permissionToWriteAccepted = false;

    private String [] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sectionsPageAdapter=new SectionsPageAdapter(getSupportFragmentManager());

        TabLayout tabLayout=(TabLayout)findViewById(R.id.tabs);

        viewPager=(ViewPager)findViewById(R.id.container);
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);


       // viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //viewPager.setAdapter(viewPagerAdapter);


        int requestCode = 200;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestPermissions(permissions, requestCode);

        }



    }


    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new recordFragment(), "Record");
        adapter.addFragment(new playFragment(), "Play");
        viewPager.setAdapter(adapter);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,

                                                     @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case 200:

                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                permissionToWriteAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                break;

        }

        if (!permissionToRecordAccepted ) MainActivity.super.finish();

        if (!permissionToWriteAccepted ) MainActivity.super.finish();

    }


    @Override
    public void sendData(String name) {
        Log.i("File Name ",name);
            String tag = "android:switcher:" +R.id.container+ ":" + 1;
            playFragment f = (playFragment) getSupportFragmentManager().findFragmentByTag(tag);
            try {
                f.ReceivedData(name);
                Toast.makeText(this,name,Toast.LENGTH_LONG).show();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
    }
    public int view(){
        return R.id.container;
    }

}
