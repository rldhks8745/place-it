package com.mini_mo.viewpager.ReadAndWrite;

import android.content.Context;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mini_mo.viewpager.R;

import java.util.ArrayList;

/**
 * Created by sasor on 2018-04-14.
 */

public class CustomAdapter extends BaseAdapter {

    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<CustomListviewitem> mItems = new ArrayList<>();

    public CustomAdapter(){

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
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rnw_custom_listview, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        ImageView iv_img = (ImageView) convertView.findViewById(R.id.image) ;
        TextView tv_title = (TextView) convertView.findViewById(R.id.title) ;
        TextView tv_date = (TextView) convertView.findViewById(R.id.date) ;

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        CustomListviewitem myItem = getItem(position);



        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        iv_img.setImageDrawable(myItem.getIcon());
        tv_title.setText(myItem.getTile());
        tv_date.setText(myItem.getDate());

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */


        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(RoundedBitmapDrawable icon, String title, String date) {

        CustomListviewitem mItem = new CustomListviewitem();

        /* MyItem에 아이템을 setting한다. */

        mItem.setTitle(title);
        mItem.setDate(date);
        mItem.setIcon(icon);

        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);

    }
}
