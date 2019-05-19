package com.example.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyLoadMoreView extends View {

    private Paint mPaint;
    private float mCirleRadios;

    public MyLoadMoreView(Context context) {
        super(context);
        init();
    }

    public MyLoadMoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyLoadMoreView);
        mCirleRadios = typedArray.getDimension(R.styleable.MyLoadMoreView_width, 20);
        init();
    }

    public MyLoadMoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyLoadMoreView);
        mCirleRadios = typedArray.getDimension(R.styleable.MyLoadMoreView_width, 20);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Path path = new Path();
        path.addCircle(width/2, height/2, mCirleRadios, Path.Direction.CCW);
        PathMeasure pathMeasure = new PathMeasure();
        pathMeasure.setPath(path, false);
        float length = pathMeasure.getLength();
        float[] pos=new float[2];
        float[] tan=new float[2];
        for(int i=0; i<8; i++){
            pathMeasure.getPosTan(length/8*(i+1), pos, tan);
            canvas.drawCircle(pos[0], pos[1], 5, mPaint);
        }
    }
}
