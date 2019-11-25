package com.example.barmijmycamer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {


    protected static final int CAMERA_REQUEST = 1;
    String photoFilepath;
    ImageView cameraresultphoto;

    Button sharebutton;
    File file;
    String uriSting;
    Button temp;
    Bitmap tempPhoto;
    EditText EnterText;
    ImageView TextOnPhoto,capturedphoto;
    Button ok_button,cancle_button;
    Bitmap photo;
    Uri imageUri;
    String path_photo_saved_for_whatspp;
    Bitmap newbitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editphoto_layout);

        cameraresultphoto = findViewById(R.id.camera_resultphoto);
        sharebutton=findViewById(R.id.share_button);
        temp=findViewById(R.id.temp);
        TextOnPhoto=findViewById(R.id.text_on_photo);
        EnterText=findViewById(R.id.edit_Text);
        ok_button=findViewById(R.id.ok_button);
        cancle_button=findViewById(R.id.cancle_button);
        capturedphoto=findViewById(R.id.captured_photo);


        TextOnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterText.setVisibility(View.VISIBLE);

            }
        });
        capturedphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap.Config config=photo.getConfig();
                if(config==null){config=Bitmap.Config.ARGB_8888;}
                 newbitmap=Bitmap.createBitmap(photo.getWidth(),photo.getHeight(),config);

                Canvas newcanvas=new Canvas(newbitmap);
                newcanvas.drawBitmap(photo,0,0,null);
                String captionstring=EnterText.getText().toString();

                if(captionstring!=null){
                    Paint paintText=new Paint(Paint.ANTI_ALIAS_FLAG);
                    paintText.setColor(Color.BLUE);
                    paintText.setTextSize(200);
                    paintText.setStyle(Paint.Style.FILL);

                    Rect recttext=new Rect();
                    paintText.getTextBounds(captionstring,0,captionstring.length(),recttext);

                    int centreX = (newbitmap.getWidth()  - recttext.width()) /2;
                    int centreY = (newbitmap.getHeight()  + recttext.height()) /2;


                    //////////////////////
                    newcanvas.save();
                    newcanvas.rotate(-90, centreX, centreY);
                    newcanvas.drawText(captionstring,centreX,centreY,paintText);
                    newcanvas.restore();
                    /////////////////////

                   // Toast.makeText(getApplicationContext(),"draw"+captionstring,Toast.LENGTH_LONG).show();
                    capturedphoto.setImageBitmap(newbitmap);
                    EnterText.setVisibility(View.GONE);


                }
                else {
                    Toast.makeText(getApplicationContext(),"captionstring is empty",Toast.LENGTH_LONG).show();
                }


            }
        });


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_writetext:
                            EnterText.setVisibility(View.VISIBLE);
                        break;

                    case R.id.action_favorites:
                        //Toast.makeText(getApplicationContext(), "Favorites", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.action_share:
                        {
                            EnterText.setVisibility(View.GONE);

                            PackageManager pm = getApplicationContext().getPackageManager();
                            try {
                                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                Matrix matrix = new Matrix();
                                matrix.postRotate(90);
                                Bitmap tempBitmap =Bitmap.createBitmap(newbitmap, 0, 0, newbitmap.getWidth(), newbitmap.getHeight(), matrix, true);

                                tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), tempBitmap, "Title", null);
                                Uri imageUri = Uri.parse(path);

                                @SuppressWarnings("unused")
                                PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);

                                Intent waIntent = new Intent(Intent.ACTION_SEND);
                                waIntent.setType("image/*");
                                waIntent.setPackage("com.whatsapp");
                                waIntent.putExtra(android.content.Intent.EXTRA_STREAM, imageUri);
                                waIntent.putExtra(Intent.EXTRA_TEXT, "My Photo");
                                startActivity(Intent.createChooser(waIntent, "Share with"));
                            } catch (Exception e) {
                                Log.e("Error on sharing", e + " ");
                                Toast.makeText(getApplicationContext(), "please check the text ", Toast.LENGTH_SHORT).show();
                            }


                        }
                        break;
                }
                return true;
            }
        });


        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        ///////////////////////////////////////
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
         imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {

            case CAMERA_REQUEST:
                if (requestCode == CAMERA_REQUEST)
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            photo = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), imageUri);
                            Toast.makeText(getApplicationContext(),"this is image"+imageUri.toString(),Toast.LENGTH_LONG).show();

                            //////
                            int currentBitmapWidth = photo.getWidth();
                            int currentBitmapHeight = photo.getHeight();

                            /////
                            capturedphoto.setImageBitmap(photo);
                            final RotateAnimation rotateAnim = new RotateAnimation(0.0f, 90,
                                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                            rotateAnim.setDuration(0);
                            rotateAnim.setFillAfter(true);
                            capturedphoto.startAnimation(rotateAnim);

                          //  String imageurl = getRealPathFromURI(imageUri);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
        }

    }










}
