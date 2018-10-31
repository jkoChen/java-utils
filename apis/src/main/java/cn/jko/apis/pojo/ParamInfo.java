package cn.jko.apis.pojo;

/**
 * 接口的参数信息
 *
 *  ApiMethodResolver#requestParam
 *
 * @author slsm258@126.com
 * create on 2017/8/18
 */
public class ParamInfo {
    private String name;
    private String type;
    private String desc;
    private Boolean required = Boolean.TRUE;
    private String defaultValue;

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
