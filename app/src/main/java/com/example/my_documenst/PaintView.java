package com.example.my_documenst;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;
public class PaintView extends View {
    public static int BRUSH_SIZE=10;
    public static final  int DEFAULT_COLOR= Color.MAGENTA,
            DEFAULT_BG_COLOR=Color.WHITE;
    public static final float TOUCH_TOLERANCE=4;
    private  float mx,my;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths=new ArrayList<>();
    private ArrayList<FingerPath> removedpaths=new ArrayList<>();
    private int currentColor=DEFAULT_COLOR;
    private int backgroundColor=DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean emboss;
    private  boolean blur;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmatPaint=new Paint(Paint.DITHER_FLAG);
    boolean drawing=true;
    public PaintView(Context context) {
        super(context,null);
    }
    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

        mEmboss=new EmbossMaskFilter(new float[]{1,1,1},0.4f,6,3.5f);
        mBlur=new BlurMaskFilter(5,BlurMaskFilter.Blur.NORMAL);
    }
    public void init(DisplayMetrics metrics){

        int height=metrics.heightPixels;
        int width=metrics.widthPixels;
        mBitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        mCanvas=new Canvas(mBitmap);
        strokeWidth=BRUSH_SIZE;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public void setDrawing(boolean drawing) {
        this.drawing = drawing;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void normal(){
        emboss=false;
        blur=false;
    }
    public void emboss(){
        emboss=true;
        blur=false;
    }
    public void blur(){
        emboss=false;
        blur=true;
    }
    public void clear(){
        backgroundColor=DEFAULT_BG_COLOR;
        paths.clear();
        normal();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        mCanvas.drawColor(backgroundColor);
        for(FingerPath fp:paths){
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);
            if(fp.emboss)
                mPaint.setMaskFilter(mEmboss);
            else if(fp.blur)
                mPaint.setMaskFilter(mBlur);
            mCanvas.drawPath(fp.path,mPaint);
            canvas.drawBitmap(mBitmap,0,0,mBitmatPaint);
        }
        canvas.restore();
    }
    public void touchstart(float x,float y){
        mPath=new Path();
        FingerPath fp;
        if(drawing)
        fp=new FingerPath(currentColor,emboss,blur,strokeWidth,mPath);
        else
        fp=new FingerPath(backgroundColor,emboss,blur,strokeWidth,mPath);
        paths.add(fp);
        mPath.reset();
        mPath.moveTo(x,y);
        mx=x;
        my=y;
    }
    private void touchmove(float x,float y){
        float dx=Math.abs(x-mx);
        float dy=Math.abs(y-my);
        if(dx>=TOUCH_TOLERANCE || dy>=TOUCH_TOLERANCE){
            mPath.quadTo(mx,my,(x+mx)/2,(y+my)/2);
            mx=x;
            my=y;
            if(removedpaths.size()>0)
                removedpaths.clear();
        }

    }
    private void touchUp(){
        mPath.lineTo(mx,my);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchstart(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchmove(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                break;
        }
        return true;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
    public void undo(){
        if(paths.size()>0){
            removedpaths.add(paths.get(paths.size()-1));
            paths.remove(paths.size()-1);
            invalidate();
        }
    }
    public void redo(){
        if(removedpaths.size()>0){
            paths.add(removedpaths.get(removedpaths.size()-1));
            removedpaths.remove(removedpaths.size()-1);
            invalidate();
        }
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }
}
