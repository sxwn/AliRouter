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
        //创建一个class对象集合
        List<String> clazzs = new ArrayList<>();
        String path=null;
        try {
            //通过包管理器  获取到应用信息类然后获取到apk的完整路径
            String sourceDir = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir;
            //根据apk的完整路径获取编译后的dex文件
            DexFile dexFile = new DexFile(sourceDir);
            //获得编译后的dex文件中的左右class
            Enumeration<String> entries = dexFile.entries();
            //然后进行遍历
            while (entries.hasMoreElements()){
                //通过遍历所有的class 的包名
                String name = entries.nextElement();
                //判断累的包名是否符合
                if (name.contains(packageName)){
                    //符合添加到集合中
                    clazzs.add(name);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clazzs;
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
            intent.putExtra("bundle",bundle);
//            intent.putExtras(bundle);
        }
        /**
         * 这里这行必须加一下
         * 网上查询了一下说明如下  参考链接:
         * 1.在Activity上下文之外启动Activity需要给Intent设置FLAG_ACTIVITY_NEW_TASK标志，不然会报异常。
         * 2.加了该标志，如果在同一个应用中进行Activity跳转，不会创建新的Task，只有在不同的应用中跳转才会创建新的Task
         */
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
