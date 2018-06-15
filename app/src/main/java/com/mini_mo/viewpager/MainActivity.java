package com.mini_mo.viewpager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

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

    public static MainActivity getInstance(){ return instance; }

    public MainActivity()
    {
        instance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        /*** DrawerLayout ***/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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
                default:
                    return null;
            }
        }
        @Override // ViewPager의 Page 수
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    /*** Navigation Drawer ( 왼쪽 메뉴 ) ***/
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /* Convert Camera Activity */
        if (id == R.id.nav_camera) {
            Intent intent = new Intent( this, CameraActivity.class );
            startActivity( intent );
        }
        /* Convert Map Activity */
        else if (id == R.id.nav_map) {
            Intent intent = new Intent(getApplication(),ClusterMap.class);
            startActivity( intent );
        } else if (id == R.id.nav_user_account) {

        } else if (id == R.id.nav_setting) {
            //원래 setting이 들어가야되지만 버튼을 만드는 방법을 모르므로 일단 위치저장버튼으로 쓰겠소.

            Intent intent = new Intent(this, SaveLocateActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE); //로그아웃 버튼 클릭시
            SharedPreferences.Editor editor = auto.edit();
            editor.clear(); //안에 내용 다 지움
            editor.commit();

            Toast.makeText(getApplicationContext(),"로그아웃 되었습니다!",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class); // 로그인 액티비티로 이동
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 뒤로가기 버튼을 눌렀을 때 처리 함수
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
