package cn.jko.apis.visitor;

import com.github.javaparser.ast.type.Type;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * 将class 转化成 ClassModel 的参数
 *
 *
 * @author slsm258@126.com  create on 2018/10/29
 */
public class ClassTypeParseVisitorParam {
    private boolean start;
    private String projectSrcPath;

    private File javaFile;
    private LinkedHashMap<String,TypeArgumentInfo> genericMap = new LinkedHashMap<>();
    private Type fieldFactType;


    public File getJavaFile() {
        return javaFile;
    }

    public ClassTypeParseVisitorParam setJavaFile(File javaFile) {
        this.javaFile = javaFile;
        return this;
    }

    public LinkedHashMap<String, TypeArgumentInfo> getGenericMap() {
        return genericMap;
    }

    public ClassTypeParseVisitorParam setGenericMap(LinkedHashMap<String, TypeArgumentInfo> genericMap) {
        this.genericMap = genericMap;
        return this;
    }

    public Type getFieldFactType() {
        Type t = fieldFactType;
        fieldFactType = null;
        return t;
    }

    public boolean isStart() {
        if (start) {
            start = false;
            return true;
        }
        return start;
    }

    public ClassTypeParseVisitorParam setStart(boolean start) {
        this.start = start;
        return this;
    }

    public String getProjectSrcPath() {
        return projectSrcPath;
    }

    public ClassTypeParseVisitorParam setProjectSrcPath(String projectSrcPath) {
        this.projectSrcPath = projectSrcPath;
        return this;
    }


    public ClassTypeParseVisitorParam getNewParam(LinkedHashMap<String,TypeArgumentInfo> genericMap) {
        if (genericMap == null || genericMap.size() == 0) {
            genericMap = new LinkedHashMap<>();
        }
        return copy().setGenericMap(genericMap);
    }

    public ClassTypeParseVisitorParam getNewParam(File javaFile) {
        return copy().setJavaFile(javaFile);
    }

    public ClassTypeParseVisitorParam getNewParam(Type fieldFactType) {
        return copy().setFieldFactType(fieldFactType);
    }

    private ClassTypeParseVisitorParam setFieldFactType(Type fieldFactType) {
        this.fieldFactType = fieldFactType;
        return this;
    }

    private ClassTypeParseVisitorParam copy() {
        ClassTypeParseVisitorParam tmp = new ClassTypeParseVisitorParam();
        tmp.setStart(isStart());
        tmp.setProjectSrcPath(getProjectSrcPath());
        tmp.fieldFactType = this.fieldFactType;
        tmp.genericMap = this.genericMap;
        tmp.javaFile = this.javaFile;
        return tmp;


    }
}
