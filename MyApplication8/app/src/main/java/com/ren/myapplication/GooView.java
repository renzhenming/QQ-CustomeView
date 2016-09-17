package com.ren.myapplication;

import android.animation.ValueAnimator;
import android.app.Notification;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * Created by Administrator on 2016/9/16.
 */
public class GooView extends View{

    private Paint paint;
    //拖拽圆
    PointF mDragCenter = new PointF(250f,250f);
    float mDragRadius = 30f;
    //固定圆
    PointF mStickCenter = new PointF(250f,250f);
    float mStickRadius = 20f;
    //固定圆两个附着点
    PointF [] mStickPoints = new PointF[]{
      new PointF(250f,250f), new PointF(250f,350f)
    };
    //限制点
    PointF mLimitPoint = new PointF(150f,300f);
    //拖拽圆两个附着点
    PointF [] mDragPoints = new PointF[]{
            new PointF(50f,250f),
            new PointF(50f,350f)
    };

    private int mTopBarHeight;//顶栏上方的高度
    private float biggestDistance;
    private boolean isOutOfRange;//是否超出拖拽边界
    private boolean isDisAppear;//松手时yuan是否消失

    public GooView(Context context) {
        this(context, null);
    }

    public GooView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public GooView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //通过几何方法获取到两个圆上的四个与曲线相交的点坐标
        double offsetX = mStickCenter.x - mDragCenter.x;
        double offsetY = mStickCenter.y - mDragCenter.y;
        //过圆圆心与两个焦点的直线的正切值为
        double lineK = 0;
        if(offsetX != 0){
            lineK = offsetY / offsetX;
        }
        //获取两点间的距离
        float distance = GeometryUtil.getDistanceBetween2Points(mDragCenter, mStickCenter);

        //假设两点可拉伸的最大距离为100
        biggestDistance = 200;
        //根据当前距离到最大距离的变化获取到一个percent，根据这个percent获取到固定圆半径的变化范围
        distance = Math.min(distance, biggestDistance);
        float percent = distance/ biggestDistance;
        System.out.println("percent:"+percent);
        //使用float类型估值器获取半径变化
        Float mTempStickRadius = evaluate(percent, mStickRadius, mStickRadius * 0.3f);
        System.out.println("mStickRadius:"+mStickRadius);
        //通过几何工具类得到交点坐标
        mDragPoints = GeometryUtil.getIntersectionPoints(mDragCenter, mDragRadius, lineK);
        mStickPoints= GeometryUtil.getIntersectionPoints(mStickCenter, mTempStickRadius, lineK);
        //获取控制点坐标
        mLimitPoint = GeometryUtil.getPointByPercent(mDragCenter, mStickCenter, 0.618f);


        /**
         * 开始绘制
         */
        canvas.save();
        canvas.translate(0, -mTopBarHeight);
        //只有在拖拽范围内才绘制连接部分
        if (!isDisAppear){
            if (!isOutOfRange){
                //贝塞尔曲线起点
                Path path = new Path();
                path.moveTo(mStickPoints[0].x, mStickPoints[0].y);
                //绘制曲线
                path.quadTo(mLimitPoint.x, mLimitPoint.y, mDragPoints[0].x, mDragPoints[0].y);
                //绘制直线
                path.lineTo(mDragPoints[1].x, mDragPoints[1].y);
                //绘制曲线
                path.quadTo(mLimitPoint.x, mLimitPoint.y, mStickPoints[1].x, mStickPoints[1].y);
                canvas.drawPath(path, paint);
                //绘制固定圆

                canvas.drawCircle(mStickCenter.x, mStickCenter.y, mTempStickRadius, paint);
            }

            //绘制拖拽圆

            canvas.drawCircle(mDragCenter.x, mDragCenter.y, mDragRadius, paint);
        }


        //画一个边界圆
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mStickCenter.x, mStickCenter.y, biggestDistance, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float rawX = 0;
        float rawY = 0;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                rawX = event.getRawX();
                rawY = event.getRawY();
                updateView(rawX,rawY);
                break;
            case MotionEvent.ACTION_MOVE:
                rawX = event.getRawX();
                rawY = event.getRawY();
                updateView(rawX, rawY);
                float d = GeometryUtil.getDistanceBetween2Points(mDragCenter, mStickCenter);
                if (d > biggestDistance){
                    isOutOfRange = true;
                    invalidate();
                } else{
                    isOutOfRange = false;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                //松手判断是否距离大于边界
                float distanceBetween2Points = GeometryUtil.getDistanceBetween2Points(mDragCenter, mStickCenter);
                if (distanceBetween2Points > biggestDistance){
                    //消失
                    isDisAppear = true;
                    invalidate();
                }else {
                    isDisAppear = false;
//                    updateView(mStickCenter.x,mStickCenter.y);
                    //回到原处
                    //值动画平滑偏移
                    final PointF pointF = new PointF(mDragCenter.x,mDragCenter.y);
                    ValueAnimator anim = ValueAnimator.ofFloat(0f,1.0f);
                    anim.setDuration(300);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float percent = animation.getAnimatedFraction();
                            PointF pointByPercent = GeometryUtil.getPointByPercent(pointF, mStickCenter, percent);
                            updateView(pointByPercent.x, pointByPercent.y);
                        }
                    });
                    anim.setInterpolator(new OvershootInterpolator(5));
                    anim.start();
                }
                break;

        }
        return true;
    }

    private void updateView(float rawX,float rawY) {
        mDragCenter.set(rawX,rawY);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);
        mTopBarHeight = rect.top;
    }
}
