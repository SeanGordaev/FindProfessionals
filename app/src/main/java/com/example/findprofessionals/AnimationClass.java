package com.example.findprofessionals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.media.SoundPool;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.InputStream;

public class AnimationClass extends SurfaceView implements Runnable {

    private boolean isRunning = true;
    private final SurfaceHolder holder = getHolder();

    // BackGround Definitions
    private Bitmap background,back;

    // Screen Definitions
    private WindowManager wm;
    private Display display;
    private DisplayMetrics metrics;

    //images Definitions
    private Bitmap nintendo,bp1,bp2,bp3,bp4,xbox,wii,atari;
    private int height;

    // Bomb definition
    private Movie bomb;
    private long startTime;
    private boolean explosionPlayed;

    //Text definition
    private Paint paint;

    //Sound
    private SoundPool soundPool,soundPool_explosion;
    private int soundId,soundId_Explosion;


    public AnimationClass(Context context) {
        super(context);
        // display gives you information about the screen,
        // metrics stores that information, and background is the resized image
        // that you want to use.

        //Get screen size
        this.wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.display = this.wm.getDefaultDisplay();
        this.metrics = new DisplayMetrics();
        this.display.getMetrics(this.metrics);

        //BackGround Image
        this.back= BitmapFactory.decodeResource(getResources(),R.drawable.my_back);
        this.background=Bitmap.createScaledBitmap(this.back,back.getWidth(),this.metrics.heightPixels,false);

        //initialize nintendo,xbox,wii,bomb
        this.bp1 = BitmapFactory.decodeResource(getResources(), R.drawable.nintendo);
        this.nintendo=Bitmap.createScaledBitmap(bp1,200,200,false);

        this.bp3 = BitmapFactory.decodeResource(getResources(), R.drawable.wii);
        this.wii=Bitmap.createScaledBitmap(bp3,200,200,false);

        this.height = 300;

        // Initialize the bomb
        InputStream is = getResources().openRawResource(R.raw.bomb);
        bomb = Movie.decodeStream(is);
        startTime = android.os.SystemClock.uptimeMillis();
        explosionPlayed=false;

        //initialize text
        this.paint = new Paint();
        this.paint.setColor(Color.YELLOW);
        this.paint.setTextSize(100);

        //initialize sound
        this.soundPool = new SoundPool.Builder().setMaxStreams(2).build();

        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
        {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId, 1, 1, 1, 0, 1);
            }
        });

        this.soundId = this.soundPool.load(context, R.raw.fall, 1);

        //and

        this.soundPool_explosion = new SoundPool.Builder().setMaxStreams(2).build();

        this.soundId_Explosion = this.soundPool_explosion.load(context, R.raw.bomb_explosion, 1);
    }

    public void Cancel_Animation() {
        this.isRunning = false;
    }
    public void Start_Animation() {
        this.isRunning = true;
    }

    @Override
    public void run() {
        while(this.isRunning)
        {
            if(!this.holder.getSurface().isValid()) continue;

            long now = android.os.SystemClock.uptimeMillis();

            //Aqui	vamos	desenhar	os	elementos	do	jogo!
            Canvas canvas = holder.lockCanvas();

            canvas.drawBitmap(this.background,0,0,null);

            if(this.height<= this.metrics.heightPixels)
                canvas.drawText("Welcome Friends", 200, 300, this.paint);

            canvas.drawBitmap(this.nintendo,100,this.height,null);

            canvas.drawBitmap(this.wii,580,this.height,null);

            this.height+=50;

            if(this.height> this.metrics.heightPixels)
            {
                canvas.drawText("Nevo Games!!!", 200, 300, this.paint);

                if (!explosionPlayed)
                {
                    soundPool_explosion.play(soundId_Explosion, 1, 1, 1, 0, 1);

                    explosionPlayed=true;
                }

                //Bomb
                int duration = bomb.duration();
                if (duration == 0)
                    duration = 1000;

                int time = (int)((now - startTime) % duration);
                bomb.setTime(time);

                // Calculate GIF dimensions
                float scaleX = (float) metrics.widthPixels / bomb.width();
                float scaleY = scaleX;

                // Calculate height after scaling
                float gifHeight = bomb.height() * scaleY;

                // Draw at the bottom
                canvas.save();
                canvas.scale(scaleX, scaleY);

                bomb.draw(canvas, 0, (metrics.heightPixels - gifHeight) / scaleY);

                canvas.restore();
                //
            }

            this.holder.unlockCanvasAndPost(canvas);

        }
    }
}
