package com.sendcloud.jigsawpuzzle.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sendcloud.jigsawpuzzle.R;
import com.sendcloud.jigsawpuzzle.page.PuzzlePage;
import com.sendcloud.jigsawpuzzle.util.FileUtils;
import com.sendcloud.jigsawpuzzle.util.SizeHelper;
import com.sendcloud.jigsawpuzzle.util.StatusBarUtil;
import com.sendcloud.jigsawpuzzle.util.Typefaces;

import java.io.File;

public class WelcomePage extends Activity {

    private LinearLayout group ;
    private Button button1, button2, button3 ;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            setButtonEbable(true);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                    animation.setDuration(2000);
                    group.startAnimation(animation);
                    group.setAlpha(1);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.transparencyBar(this);
        setContentView(R.layout.activity_splash);

        initView();

        setTextTyoeface();

        new Thread(start).start();
    }

    private void setTextTyoeface() {
        TextView splashTitle = (TextView) findViewById(R.id.splash_title);
        TextView youKe = (TextView) findViewById(R.id.youke);
        TextView version = (TextView) findViewById(R.id.version);

        SizeHelper.prepare(this);
        splashTitle.setTextSize(SizeHelper.fromPx(30));
        youKe.setTextSize(SizeHelper.fromPx(12));
        youKe.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));
        version.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));
    }

    private void initView() {
        group = (LinearLayout) findViewById(R.id.button_group) ;
        group.setAlpha(0);

        button1 = (Button) findViewById(R.id.button1) ;
        button2 = (Button) findViewById(R.id.button2) ;
        button3 = (Button) findViewById(R.id.button3) ;

        setButtonEbable(false);
    }

    private void setButtonEbable(boolean enable){
        button1.setEnabled(enable);
        button2.setEnabled(enable);
        button3.setEnabled(enable);
    }

    Runnable start = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == 1 && data != null){
                Cursor cursor = this.getContentResolver().query(
                        data.getData(), null, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(
                        cursor.getColumnIndex("_data"));
                Intent intent = new Intent(WelcomePage.this, PuzzlePage.class);
                intent.putExtra("imagePath", imagePath);
                cursor.close();
                startActivity(intent);
            }
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.button1:
                AlertDialog.Builder builder = new AlertDialog.Builder(  WelcomePage.this);
                builder.setTitle("难度选择：");
                builder.setSingleChoiceItems(new String[]{"3X3", "4X4", "5X5"}, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        PuzzlePage.GAME_TYPE = 3;
                                        break;
                                    case 1:
                                        PuzzlePage.GAME_TYPE = 4;
                                        break;
                                    case 2:
                                        PuzzlePage.GAME_TYPE = 5;
                                        break;
                                }
                            }
                        });

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                "image/*");
                        startActivityForResult(intent, 1);
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                break;
            case R.id.button2:
                break;
            case R.id.button3:
                break;
        }
    }
}
