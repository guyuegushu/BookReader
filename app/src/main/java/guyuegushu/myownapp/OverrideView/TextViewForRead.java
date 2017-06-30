package guyuegushu.myownapp.OverrideView;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import guyuegushu.myownapp.Activity.ReadBook;
import guyuegushu.myownapp.Interface.PageOverListener;
import guyuegushu.myownapp.R;
import guyuegushu.myownapp.Util.LogUtil;

/**
 * Created by Administrator on 2017/6/26.
 */

public class TextViewForRead extends TextView {

    private String text;
    private double textSize,
            marginStart,
            marginEnd,
            practicalWidth,
            charHeight,
//            lineSpaceHeight,
            practicalPageLineCount;//PL maxLine
    private int textColor;

    private Paint textPaint;

    private int lineCount = 0;
    private int charCount = 0;

    private PageOverListener pageOverListener;


    public TextViewForRead(Context context) {
        this(context, null);
    }

    public TextViewForRead(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextViewForRead(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        pageOverListener = new ReadBook();

        textPaint = new Paint();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewForRead);
        text = typedArray.getString(R.styleable.TextViewForRead_android_text);
        textColor = typedArray.getColor(R.styleable.TextViewForRead_textColor, Color.BLACK);
        textSize = typedArray.getDimensionPixelSize(R.styleable.TextViewForRead_textSize, 20);
        marginEnd = typedArray.getDimensionPixelSize(R.styleable.TextViewForRead_android_layout_marginEnd, 0);
        marginStart = typedArray.getDimensionPixelSize(R.styleable.TextViewForRead_android_layout_marginStart, 0);

        textPaint.setColor(textColor);
        textPaint.setTextSize((float) textSize);
        textPaint.setAntiAlias(true);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        charHeight = Math.ceil(fontMetrics.descent - fontMetrics.ascent);

//        lineSpaceHeight = 0.5 * charHeight;

        practicalWidth = ((Activity) context)
                .getWindowManager()
                .getDefaultDisplay()
                .getWidth() - (marginStart + marginEnd);

        practicalPageLineCount = Math.floor(((Activity) context)
                .getWindowManager()
                .getDefaultDisplay()
                .getHeight() / (charHeight));

        LogUtil.d("practicalPageLineCount= " + practicalPageLineCount);
        LogUtil.d("charHeight= " + charHeight);

    }

    public TextViewForRead(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //标志位，是否需要对linecount进行计数，从setText处进行设置变化
    boolean isNeedCount = true;

    @Override
    protected void onDraw(Canvas canvas) {

        int tmpLineCount = 0;
        int tmpCharCount = 0;

        text = this.getText().toString();

        char[] textCharArray = text.toCharArray();

        //the width already used
        double drawedWidth = 0;

        //the width for a single char
        double charWidth =textSize;
        for (int i = 0; i < textCharArray.length; i++) {

//            charWidth = textPaint.measureText(textCharArray, i, 1);

            tmpCharCount++;

            if (isNeedCount) {
                charCount = tmpCharCount;
            }

            if (textCharArray[i] == '\n') {

                tmpLineCount++;

                if (isNeedCount) {
                    lineCount = tmpLineCount;
                }

                drawedWidth = 0;

                if (lineCount > practicalPageLineCount) {
                    lineCount = 0;
                    //再把charCount传出去
                    pageOverListener.onePageOver(charCount);
                    charCount = 0;
                    isNeedCount = false;
                    return;
                } else if (lineCount > practicalPageLineCount && ++i < textCharArray.length) {
                    return;
                }

                continue;
            }

            if (lineCount > practicalPageLineCount) {
                lineCount = 0;
                //再把charCount传出去
                pageOverListener.onePageOver(charCount);
                charCount = 0;
                isNeedCount = false;
                return;
            }

            // space is not enough to drawing a single char
            if ((practicalWidth - drawedWidth) < charWidth) {

                tmpLineCount++;
                if (isNeedCount) {
                    lineCount = tmpLineCount;
                }

                drawedWidth = 0;
            }

            float y = (float) ((tmpLineCount+1) * (charHeight));

            LogUtil.d("lineCount=  " + lineCount);
            LogUtil.d("tmpLineCount=  " + tmpLineCount);
            LogUtil.d("y= " + y);
            LogUtil.d("textCharArray= " + textCharArray[i]);
            LogUtil.d("textCharArraySize = " + charWidth + " | " + charHeight );

            canvas.drawText(textCharArray, i, 1, (float) drawedWidth, y, textPaint);
            drawedWidth += charWidth;
        }

//        setHeight((lineCount + 1) * (int) textSize + (int) (lineCount * lineSpaceHeight));
//        isNeedCount = false;

    }
}
