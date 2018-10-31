package cn.jko.apis.spring_api;

import cn.jko.apis.pojo.ParamInfo;
import cn.jko.apis.resolver.ApiMethodResolver;
import cn.jko.apis.utils.ParseUtils;
import cn.jko.common.StringUtils;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;

import java.util.*;

/**
 * spring 请求方法处理器
 *
 * @author slsm258@126.com  create on 2018/10/29
 */
public class SpringApiMethodResolver implements ApiMethodResolver {
    @Override
    public String requestUrl(MethodDeclaration methodDeclaration) {
        return methodDeclaration.getAnnotations().stream().filter(s -> {
            return s.getNameAsString().equals("GetMapping")
                    || s.getNameAsString().equals("RequestMapping")
                    || s.getNameAsString().equals("PostMapping");
        }).findFirst().map(s -> {
            if (s instanceof SingleMemberAnnotationExpr) {
                String url = ((SingleMemberAnnotationExpr) s).getMemberValue().toString();
                return StringUtils.removeQuotations(url);
            }
            if (s instanceof NormalAnnotationExpr) {
                for (MemberValuePair p : ((NormalAnnotationExpr) s).getPairs()) {
                    String name = p.getNameAsString();
                    if (name.equals("path") || name.equals("value")) {
                        return StringUtils.removeQuotations(p.getValue().toString());
                    }
                }
            }
            return null;


        }).orElse(null);
    }

    @Override
    public String requestMethod(MethodDeclaration methodDeclaration) {

        if (methodDeclaration.getAnnotations().stream().anyMatch(s -> s.getNameAsString().equals("PostMapping"))) {
            return "POST";
        }
        if (methodDeclaration.getAnnotations().stream().anyMatch(s -> s.getNameAsString().equals("GetMapping"))) {
            return "GET";
        }

        return methodDeclaration.getAnnotationByName("RequestMapping").map(r -> {
            if (r instanceof NormalAnnotationExpr) {
                for (MemberValuePair p : ((NormalAnnotationExpr) r).getPairs()) {
                    String name = p.getNameAsString();
                    if ("method".equals(name)) {
                        if (p.getValue().toString().contains("POST")) {
                            return "POST";
                        } else if (p.getValue().toString().contains("GET")) {
                            return "GET";
                        }
                    }
                }
            }
            return "GET|POST";
        }).orElse("GET|POST");
    }

    @Override
    public String requestName(MethodDeclaration methodDeclaration) {
        return methodDeclaration.getJavadoc().map(s -> s.getDescription().toText()).orElse(methodDeclaration.getNameAsString());
    }

    @Override
    public String requestDescription(MethodDeclaration methodDeclaration) {
        return requestName(methodDeclaration);
    }

    @Override
    public List<ParamInfo> requestParam(MethodDeclaration methodDeclaration) {
        List<ParamInfo> list = new ArrayList<>();
        methodDeclaration.getJavadoc().ifPresent(d -> {

            d.getBlockTags().stream()
                    .forEach(t -> {
                        switch (t.getTagName()) {
                            case "param": {
                                ParamInfo paramNode = new ParamInfo();
                                paramNode.setName(t.getName().get());
                                paramNode.setDesc(t.getContent().toText());
                                list.add(paramNode);

                                break;
                            }
                        }
                    });

        });


        methodDeclaration.getParameters().forEach(p -> {
            String paraName = p.getName().asString();
            String paramType = p.getType().asString();
            //去除 request 和 response 参数
            if (paramType.endsWith("Request") || paramType.endsWith("Response")) {
                ParamInfo info = getParamNode(list, paraName);
                if (info != null) {
                    list.remove(info);
                }
                return;
            }
            ParamInfo paramNode = getParamNode(list, paraName);
            ParamInfo _paramNode = getParamFromMethodParameter(p);
            if (paramNode == null) {
                list.add(_paramNode);
            } else {
                paramNode.setName(_paramNode.getName());
                paramNode.setRequired(_paramNode.getRequired());
                paramNode.setDefaultValue(_paramNode.getDefaultValue());
                paramNode.setType(_paramNode.getType());


            }
        });

        return list;
    }

    @Override
    public List<String> requestFailResults(MethodDeclaration methodDeclaration) {
        return Collections.emptyList();
    }

    private static ParamInfo getParamNode(List<ParamInfo> list, String paraName) {
        return list.stream().filter(pa -> pa.getName().equals(paraName)).findFirst().orElse(null);
    }

    public ParamInfo getParamFromMethodParameter(Parameter parameter) {
        ParamInfo paramNode = new ParamInfo();
        paramNode.setName(parameter.getNameAsString());
        paramNode.setDesc(parameter.getNameAsString());
        paramNode.setType(ParseUtils.unifyType(parameter.getType().asString()));
        parameter.getAnnotationByName("RequestParam").ifPresent(r -> {
            if (r instanceof SingleMemberAnnotationExpr) {
                String name = ((SingleMemberAnnotationExpr) r).getMemberValue().toString();
                paramNode.setName(StringUtils.removeQuotations(name));
            }
            if (r instanceof MarkerAnnotationExpr) {
                paramNode.setRequired(true);
                return;
            }

            if (r instanceof NormalAnnotationExpr) {
                ((NormalAnnotationExpr) r).getPairs().stream()
                        .filter(name ->
                                name.getNameAsString().equals("required") ||
                                        name.getNameAsString().equals("name") ||
                                        name.getNameAsString().equals("defaultValue") ||
                                        name.getNameAsString().equals("value")
                        )
                        .forEach(v -> {
                            switch (v.getNameAsString()) {
                                case "name":
                                case "value":
                                    paramNode.setName(StringUtils.removeQuotations(v.getValue().toString()));
                                    break;
                                case "required":
                                    paramNode.setRequired(Boolean.valueOf(v.getValue().toString()));
                                    break;
                                case "defaultValue":
                                    paramNode.setDefaultValue(v.getValue().toString());
                                    break;
                            }
                        });
            }
        });
        return paramNode;
    }
}
