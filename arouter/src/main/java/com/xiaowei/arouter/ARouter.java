package com.xiaowei.arouter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;

/**
 * 中间人
 */
public class ARouter {

    //装载所有Activity所有类对象的容器
    //使用map  list是无序，还得一个个找
    private Map<String,Class<? extends Activity>> activityMap;
    private Context context;
    private static ARouter aRouter = new ARouter();//饿汉式

    private ARouter(){
        activityMap = new HashMap<>();
    }

    public static ARouter getInstance(){
        return aRouter;
    }


    //防止context引起内存泄漏
    public void init(Application application){
        this.context = application;
        //去执行生成的这些文件的方法
        List<String> classNames = getClassName("com.xiaowei.util");
        for (String className : classNames) {
            try {
                Class<?> aClass = Class.forName(className);
                //判断这个类是否是IRouter这个接口的实现类
                if (IRouter.class.isAssignableFrom(aClass)){
                    IRouter iRouter = (IRouter) aClass.newInstance();//接口引用指向子类实例
                    iRouter.putActivity();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getClassName(String packageName) {
        List<String> classList = new ArrayList<>();
        String path = null;
        try {
            //apk完整路径
            path = context.getPackageManager().getApplicationInfo(context.getPackageName(),0).sourceDir;
            //获取到编译后的dex文件
            DexFile dexFile = new DexFile(path);
            //dex文件中的所有class
            Enumeration<String> entries = dexFile.entries();
            while (entries.hasMoreElements()){
                //遍历class的包名
                String name = entries.nextElement();
                if (name.contains(packageName)){
                    classList.add(name);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classList;
    }

    /**
     * 将ACTIVITY的类对象加入到容器中
     * @param path
     * @param clazz
     */
    public void putActivity(String path,Class<? extends Activity> clazz){
        if (path!=null && clazz!=null){
            activityMap.put(path,clazz);
        }
    }

    /**
     * 跳转
     * @param path
     * @param bundle
     */
    public void jumpActivity(String path, Bundle bundle){
        Class<? extends Activity> aClass = activityMap.get(path);
        if (aClass==null){
            return;
        }
        Intent intent = new Intent(context,aClass);
        if (bundle!=null){
//            intent.putExtra("bundle",bundle);
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

}
