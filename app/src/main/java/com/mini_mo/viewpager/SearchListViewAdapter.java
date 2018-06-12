package com.mini_mo.viewpager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mini_mo.viewpager.ReadAndWrite.AddressTransformation;
import com.mini_mo.viewpager.ReadAndWrite.LocateListviewItem;

import java.util.ArrayList;

public class SearchListViewAdapter extends BaseAdapter {

    Activity activity;

    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<SearchListViewItem> searchListViewItems = new ArrayList<>();

    public SearchListViewAdapter(){

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

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rnw_locatelistview_item, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        TextView tv_message = (TextView) convertView.findViewById(R.id.message) ;
        TextView tv_locate = (TextView) convertView.findViewById(R.id.locate) ;

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        SearchListViewItem myItem = getItem(position);

        //여기서 리스트뷰 xml에 각각 값들 주면 됨.

        //String address = AddressTransformation.getAddress(activity,myItem.getLatitude(),myItem.getLongitude());

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
       // tv_locate.setText(address);
       // tv_message.setText(myItem.getMessage());

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */


        return convertView;
    }

    public void addItem(int board_num,String content,String date_board, int good, double latitude, double longitude, String user_id, String user_photo) {

        SearchListViewItem searchListViewItem = new SearchListViewItem(board_num,content,date_board,good,latitude,longitude,user_id,user_photo);

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
