package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class WriterActivity extends AppCompatActivity {
    EditText writer_name;
    EditText writer_introduction;
    Button button_returnMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer);
        writer_name=findViewById(R.id.writer_name);
        writer_introduction=(EditText) findViewById(R.id.writer_introduction);
        button_returnMain=(Button) findViewById(R.id.button_returnMain);
        SharedPreferences pref=getSharedPreferences("writer",MODE_PRIVATE);
        String strname=pref.getString("name","Eason Chan");
        String strintroduction=pref.getString("introduction","歌神");
        writer_name.setText(strname);//将存储的默认作者显示
        writer_introduction.setText(strintroduction);//显示个人介绍
        button_returnMain.setOnClickListener(new View.OnClickListener() {//编写保存按钮其点击事件,将默认作画保存到SharedPreferences里面
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=getSharedPreferences("writer",MODE_PRIVATE).edit();//指定SharedPreferences的文件名，并得到了SharedPreferences.Editor对象
                String writerName=writer_name.getText().toString();
                String writerIntroduction=writer_introduction.getText().toString();
                editor.putString("name",writerName);
                editor.putString("introduction",writerIntroduction);
                editor.apply();//把默认作者和个人介绍存储到SharedPreferences中
                finish();
            }
        });
    }
}