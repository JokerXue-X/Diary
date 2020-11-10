package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class List_ViewActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;//通过借助MyDatabaseHelper对象dbHelper来创建数据库和对数据库进行操作
    EditText content;
    SQLiteDatabase db;
    Cursor cursor;
    Button button_update, button_delete, button_return;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__view);
        dbHelper = new MyDatabaseHelper(this, "Diary.db", null, 2);//创建一个对数据库进行读和写的对象dbHelper
        button_update = (Button) findViewById(R.id.button_update);//获得update按钮的实例
        button_delete = (Button) findViewById(R.id.button_delete);//获得delete按钮的实例
        button_return = (Button) findViewById(R.id.button_return);//获得return按钮的实例
        content = (EditText) findViewById(R.id.content);
        imageView = (ImageView) findViewById(R.id.listview_photo);
        db = dbHelper.getWritableDatabase();//通过getWritableDatabase()方法会创建SQLiteDatabase对象,这里通过这个对象db来对数据库进行增删改查。
        cursor = db.query("Diary", null, null, null, null, null, null);
        Intent intent = getIntent();//创建intent对象来获取传来的数据即position
        cursor.moveToPosition(intent.getIntExtra("position", 0));//通过传来的position，使游标选中修改的表中那一行

        content.setText(cursor.getString(cursor.getColumnIndex("content")));//点开一个日记后，通过数据库显示日记的内容
        String photoPath = cursor.getString(cursor.getColumnIndex("photo_path"));
        if (photoPath != null && !photoPath.equals(""))
            imageView.setImageBitmap(BitmapFactory.decodeFile(photoPath));//通过数据库里面存放的路径来获得图片，然后设置imageView

        button_update.setOnClickListener(new View.OnClickListener() {//编写保存按钮其点击事件,即将新日记保存到数据库里
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();//使用ContentValues对添加的数据进行组装
                String content1 = content.getText().toString();
                values.put("content", content1);
                String id = String.valueOf(cursor.getInt(cursor.getColumnIndex("id")));
                db.update("Diary", values, "id=?", new String[]{id});//调用SQLiteDatabase对象的updata()方法来向数据库修改数据
                Toast.makeText(List_ViewActivity.this, "更新日记成功", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        button_delete.setOnClickListener(new View.OnClickListener() {//编写删除按钮其点击事件,即将日记从数据库里删除
            @Override
            public void onClick(View v) {
                String id = String.valueOf(cursor.getInt(cursor.getColumnIndex("id")));
                db.delete("Diary", "id=?", new String[]{id});//调用SQLiteDatabase对象的updata()方法来向数据库修改数据
                Toast.makeText(List_ViewActivity.this, "删除日记成功", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        button_return.setOnClickListener(new View.OnClickListener() {//编写删除按钮其点击事件,即将日记从数据库里删除
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}