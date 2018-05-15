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
import android.widget.Toast;

import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.ReadAndWrite.ReadActivity;
import com.mini_mo.viewpager.Store;

import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;

// RecyclerView 어댑터
// ViewHolder : 뷰들을 홀더에 꼽아놓듯 보관하는 객체
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public static Fragment instance ;
    public AppCompatActivity activity;

    private ArrayList<ListViewItemData> listViewItems;
    private int itemLayout;

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
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        ListViewItemData item = listViewItems.get(position);

        // 값 설정 ( set )
        /** 각각의 Item의 클릭 이벤트 --> 글 자세히 보기 액티비티 전환 **/
        //Here it is simply write onItemClick listener here
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                //Toast.makeText(context, position+"", Toast.LENGTH_LONG).show();
                Intent intent;

                if( instance != null )
                    intent = new Intent( instance.getActivity(), ReadActivity.class );
                else
                    intent = new Intent( activity, ReadActivity.class );

                //여기는 DB에서 게시글번호를 가져와서 스트링으로 넣어주면 됨  intent.putExtra("Board_num","")
                instance.startActivity(intent);
                Store.board_num = 1; // 게시글번호를 서버에서 받으면 이쪽에 넣어주기
                instance.getActivity().overridePendingTransition(R.anim.goup, R.anim.godown);
            }
        });

    }

    //테스트

    //테스트

    @Override
    public int getItemCount() {
        return listViewItems.size();
    }

    /**
     * 뷰 재활용을 위한 viewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        public ViewHolder(View itemView){
            super(itemView);
            mView = itemView;

            // 레이아웃 객체화 findViewById
        }
    }
}