package com.mini_mo.viewpager.Camera;

import java.util.Vector;

/**
 * Created by userForGame on 2018-04-20.
 */

public class Vector2 {
    public double x = 0.0f;
    public double y = 0.0f;

    public Vector2(){}
    public Vector2( double x, double y )
    {
        this.x = x;
        this.y = y;
    }

    public void setX( double x ) { this.x = x; }
    public void setY( double y ) { this.y = y; }

    public void scale( double d )
    {
        x *= d;
        y *= d;
    }

    // 노말벡터로 만들기
    public void normalize()
    {
        double scale = getLength();
        x = x / scale; // X 노멀화
        y = y / scale; // Y 노멀화
    }

    public static Vector2 normalize( Vector2 v )
    {
        Vector2 tv = new Vector2( v.x, v.y );
        double scale = tv.getLength();
        tv.x = tv.x / scale; // X 노멀화
        tv.y = tv.y / scale; // Y 노멀화

        return tv;
    }

    public double getLength()
    {
        return Math.sqrt( ( x * x ) + ( y * y ) );
    }

    public static Vector2 subtract( Vector2 v1, Vector2  v2)
    {
        return new Vector2( v1.x - v2.x, v1.y - v2.y );
    }
    public static Vector2 add( Vector2 v1, Vector2  v2)
    {
        return new Vector2( v1.x + v2.x, v1.y + v2.y );
    }
    public static double multiply( Vector2 v1, Vector2 v2) // 내적
    {
        return (v1.x * v2.x) + (v1.y * v2.y);
    }
}
