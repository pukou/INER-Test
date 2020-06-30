package com.bsoft.mob.ienr.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.api.KernelApi;
import com.bsoft.mob.ienr.dynamicui.DialogListener;
import com.bsoft.mob.ienr.model.ParserModel;
import com.bsoft.mob.ienr.model.SignData;
import com.bsoft.mob.ienr.util.JsonUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * Created by TXM on 2016-5-30.
 */
public class WritePadDialog extends AlertDialog {
    LayoutParams p;

    Context context;
    Context applicationContext;
    //事件监听 牵涉函数的回调
    DialogListener dialogListener;
    //图片展示在哪个ImageView中
    ImageView ivSign;
    //是否弹出提示框
    boolean isShowAlert;

    IenrProgressDialog progressDialog;

    /**
     * 手写签名图片保存用构造函数
     *
     * @param context
     * @param applicationContext
     * @param dialogListener
     * @param isShowAlert
     */
    public WritePadDialog(Context context, Context applicationContext, DialogListener dialogListener, boolean isShowAlert) {
        super(context);
        this.context = context;
        this.applicationContext = applicationContext;
        this.dialogListener = dialogListener;
        this.isShowAlert = isShowAlert;
    }

    /**
     * 手写签名图片获取用构造函数
     *
     * @param context
     * @param applicationContext
     * @param ivSign
     * @param isShowAlert
     */
    public WritePadDialog(Context context, Context applicationContext, ImageView ivSign, boolean isShowAlert) {
        super(context);
        this.context = context;
        this.applicationContext = applicationContext;
        this.ivSign = ivSign;
        this.isShowAlert = isShowAlert;
    }

    static final int BACKGROUND_COLOR = Color.WHITE;

    PaintView mView;

    /**
     * The index of the current color to use.
     */
    int mColorIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.write_pad);
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = windowManager.getDefaultDisplay().getHeight();

        p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (height * 0.4);   //高度设置为屏幕的0.4
        p.width = (int) (width * 1.0);    //宽度设置为屏幕的1.0
        getWindow().setAttributes(p);     //设置生效


        mView = new PaintView(context);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.tablet_view);
        frameLayout.addView(mView);
        mView.requestFocus();
        Button btnClear = (Button) findViewById(R.id.tablet_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mView.clear();
            }
        });

        Button btnOk = (Button) findViewById(R.id.tablet_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    dialogListener.refreshActivity(mView.getCachebBitmap());
                    dialogListener.saveImage(mView.BitmapToBytes());
                    WritePadDialog.this.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button btnCancel = (Button) findViewById(R.id.tablet_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }


    public void SaveImage(SignData signData) {
        SaveSignImageTask task = new SaveSignImageTask();
        task.execute(signData);
    }

    public void GetImage(SignData signData) {
        GetSignImageTask task = new GetSignImageTask();
        task.execute(signData);
    }


    private class SaveSignImageTask extends AsyncTask<SignData, Void, ParserModel> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = new IenrProgressDialog(context,
                        "签名文件保存...");
            }
            progressDialog.start();
        }

        @Override
        protected ParserModel doInBackground(SignData... params) {
            KernelApi api = KernelApi.getInstance(applicationContext);

            String data = "";
            SignData signData = params[0];
            try {
                signData.ImageStr = Base64.encodeToString(signData.Image, Base64.DEFAULT);
                signData.Image = null;//mSignData;

                data = JsonUtil.toJson(signData);
                data = "{\"signData\":" + data + "}";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return api.SaveSignImage(data);
        }

        @Override
        protected void onPostExecute(ParserModel result) {

            if (progressDialog != null) {
                progressDialog.stop();
                progressDialog = null;
            }
            if (result == null) {
                Log.e(Constant.TAG, "LocalLoginTask result is null");
                return;
            }
            if (result.isOK()) {
                if (isShowAlert&&context!=null) {
                    AlertBox.Show(context, context.getString(R.string.project_tips), "保存签名文件成功！", context.getString(R.string.project_operate_ok));
                }
            } else {
                if (isShowAlert&&context!=null) {
                    AlertBox.Show(context, context.getString(R.string.project_tips), "保存签名文件失败！", context.getString(R.string.project_operate_ok));
                }
            }

        }

    }

    private class GetSignImageTask extends AsyncTask<SignData, Void, ParserModel> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = new IenrProgressDialog(context,
                        "签名文件获取...");
            }
            progressDialog.start();
        }

        @Override
        protected ParserModel doInBackground(SignData... params) {
            KernelApi api = KernelApi.getInstance(applicationContext);

            SignData signData = params[0];
            signData.Image = null;
            ;
            signData.ImageStr = null;
            return api.GetSignImage(signData.HSGH, signData.ZYH, signData.BQDM, signData.Type, signData.GSLX, signData.QMDH, signData.JGID);
        }

        @Override
        protected void onPostExecute(ParserModel result) {

            if (progressDialog != null) {
                progressDialog.stop();
                progressDialog = null;
            }
            if (result == null) {
                Log.e(Constant.TAG, "LocalLoginTask result is null");
                return;
            }
            if (result.isOK()) {
                ArrayList<SignData> list = result.getList("Table1");

                Bitmap temp = getBitmapForBase64String(list.get(0).ImageStr);

                ivSign.setImageBitmap(temp);

                if (isShowAlert&&context!=null) {
                    AlertBox.Show(context, context.getString(R.string.project_tips), "获取签名文件成功！", context.getString(R.string.project_operate_ok));
                }
            } else {
                if (isShowAlert&&context!=null) {
                    AlertBox.Show(context, context.getString(R.string.project_tips), "获取签名文件失败！", context.getString(R.string.project_operate_ok));
                }
            }

        }

    }

    private Bitmap getBitmapForBase64String(String imageStr) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(imageStr, Base64.DEFAULT);
            bitmapArray = unGZip(bitmapArray);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 压缩GZip
     *
     * @param data
     * @return
     */
    public static byte[] gZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(data);
            gzip.finish();
            gzip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    /**
     * 解压GZip
     *
     * @param data
     * @return
     */
    public static byte[] unGZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            byte[] buf = new byte[1024];
            int num = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }
            b = baos.toByteArray();
            baos.flush();
            baos.close();
            gzip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }


    /**
     * <p/>
     * It handles all of the input events and drawing functions.
     */
    class PaintView extends View {
        private Paint paint;
        private Canvas cacheCanvas;
        private Bitmap cachebBitmap;
        private Path path;

        public Bitmap getCachebBitmap() {
            return cachebBitmap;
        }

        public byte[] BitmapToBytes() {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            cachebBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            int options = 100;
            while (byteArrayOutputStream.toByteArray().length / 1024 > 10) {  //循环判断如果压缩后图片是否大于10kb,大于继续压缩
                //int len = byteArrayOutputStream.toByteArray().length / 1024;
                options -= 10;//每次都减少10

                if (options == 0) {
                    break;
                }
                byteArrayOutputStream.reset();//重置byteArrayOutputStream即清空byteArrayOutputStream
                cachebBitmap.compress(Bitmap.CompressFormat.JPEG, options, byteArrayOutputStream);//这里压缩options%，把压缩后的数据存放到byteArrayOutputStream中

            }
            byte[] bytes = byteArrayOutputStream.toByteArray();

            bytes = gZip(bytes);

            return bytes;
        }


        public PaintView(Context context) {
            super(context);
            init();
        }

        private void init() {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            path = new Path();
            cachebBitmap = Bitmap.createBitmap(p.width, (int) (p.height * 0.8), Config.ARGB_8888);
            cacheCanvas = new Canvas(cachebBitmap);
            cacheCanvas.drawColor(Color.WHITE);
        }

        public void clear() {
            if (cacheCanvas != null) {

                paint.setColor(BACKGROUND_COLOR);
                cacheCanvas.drawPaint(paint);
                paint.setColor(Color.BLACK);
                cacheCanvas.drawColor(Color.WHITE);
                invalidate();
            }
        }


        @Override
        protected void onDraw(Canvas canvas) {
            // canvas.drawColor(BRUSH_COLOR);
            canvas.drawBitmap(cachebBitmap, 0, 0, null);
            canvas.drawPath(path, paint);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {

            int curW = cachebBitmap != null ? cachebBitmap.getWidth() : 0;
            int curH = cachebBitmap != null ? cachebBitmap.getHeight() : 0;
            if (curW >= w && curH >= h) {
                return;
            }

            if (curW < w)
                curW = w;
            if (curH < h)
                curH = h;

            Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
            Canvas newCanvas = new Canvas();
            newCanvas.setBitmap(newBitmap);
            if (cachebBitmap != null) {
                newCanvas.drawBitmap(cachebBitmap, 0, 0, null);
            }
            cachebBitmap = newBitmap;
            cacheCanvas = newCanvas;
        }

        private float cur_x, cur_y;

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    cur_x = x;
                    cur_y = y;
                    path.moveTo(cur_x, cur_y);
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    path.quadTo(cur_x, cur_y, x, y);
                    cur_x = x;
                    cur_y = y;
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    cacheCanvas.drawPath(path, paint);
                    path.reset();
                    break;
                }
            }

            invalidate();

            return true;
        }
    }

}

