package com.mini_mo.viewpager.DAO;

import android.os.Parcel;
import android.os.Parcelable;

public class ListViewItemData implements Parcelable
{
    public int board_num;
    public String content;
    public String date_board;
    public int good;
    public double latitude;
    public double longitude;
    public String user_id;
    public String user_photo;
    public int category;
    public int comment_cnt;
    public String nickname;

    public ListViewItemData()
    {

    }

    protected ListViewItemData(Parcel in) {
        board_num = in.readInt();
        content = in.readString();
        date_board = in.readString();
        good = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        user_id = in.readString();
        user_photo = in.readString();
        category = in.readInt();
        comment_cnt = in.readInt();
        nickname = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(board_num);
        dest.writeString(content);
        dest.writeString(date_board);
        dest.writeInt(good);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(user_id);
        dest.writeString(user_photo);
        dest.writeInt(category);
        dest.writeInt(comment_cnt);
        dest.writeString(nickname);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ListViewItemData> CREATOR = new Creator<ListViewItemData>() {
        @Override
        public ListViewItemData createFromParcel(Parcel in) {
            return new ListViewItemData(in);
        }

        @Override
        public ListViewItemData[] newArray(int size) {
            return new ListViewItemData[size];
        }
    };
}
