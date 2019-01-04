package cn.jko.apis;

import cn.jko.apis.filter.ApiClassFilter;
import cn.jko.apis.filter.ApiMethodFilter;
import cn.jko.apis.filter.JavaFileFilter;
import cn.jko.apis.pojo.ApiInfo;
import cn.jko.apis.pojo.RequestInfo;
import cn.jko.apis.resolver.ApiClassResolver;
import cn.jko.apis.resolver.ApiMethodResolver;
import cn.jko.apis.result.ClassModel;
import cn.jko.apis.utils.ParseUtils;
import cn.jko.apis.visitor.ApiClassVisitor;
import cn.jko.apis.visitor.ApiMethodVisitor;
import cn.jko.apis.visitor.ApiReturnVisitor;
import cn.jko.apis.visitor.ClassTypeParseVisitorParam;
import cn.jko.common.FileUtils;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Consumer;

/**
 * @author slsm258@126.com  create on 2018/10/27
 */
@Slf4j
public class ApiContext {


    //项目路径
    private String projectPath;

    private String srcPath = "";


    private JavaFileFilter javaFileFilter;
    private ApiClassFilter apiClassFilter;
    private ApiMethodFilter apiMethodFilter;


    private ApiClassVisitor apiClassVisitor;
    private ApiMethodVisitor apiMethodVisitor;
    private ApiReturnVisitor apiReturnVisitor;

    private ApiClassResolver apiClassResolver;
    private ApiMethodResolver apiMethodResolver;

    private ConcurrentSkipListMap<String, ApiParseInfo> apiInfoMap;

    public ApiContext(String projectPath, String srcPath) {
        apiInfoMap = new ConcurrentSkipListMap<>();
        javaFileFilter = JavaFileFilter.defaultJavaFileFilter();
        apiClassFilter = ApiClassFilter.defaultApiClassFilter();
        apiMethodFilter = ApiMethodFilter.defaultApiMethodFilter();

        apiClassVisitor = new ApiClassVisitor();
        apiMethodVisitor = new ApiMethodVisitor();
        apiReturnVisitor = new ApiReturnVisitor();

        setProjectPath(projectPath);
        setSrcPath(srcPath);
    }

    public ApiContext setApiClassVisitor(ApiClassVisitor apiClassVisitor) {
        this.apiClassVisitor = apiClassVisitor;
        return this;
    }

    public ApiContext setApiMethodVisitor(ApiMethodVisitor apiMethodVisitor) {
        this.apiMethodVisitor = apiMethodVisitor;
        return this;
    }

    public ApiContext setApiReturnVisitor(ApiReturnVisitor apiReturnVisitor) {
        this.apiReturnVisitor = apiReturnVisitor;
        return this;
    }

    public ApiContext setJavaFileFilter(JavaFileFilter javaFileFilter) {
        this.javaFileFilter = javaFileFilter;
        return this;
    }

    public ApiContext setApiClassFilter(ApiClassFilter apiClassFilter) {
        this.apiClassFilter = apiClassFilter;
        return this;
    }

    public ApiContext setApiMethodFilter(ApiMethodFilter apiMethodFilter) {
        this.apiMethodFilter = apiMethodFilter;
        return this;
    }

    private ApiContext setProjectPath(String projectPath) {
        this.projectPath = projectPath;
        return this;
    }

    private ApiContext setSrcPath(String srcPath) {
        this.srcPath = srcPath;
        return this;
    }

    public ApiContext setApiClassResolver(ApiClassResolver apiClassResolver) {
        this.apiClassResolver = apiClassResolver;
        return this;
    }

    public ApiContext setApiMethodResolver(ApiMethodResolver apiMethodResolver) {
        this.apiMethodResolver = apiMethodResolver;
        return this;
    }

    private String getProjectSrcPath() {
        return projectPath.concat(File.separator).concat(srcPath);
    }


    public void init() {
        init(null);
    }

    private void loopFile(Consumer<ApiParseInfo> apiInfoConsumer) {
        log.info(getClass().getSimpleName() + " 遍历开始。");
        //遍历路径下的所有java文件
        FileUtils.listFile(new File(projectPath), (f, name) -> javaFileFilter.isJavaFile(f)).forEach(f -> {
            //f api 文件
            //文件名
            String fileName = f.getName();
            String javaClassName = fileName.substring(0, fileName.lastIndexOf("."));

            //api java 文件 转化的单元
            CompilationUnit compilationUnit = ParseUtils.compilationUnit(f);
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = ParseUtils.getClassOrInterfaceDeclaration(compilationUnit, javaClassName);
            if (apiClassFilter.isApiClass(f, classOrInterfaceDeclaration)) {
                ApiInfo apiInfo = parseApiInfo(f, compilationUnit);
                if (apiInfo != null) {
                    apiInfoConsumer.accept(new ApiParseInfo(f, compilationUnit, apiInfo));
                }
            }
        });
        log.info(getClass().getSimpleName() + " 遍历结束。");
    }

    public void refresh(Consumer<ApiInfo> consumer) {
        LinkedHashMap<String, ApiParseInfo> tmp = new LinkedHashMap<>();
        loopFileIntoMap(consumer, tmp);
        apiInfoMap.clear();
        apiInfoMap.putAll(tmp);
    }

    private void loopFileIntoMap(Consumer<ApiInfo> consumer, Map<String, ApiParseInfo> map) {
        loopFile(apiParseInfo -> {
            if (apiParseInfo != null) {
                map.put(apiParseInfo.getApiInfo().getTitle(), apiParseInfo);
                if (consumer != null) {
                    consumer.accept(apiParseInfo.getApiInfo());
                }
            }
        });
    }

    public void init(Consumer<ApiInfo> consumer) {
        loopFileIntoMap(consumer, apiInfoMap);
    }

    public Set<String> titles() {
        return apiInfoMap.keySet();
    }

    public ApiInfo getApiInfo(String title) {
        return getApiInfo(title, false);
    }

    public ApiInfo getApiInfo(String title, boolean isRefresh) {
        ApiParseInfo parseInfo = apiInfoMap.get(title);
        if (parseInfo == null) {
            return null;
        }

        if (isRefresh) {
            ApiInfo apiInfo = parseApiInfo(parseInfo.getJavaFile(), ParseUtils.compilationUnit(parseInfo.getJavaFile()));
            parseInfo.setApiInfo(apiInfo);
            if (!apiInfo.getTitle().equals(title)) {
                apiInfoMap.remove(title);
                apiInfoMap.put(apiInfo.getTitle(), parseInfo);
            }
        }
        return parseInfo.getApiInfo();
    }

    private ApiInfo parseApiInfo(File javaFile, CompilationUnit compilationUnit) {
        ApiInfo apiInfo = compilationUnit.accept(apiClassVisitor, apiClassResolver);
        log.info("解析 : {}", javaFile.getAbsolutePath());
        if (apiInfo != null) {
            ClassTypeParseVisitorParam param = new ClassTypeParseVisitorParam();
            param.setProjectSrcPath(getProjectSrcPath());
            param.setStart(true);
            compilationUnit.findAll(MethodDeclaration.class, s -> apiMethodFilter.isRequestMethod(s)).forEach(s -> {
                RequestInfo requestInfo = s.accept(apiMethodVisitor, apiMethodResolver);
                ClassModel classModel = s.accept(apiReturnVisitor, param.getNewParam(javaFile));
                if (classModel != null) {
                    requestInfo.addSuccessResult(classModel.to());
                    requestInfo.setModelStr(classModel.toString());
                    requestInfo.setClassModel(classModel);
                    apiInfo.addRequest(requestInfo);
                } else {
                    log.warn("this method is error {} {}.", javaFile.getAbsolutePath(), s.getNameAsString());
                }
            });
            return apiInfo;
        }
        return null;
    }

    private class ApiParseInfo {
        private File javaFile;
        private CompilationUnit compilationUnit;
        private ApiInfo apiInfo;

        public ApiParseInfo(File javaFile, CompilationUnit compilationUnit, ApiInfo apiInfo) {
            this.javaFile = javaFile;
            this.compilationUnit = compilationUnit;
            this.apiInfo = apiInfo;
        }

        public ApiInfo getApiInfo() {
            return apiInfo;
        }

        public ApiParseInfo setApiInfo(ApiInfo apiInfo) {
            this.apiInfo = apiInfo;
            return this;
        }

        public File getJavaFile() {
            return javaFile;
        }

        public ApiParseInfo setJavaFile(File javaFile) {
            this.javaFile = javaFile;
            return this;
        }

        public CompilationUnit getCompilationUnit() {
            return compilationUnit;
        }

        public ApiParseInfo setCompilationUnit(CompilationUnit compilationUnit) {
            this.compilationUnit = compilationUnit;
            return this;
        }
    }

}
