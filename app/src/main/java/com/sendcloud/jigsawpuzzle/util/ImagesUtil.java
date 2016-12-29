package com.sendcloud.jigsawpuzzle.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.sendcloud.jigsawpuzzle.R;

import java.util.ArrayList;
import java.util.List;

public class ImagesUtil {
    public static ImgBean imgBean;
    public static int itemWidth = 0;
    public static int itemHeight = 0;

    private static Paint mPaint;
    public static float scale ;

    public static void createInitBitmaps(Context context, int type, Bitmap picSelected){
        GameUtil.mImgBeans.clear();

        Bitmap bitmap = null;
        int width = picSelected.getWidth();
        int height = picSelected.getHeight();
        scale = (float)width/height;
        int screenWidht = ScreenUtils.getScreenWidth(context);
        picSelected = resizeBitmap(screenWidht, screenWidht/scale, picSelected);

        List<Bitmap> bitmapItems = new ArrayList<>();
        itemWidth = picSelected.getWidth()/type;
        itemHeight = picSelected.getHeight()/type;
        for (int i=1; i<=type; i++){
            for (int j=1; j<=type; j++){
                bitmap = Bitmap.createBitmap(
                        picSelected,
                        (j-1)*itemWidth,
                        (i-1)*itemHeight,
                        itemWidth,
                        itemHeight
                );
                bitmap = getRoundedCornerBitmap(bitmap);

                bitmapItems.add(bitmap);
                imgBean = new ImgBean(
                        (i-1)*type+j,
                        (i-1)*type+j,
                        false,
                        bitmap
                );
                GameUtil.mImgBeans.add(imgBean);
            }
        }

        GameUtil.mLastBitmap = bitmapItems.get(type*type-1);
        bitmapItems.remove(type*type-1);
        GameUtil.mImgBeans.remove(type*type-1);
        Bitmap blankBitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.blank
        );
        blankBitmap = Bitmap.createBitmap(
                blankBitmap, 0, 0, itemWidth, itemHeight);
        bitmapItems.add(blankBitmap);
        GameUtil.mImgBeans.add(new ImgBean(type*type, 0, false, blankBitmap));
        GameUtil.mBlankImgBean = GameUtil.mImgBeans.get(type*type-1);
    }

    public static Bitmap resizeBitmap(float newWidth, float newHeight, Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.postScale(
                newWidth/bitmap.getWidth(),
                newHeight/bitmap.getHeight()
        );

        Bitmap newBitmap = Bitmap.createBitmap(
                bitmap, 0, 0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix, true);
        return newBitmap;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap mBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        canvas.drawRoundRect(0, 0, bitmap.getWidth(), bitmap.getHeight(),
                20, 20, mPaint);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, mPaint);

        return mBitmap;
    }
}
