package com.mini_mo.viewpager.DAO;

import java.util.ArrayList;

public class Board_Location
{
    public double latitude;
    public double longitude;
    public int board_count;
    public ArrayList<Board_Location_List> bll;

    public Board_Location()
    {
        bll = new ArrayList<Board_Location_List>();
    }
    public Board_Location( double lat, double lon, int count )
    {
        latitude = lat;
        longitude = lon;
        board_count = count;
    }
}
