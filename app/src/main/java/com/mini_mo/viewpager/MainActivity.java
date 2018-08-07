package com.mini_mo.viewpager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mini_mo.viewpager.Camera.CameraActivity;
import com.mini_mo.viewpager.Camera.LoadingDialog;
import com.mini_mo.viewpager.Cluster.ClusterMap;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.User_Info;
import com.mini_mo.viewpager.FriendListView.FriendListFragment;
import com.mini_mo.viewpager.Login.LoginActivity;
import com.mini_mo.viewpager.ReadAndWrite.SaveLocateActivity;
import com.mini_mo.viewpager.ReadAndWrite.Util;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity{

    public static MainActivity instance;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    Data data;
    User_Info user_info;
    RoundedBitmapDrawable roundedBitmapDrawable;
    SearchView searchView;

    Activity activity;

    SharedPreferences auto;
    public String loginId;

    public ImageButton arCameraButton;

    public static MainActivity getInstance(){ return instance; }

    public MainActivity()
    {
        instance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Store.display_width = displayMetrics.widthPixels;// 가로
        Store.display_height = displayMetrics.heightPixels;// 세로

        Log.i("가로", Store.display_width+"");
        Log.i("세로", Store.display_height+"");


        // 상태바 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // View mainview = (View) this.getLayoutInflater().inflate(R.layout.activity_main, null);

        activity = this;

        auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginId = auto.getString("inputId",null);
        Store.userid = auto.getString("inputId",null);


        /*Glide.with( context )
                .load( listViewItemList.get( position ).user_photo )
                .apply( new RequestOptions().override(100,100).placeholder( R.drawable.user ).error( R.drawable.user  ))
                .into( iconImageView );*/


        /*** Tool bar ***/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //실험
        searchView = (SearchView) findViewById(R.id.searchView);

        // SearchView 검색어 입력/검색 이벤트 처리
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.equals("") || query.equals(" ")) {
                    Util.Toast(activity, "검색어를 입력하세요!");

                }else {
                    Intent intent = new Intent(activity, SearchActivity.class);
                    intent.putExtra("content",query);
                    startActivity(intent);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        //실험

        /*** View Pager ***/
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        arCameraButton = findViewById( R.id.main_camera );
        arCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( MainActivity.this , CameraActivity.class );
                startActivity( intent );
            }
        });
    }

    /** ViewPage Adapter ( 횡 스크롤 화면 전환 )**/
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch(position)
            {
                case 0:
                    return new MainPageFragment();
                case 1:
                    MyPageFragment myPageFragment = new MyPageFragment();
                    myPageFragment.setLoginId(loginId);
                    return  myPageFragment;
                case 2:
                    return new FriendListFragment();
                case 3:
                    return new SettingFrg();
                default:
                    return null;
            }
        }
        @Override // ViewPager의 Page 수
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( grantResults.length > 0)
        {
            // 권한허가를 받았는가
            boolean cameraAccepted = ( grantResults[0] == PackageManager.PERMISSION_GRANTED );
            if( !cameraAccepted )
            {
                return;
            }
            else
            {
                if (requestCode == 100)
                {
                    if( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
                    {
                        ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101 );
                    }
                    else
                    {
                        // GPS 사용유무 가져오기
                        if (!MainPageFragment.getInstance().gps.isGetLocation())
                        {
                            // GPS 를 사용할수 없으므로
                            MainPageFragment.getInstance().loading.progressOFF();
                            MainPageFragment.getInstance().gps.showSettingsAlert();
                        }
                    }

                }
                else if (requestCode == 101)
                {
                    // GPS 사용유무 가져오기
                    if (!MainPageFragment.getInstance().gps.isGetLocation())
                    {
                        // GPS 를 사용할수 없으므로
                        MainPageFragment.getInstance().loading.progressOFF();
                        MainPageFragment.getInstance().gps.showSettingsAlert();
                    }
                }
            }
        }
    }



}
