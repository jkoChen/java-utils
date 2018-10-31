package cn.jko.apis.pojo;


import cn.jko.apis.result.ClassModel;
import cn.jko.common.JsonFormatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 接口信息
 * <p>
 * name:取自 javadoc中的内容 如为空 则取方法名
 * url、method、deprecated: 取自RequestMapping注解 中的信息
 * params : 分三步读取 修正 先读取 javadoc中的 param 在解析 方法参数 最后 通过 RequestParam注解的信息最后修正
 * codesStr:取自javadoc的 code
 * returnStr: 默认为{"code":1} \t\t\t//成功
 * 如果 javadoc return不为空 则解析方法内容中的 注释
 * 支持方法返回的类型解析
 * 支持ResultMap 类型的返回
 * 自动解析 addProp方法
 * 支持的解析注释如下
 * //int(0-100) 这是返回说明
 * ret.addProp("code",index);
 * /*string(NAME1,NAME2) 这是返回说明*\/
 * ret.addProp("name",name);
 * //[{goodsId:int 物品id,numUnit:int 数量,goodsName:string 物品名称}]
 * ret.addProp("list",list);
 * <p>
 * 注释格式为  类型(范围) 返回说明
 * 范围支持 两种形式 (1,2,3) (1-3) 前者表示 1 2 3 中任取一个 后者表示 1-3 随机一个数字
 * 类型支持 int double string arr object 其中 arr用[] 包围  object用 {}包围  object arr 支持 嵌套
 * 注释中的 (范围可以省略 仅对int double string 有效 arr和object 不支持范围设置)
 * <p>
 * 程序会自动解析 addProp中的 参数名 和 注释内容 生成相应的带注释的 json字符串
 *
 * @author slsm258@126.com
 * create on 2017/8/18
 */
public class RequestInfo {
    private String name;
    private String url;
    private List<ParamInfo> params = new ArrayList<>();
    private String modelStr = "code [int]";
    private List<String> results = new ArrayList<>(Arrays.asList("success"));
    private List<String> failResults = new ArrayList<>();

    private String method = "GET|POST";
    private Boolean deprecated = Boolean.FALSE;

    private String description;

    private ClassModel classModel;

    public ClassModel getClassModel() {
        return classModel;
    }

    public RequestInfo setClassModel(ClassModel classModel) {
        this.classModel = classModel;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Deprecated
    public void addResult(String result, boolean isSuccess) {
        if (isSuccess) {
            addSuccessResult(result);
        } else {
            addFailResult(result);
        }

    }

    public void addSuccessResult(String result) {
        this.results.remove(0);
        this.results.add(0, JsonFormatUtils.formatJson(result));
    }

    public List<String> getFailResults() {
        return failResults;
    }


    public void addFailResult(String failResult) {
        this.failResults.add(JsonFormatUtils.formatJson(failResult));
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public boolean hasParam() {
        return this.params.size() > 0;
    }

    public void addParam(ParamInfo paramInfo) {
        this.params.add(paramInfo);
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ParamInfo> getParams() {
        return params;
    }

    public void setParams(List<ParamInfo> params) {
        this.params = params;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getModelStr() {
        return modelStr;
    }

    public void setModelStr(String modelStr) {
        this.modelStr = modelStr;
    }
}
