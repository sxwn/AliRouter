package com.xiaowei.arouterdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.xiaowei.annotation.BindPath;
import com.xiaowei.arouter.ARouter;

@BindPath("main/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void jump(View view){
        ARouter.getInstance().jumpActivity("login/login",null);
    }
}
