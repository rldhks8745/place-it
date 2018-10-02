package com.mini_mo.viewpager.ReadAndWrite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.Store;
import com.mini_mo.viewpager.YourPageActivity;

import org.json.JSONException;

import java.util.ArrayList;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by sasor on 2018-04-14.
 */

public class CustomAdapter extends BaseAdapter {

    View view;
    Data data;

    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<CustomListviewitem> mItems = new ArrayList<>();

    View.OnClickListener clickListener;
    Activity activity;

    public CustomAdapter(Activity activity,View.OnClickListener listener){
        this.activity = activity;
        clickListener = listener;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public CustomListviewitem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rnw_custom_listview, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        ImageView iv_img = (ImageView) convertView.findViewById(R.id.usericon);
        TextView tv_nickname = (TextView) convertView.findViewById(R.id.nickname);
        TextView tv_date = (TextView) convertView.findViewById(R.id.date);
        ImageView tv_image = (ImageView) convertView.findViewById(R.id.image);
        TextView tv_title = (TextView) convertView.findViewById(R.id.contents);


        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        final CustomListviewitem myItem = getItem(position);

        /*if(!Store.userid.equals(myItem.getNickname())){
            linearLayout.removeView(button);

            iv_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(view.getContext(), position+"번째 리스트", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, YourPageActivity.class);
                    intent.putExtra("id",myItem.getNickname());
                    activity.startActivity(intent);
                }
            });
        }else{
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(view.getContext(), position+"번째 리스트", Toast.LENGTH_SHORT).show();

                    CustomListviewitem Item = getItem(position);

                    data = new Data();
                    mItems.remove(position);
                    try {
                        data.deleteComment(Integer.parseInt(myItem.getBoard_number()),Integer.parseInt(myItem.getComment_number()));// DB에 DELETE해주는 메소드에 넣으면 OK
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    notifyDataSetChanged();
                }
            });
        }*/

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        if(myItem.getComment_id().equals("Guest")) {

            if (myItem.getGuestphoto() == 1) {
                iv_img.setImageResource(R.drawable.comment_1);
            } else if (myItem.getGuestphoto() == 2) {
                iv_img.setImageResource(R.drawable.comment_2);
            } else if (myItem.getGuestphoto() == 3) {
                iv_img.setImageResource(R.drawable.comment_3);
            } else if (myItem.getGuestphoto() == 4) {
                iv_img.setImageResource(R.drawable.comment_4);
            } else if (myItem.getGuestphoto() == 5) {
                iv_img.setImageResource(R.drawable.comment_5);
            }


        }else{
            if(!(myItem.getUserphoto().equals("No Photo"))) {

                Glide.with(convertView).load(myItem.getUserphoto()).apply(bitmapTransform(new CircleCrop())).into(iv_img);

            }else {
                Glide.with(convertView)
                        .load(myItem.getUserphoto())
                        .apply(new RequestOptions().override(100, 100).placeholder(R.drawable.user))
                        .into(iv_img);
            }
        }

        if(!(myItem.getPhoto().equals("No Photo"))) {
            Glide.with(convertView)
                    .load( myItem.getPhoto())
                    .apply( new RequestOptions().override(300,300).placeholder(R.drawable.noimg).error(R.drawable.noimg))
                    .into(tv_image);
        }

        tv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(activity, Comment_Image_Activity.class);
               intent.putExtra("img",myItem.getPhoto());
               activity.startActivity(intent);
            }
        });

        //Glide.with(convertView).load(myItem.getIcon()).apply(bitmapTransform(new CircleCrop())).into(iv_img);
        tv_nickname.setText(myItem.getNickname());
        tv_date.setText(myItem.getDate().substring(0,16));
        tv_title.setText(myItem.getTile());

        view = convertView;

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */
        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(String board_num,String comment_num,String comment_id,int guestphoto,String userphoto, String title, String nickname, String date, String photo) {

        CustomListviewitem mItem = new CustomListviewitem();

        /* MyItem에 아이템을 setting한다. */

        mItem.setBoard_number(board_num);
        mItem.setComment_number(comment_num);
        mItem.setComment_id(comment_id);
        mItem.setUserphoto(userphoto);
        mItem.setGuestphoto(guestphoto);
        mItem.setNickname(nickname);
        mItem.setTitle(title);
        mItem.setDate(date);
        mItem.setPhoto(photo);


        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);

    }
}
