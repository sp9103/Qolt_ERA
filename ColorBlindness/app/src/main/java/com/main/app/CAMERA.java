package com.main.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class CAMERA extends Activity {
    ImageView imageView;
    public CameraSurfaceView cameraSurfaceView;
    Button button;
    FrameLayout fl;
    Camera camera = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraSurfaceView = new CameraSurfaceView(getApplicationContext());
        setContentView(R.layout.activity_camera);
        imageView = (ImageView)findViewById(R.id.camera_image);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    camera.autoFocus(null);
                }
                return false;
            }
        });
        button = (Button)findViewById(R.id.camera_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSurfaceView.capture(new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        camera.startPreview();
                    }
                });
            }
        });
        fl = (FrameLayout)findViewById(R.id.camera_frame);
        fl.addView(cameraSurfaceView);
    }
    private class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
        private SurfaceHolder mHolder;

        public CameraSurfaceView(Context context) {
            super(context);

            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @TargetApi(Build.VERSION_CODES.FROYO)
        public void surfaceCreated(SurfaceHolder holder) {
            camera = Camera.open();
            if(camera != null){
                Camera.Parameters params = camera.getParameters();
                List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
                params.setPreviewSize(640, 480);
                camera.setParameters(params);
                camera.setDisplayOrientation(90);
                camera.setPreviewCallback(this);
                try {
                    camera.setPreviewDisplay(mHolder);
                } catch (Exception e) {
                    Log.e("CameraSurfaceView", "Failed to set camera preview.", e);
                }
            }
            else{
                Toast.makeText(getBaseContext(),"카메라가 없습니다",Toast.LENGTH_SHORT).show();
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            camera.startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            camera.stopPreview();
            camera = null;
        }

        public boolean capture(Camera.PictureCallback handler) {
            if (camera != null) {
                camera.takePicture(null, null, handler);
                return true;
            } else {
                return false;
            }
        }

        @TargetApi(Build.VERSION_CODES.FROYO)
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters params = camera.getParameters();
            int w = params.getPreviewSize().width;
            int h = params.getPreviewSize().height;
            int format = params.getPreviewFormat();

            YuvImage image = new YuvImage(data, format, w, h, null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Rect area = new Rect(0, 0, w, h);
            image.compressToJpeg(area, 50, out);
            Bitmap bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
            bitmap = rotate(bitmap,90);
            imageView.setImageBitmap(bitmap);
        }
    }
    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();

            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                throw ex;
            }
        }
        return b;
    }
}


