package com.mini_mo.viewpager.Camera;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.mini_mo.viewpager.DAO.Board_Location_List;
import com.mini_mo.viewpager.MainActivity;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.ReadAndWrite.ReadActivity;
import com.mini_mo.viewpager.Store;

import java.util.ArrayList;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class CommentVector2 implements Comparable<CommentVector2>{

    public static int LAYOUT_WIDTH = 600;
    public static int LAYOUT_HEIGHT = 1100;

    public ViewGroup layoutView; // 화면에 보여질 코멘트 레이아웃

    public int mCount; // 디비 게시글 수
    public Vector2 mvecAbsolutePosition = null; // 현재 코멘트 위치 절대좌표 ( 경도(x), 위도(y) )

    public Vector2 mvecScreenPos = null; // 핸드폰 화면 좌표

    public Vector2 mvecRelativePosition = null; // 현재위치에서부터 현재 코멘트까지의 상대거리
    public double mDistance = 0.0; // 현재 위치와 코멘트 간의 거리
    public boolean mIsinCamera = false; // 카메라 각도 안에 들어왔나?
    public double mScreenWIdthRatio = 0.0; // 화면가로 어디에 코멘트를 띄워야할지 비율

    public boolean mAddView = false;
    public int layoutWidth; // 증강현실 코멘트 width
    public int layoutHeight;
    public float textIdSIze = 30.0f;
    public float textContextSize = 24.0f;
    public float textImgCountSIze = 40.0f;
    public float pageTextSize = 30.0f;
    public int pageIndex = 0;

    ArrayList<Board_Location_List> contents;
    LinearLayout wrapLayout;
    RelativeLayout.LayoutParams wrapLayoutParams;
    TextView custom_id; // 유저 아이디
    TextView custom_context; // 게시글 내용
    ImageView custom_userimg; // 유저 이미지
    ImageView custom_firstimg; // 첫번째 이미지
    TextView custom_imgcount; // 이미지 몇개인가
    TextView textViewPage; // 현재 모여잇는 게시글 몇페이지인가
    ImageView btLeftPage;
    ImageView btRightPage;

    double changedDistance = 0.0;

    public CommentVector2(Vector2 absPos, Vector2 relPos, double distance, int count, final ArrayList<Board_Location_List> content_list )
    {
        mvecScreenPos = new Vector2();
        // 코멘트 벡터 생성
        mvecAbsolutePosition = absPos;
        mvecRelativePosition = relPos;
        mDistance = ( distance > 1.0 ) ? distance : 1.0; // 1m 이하 코멘트들은 최소 1m로 맞춰주기
        mCount = count;
        contents = content_list;

        LayoutInflater inflater = (LayoutInflater) MainActivity.getInstance().getSystemService( MainActivity.getInstance().LAYOUT_INFLATER_SERVICE );
        layoutView = ( ViewGroup ) inflater.inflate( R.layout.camera_custom_item, null );

        wrapLayout = (LinearLayout)layoutView.findViewById(R.id.camera_custom_layout);
        wrapLayoutParams = new RelativeLayout.LayoutParams( 0, 0 );

        double percentageSize = ( mDistance / 10 ) + 1; // 1 ~ 1/5 배율로 거리 표시

        // 게시글 작성자 id
        custom_id = (TextView)layoutView.findViewById(R.id.camera_custom_id);
        custom_id.setTextSize( textIdSIze / (float)percentageSize );
        custom_id.setText(contents.get( pageIndex ).id);
        custom_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent( CameraActivity.getInstance(), ReadActivity.class);

                //여기는 DB에서 게시글번호를 가져와서 스트링으로 넣어주면 됨  intent.putExtra("Board_num","")
                CameraActivity.getInstance().startActivity(intent);
                //Store.board_num = Store.sendboard.get(position).board_num;
                Store.board_num = contents.get(0).board_num;
                CameraActivity.getInstance().overridePendingTransition(R.anim.goup, R.anim.godown);
            }
        });

        // 게시글 내용
        custom_context = (TextView)layoutView.findViewById(R.id.camera_custom_context);
        custom_context.setTextSize( textContextSize / (float)percentageSize );
        custom_context.setText(contents.get( pageIndex ).content);
        custom_context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent( CameraActivity.getInstance(), ReadActivity.class);

                //여기는 DB에서 게시글번호를 가져와서 스트링으로 넣어주면 됨  intent.putExtra("Board_num","")
                CameraActivity.getInstance().startActivity(intent);
                //Store.board_num = Store.sendboard.get(position).board_num;
                Store.board_num = contents.get(0).board_num;
                CameraActivity.getInstance().overridePendingTransition(R.anim.goup, R.anim.godown);
            }
        });

        // 게시글 이미지 갯수
        custom_imgcount = (TextView)layoutView.findViewById(R.id.camera_custom_imgcount);
        custom_imgcount.setTextSize( textImgCountSIze / (float)percentageSize );
        if( contents.get( pageIndex ).photo_count == 1 )
            custom_imgcount.setText("") ;
        else
            custom_imgcount.setText( String.valueOf( contents.get( pageIndex ).photo_count ) );

        // 게시글 작성자 icon
        custom_userimg = (ImageView)layoutView.findViewById(R.id.camera_custom_userimage);
        if( contents.get( pageIndex ).user_photo != null )
            Glide.with( CameraActivity.getInstance() ).load( contents.get( pageIndex ).user_photo ).apply(bitmapTransform(new CircleCrop())).into( custom_userimg );
        else
            Glide.with( CameraActivity.getInstance() )
                    .load( R.drawable.user )
                    .apply(new RequestOptions().override(100, 100).placeholder(R.drawable.user))
                    .into( custom_userimg );

        // 게시글 첫번째 이미지
        custom_firstimg = (ImageView)layoutView.findViewById(R.id.camera_custom_firstimg);
        if( contents.get( pageIndex ).photo_count != 0 )
        {
            Glide.with(CameraActivity.getInstance())
                    .load(contents.get( pageIndex ).board_photo)
                    .into(custom_firstimg);
        }
        else
        {
            FrameLayout frame = (FrameLayout)layoutView.findViewById(R.id.camera_frameLayout);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( 0,0 );
            frame.setLayoutParams( layoutParams );
        }
        custom_context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent( CameraActivity.getInstance(), ReadActivity.class);

                //여기는 DB에서 게시글번호를 가져와서 스트링으로 넣어주면 됨  intent.putExtra("Board_num","")
                CameraActivity.getInstance().startActivity(intent);
                //Store.board_num = Store.sendboard.get(position).board_num;
                Store.board_num = contents.get(0).board_num;
                CameraActivity.getInstance().overridePendingTransition(R.anim.goup, R.anim.godown);
            }
        });

        // 게시글 페이지 표시
        textViewPage = (TextView)layoutView.findViewById( R.id.camera_page );
        textViewPage.setTextSize( pageTextSize / (float)percentageSize );
        textViewPage.setText( (pageIndex+1) + " / " + contents.size() );

        // 게시글 좌,우 버튼
        btLeftPage = (ImageView)layoutView.findViewById(R.id.camera_left_page);
        btLeftPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( pageIndex > 0 ) // 1페이지보다 작지 않으면
                {
                    pageIndex--;
                    setBoardChanged( pageIndex );
                }
            }
        });

        btRightPage = (ImageView)layoutView.findViewById(R.id.camera_right_page);
        btRightPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( pageIndex < contents.size()-1 ) // 총 게시글 갯수를 넘지 않으면
                {
                    pageIndex++;
                    setBoardChanged( pageIndex );
                }
            }
        });

        layoutWidth = (int)( LAYOUT_WIDTH / percentageSize );
        layoutHeight =  (int)( LAYOUT_HEIGHT / percentageSize );
        setRect( layoutWidth, layoutHeight );
    }

    public void setmNumber( int num ) { mCount = num; }

    public void setDistance( double dis ) { mDistance = dis; }

    public void setScreenRatio( double ratio )
    {
        mScreenWIdthRatio = ratio;
    }

    public void setMvecAbsolutePosition( Vector2 abs ){ mvecAbsolutePosition = abs; }
    public void setMvecRelativePosition( Vector2 rel ){ mvecRelativePosition = rel; }

    public void reSizeLayout( int zoom )
    {
        changedDistance = mDistance - zoom;
        double percentageSize = ( changedDistance / 10 ) + 1; // 1 ~ 1/5 배율로 거리 표시

        // 게시글 작성자 id
        custom_id.setTextSize( textIdSIze / (float)percentageSize );

        // 게시글 내용
        custom_context.setTextSize( textContextSize / (float)percentageSize );

        // 게시글 이미지 갯수
        custom_imgcount.setTextSize( textImgCountSIze / (float)percentageSize );

        // 게시글 페이지 표시
        textViewPage.setTextSize( pageTextSize / (float)percentageSize );

        layoutWidth = (int)( LAYOUT_WIDTH / percentageSize );
        layoutHeight =  (int)( LAYOUT_HEIGHT / percentageSize );

        setRect( layoutWidth, layoutHeight );
        CustomCamera.getInstnce().setCameraZoom( zoom );
    }

    public void setBoardChanged( int pageIndex )
    {
        // 작성자 ID 변경
        custom_id.setText( contents.get( pageIndex ).id );

        // 작성자 이미지 변경
        if( contents.get( pageIndex ).user_photo != null )
            Glide.with( CameraActivity.getInstance() ).load( contents.get( pageIndex ).user_photo ).apply(bitmapTransform(new CircleCrop())).into( custom_userimg );
        else
            Glide.with( CameraActivity.getInstance() )
                    .load( R.drawable.user )
                    .apply(new RequestOptions().override(100, 100).placeholder(R.drawable.user))
                    .into( custom_userimg );

        // 게시글 내용 변경
        custom_context.setText( contents.get( pageIndex ).content );

        // 게시글 첫번째 이미지 카운트 변경
        if( contents.get( pageIndex ).photo_count == 1 )
            custom_imgcount.setText("") ;
        else
            custom_imgcount.setText( String.valueOf( contents.get( pageIndex ).photo_count ) );

        // 게시글 첫번째 이미지 변경
        if( contents.get( pageIndex ).photo_count != 0 )
        {
            Glide.with(CameraActivity.getInstance())
                    .load(contents.get( pageIndex ).board_photo)
                    .into(custom_firstimg);
        }
        else
        {
            FrameLayout frame = (FrameLayout)layoutView.findViewById(R.id.camera_frameLayout);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( 0,0 );
            frame.setLayoutParams( layoutParams );
        }

        // 게시글 페이지 변경
        textViewPage.setText( (pageIndex+1) + " / " + contents.size() );

    }

    @Override
    public int compareTo(@NonNull CommentVector2 commentVector2) { //  내림차순으로 compare , 가까운 순서대로 먼저 뿌려야 하므로
        if( mDistance > commentVector2.mDistance )
            return -1;
        else if( mDistance < commentVector2.mDistance )
            return 1;

        return 0;
    }

    public void setRect( int w, int h )
    {
        wrapLayoutParams.width = w;
        wrapLayoutParams.height = h;
        wrapLayout.setLayoutParams( wrapLayoutParams );
    }

    public void show()
    {
        CameraActivity.getInstance().camera_rel.addView( layoutView );
        mAddView = true;
    }
    public void hide()
    {
        CameraActivity.getInstance().camera_rel.removeView( layoutView );
        mAddView = false;
    }

    public void setXY( float x, float y )
    {
        layoutView.setX(x);
        layoutView.setY(y);
    }


}