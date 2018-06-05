package com.mini_mo.viewpager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.mini_mo.viewpager.Camera.CameraActivity;
import com.mini_mo.viewpager.Cluster.ClusterMap;
import com.mini_mo.viewpager.FriendListView.FriendListFragment;
import com.mini_mo.viewpager.Login.LoginActivity;
import com.mini_mo.viewpager.ReadAndWrite.SaveLoacateActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static MainActivity instance;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    SharedPreferences auto;
    String loginId;

    public static MainActivity getInstance(){ return instance; }

    public MainActivity()
    {
        instance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginId = auto.getString("inputId",null);
        Store.userid = auto.getString("inputId",null);

        /*** Tool bar ***/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

            Intent intent = new Intent(this, SaveLoacateActivity.class);
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
