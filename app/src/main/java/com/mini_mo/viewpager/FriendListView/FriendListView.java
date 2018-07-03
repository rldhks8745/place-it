package com.mini_mo.viewpager.FriendListView;

/*
 * Created by 노현민 on 2018-04-21.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mini_mo.viewpager.DAO.FriendsList;
import com.mini_mo.viewpager.R;

import java.util.ArrayList;
public class FriendListView extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    public ArrayList<FriendsList> listViewItemList = new ArrayList<FriendsList>() ;

    // FriendListView의 생성자
    public FriendListView() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position : 리턴 할 자식 뷰의 위치
    // convertView : 메소드 호출 시 position에 위치하는 자식 뷰 ( if == null 자식뷰 생성 )
    // parent : 리턴할 부모 뷰, 어댑터 뷰
    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friend_listview, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.usericon) ;
        TextView userId = (TextView)  convertView.findViewById(R.id.userid);
        TextView messgae = (TextView)  convertView.findViewById(R.id.message);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        FriendsList listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영 ( Test 중 )
        //iconImageView.setImageDrawable(listViewItem. user_photo);
        userId.setText(listViewItem.user_id);
        messgae.setText(listViewItem.message);

        // photo 비트맵으로 전환
        // 개꿀 글라이드
        Glide.with( context )
                .load( listViewItemList.get( position ).user_photo )
                .apply( new RequestOptions().override(100,100).placeholder( R.drawable.usericon ).error( R.drawable.usericon  ))
                .into( iconImageView );

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    public void add(ArrayList<FriendsList> items, Context context )
    {
        listViewItemList.clear(); // 클리어

        for( int i = 0; i < items.size(); i++ )
        {
            listViewItemList.add( items.get(i) );
        }
    }


}
