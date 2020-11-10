package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import android.content.Context;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    Button button_add;
    ListView list_view;
    private MyDatabaseHelper dbHelper;//通过借助MyDatabaseHelper对象dbHelper来创建数据库和对数据库进行操作
    private List<Diary> diary_list=new ArrayList<>();//创建列表，然后通过将数据传入到适配器，为ListView添加数据,其中的类型为自己创建的Diary类

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//编辑右上角设置作者信息控件
        menu.add(0, 0, 0, "编辑作者信息..");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//编写右上角控件的点击事件，即传送到编辑默认作者页面
        Intent intent = new Intent(MainActivity.this, WriterActivity.class);
        startActivity(intent);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//为主活动设置布局

    }


    @Override
    protected void onResume() {//通过此方法点击添加按钮之后，更新主活动的ListView界面，把新增的日记加入到ListView
        Button buttton_add= (Button)findViewById(R.id.button_add);//获得布局里面add按钮的实例
        buttton_add.setOnClickListener(new View.OnClickListener() {//编写其点击事件
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);
                //点击添加按钮之后就会跳转到AddActivi活动来填写作者和日记内容
            }
        });
        list_view=(ListView) findViewById(R.id.list_view);//获得list_view实例
        init();//为ListView添加数据
        DiaryAdapter adapter=new DiaryAdapter(MainActivity.this,
                R.layout.listview_interface,diary_list);//创建适配器，其中ListView的子布局为listview_interface，其中的数据为diary_list列表的数据
        ListView list_view=(ListView) findViewById(R.id.list_view);
        list_view.setAdapter(adapter);
        super.onResume();//更新数据库即刷新

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(MainActivity.this,List_ViewActivity.class);
                intent.putExtra("position",position);//通过intent.putExtra传给跳转的活动当前数据库的position
                startActivity(intent);
            }
        });

    }





    private void init() {
        diary_list.clear();
        dbHelper = new MyDatabaseHelper(this, "Diary.db", null, 2);//创建一个对数据库进行读和写的对象dbHelper
        SQLiteDatabase db = dbHelper.getWritableDatabase();//这里通过这个对象db来对数据库进行增删改查。
        //通过查询语句得到一个cursor对象里面存放有Diary.db这张表的所有数据
        Cursor cursor = db.query("Diary", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String writerName = cursor.getString(cursor.getColumnIndex("writer"));//获得writer这一列的值
                long createTime1 = cursor.getLong(cursor.getColumnIndex("createTime"));//获得createTime这一列的值
                SimpleDateFormat createTime2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createTime = createTime2.format(new Date(createTime1));//将long类型的createTime转化为String类型
                Diary diary = new Diary(writerName, createTime);//通过获得的属性创建diary对象然后放到diary_list列表然后在ListView显示
                diary_list.add(diary);//获得数据库里面每一行的作者和创建时间然后创造diary对象并它放到diary_list列表里面然后为ListView添加数据
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

}






class Diary{//创建Diary类，将listview里面的每一列所包含的数据为Diary(包括作者名和创建时间)
    private String writer_name;
    private String create_time;
    public Diary(String writer_name,String create_time){
        this.writer_name=writer_name;
        this.create_time=create_time;
    }
    public String getWriter_name(){
        return writer_name;
    }
    public String getCreate_time(){
        return create_time;
    }
}



class DiaryAdapter extends ArrayAdapter<Diary>{//创建一个自定义适配器，其类型为Diary
    private int resourceId;
    public DiaryAdapter(Context context, int textViewResourceId, List<Diary> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){//重新getView方法用来返回布局
        Diary diary=getItem(position);//获得当前滚到屏幕类的实例，然后将这个实例加载到界面上
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView writer=(TextView) view.findViewById(R.id.TextView_writer);
        TextView time=(TextView) view.findViewById(R.id.TextView_time);
        writer.setText(diary.getWriter_name());
        time.setText(diary.getCreate_time());
        return view;
    }
}


