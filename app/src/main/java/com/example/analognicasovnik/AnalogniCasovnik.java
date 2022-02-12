package com.example.analognicasovnik;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

public class AnalogniCasovnik extends View
{
    public static final int BASE_WIDTH = 300;
    public static final int BASE_HEIGHT = 300;

    Bitmap bitmapCasovnik;
    Canvas canvas;
    Paint paint;

    private int padding;
    private int sirina;
    private int visina;
    private int kracaStranica;
    private int poluPrecnik;
    private double ugao;
    private int [] brojevi;
    private Paint kazaljka;
    private Paint brojeviPaint;
    private float sati;
    private float minuti;
    private float sekunde;
    private int pSate,pMinute,pSekunde;

    private float mH,mM,mS;

    Rect backgroundBounds = new Rect();

    public AnalogniCasovnik(Context context)
    {
        super(context);

        Calendar mCalendar= Calendar.getInstance();
        int hours = mCalendar.get(Calendar.HOUR);
        int minutes = mCalendar.get(Calendar.MINUTE);
        int seconds = mCalendar.get(Calendar.SECOND);

        postaviVreme(hours,minutes,seconds);
        init(context,null);
        initPaint();
    }

    public AnalogniCasovnik(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        init(context,attrs);
        initPaint();
    }

    private void init(Context context,AttributeSet attrs)
    {
        paint = new Paint();
        canvas = new Canvas();
        backgroundBounds = new Rect();
        bitmapCasovnik = Bitmap.createBitmap(400,400,Bitmap.Config.ARGB_8888);

        if (attrs!=null)
        {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.AnalogniCasovnik);
            sirina = typedArray.getInt(R.styleable.AnalogniCasovnik_sirinaSata,600);
            visina = typedArray.getInt(R.styleable.AnalogniCasovnik_visinaSata,600);
            padding = typedArray.getInt(R.styleable.AnalogniCasovnik_paddingSata,50);

        }

        ugao = (float) ((Math.PI/30)-(Math.PI/2));
        visina = getHeight();
        sirina = getWidth();

        brojevi = new int[] {1,2,3,4,5,6,7,8,9,10,11,12};
    }

    private void initPaint()
    {
        kazaljka = new Paint();
        kazaljka.setAntiAlias(true);
        kazaljka.setStyle(Paint.Style.FILL_AND_STROKE);
        kazaljka.setStrokeCap(Paint.Cap.ROUND);

        brojeviPaint = new Paint();
        brojeviPaint.setStyle(Paint.Style.FILL);
        brojeviPaint.setTextSize(50);
        brojeviPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int w;
        int h;

        if (widthMode == MeasureSpec.EXACTLY) { w = widthSize; }
        else if (widthMode == MeasureSpec.AT_MOST) { w = Math.min(BASE_WIDTH,widthSize); }
        else { w = BASE_WIDTH; }

        if (heightMode == MeasureSpec.EXACTLY) { h = heightSize; }
        else if (heightMode == MeasureSpec.AT_MOST) { h = Math.min(BASE_HEIGHT,heightSize); }
        else { h = BASE_HEIGHT; }

        int smaller = Math.min(w,h);
        w = smaller;
        h = smaller;

        if (!areAllEqual(getPaddingTop(),getPaddingBottom(),getPaddingLeft(),getPaddingRight()))
        {
            w = smaller - (getPaddingTop() + getPaddingBottom());
            h = smaller - (getPaddingLeft() + getPaddingRight());
        }

        setMeasuredDimension(w,h);
    }

    public static boolean areAllEqual(int... values)
    {
        if (values.length==0)
        {
            return true;
        }
        int checkValue = values[0];
        for (int i = 1; i<values.length;i++)
        {
            if (values[i] != checkValue)
            {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        sirina = w;
        visina = h;

        backgroundBounds.left = getPaddingLeft();
        backgroundBounds.right = w - getPaddingRight();
        backgroundBounds.top = getPaddingTop();
        backgroundBounds.bottom = h - getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmapCasovnik,null,backgroundBounds,null);
        crtanjeKruga(canvas);
        crtanjeKazaljki(canvas);
        crtanjeBrojeva(canvas);

        postInvalidateDelayed(500);
    }

    private void crtanjeKruga(Canvas canvas)
    {
        paint.reset();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30);
        paint.setAntiAlias(true);
        canvas.drawCircle(sirina / 2,visina / 2,sirina/2,paint);
    }

    private void crtanjeKazaljki(Canvas canvas)
    {
        Calendar calendar = Calendar.getInstance();
        sati = calendar.get(Calendar.HOUR_OF_DAY);
        sati = sati > 12 ? sati - 12 : sati;
        minuti = calendar.get(Calendar.MINUTE);
        sekunde = calendar.get(Calendar.SECOND);


        kazaljkaSati(canvas,(sati + minuti/60.0)*5f);
        kazaljkaMinuti(canvas,minuti);
        kazaljkaSekunde(canvas,sekunde);

    }

    private void kazaljkaSati(Canvas canvas,double pozicija)
    {
        paint.reset();
        canvas.save();

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(20);
        ugao = Math.PI * pozicija / 30 - Math.PI /2;

        canvas.rotate(mH,0,0);
        canvas.drawLine(sirina/2,visina/2,
                (float) (sirina/2 + Math.cos(ugao)
                        *((sirina/2)/2)),
                (float) (visina/2 + Math.sin(ugao)*((sirina/2)/2)),paint);
        canvas.restore();
    }

    private void kazaljkaMinuti(Canvas canvas,float pozicija)
    {
        paint.reset();
        canvas.save();

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(15);
        ugao = Math.PI * pozicija / 30 - Math.PI /2;

        canvas.rotate(mM,0,0);
        canvas.drawLine(sirina/2,visina/2,(float) (sirina/2 + Math.cos(ugao)*((sirina/2)
                -(sirina/8))),(float) (visina/2 + Math.sin(ugao)
                *((sirina/2)-(sirina/8))),paint);
        canvas.restore();
    }

    private void kazaljkaSekunde(Canvas canvas,float pozicija)
    {
        paint.reset();
        canvas.save();

        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        ugao = Math.PI * pozicija / 30 - Math.PI /2;

        canvas.rotate(mS,0,0);
        canvas.drawLine(sirina/2,visina/2,
                (float) (sirina/2 + Math.cos(ugao)
                        *((sirina/2) -(sirina/8)))
                , (float) (visina/2 + Math.sin(ugao)*((sirina/2)-(sirina/8))),paint);
        canvas.restore();
    }

    private void crtanjeBrojeva(Canvas canvas)
    {
        paint.setTextSize(50);

        for (int broj : brojevi)
        {
            String br = String.valueOf(broj);
            paint.getTextBounds(br,0,br.length(),backgroundBounds);
            double ugao2 = Math.PI/6*(broj-3);
            int a = (int) (sirina/2 + Math.cos(ugao2) * sirina/2-backgroundBounds.width()/2);
            int b = (int) (visina/2 + Math.sin(ugao2) * sirina/2+backgroundBounds.height()/2);
            canvas.drawText(br,a,b,paint);
        }
    }

    public void postaviVreme(int h,int m,int s)
    {
        if (h >= 24 || h < 0 || m >= 60 || m < 0 || s >= 60 || s < 0)
        {
            Toast.makeText(getContext(),"Lose uneseno",Toast.LENGTH_SHORT).show();
            return;
        }
        if (h>=12)
        {
            mH = (h + m * 1.0f/60f + s * 1.0f/3600f - 12)*30f - 180;
        } else
            {
                mH = (h + m * 1.0f/60f + s * 1.0f/3600f)*30f - 180;
            }

        mM = (m + s * 1.0f/60f) * 6f-180;
        mS = s * 6f-180;
    }
}























