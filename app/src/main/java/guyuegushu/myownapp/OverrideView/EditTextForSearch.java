package guyuegushu.myownapp.OverrideView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import guyuegushu.myownapp.R;

/**
 * Created by guyuegushu on 2017/2/17.
 *
 */

public class EditTextForSearch extends EditText implements OnFocusChangeListener, TextWatcher {


    public EditTextForSearch(Context context) {
        this(context, null);
    }

    public EditTextForSearch(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public EditTextForSearch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSearchBar();
    }

//    public EditTextForSearch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        initSearchBar();
//    }

    private Drawable cancelDrawable;

    private void initSearchBar() {
        cancelDrawable = getCompoundDrawables()[2];
        if (cancelDrawable == null) {
            cancelDrawable = getResources().getDrawable(R.drawable.search_cancel, null);
        }
        cancelDrawable.setBounds(0, 0, cancelDrawable.getIntrinsicWidth(), cancelDrawable.getIntrinsicHeight());
        setCancelDrawableVisible(false);
        setOnFocusChangeListener(this);
    }

    protected void setCancelDrawableVisible(boolean visible) {
        Drawable right;
        if (visible) {
            right = cancelDrawable;
        } else {
            right = null;
        }
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1],
                right, getCompoundDrawables()[3]);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (getCompoundDrawables()[2] != null) {
            int right = getWidth() - getPaddingRight() - cancelDrawable.getIntrinsicWidth();
            int left = getWidth() - getPaddingRight();
            if (event.getAction() == MotionEvent.ACTION_UP) {
                boolean touchable = event.getX() > right && event.getX() < left;
                if (touchable) {
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        setCancelDrawableVisible(text.length() > 0);
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        setCancelDrawableVisible((getText().length() > 0));

//        if (hasFocus) {
//            setCancelDrawableVisible((getText().length() > 0));
//        } else {
//            setCancelDrawableVisible(false);
//        }
    }

}
