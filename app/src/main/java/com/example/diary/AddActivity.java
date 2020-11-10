package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class AddActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;//通过借助MyDatabaseHelper对象dbHelper来创建数据库和对数据库进行操作
    public static final int TAKE_PHOTO = 1;//拍照片
    private static final int CHOOSE_PHOTO = 2;//选择相册中的图片
    private ImageView diary_picture;//放置图片的view
    private Uri imageUri;//图片在手机中存放位置的view
    EditText diary_title, diary_content, diary_writer;
    String photoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);//为该活动加载布局
        dbHelper = new MyDatabaseHelper(this, "Diary.db", null, 2);//创建一个对数据库进行读和写的对象dbHelper
        Button button_save = (Button) findViewById(R.id.button_save);//获得save按钮的实例
        Button button_cancel = (Button) findViewById(R.id.button_cancel);//获得cancel按钮的实例
        Button button_shot = (Button) findViewById(R.id.button_shot);
        Button button_album = (Button) findViewById(R.id.button_album);


        diary_title = (EditText) findViewById(R.id.diary_title);//获得EditText实例
        diary_content = (EditText) findViewById(R.id.diary_content);
        diary_writer = (EditText) findViewById(R.id.diary_writer);
        diary_picture = (ImageView) findViewById(R.id.diary_picture);


        button_save.setOnClickListener(new View.OnClickListener() {//编写保存按钮其点击事件,即将新日记保存到数据库里
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();//通过getWritableDatabase()方法会创建SQLiteDatabase对象
                //，这里通过这个对象db来对数据库进行增删改查。
                ContentValues values = new ContentValues();//使用ContentValues对添加的数据进行组装
                String title = diary_title.getText().toString();
                String content = diary_content.getText().toString();
                SharedPreferences pref = getSharedPreferences("writer", MODE_PRIVATE);//得到SharedPreferences对象，然后可通过getString()等方法得到存储的值
                String writer = diary_writer.getText().toString();
                if ("".equals(writer)) {//如果填写日记时，未填写作者姓名则使用默认作者"陈奕迅"，默认作者也可以设置。
                    writer = pref.getString("name", "Eason Chan");
                }
                java.util.Date date = new java.util.Date();
                long time = date.getTime();
                values.put("title", title);
                values.put("content", content);
                values.put("createTime", time);
                values.put("writer", writer);
                values.put("photo_path",photoPath);
                db.insert("Diary", null, values);//调用SQLiteDatabase对象的insert()方法来向数据库添加数据
                Toast.makeText(AddActivity.this, "添加日记成功", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {//编写保存按钮其点击事件,即将新日记保存到数据库里
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button_shot.setOnClickListener(new View.OnClickListener() {//编写调用摄像头拍照事件
            @Override
            public void onClick(View v) {
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");//创建file对象，用于存储拍照后的照片，并存放在SD卡存放当前照片的位置
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();//如果这个名字的图片存在，那么删除
                    }
                    outputImage.createNewFile();//创建这个file
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    //如果Android版本大于7.0
                    imageUri = FileProvider.getUriForFile(AddActivity.this,
                            "com.example.diary.fileProvider", outputImage);
                } else {//调用Uri的fromFile()方法将file对象转化为Uri对象，imageUri表示该图片的真实路径
                    imageUri = Uri.fromFile(outputImage);
                }
                //启动相机程序
                photoPath=outputImage.getPath();//photoPath将路径转化为字符串
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");//
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//putExtra()来指定图片的输出位置，这里填Uri
                startActivityForResult(intent, TAKE_PHOTO);//调用照相机，即打开照相机拍照，拍下的照片存放在output_image.jpg文件里面
                //并且会启动onActivityResult()方法
            }
        });
        button_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                        PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddActivity.this, new
                            String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);//申请权限
                } else if(ContextCompat.checkSelfPermission(AddActivity.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.
                            PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddActivity.this, new
                                String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }else {
                    openAlbum();//申请权限后，调用openAlbum()方法，去打开相册程序，选择照片
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//将拍摄成功的照片，显示在ImageView上
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {//表示拍照成功
                    try {
                        //将拍照的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(imageUri));//把照片解析为Bitmap对象
                        diary_picture.setImageBitmap(bitmap);//然后把它在diary_picture（ImageView类型）显示出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {//已经选择图片成功
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        //4.4一下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {//如果版本高于4.4则调用该方法来处理图片，需要对Uri进行解析
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("mytag", String.valueOf(uri));
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的URI，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            Log.d("mytagd", String.valueOf(docId));
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection);
                Log.d("mytagm", String.valueOf(imagePath));
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
                Log.d("mytagd", String.valueOf(imagePath));
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的Uri,则用普通方式处理
            imagePath = getImagePath(uri, null);
            Log.d("mytagc", String.valueOf(imagePath));
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri,直接获取图片路径即可
            imagePath = uri.getPath();
            Log.d("mytagf", String.valueOf(imagePath));
        }
        photoPath=imagePath;
        displayImage(photoPath); //根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {//版本低的话用这个方法
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            diary_picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "获取图片失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri uri, String selection) {//来将获取的路径转化为字符串
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    public void openAlbum() {//打开相册程序然后选择照片
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);//调用onActivityResult()方法，同打开相机拍摄
    }
}
