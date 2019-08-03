package com.xiaowei.annotation_complier;

import com.google.auto.service.AutoService;
import com.xiaowei.annotation.BindPath;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;


/**
 * 注解处理器
 */
@AutoService(Processor.class)//注册我们的注解处理器
public class AnnotationCompiler extends AbstractProcessor {

    //生成文件的对象
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
    }

    /**
     * 声明这个注解处理器要处理的注解
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        /**
         * 拿到我们注解处理器名字
         * 1、getCanonicalName() 是获取所传类从java
         *  语言规范定义的格式输出。
         * 2、getName() 是返回实体类型名称
         * 3、getSimpleName() 返回从源代码中返回实例的名称。
         */
        types.add(BindPath.class.getCanonicalName());
        return types;
    }

    /**
     * 声明我们的注解处理器所支持的Java版本
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }

    /**
     * 自动生成代码
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //获取到类节点的集合 java的概念，Element
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindPath.class);
        //类节点 :TypeElement         方法节点 : ExecutableElement 成员变量节点：VariableElement
        //初始化数据
        Map<String,String> map = new HashMap<>();
        for (Element element:elements) {
            TypeElement typeElement = (TypeElement) element;
            //获取到key
            String key = typeElement.getAnnotation(BindPath.class).value();
            //获取到Activity的类对象的名字
//            typeElement.getSimpleName() 不带包名
            String activityName = typeElement.getQualifiedName().toString();
            map.put(key,activityName);
        }
        //开始写文件
        if (map.size()>0){
            Writer write = null;
            //创建一个新类名
            String utilName = "ActivityUtil"+System.currentTimeMillis();
            //创建文件
            try {
                JavaFileObject sourceFile = filer.createSourceFile("com.xiaowei.util." + utilName);
                System.out.println("namenmae:"+sourceFile);
                write = sourceFile.openWriter();
                write.write("package com.xiaowei.util;\n");
                write.write("import com.xiaowei.arouter.ARouter;\n" +
                        "import com.xiaowei.arouter.IRouter;\n" +
                        "\n"+
                        "public class "+ utilName+" implements IRouter {\n" +
                        "    @Override\n" +
                        "    public void putActivity() {");
                Iterator<String> iterator = map.keySet().iterator();
                while (iterator.hasNext()){
                    String key = iterator.next();
                    String activityName = map.get(key);
                    write.write(" ARouter.getInstance().putActivity(\""+key+"\", "+activityName+".class);\n");
                }
                write.write("}\n}\n");
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (write!=null){
                    try {
                        write.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
}
