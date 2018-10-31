package cn.jko.apis.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 文档信息
 *
 * Api 所包含的信息
 * ApiClassResolver 获取
 *
 * @author slsm258@126.com
 * create on 2017/8/17
 */
public class ApiInfo {

    /**
     * java文件名 不包括后缀
     */
    private String name;
    /**
     * 活动分组
     */
    private String apiGroup;
    /**
     * 接口文档名
     */
    private String title;
    /**
     * 接口地址前缀
     */
    private String apiPrefixUrl;
    /**
     * 所有的请求信息
     */
    private List<RequestInfo> requests = new ArrayList<>();


    public String getApiGroup() {
        return apiGroup;
    }

    public void setApiGroup(String apiGroup) {
        this.apiGroup = apiGroup;
    }

    public void addRequest(RequestInfo requestInfo) {
        this.requests.add(requestInfo);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiPrefixUrl() {
        return apiPrefixUrl == null ? "" : apiPrefixUrl;
    }

    public void setApiPrefixUrl(String apiPrefixUrl) {
        this.apiPrefixUrl = apiPrefixUrl;
    }

    public List<RequestInfo> getRequests() {
        return requests;
    }

    public void setRequests(List<RequestInfo> requests) {
        this.requests = requests;
    }


}
