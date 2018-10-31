package cn.jko.apis.utils;

import cn.jko.apis.result.ClassModel;
import cn.jko.apis.result.EnumModelType;
import cn.jko.apis.visitor.ClassModelVisitor;
import cn.jko.apis.visitor.ClassTypeParseVisitorParam;
import cn.jko.common.StringUtils;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.Type;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * some util methods during parse
 *
 * @author yeguozhong yedaxia.github.com
 */
public class ParseUtils {

    /**
     * means a model class type
     */
    private static final String TYPE_MODEL = "unkown";

    public static ClassOrInterfaceDeclaration getClassOrInterfaceDeclaration(CompilationUnit compilationUnit, String className) {
        String cn = className.contains(".") ? className.substring(className.lastIndexOf(".") + 1) : className;
        return compilationUnit.findAll(ClassOrInterfaceDeclaration.class).stream().filter(s -> s.getNameAsString().equals(cn)).findFirst().orElse(null);
    }

    public static CompilationUnit compilationUnit(File inJavaFile, String className, String srcPath) {
        File file = searchJavaFile(inJavaFile, className, srcPath);
        return compilationUnit(file);
    }

    /**
     * search File of className in the java file
     *
     * @param inJavaFile
     * @param className
     * @return
     */
    public static File searchJavaFile(File inJavaFile, String className, String srcPath) {
        File file = searchJavaFileInner(srcPath, inJavaFile, className);
        if (file == null) {
            throw new RuntimeException("Cannot find java file , in java file : " + inJavaFile.getAbsolutePath() + ", className : " + className);
        }

        return file;
    }

    private static File searchJavaFileInner(String javaSrcPath, File inJavaFile, String className) {
        CompilationUnit compilationUnit = compilationUnit(inJavaFile);
        if (compilationUnit.findAll(ClassOrInterfaceDeclaration.class).stream().anyMatch(s -> s.getNameAsString().equals(className))) {
            return inJavaFile;
        }

        String[] cPaths;

        Optional<ImportDeclaration> idOp = compilationUnit.getImports()
                .stream()
                .filter(im -> im.getNameAsString().endsWith("." + className))
                .findFirst();

        //found in import
        if (idOp.isPresent()) {
            cPaths = idOp.get().getNameAsString().split("\\.");
            return backTraceJavaFileByName(javaSrcPath, cPaths);
        }

        //inner class
        if (getInnerClassNode(compilationUnit, className).isPresent()) {
            return inJavaFile;
        }

        cPaths = className.split("\\.");

        //current directory
        if (cPaths.length == 1) {

            File[] javaFiles = inJavaFile.getParentFile().listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.equals(className + ".java");
                }
            });

            if (javaFiles != null && javaFiles.length == 1) {
                return javaFiles[0];
            }

        } else {

            final String firstPath = cPaths[0];
            //same package inner class
            File file = inJavaFile;
            for (int i = 0; i < cPaths.length; i++) {
                file = searchJavaFile(file, cPaths[i], javaSrcPath);
                if (i == cPaths.length - 1 && file != null) {
                    return file;
                }
            }

        }

        //maybe a complete class name
        File javaFile = backTraceJavaFileByName(javaSrcPath, cPaths);
        if (javaFile != null) {
            return javaFile;
        }

        //.* at import
        NodeList<ImportDeclaration> importDeclarations = compilationUnit.getImports();
        if (importDeclarations.isNonEmpty()) {
            for (ImportDeclaration importDeclaration : importDeclarations) {
                if (importDeclaration.toString().contains(".*")) {
                    String packageName = importDeclaration.getNameAsString();
                    cPaths = (packageName + "." + className).split("\\.");
                    javaFile = backTraceJavaFileByName(javaSrcPath, cPaths);
                    if (javaFile != null) {
                        break;
                    }
                }
            }
        }

        return javaFile;
    }

    /**
     * get inner class node
     *
     * @param compilationUnit
     * @param className
     * @return
     */
    private static Optional<TypeDeclaration> getInnerClassNode(CompilationUnit compilationUnit, String className) {
        return compilationUnit.findAll(TypeDeclaration.class)
                .stream()
                .filter(c -> c instanceof ClassOrInterfaceDeclaration || c instanceof EnumDeclaration)
                .filter(c -> className.equals(c.getName().getIdentifier()))
                .findFirst();
    }

    private static File backTraceJavaFileByName(String javaSrcPath, String[] cPaths) {
        if (cPaths.length == 0) {
            return null;
        }

        String javaFilePath = Paths.get(javaSrcPath, StringUtils.joinArrayString(cPaths, File.separator) + ".java").toString();
        File javaFile = new File(javaFilePath);
        if (javaFile.exists() && javaFile.isFile()) {
            return javaFile;
        } else {
            return backTraceJavaFileByName(javaSrcPath, Arrays.copyOf(cPaths, cPaths.length - 1));
        }
    }

    /**
     * get java file parser object
     *
     * @param javaFile
     * @return
     */
    public static CompilationUnit compilationUnit(File javaFile) {
        try {
            return JavaParser.parse(javaFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("java file not exits , file path : " + javaFile.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("parser error , file path : " + javaFile.getAbsolutePath());
        }
    }


    /**
     * unify the type show in docs
     *
     * @param className
     * @return
     */
    public static String unifyType(String className) {
        String[] cPaths = className.split("\\.");
        String rawType = cPaths[cPaths.length - 1];
        if ("byte".equalsIgnoreCase(rawType)) {
            return "byte";
        } else if ("short".equalsIgnoreCase(rawType)) {
            return "short";
        } else if ("int".equalsIgnoreCase(rawType)
                || "Integer".equalsIgnoreCase(rawType)
                || "BigInteger".equalsIgnoreCase(rawType)) {
            return "int";
        } else if ("long".equalsIgnoreCase(rawType)) {
            return "long";
        } else if ("float".equalsIgnoreCase(rawType)) {
            return "float";
        } else if ("double".equalsIgnoreCase(rawType)
                || "BigDecimal".equalsIgnoreCase(rawType)) {
            return "double";
        } else if ("boolean".equalsIgnoreCase(rawType)) {
            return "boolean";
        } else if ("char".equalsIgnoreCase(rawType)
                || "Character".equalsIgnoreCase(rawType)) {
            return "char";
        } else if ("String".equalsIgnoreCase(rawType)) {
            return "string";
        } else if ("date".equalsIgnoreCase(rawType)
                || "ZonedDateTime".equalsIgnoreCase(rawType)) {
            return "date";
        } else if ("file".equalsIgnoreCase(rawType)) {
            return "file";
        } else {
            return TYPE_MODEL;
        }
    }


    public static EnumModelType unifyReturnType(String className) {
        if (isListType(className)) {
            return EnumModelType.ARRAY;
        }
        String type = unifyType(className);
        if (type.equals(TYPE_MODEL)) {
            return EnumModelType.OBJECT;
        }
        switch (type) {
            case "byte":
            case "short":
            case "int":
            case "long":
                return EnumModelType.INT;
            case "string":
            case "char":
            case "date":
                return EnumModelType.STRING;
            case "boolean":
                return EnumModelType.BOOLEAN;
            case "float":
            case "double":
                return EnumModelType.DOUBLE;

        }
        return EnumModelType.OBJECT;
    }

    /**
     * is implements from Collection or not
     *
     * @param className
     * @return
     */
    public static boolean isListType(String className) {
        int genericLeftIndex = className.indexOf("<");
        String genericType = genericLeftIndex != -1 ? className.substring(0, genericLeftIndex) : className;
        String[] cPaths = genericType.split("\\.");
        String rawType = cPaths[cPaths.length - 1];
        String collectionClassName = "java.util." + rawType;
        try {
            Class collectionClass = Class.forName(collectionClassName);
            return !Map.class.isAssignableFrom(collectionClass) && Collection.class.isAssignableFrom(collectionClass);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    public static ClassModel parseClassModel(Type type, ClassTypeParseVisitorParam parseVisitorParam) {
        return type.accept(new ClassModelVisitor(), parseVisitorParam);
    }
}
