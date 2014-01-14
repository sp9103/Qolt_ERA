package com.main.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import static java.lang.Thread.sleep;

public class VIDEO extends Activity {
    private static final int SELECT_VIDEO = 2;
    VideoView videoView;
    ImageView imageView;
    MediaMetadataRetriever meta;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("video/*");
        startActivityForResult(photoPickerIntent, SELECT_VIDEO);

        imageView = (ImageView)findViewById(R.id.video_imageView);
        videoView = (VideoView)findViewById(R.id.video_videoView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent){
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case SELECT_VIDEO:
                if(resultCode == RESULT_OK){
                    Uri selectedVideo =  imageReturnedIntent.getData();
                    TextView tv = (TextView)findViewById(R.id.video_textView);
                    tv.setText(selectedVideo.toString());
                    PlayVideo(selectedVideo);
                }
        }
    }
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    protected void PlayVideo(Uri uri){
        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        meta = new MediaMetadataRetriever();
        meta.setDataSource(this,uri);
//        Async async = new Async();
//        async.execute();
    }

    public class Async extends AsyncTask<Void,Void,Void>{
        @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
        @Override
        protected Void doInBackground(Void... params) {
            while(true){
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int currentPosition = videoView.getCurrentPosition();
                Bitmap bitmap = meta.getFrameAtTime(currentPosition * 1000);
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
