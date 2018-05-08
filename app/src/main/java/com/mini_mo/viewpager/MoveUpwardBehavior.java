package com.mini_mo.viewpager;

/*
 * Created by 노현민 on 2018-04-20.
 */

import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;

public class MoveUpwardBehavior extends CoordinatorLayout.Behavior<View> {
    private static final boolean SNACKBAR_BEHAVIOR_ENABLED;

    // 스낵바가 움직일 때 OR 사용자가 스크롤 시 실행
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return SNACKBAR_BEHAVIOR_ENABLED && dependency instanceof Snackbar.SnackbarLayout;
    }
    // layoutDependsOn이 true를 반환할 시 호출
    // 동작 구현 메소드
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }

    static {
        SNACKBAR_BEHAVIOR_ENABLED = Build.VERSION.SDK_INT >= 11;
    }
}