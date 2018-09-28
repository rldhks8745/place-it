package com.mini_mo.viewpager;

import android.app.Activity;
import android.content.Context;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.mini_mo.viewpager.ReadAndWrite.Util;

import java.util.ArrayList;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class SearchListViewAdapter extends BaseAdapter {

    public static SearchListViewAdapter instance;
    public static boolean isShowing = false;
    Activity activity;

    /* 아이템을 세트로 담기 위한 어레이 */
    public ArrayList<SearchListViewItem> searchListViewItems = new ArrayList<>();

    public static SearchListViewAdapter getInstance()
    {
        return instance;
    }

    public SearchListViewAdapter(){
        instance = this;
    }

    @Override
    public int getCount() {
        return searchListViewItems.size();
    }

    @Override
    public SearchListViewItem getItem(int position) {
        return searchListViewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        //-----------'listview_custom' Layout을 inflate하여 convertView 참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item1, parent, false);
        }

        //-----------'listview_custom'에 정의된 위젯에 대한 참조 획득
        ImageView profile = (ImageView)convertView.findViewById(R.id.usericon);
        TextView id = (TextView)convertView.findViewById(R.id.userid);
        TextView text = (TextView)convertView.findViewById(R.id.contents);
        TextView date = (TextView)convertView.findViewById(R.id.date);
        TextView good = (TextView)convertView.findViewById(R.id.like);
        TextView comment = (TextView)convertView.findViewById(R.id.comment);

        //-----------각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용
        SearchListViewItem myItem = getItem(position);

        //-----------여기서 리스트뷰 xml에 각각 값들 주면 됨.
        if(!(myItem.getUser_photo().equals("No Photo"))) {
            Glide.with(convertView).load(myItem.getUser_photo()).apply(bitmapTransform(new CircleCrop())).into(profile);
        }else {
            Glide.with(convertView)
                    .load(myItem.getUser_photo())
                    .apply(new RequestOptions().override(100, 100).placeholder(R.drawable.user))
                    .into(profile);
        }

        id.setText(myItem.getUser_id());
        text.setText(myItem.getContent());
        date.setText(myItem.getDate_board());
        good.setText(String.valueOf(myItem.getGood()));
        comment.setText(String.valueOf(myItem.getCommentSize()));


        return convertView;
    }

    public void addItem(int board_num,String content,String date_board, int good, double latitude, double longitude, String user_id, String user_photo,int commentsize) {

        SearchListViewItem searchListViewItem = new SearchListViewItem(board_num,content,date_board,good,latitude,longitude,user_id,user_photo,commentsize);

        /* LocateItems에 MyItem을 추가한다. */
        searchListViewItems.add(searchListViewItem);
    }

    public double[] getLocate(int position){

        return searchListViewItems.get(position).getLocate();
    }

    public int getBoardNumber(int position){
        return searchListViewItems.get(position).getBoard_num();
    }

    public void removeItem(int positoin) {

        searchListViewItems.remove(positoin);

    }
}
