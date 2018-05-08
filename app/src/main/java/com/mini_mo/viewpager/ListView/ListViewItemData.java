package com.mini_mo.viewpager.ListView;

import android.graphics.Bitmap;

/*
 * Created by 노현민 on 2018-04-29.
 */

public class ListViewItemData {

    /**
     * ID : 작성자 아이디
     * userPhoto : 작성자 아이콘
     * board_content : 내용
     * date_board : 작성일
     * like : 좋아요 수
     * photo_count : 사진 갯수
     * ( 사진 여부 판별 : IF ( photo_count == 0 )     사진 X
     *                    ELSE IF ( photo_count > 0 ) 사진 O
     * board_num : 글 번호 ( 해당 코멘트 클릭 시 (intent 시 ) 넘겨 주어야 하는 정보
     * */
    public int Board_num; //게시글 번호
    public String ID; // 코멘트 작성자 아이디
    public Bitmap userPhoto; // imageView.setBitmapImage
    public String board_content;
    public String date_board;
    public int like;
    public int photo_count; // 사진 수
    public int board_num; // intent시 글 번호 넘겨 줄 것
}
