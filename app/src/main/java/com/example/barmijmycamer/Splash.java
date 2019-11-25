package com.example.barmijmycamer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {

    Button TakeAPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spalsh);

        final Timer timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {




                         //  Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                         //   startActivity(intent);
                        setContentView(R.layout.startactivity);
                        TakeAPhoto=findViewById(R.id.takeaphoto_button);
                        TakeAPhoto.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            }
                        });

                            timer.cancel();
                          //  finish();


                    }
                });




            }
        }, 1500, 1000);
    }
}
