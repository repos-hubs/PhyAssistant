package com.jibo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class TextProgressBar extends ProgressBar{
    String text;
    Paint mPaint;
     
    public TextProgressBar(Context context) {
        super(context);
        System.out.println("1");
        initText(); 
    }
     
    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        System.out.println("2");
        initText();
    }
 
 
    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        System.out.println("3");
        initText();
    }
     
    @Override
    public synchronized void setProgress(int progress) {
    	System.out.println("progress **    "+progress);
        setText(progress);
        super.setProgress(progress);
         
    }
 
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //this.setText();
        Rect rect = new Rect();
        this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();  
        int y = (getHeight() / 2) - rect.centerY();  
        canvas.drawText(this.text, x, y, this.mPaint);  
    }
     
    //��ʼ��������
    private void initText(){
        this.mPaint = new Paint();
        this.mPaint.setColor(Color.WHITE);
         
    }
     
    private void setText(){
        setText(this.getProgress());
    }
     
    //������������
    private void setText(int progress){
        int i = (progress * 100)/this.getMax();
        this.text = String.valueOf(i) + "%";
    }
     
     
}

