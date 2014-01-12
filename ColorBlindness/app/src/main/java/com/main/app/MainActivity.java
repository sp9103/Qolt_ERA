package com.main.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button Button_Test = (Button)findViewById(R.id.Main_Button_Test);
        Button_Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), TEST.class);
                startActivity(intent);
            }
        });
        Button Button_Image = (Button)findViewById(R.id.Main_Button_Image);
        Button_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), IMAGE.class);
                startActivity(intent);

            }
        });
        Button Button_Movie = (Button)findViewById(R.id.Main_Button_Movie);
        Button_Movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), VIDEO.class);
                startActivity(intent);
            }
        });
        Button Button_Camera = (Button)findViewById(R.id.Main_Button_Camera);
        Button_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CAMERA.class);
                startActivity(intent);
            }
        });
        Button Button_Setting = (Button)findViewById(R.id.Main_Button_Setting);
        Button_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Setting.class);
                startActivity(intent);
            }
        });
    }
}
