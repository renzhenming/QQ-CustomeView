package com.ren.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2016/9/15.
 */
public class QuickIndexView extends View {

    private Paint paint;
    private static final String[] LETTERS = new String[] {
            "A", "B", "C", "D",
            "E", "F", "G", "H",
            "I", "J", "K", "L",
            "M", "N", "O", "P",
            "Q", "R", "S", "T",
            "U", "V", "W", "X",
            "Y", "Z" };

    private int mWidth;
    private int mHeight;
    private float mRectHeight;
    private float mRectWidth;
    private OnLetterUpdateListener listener;

    public QuickIndexView(Context context) {
        this(context, null);
    }

    public QuickIndexView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public QuickIndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(20);

        paint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0 ; i < 26 ; i++){
            String letter = LETTERS[i];
            Rect bounds = new Rect();
            //获取字符串的宽高
            paint.getTextBounds(letter,0,letter.length(),bounds);
            int letterHeigh = bounds.height();
            float y = (float) (mRectHeight*0.5+letterHeigh*0.5+i*mRectHeight);

            int letterWidth = bounds.width();
            float x = (float) (mRectWidth*0.5 - letterWidth*0.5);
            canvas.drawText(letter,x,y,paint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mRectHeight = mHeight/LETTERS.length;
        mRectWidth = mWidth;
    }

    /**
     * 处理触摸事件
     * @param event
     * @return
     */
    int currentIndex = -1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = -1;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                index = (int) (event.getY() / mRectHeight);
                //防止多次点击
                if (currentIndex != index){
                    //防止角标越界
                    if (index >= 0 && index<LETTERS.length){
                        System.out.println("index:"+index);
                        if (listener != null){
                            listener.onLetterUpdate(LETTERS[index]);
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                index = (int) (event.getY() / mRectHeight);
                //防止多次点击
                if (currentIndex != index){
                    //防止角标越界
                    if (index >= 0 && index<LETTERS.length){
                        if (listener != null){
                            listener.onLetterUpdate(LETTERS[index]);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
    public void setOnLetterUpdateListener(OnLetterUpdateListener listener){
        this.listener = listener;
    }
    public interface OnLetterUpdateListener{
        void onLetterUpdate(String letter);
    }
}
