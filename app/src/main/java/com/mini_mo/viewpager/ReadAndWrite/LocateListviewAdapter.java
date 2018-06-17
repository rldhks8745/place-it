package com.mini_mo.viewpager.ReadAndWrite;

import android.app.Activity;
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

public class LocateListviewAdapter extends BaseAdapter {

    Activity activity;

    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<LocateListviewItem> LocateItems = new ArrayList<>();

    public LocateListviewAdapter(){

    }

    public LocateListviewAdapter(Activity activity){
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return LocateItems.size();
    }

    @Override
    public LocateListviewItem getItem(int position) {

        if (position == getCount()){
            position--;
        }

        Util.Log("포지션 값3" , String.valueOf(position));

        return LocateItems.get((position));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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

        LocateListviewItem myItem = getItem(position);

        String address = AddressTransformation.getAddress(activity,myItem.getLatitude(),myItem.getLongitude());

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        tv_locate.setText(address);
        tv_message.setText(myItem.getMessage());

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */


        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(double latitude, double longitude, int savenumber, String message) {

        LocateListviewItem LocateItem = new LocateListviewItem();

        /* MyItem에 아이템을 setting한다. */

        LocateItem.setLocate(latitude,longitude);
        LocateItem.setMessage(message);
        LocateItem.setSaveNumber(savenumber);

        /* LocateItems에 MyItem을 추가한다. */
        LocateItems.add(LocateItem);

    }

    public double[] getLocate(int position){
        double[] arr = new double[2];

        arr[0] = LocateItems.get(position).getLatitude();
        arr[1] = LocateItems.get(position).getLongitude();

        return arr;
    }

    public int getSaveNumber(int position){
        return LocateItems.get(position).getSaveNumber();
    }

    public void removeItem(int positoin) {

        LocateItems.remove(positoin);

    }
}
