package cn.jko.apis.visitor;

import com.github.javaparser.ast.type.Type;

import java.io.File;

/**
 * @author slsm258@126.com  create on 2018/10/30
 */
public class TypeArgumentInfo {
    private Type type;
    private File inFile;

    public TypeArgumentInfo(Type type, File inFile) {
        this.type = type;
        this.inFile = inFile;
    }

    public Type getType() {
        return type;
    }

    public TypeArgumentInfo setType(Type type) {
        this.type = type;
        return this;
    }

    public File getInFile() {
        return inFile;
    }

    public TypeArgumentInfo setInFile(File inFile) {
        this.inFile = inFile;
        return this;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
