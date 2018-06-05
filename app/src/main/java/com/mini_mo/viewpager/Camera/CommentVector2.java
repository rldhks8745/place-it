package com.mini_mo.viewpager.Camera;


import android.support.annotation.NonNull;


public class CommentVector2 implements Comparable<CommentVector2>{
    public int mCount; // 디비 게시글 수
    public Vector2 mvecAbsolutePosition = null; // 현재 코멘트 위치 절대좌표 ( 경도(x), 위도(y) )

    public Vector2 mvecScreenPos = null; // 핸드폰 화면 좌표
    public float radius; // 코멘트 반지름 길이

    public Vector2 mvecRelativePosition = null; // 현재위치에서부터 현재 코멘트까지의 상대거리
    public double mDistance = 0.0; // 현재 위치와 코멘트 간의 거리
    public boolean mIsinCamera = false; // 카메라 각도 안에 들어왔나?
    public double mScreenWIdthRatio = 0.0; // 화면가로 어디에 코멘트를 띄워야할지 비율

    public CommentVector2( int count, Vector2 pos )
    {
        mvecScreenPos = new Vector2();
        mCount = 0;
        mCount = count;
        mvecAbsolutePosition = pos;
    }
    public CommentVector2(Vector2 absPos, Vector2 relPos )
    {
        mvecScreenPos = new Vector2();
        // 코멘트 벡터 생성
        mvecAbsolutePosition = absPos;
        mvecRelativePosition = relPos;
        mDistance = 0.0;
        mCount = 0;
    }

    public CommentVector2(Vector2 absPos, Vector2 relPos, double distance, int count )
    {
        mvecScreenPos = new Vector2();
        // 코멘트 벡터 생성
        mvecAbsolutePosition = absPos;
        mvecRelativePosition = relPos;
        mDistance = distance;
        mCount = 0;
        mCount = count;
    }

    public void setmNumber( int num ) { mCount = num; }

    public void setDistance( double dis ) { mDistance = dis; }

    public void setScreenRatio( double ratio )
    {
        mScreenWIdthRatio = ratio;
    }


    @Override
    public int compareTo(@NonNull CommentVector2 commentVector2) { //  내림차순으로 compare , 가까운 순서대로 먼저 뿌려야 하므로
        if( mDistance > commentVector2.mDistance )
            return -1;
        else if( mDistance < commentVector2.mDistance )
            return 1;

        return 0;
    }
}