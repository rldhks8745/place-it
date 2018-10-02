package com.mini_mo.viewpager.ListView;

/*
 * Created by 노현민 on 2018-04-29.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.mini_mo.viewpager.DAO.ReadCommentInfo;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.ReadAndWrite.ReadActivity;
import com.mini_mo.viewpager.Store;

import java.util.ArrayList;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

;


// RecyclerView 어댑터
// ViewHolder : 뷰들을 홀더에 꼽아놓듯 보관하는 객체
public class RecyclerViewAdapter_review extends RecyclerView.Adapter<RecyclerViewAdapter_review.ViewHolder> { ;

    public static Fragment instance = null ;
    public AppCompatActivity activity = null ;

    private ArrayList<ReadCommentInfo> listViewItems;
    private int itemLayout;
    //public ListViewItemData item;

    /**
     * 생성자
     * @param items
     * @param itemLayout
     */
    public RecyclerViewAdapter_review(ArrayList<ReadCommentInfo> items , int itemLayout , Fragment fragment){

        this.listViewItems = items;
        this.itemLayout = itemLayout;
        this.instance = fragment;
    }

    public RecyclerViewAdapter_review(ArrayList<ReadCommentInfo> items , int itemLayout , AppCompatActivity activity ){

        this.listViewItems = items;
        this.itemLayout = itemLayout;
        this.activity = activity;
    }

    /**
     * 레이아웃을 만들어서 Holer에 저장
     * @param viewGroup
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout,viewGroup,false);

        return new ViewHolder(view);
    }

    /**
     * listView getView 를 대체
     * 넘겨 받은 데이터를 화면에 출력하는 역할
     *
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final ReadCommentInfo item = listViewItems.get(position);

        // 값 설정 ( set )
        if(item.comment_id.equals("Guest")) {
            if (item.guest_photo == 1) {
                viewHolder.userIcon.setImageResource(R.drawable.comment_1);
            } else if (item.guest_photo == 2) {
                viewHolder.userIcon.setImageResource(R.drawable.comment_2);
            } else if (item.guest_photo == 3) {
                viewHolder.userIcon.setImageResource(R.drawable.comment_3);
            } else if (item.guest_photo == 4) {
                viewHolder.userIcon.setImageResource(R.drawable.comment_4);
            } else if (item.guest_photo == 5) {
                viewHolder.userIcon.setImageResource(R.drawable.comment_5);
            }
        }else{
            if(!(item.user_photo.equals("No Photo"))) {
                Glide.with(viewHolder.mView).load(item.user_photo).apply(bitmapTransform(new CircleCrop())).into(viewHolder.userIcon);
            }else {
                Glide.with(viewHolder.mView)
                        .load(item.user_photo)
                        .apply(new RequestOptions().override(100, 100).placeholder(R.drawable.user))
                        .into(viewHolder.userIcon);
            }
        }

        if(!(item.comment_photo.equals("No Photo"))) {
            Glide.with(viewHolder.mView)
                    .load( item.comment_photo)
                    .apply( new RequestOptions().override(500,500).placeholder(R.drawable.noimg).error(R.drawable.noimg))
                    .into(viewHolder.image);
        }

        viewHolder.date.setText(item.comment_date.substring(0,16));
        viewHolder.nickname.setText(item.comment_nickname);
        viewHolder.contents.setText(item.comment_content);
    }

    @Override
    public int getItemCount() {
        return listViewItems.size();
    }

    /**
     * 뷰 재활용을 위한 viewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        // 뷰 재활용을 위한 ViewHolder
        public ImageView userIcon;
        public ImageView image;
        public TextView nickname;
        public TextView date;
        public TextView contents;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            // 레이아웃 객체화 findViewById
            userIcon = (ImageView) itemView.findViewById(R.id.usericon);
            image = (ImageView) itemView.findViewById(R.id.image);
            nickname = (TextView) itemView.findViewById(R.id.nickname);
            date = (TextView) itemView.findViewById(R.id.date);
            contents = (TextView) itemView.findViewById(R.id.contents);

        }
    }
}