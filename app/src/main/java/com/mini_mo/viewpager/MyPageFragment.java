package com.mini_mo.viewpager;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mini_mo.viewpager.Cluster.Selectlocationmap;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.DAO.ReadCommentInfo;
import com.mini_mo.viewpager.DAO.User_Info;
import com.mini_mo.viewpager.ListView.RecyclerListView;
import com.mini_mo.viewpager.ListView.RecyclerListView_review;
import com.mini_mo.viewpager.ReadAndWrite.AddressTransformation;
import com.mini_mo.viewpager.ReadAndWrite.CustomListviewitem;
import com.mini_mo.viewpager.ReadAndWrite.WriteActivity;

import org.json.JSONException;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class MyPageFragment extends Fragment {

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000;
    private final int GALLERY_CODE=1112; // 이미지 불러오기 후 onActivityResult로 받을 request코드값
    final int TAPMAP = 300;

    private View view;
    private NestedScrollView nestedScrollView;
    private RecyclerListView recyclerListView;
    private RecyclerListView_review recyclerListView_review;
    private User_Info user_info; // 사용자 정보
    private Animation ani;
    String loginId; // 로그인 아이디
    int tap_onoff; // 1=게시글 , 2= review

    ArrayList<ListViewItemData> mylistItem;
    ArrayList<ReadCommentInfo> reviewlistitem;
    private User_Info read_location;
    private TextView userId;
    private TextView location;
    private TextView message;
    private TextView review;
    private TextView board;
    private ImageView board_line;
    private ImageView review_line;
    private ImageView usericon;

    private static MyPageFragment instance = null;

    public static MyPageFragment getInstance()
    {
        return instance;
    }

    // 생성자 필수
    public MyPageFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(Store.userid.equals("Guest")){
            view = inflater.inflate(R.layout.guest_view, container, false);
        }else {
            view = inflater.inflate(R.layout.activity_mypage, container, false);

            nestedScrollView = (NestedScrollView) view.findViewById(R.id.include);
            userId = (TextView) view.findViewById(R.id.userid);
            location = (TextView) view.findViewById(R.id.location);
            message = (TextView) view.findViewById(R.id.message);
            review = (TextView) view.findViewById(R.id.review);
            board = (TextView) view.findViewById(R.id.board);
            review_line = (ImageView) view.findViewById(R.id.review_line);
            board_line = (ImageView) view.findViewById(R.id.board_line);
            usericon = (ImageView) view.findViewById(R.id.usericon);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        if(!Store.userid.equals("Guest")){
            tap_onoff = 1;

            /** Fab 클릭 이벤트 --> 코멘트 작성 액티비티로 전환 **/
            FloatingActionButton writeButton = (FloatingActionButton) view.findViewById(R.id.write_fab);
            writeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 게시글 작성 액티비티로 전환
                    Intent intent = new Intent(getActivity(), WriteActivity.class);
                    startActivity(intent);
                }
            });

            //닉네임 변경
            userId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent( MyPageFragment.this.getContext(), NicknameActivity.class ); // 앱안에는 없지만 안드로이드 폰에 존재하는 컨텐트들의 URI를 받아오자
                    intent.putExtra("loginId", loginId);
                    intent.putExtra("NIckname", userId.getText().toString() );

                    startActivity( intent );
                }
            });

            // 유저 이미지 클릭해서 변경할 때
            usericon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

                    if(permissionCheck== PackageManager.PERMISSION_DENIED){

                        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    }else{
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI ); // 앱안에는 없지만 안드로이드 폰에 존재하는 컨텐트들의 URI를 받아오자

                        intent.setType( MediaStore.Images.Media.CONTENT_TYPE ); // image Content 타입으로 받아오자

                        startActivityForResult( Intent.createChooser( intent, "Select Pictures"), GALLERY_CODE); // Start
                    }
                }
            });

            // 가게주소 변경
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent cintent = new Intent(MyPageFragment.this.getContext(), Selectlocationmap.class);
                    startActivityForResult(cintent,TAPMAP);
                }
            });

            // 상태메세지 변경
            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent( MyPageFragment.this.getContext(), StateMessageActivity.class ); // 앱안에는 없지만 안드로이드 폰에 존재하는 컨텐트들의 URI를 받아오자
                    intent.putExtra("loginId", loginId);
                    intent.putExtra("stateMessage", message.getText().toString() );

                    startActivity( intent );
                }
            });

            review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ani = AnimationUtils.loadAnimation(getActivity(),R.anim.alpha2);
                    review_line.startAnimation(ani);

                    ani = AnimationUtils.loadAnimation(getActivity(),R.anim.alpha2);
                    review.startAnimation(ani);

                    tap_onoff = 2;
                    onResume();

                    review_line.setImageResource(R.drawable.click_line);
                    board_line.setImageResource(R.drawable.clickoff_line);
                }
            });

            board.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ani = AnimationUtils.loadAnimation(getActivity(),R.anim.alpha2);
                    board_line.startAnimation(ani);

                    ani = AnimationUtils.loadAnimation(getActivity(),R.anim.alpha2);
                    board.startAnimation(ani);

                    tap_onoff = 1;
                    onResume();

                    review_line.setImageResource(R.drawable.clickoff_line);
                    board_line.setImageResource(R.drawable.click_line);
                }
            });
        }

        super.onViewCreated(view, savedInstanceState);
    }
    public void setLoginId(String id)
    {
        loginId = id;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!Store.userid.equals("Guest")){

            if(tap_onoff==1){
                recyclerListView = new RecyclerListView(getContext(), this.view, this);
                try {
                    // 사용자 정보
                    user_info = new Data().read_myPage(loginId);
                    mylistItem = new Data().read_myBoard(loginId, 0);
                    read_location = new Data().read_myLocation(Store.userid);

                    // 보드 정보
                    recyclerListView.loginId = loginId;
                    recyclerListView.listViewItems.clear();
                    recyclerListView.add(mylistItem);
                    recyclerListView.loadItems(nestedScrollView, getContext());

                    userId.setText(user_info.nickname);
                    if(read_location.latitude == 0.0 || read_location.longitude == 0.0){
                        location.setText("위치가 설정되지 않았습니다.");
                    }else{
                        location.setText(AddressTransformation.getAddress(getActivity(), read_location.latitude, read_location.longitude));
                    }

                    // photo 넣는곳
                    Glide.with( this.getContext() )
                            .load( user_info.user_photo )
                            .apply( new RequestOptions().override(usericon.getWidth(),usericon.getHeight()).placeholder( R.drawable.user ).error( R.drawable.user ))
                            .into( usericon );

                    Store.myprofile_img = user_info.user_photo;

                    message.setText(user_info.massage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                recyclerListView.adapter.notifyDataSetChanged();
            }else{
                recyclerListView_review = new RecyclerListView_review(getContext(), this.view, this);

                try {
                    // 사용자 정보
                    reviewlistitem = new Data().read_myPage_comment(loginId);

                    // 보드 정보
                    recyclerListView_review.loginId = loginId;
                    recyclerListView_review.listViewItems.clear();
                    recyclerListView_review.add(reviewlistitem);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                recyclerListView_review.adapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == TAPMAP){
            if(Store.point!=null) {
                Data change_location = new Data();

                location.setText(AddressTransformation.getAddress(getActivity(), Store.point.latitude, Store.point.longitude));

                try {
                    change_location.change_myLocation(Store.point.latitude,Store.point.longitude,Store.userid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(getActivity().getApplicationContext(), "주소 입력 실패!", Toast.LENGTH_SHORT).show();
                Log.i("위치 저장 값", "주소 널 포인트");
            }
        }else if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case GALLERY_CODE:
                    sendPicture( data ); //갤러리에서 가져오기
                    break;
                default:
                    break;
            }

        }
    }

    public void sendPicture( Intent data )
    {
        /*** 싱글 사진 로드 ***/
        Uri imgUri = data.getData(); // intent 객체에서 data 빼내기 ( 여기서는 Image Uri 임 )

        String imagePath = getImageRealPathAndTitleFromURI( imgUri );

        // 프로필 사진에 절대경로로 이미지 표시
        usericon.setImageURI( imgUri );
        // 서버로 imagePath 보내기
        try
        {
            new Data().change_user_photo( MainActivity.getInstance().loginId, imagePath, true, true );
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    public String getImageRealPathAndTitleFromURI( Uri contentUri )
    {
        String filePath = null;

        String[] proj = { MediaStore.Images.Media.DATA }; // content 테이블에서 DATA, TITLE 열 부분만 받아오기 위해

        // uri에 해당하는 컨텐트 테이블에서 DATA, TITLE 부분만 받아와서 cursor로 가르키고 있는다.
        Cursor cursor = getActivity().getContentResolver().query( contentUri, proj, null, null, null );

        if( cursor != null && cursor.moveToNext() )  // 커서가 생성 됬을 때, 다음 행 가르켰을때 null이 아니면
        {
            int column_index = cursor.getColumnIndexOrThrow( proj[0] ); // data부분 ( 파일경로 )의 index 받아오기
            filePath = cursor.getString( column_index );

            cursor.close();
        }
        return filePath;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
