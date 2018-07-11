package com.mini_mo.viewpager.ListView;

/*
 * Created by 노현민 on 2018-04-29.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.MainActivity;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.ReadAndWrite.ReadActivity;
import com.mini_mo.viewpager.Store;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


// RecyclerView 어댑터
// ViewHolder : 뷰들을 홀더에 꼽아놓듯 보관하는 객체
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public static Fragment instance = null ;
    public AppCompatActivity activity = null ;

    private ArrayList<ListViewItemData> listViewItems;
    private int itemLayout;
    //public ListViewItemData item;

    /**
     * 생성자
     * @param items
     * @param itemLayout
     */
    public RecyclerViewAdapter(ArrayList<ListViewItemData> items , int itemLayout , Fragment fragment){

        this.listViewItems = items;
        this.itemLayout = itemLayout;
        this.instance = fragment;
    }

    public RecyclerViewAdapter(ArrayList<ListViewItemData> items , int itemLayout , AppCompatActivity activity ){

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
        final ListViewItemData item = listViewItems.get(position);

        // 값 설정 ( set )
        viewHolder.userId.setText(item.user_id);
        viewHolder.contents.setText(item.content);
        viewHolder.date.setText(item.date_board);
        // 사진 넣기

        if(item.user_photo!=null) {
            Glide.with(viewHolder.mView).asBitmap().load(item.user_photo)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Bitmap bitmap = ReSizing(bitmapToByteArray(resource));

                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(instance.getResources(), bitmap);
                            roundedBitmapDrawable.setCircular(true);

                            viewHolder.userIcon.setImageDrawable(roundedBitmapDrawable);
                        }
                    });
        }else {
            Glide.with(viewHolder.mView)
                    .load(item.user_photo)
                    .apply(new RequestOptions().override(100, 100).placeholder(R.drawable.user))
                    .into(viewHolder.userIcon);
        }

        /** 각각의 Item의 클릭 이벤트 --> 글 자세히 보기 액티비티 전환 **/

        //Here it is simply write onItemClick listener here
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                //Toast.makeText(context, position+"", Toast.LENGTH_LONG).show();
                Intent intent;

                if( instance != null ) {
                    intent = new Intent(instance.getActivity(), ReadActivity.class);
                }
                else
                    intent = new Intent( activity, ReadActivity.class );
                //여기는 DB에서 게시글번호를 가져와서 스트링으로 넣어주면 됨  intent.putExtra("Board_num","")
                instance.startActivity(intent);
                //Store.board_num = Store.sendboard.get(position).board_num;
				Store.board_num = item.board_num;
                instance.getActivity().overridePendingTransition(R.anim.goup, R.anim.godown);
            }
        });

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
        public TextView userId;
        public TextView date;
        public TextView contents;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            // 레이아웃 객체화 findViewById
            userIcon = (ImageView) itemView.findViewById(R.id.usericon);
            userId = (TextView) itemView.findViewById(R.id.userid);
            date = (TextView) itemView.findViewById(R.id.date);
            contents = (TextView) itemView.findViewById(R.id.contents);
        }
    }

    static public Bitmap ReSizing(byte[] bytes){
        Bitmap bitmap;

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inSampleSize = 4;
        opt.inDither = true;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inTempStorage = new byte[32 * 1024];
        bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length,opt);

        return bitmap;
    }

    static public byte[] bitmapToByteArray( Bitmap bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }
}