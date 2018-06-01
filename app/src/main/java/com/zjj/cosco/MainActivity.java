package com.zjj.cosco;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.zjj.cosco.adapter.GreenDaoAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cosco.greendao.dao.DaoSession;
import cosco.greendao.dao.StudentDao;
import cosco.greendao.entity.Student;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.rv)
    RecyclerView rv;
    private List<Student> list;
    private StudentDao studentDao;
    private GreenDaoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        list = new ArrayList<>();
        adapter = new GreenDaoAdapter(list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);
        refresh();
    }

    @OnClick({R.id.btn_add, R.id.btn_delete, R.id.btn_update, R.id.btn_query})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_add:
                studentDao = MyApplication.getInstance().getDaoSession().getStudentDao();
                Student student = new Student();
                student.setId("200823127556");
                student.setName("陈媛媛");
                student.setAge(27);
                studentDao.insert(student);
                refresh();
                break;
            case R.id.btn_delete:
                studentDao = MyApplication.getInstance().getDaoSession().getStudentDao();
                Student student1 = studentDao.load("200823127556");
//                Student student1 = studentDao.queryBuilder().where(StudentDao.Properties.Id.eq("200823127556")).build().unique();
                if (student1 != null){
                    studentDao.delete(student1);
                    refresh();
                }else {
                    Toast.makeText(this, "该学生不存在！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_update:
                studentDao = MyApplication.getInstance().getDaoSession().getStudentDao();
                Student student2 = studentDao.load("20082312755");
                student2.setAge(30);
                studentDao.update(student2);
                refresh();
                break;
            case R.id.btn_query:
                break;
        }
    }

    private void refresh(){
        list.clear();
        studentDao = MyApplication.getInstance().getDaoSession().getStudentDao();
        List<Student> students = studentDao.queryBuilder().build().list();
        list.addAll(students);
        adapter.notifyDataSetChanged();
    }
}
