package com.xiaowei.login;
/**
 * 注解、注解处理器
 */
import com.xiaowei.arouter.ARouter;
import com.xiaowei.arouter.IRouter;

public class ActivityUtil implements IRouter {
    @Override
    public void putActivity() {
        //拿不到目的地的类对象
        //ARouter.getInstance().putActivity("login/login",LoginAcitvity.class);
    }
}
