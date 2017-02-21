package com.example.veb.bookreader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 */

public class SideBar extends View {
    public SideBar(Context context) {
        super(context);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private final static String[] letter = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};

    private TextView mLetterDialog;

    public void setTextView(TextView mLetterDialog) {

        this.mLetterDialog = mLetterDialog;
    }

    private List<String> list;

    public void setExistLetter(List<String> list) {
        this.list = list;
    }

    private int numOfLetter = letter.length;

    private Paint paint = new Paint();

    private int isChose = -1;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getHeight();
        int width = getWidth();
        int letterHeight = height / numOfLetter;//获取每个字母高度

        for (int i = 0; i < numOfLetter; i++) {

            paint.setColor(Color.BLACK);
            paint.setTextSize(30);
            paint.setTypeface(Typeface.DEFAULT_BOLD);//字体样式
            paint.setAntiAlias(true);//抗锯齿

            if (i == isChose) {
                paint.setColor(Color.RED);
                paint.setFakeBoldText(true);//粗体
            }

            //这个是字母的放置x坐标
            float x = width / 2 - paint.measureText(letter[i]) / 2;
            //这个是字母的放置y坐标
            float y = letterHeight * i + letterHeight;
            canvas.drawText(letter[i], x, y, paint);//在指定画布上绘制文字到指定坐标
            paint.reset();//重置画笔防止下一次绘图任然是上一次设置
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        final int action = event.getAction();
        final float y = event.getY();
        final int oldChose = isChose;
        final OnTouchingLetterChangedListener letterChangedListener = listener;
        // 点击y坐标所占总高度的比例*b数组的长度就等于点击的具体字母.
        final int whichLetter = (int) (y / getHeight() * numOfLetter);

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundResource(R.color.transparent);
                isChose = -1;
                invalidate();
                if (mLetterDialog != null) {
                    mLetterDialog.setVisibility(INVISIBLE);
                }
                break;

            default:
                setBackgroundResource(R.drawable.pressed_sidebar_background);
                if (oldChose != whichLetter) {
                    if (whichLetter >= 0 && whichLetter < numOfLetter) {
                        if (letterChangedListener != null) {
                            letterChangedListener
                                    .onTouchingLetterChanged(letter[whichLetter]);
                        }
                        if (mLetterDialog != null && list.contains(letter[whichLetter])) {
                            mLetterDialog.setText(letter[whichLetter]);
                            mLetterDialog.setVisibility(VISIBLE);
                        }
                        isChose = whichLetter;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String str);
    }

    private OnTouchingLetterChangedListener listener;

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener listener) {

        this.listener = listener;
    }
}
