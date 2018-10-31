package cn.jko.apis.printer;


import cn.jko.apis.pojo.ApiInfo;
import cn.jko.apis.pojo.RequestInfo;

public class StringApiPrinter implements IApiPrinter {

    public String print(ApiInfo apiInfo) {
        StringBuilder sb = new StringBuilder();
        String titleModel = "%d.%s\n";
        String urlModel = "url:%s\n";
        String paramModel = "参数说明:%s\n";
        String methodModel = "提交方式:%s\n";
        String successReturnModel = "成功返回说明:\n%s\n";
        String errorReturnModel = "失败返回说明:\n%s\n";
        for (int i = 1; i <= apiInfo.getRequests().size(); i++) {
            RequestInfo info = apiInfo.getRequests().get(i - 1);
            sb.append(String.format(titleModel, i, info.getName()));
            String url = apiInfo.getApiPrefixUrl().concat(info.getUrl());
            sb.append(String.format(urlModel, url));
            if (info.hasParam()) {
                sb.append(String.format(paramModel, getParamStr(info)));
            }

            sb.append(String.format(methodModel, info.getMethod()));
            sb.append(String.format(successReturnModel, getSuccessResult(info)));

            sb.append(String.format(errorReturnModel, getFailResults(info)));
        }

        return sb.toString();
    }
}
