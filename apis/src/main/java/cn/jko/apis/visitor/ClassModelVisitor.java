package cn.jko.apis.visitor;

import cn.jko.apis.result.ClassModel;
import cn.jko.apis.result.EnumModelType;
import cn.jko.apis.utils.ParseUtils;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * @author slsm258@126.com  create on 2018/10/31
 */
@Slf4j
public class ClassModelVisitor extends GenericVisitorAdapter<ClassModel, ClassTypeParseVisitorParam> {
    /**
     * 返回的是类
     *
     * @param n
     * @param param
     * @return
     */
    @Override
    public ClassModel visit(ClassOrInterfaceType n, ClassTypeParseVisitorParam param) {
        Type type = n;
        if (param.getGenericMap().containsKey(n.getNameAsString())) {
            TypeArgumentInfo info = param.getGenericMap().get(n.getNameAsString());
            type = info.getType();
            param = param.getNewParam(info.getInFile());
        }

        String typeName = getTypeOrClassName(type);
        EnumModelType enumModelType = ParseUtils.unifyReturnType(typeName);
        ClassModel model = new ClassModel();
        model.setStart(param.isStart());

        model.setType(enumModelType);


        if (enumModelType == EnumModelType.OBJECT) {


            File classFile = ParseUtils.searchJavaFile(param.getJavaFile(), typeName, param.getProjectSrcPath());
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = ParseUtils.getClassOrInterfaceDeclaration(ParseUtils.compilationUnit(classFile), typeName);
            if (classOrInterfaceDeclaration != null) {
                //获取返回的类的泛型
                if (n.getTypeArguments().isPresent()) {
                    LinkedHashMap<String, TypeArgumentInfo> map = genericMap(n, classOrInterfaceDeclaration, param);
                    param = param.getNewParam(map);
                }


                ClassModel tempModel = classOrInterfaceDeclaration.accept(this, param.getNewParam(classFile));
                if (tempModel != null) {
                    model.setSubModel(tempModel.getSubModel());
                }
            }

        } else if (enumModelType == EnumModelType.ARRAY && n.getTypeArguments().isPresent() && n.getTypeArguments().get().size() == 1) {
            //默认只有一个泛型
            Type type1 = n.getTypeArguments().get().get(0);
            model.getSubModel().add(type1.accept(this, param));
        }
        return model;
    }

    @Override
    public ClassModel visit(ClassOrInterfaceDeclaration n, ClassTypeParseVisitorParam param) {
        //判断类的泛型
        //用于替换泛型的类
        ClassModel model = new ClassModel();
        model.setStart(param.isStart());
        model.setType(EnumModelType.OBJECT);

        //遍历父类的所有字段 加入进去
        n.getExtendedTypes().forEach(c -> {
            ClassTypeParseVisitorParam p = param;
            ClassModel tmpNode = c.accept(this, p);
            model.getSubModel().addAll(tmpNode.getSubModel());
        });
        //遍历所有的字段 生成模型
        n.getFields().forEach(s -> {
            if(!s.isStatic()){
                ClassTypeParseVisitorParam p = param;
                VariableDeclarator v = s.getVariable(0);
                if (v.getNameAsString().equals("SEPARATOR")) {
                    log.warn("SEPARATOR is {} {}", s.isStatic(), s.isFinal());
                }
                TypeArgumentInfo info = param.getGenericMap().getOrDefault(v.getTypeAsString(), null);
                if (info != null) {
                    p = p.getNewParam(info.getType()).getNewParam(info.getInFile());
                }
                ClassModel tmpNode = s.accept(this, p);
                model.getSubModel().add(tmpNode);
            }

        });

        return model;
    }


    /*
     * 返回是数组<p> (non-Javadoc)
     *
     */
    @Override
    public ClassModel visit(ArrayType arrayType, ClassTypeParseVisitorParam param) {
        Type component = arrayType.getComponentType();
        ClassModel model = new ClassModel();
        model.setType(EnumModelType.ARRAY);
        model.getSubModel().add(component.accept(this, param));
        return model;
    }


    /**
     * @param fieldDeclaration
     * @param param
     * @return
     */
    @Override
    public ClassModel visit(FieldDeclaration fieldDeclaration, ClassTypeParseVisitorParam param) {


        NodeList<VariableDeclarator> varList = fieldDeclaration.getVariables();

        /**
         * 只取第一个
         */
        if (varList != null && varList.size() == 1) {
            VariableDeclarator var = varList.get(0);
            ClassModel model = new ClassModel();
            model.setStart(param.isStart());
            Type type = param.getFieldFactType();
            if (type == null) {
                type = var.getType();
            }
            String cl = type.asString();
            EnumModelType enumModelType = ParseUtils.unifyReturnType(cl);
            model.setType(enumModelType);
            model.setName(var.getNameAsString());
            fieldDeclaration.getComment().ifPresent(v -> {
                model.setDesc(v.getContent());
            });
            if (enumModelType == EnumModelType.OBJECT) {
                ClassModel tmp = type.accept(this, param);
                if (tmp != null) {
                    model.setSubModel(tmp.getSubModel());
                }
            } else if (enumModelType == EnumModelType.ARRAY) {
                ClassModel tmp = type.accept(this, param);
                if (tmp != null) {
                    model.getSubModel().addAll(tmp.getSubModel());
                }

            }
            return model;
        } else {
            log.error("javaFile {} field {} has a lot var", param.getJavaFile().getAbsoluteFile(), fieldDeclaration);
        }
        return null;
    }


    private String getTypeOrClassName(Type node) {

        if (node.isClassOrInterfaceType()) {
            String typeName = node.getTokenRange().map(TokenRange::toString).orElse(null);
            if (typeName == null || typeName.contains("<") || !typeName.contains(".")) {
                typeName = node.asClassOrInterfaceType().getNameAsString();
            }
            return typeName;
        }
        return node.asString();
    }


    private LinkedHashMap<String, TypeArgumentInfo> genericMap(ClassOrInterfaceType classOrInterfaceType, ClassOrInterfaceDeclaration classOrInterfaceDeclaration, ClassTypeParseVisitorParam param) {
        if (!classOrInterfaceType.getTypeArguments().isPresent()) {
            return null;
        }

        if (classOrInterfaceDeclaration.getTypeParameters().isEmpty()) {
            return null;
        }

        NodeList<Type> typeArguments = classOrInterfaceType.getTypeArguments().get();
        NodeList<TypeParameter> typeParameters = classOrInterfaceDeclaration.getTypeParameters();
        if (typeArguments.size() != typeParameters.size()) {
            return null;
        }
        LinkedHashMap<String, TypeArgumentInfo> map = new LinkedHashMap<>();
        LinkedHashMap<String, TypeArgumentInfo> oldMap = param.getGenericMap();
        for (int i = 0; i < typeArguments.size(); i++) {
            Type ta = typeArguments.get(i);
            TypeParameter tp = typeParameters.get(i);

            if (oldMap != null && oldMap.containsKey(ta.asString())) {
                map.put(tp.getNameAsString(), oldMap.get(ta.asString()));
            } else {
                map.put(tp.getNameAsString(), new TypeArgumentInfo(ta, param.getJavaFile()));
            }


        }


        return map;
    }

    @Override
    public ClassModel visit(PrimitiveType n, ClassTypeParseVisitorParam arg) {
        ClassModel classModel = new ClassModel();
        classModel.setStart(arg.isStart());
        classModel.setType(ParseUtils.unifyReturnType(n.asString()));
        return classModel;
    }
}
