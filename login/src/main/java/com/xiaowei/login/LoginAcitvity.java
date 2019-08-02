package com.xiaowei.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.xiaowei.annotation.BindPath;
import com.xiaowei.arouter.ARouter;

@BindPath("login/login")
public class LoginAcitvity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void jumpActivity(View view){
        //Arouter跳转，彼此之间模块之间没有关联，但是实际项目中又有业务往来,所以必须接入中间人，Arouter就诞生了
        ARouter.getInstance().jumpActivity("member/member",null);
    }
}
