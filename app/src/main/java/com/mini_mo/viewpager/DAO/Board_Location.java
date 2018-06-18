package com.mini_mo.viewpager.DAO;

public class Board_Location
{
    public double latitude;
    public double longitude;
    public int board_count;

    public Board_Location()
    {

    }
    public Board_Location( double lat, double lon, int count )
    {
        latitude = lat;
        longitude = lon;
        board_count = count;
    }
}
