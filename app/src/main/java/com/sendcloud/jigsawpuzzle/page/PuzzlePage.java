package com.sendcloud.jigsawpuzzle.page;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sendcloud.jigsawpuzzle.help.GameInterface;
import com.sendcloud.jigsawpuzzle.help.PuzzleAdapter;
import com.sendcloud.jigsawpuzzle.R;
import com.sendcloud.jigsawpuzzle.util.GameUtil;
import com.sendcloud.jigsawpuzzle.util.ImagesUtil;
import com.sendcloud.jigsawpuzzle.util.ScreenUtils;
import com.sendcloud.jigsawpuzzle.util.SizeHelper;
import com.sendcloud.jigsawpuzzle.view.ScrollGridView;

import java.util.Timer;
import java.util.TimerTask;

public class PuzzlePage extends Activity {

    public static int GAME_TYPE = 4;

    private int screenWidht ;

    private ScrollGridView gridView;
    private TextView timer, stepCount ;
    private Button look ;

    private Bitmap mBitmap;

    private int time = -1 ;
    private int step = 0 ;

    private GestureDetector mGestureDetector ;
    public static PuzzleAdapter adapter;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private ImageView mImageView;

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Log.d("successful", "true");
                    break;
                case 2:
                    timer.setText(time + "s");
                    break;
                case 3:
                    stepCount.setText(step + "");
            }
        }
    };

    private Timer mTimer ;
    private TimerTask task ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moving_maze);
        SizeHelper.prepare(this);

        mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        startTimer();
        initView();

        initGridView(this.getIntent().getExtras().getString("imagePath"));
    }

    private void startTimer() {
        time = -1;
        mTimer = new Timer(true);

        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 2;
                time++;
                mHandler.sendMessage(message);
            }
        };
        mTimer.schedule(task, 0, 1000);
    }

    private void initView() {
        mGestureDetector = new GestureDetector(this, listener);
        screenWidht = ScreenUtils.getScreenWidth(this);
        look = (Button) findViewById(R.id.look);
        look.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        creatMirrorImage();
                        break;
                    case MotionEvent.ACTION_UP:
                        deleteMirrorImage();
                        break;
                }
                return false;
            }
        });

        timer = (TextView) findViewById(R.id.timer) ;
        stepCount = (TextView) findViewById(R.id.step) ;

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.titlebar);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                SizeHelper.fromPx(74));
        layout.setLayoutParams(params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    GestureDetector.OnGestureListener listener = new
            GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2,
                                       float velocityX, float velocityY) {
                    float x = e2.getX() - e1.getX() ;
                    float y = e2.getY() - e1.getY() ;

                    float x_abs = Math.abs(x);
                    float y_abs = Math.abs(y);

                    int gesture = 0;
                    if (x_abs >= y_abs){
                        if (x_abs >= screenWidht / 3){
                            if (x>0){
                                gesture = ScrollGridView.Gesture_RIGHT;
                            } else {
                                gesture = ScrollGridView.Gesture_LEFT;
                            }
                        }
                    } else {
                        if (y_abs >= screenWidht / 3){
                            if (y > 0){
                                gesture = ScrollGridView.Gesture_DOWN;
                            } else {
                                gesture = ScrollGridView.Gesture_Top;
                            }
                        }
                    }

                    gridView.setGesture(gesture);

                    return true;
                }
            };

    private void initGridView(String imagePath) {
        gridView = (ScrollGridView) findViewById(R.id.grid_list);

        mBitmap = BitmapFactory.decodeFile(imagePath);

        ImagesUtil.createInitBitmaps(this, GAME_TYPE, mBitmap);
        GameUtil.getPuzzleGenertor();

        adapter = new PuzzleAdapter(this, GameUtil.mImgBeans);
        gridView.setNumColumns(GAME_TYPE);
        gridView.setAdapter(adapter);

        gridView.setPosition(GameUtil.blankPosition);
        gridView.setInterface(new GameInterface() {
            @Override
            public void isSuccessful() {
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }

            @Override
            public void addStep() {
                addStepCount();
            }
        });
    }

    private void addStepCount(){
        step ++ ;
        Message message = new Message();
        message.what = 3;
        mHandler.sendMessage(message);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.restart:
                new AlertDialog.Builder(this).setTitle("确定要重新生成游戏么？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                restartGames();
                            }
                        })
                        .setNegativeButton("返回",null).show();
                break;
            case R.id.left_btn:
                finish();
                break;
        }
    }

    private void creatMirrorImage() {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                gridView.setAlpha(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        animation.setDuration(800);
        gridView.startAnimation(animation);

        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT;
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;

        mWindowLayoutParams.x = gridView.getLeft() ;
        mWindowLayoutParams.y = gridView.getTop() ;

        mWindowLayoutParams.width = gridView.getWidth();
        mWindowLayoutParams.height = gridView.getHeight();
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE ;

        mImageView = new ImageView(this);
        mImageView.setImageBitmap(mBitmap);

        mWindowManager.addView(mImageView, mWindowLayoutParams);
    }

    private void deleteMirrorImage() {
        if (mImageView != null) {
            AlphaAnimation animation = new AlphaAnimation(0, 1);
            animation.setDuration(500);
            gridView.startAnimation(animation);
            gridView.setAlpha(1);

            mWindowManager.removeView(mImageView);
        }
    }

    private void restartGames() {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                time = -1;
                task.cancel();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addStepCount();

                GameUtil.getPuzzleGenertor();
                gridView.setPosition(GameUtil.blankPosition);
                adapter.notifyDataSetChanged();
                AlphaAnimation animation1 = new AlphaAnimation(0, 1);
                animation1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        startTimer();
                        step = -1;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {}

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                animation1.setDuration(1000);
                gridView.startAnimation(animation1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        animation.setDuration(1000);
        gridView.startAnimation(animation);
    }
}