package com.main.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.Gesture.GestureImageView;

import java.io.IOException;
import java.io.InputStream;

public class IMAGE extends Activity {
    private static final int SELECT_PHOTO = 100;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent){
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    TextView tv = (TextView)findViewById(R.id.image_textView);
                    tv.setText(selectedImage.toString());
                    PrintImage(selectedImage);
                }
        }
    }
    protected void PrintImage(Uri ImageUri){
        try{
            InputStream imageStream = getContentResolver().openInputStream(ImageUri);
            Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);

            GestureImageView imageView = (GestureImageView)findViewById(R.id.image_imageView);
            imageView.setImageBitmap(yourSelectedImage);

        }
        catch(IOException e){
            Toast.makeText(getBaseContext(),"파일이 존재하지 않습니다",Toast.LENGTH_SHORT).show();
        }
    }
}
