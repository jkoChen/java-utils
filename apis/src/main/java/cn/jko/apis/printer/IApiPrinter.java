package cn.jko.apis.printer;


import cn.jko.apis.pojo.ParamInfo;
import cn.jko.apis.pojo.RequestInfo;
import cn.jko.common.StringUtils;

/**
 * 打印功能
 *
 * @author slsm258@126.com
 * create on 2017/8/21
 */
public interface IApiPrinter {


    default String getParamStr(RequestInfo info) {
        StringBuilder param = new StringBuilder();
        for (int i = 0; i < info.getParams().size(); i++) {
            ParamInfo s = info.getParams().get(i);
            param.append(s.getName()).append("[" + s.getType() + "]").append(":").append(s.getDesc() + " ").append(s.getRequired() ? "" : "非必须").append(StringUtils.isEmpty(s.getDefaultValue()) ? "" : "默认值 " + s.getDefaultValue());
            if (i < info.getParams().size() - 1) {
                param.append(", ");
            }

        }
        return param.toString();
    }

    default String getSuccessResult(RequestInfo info) {
        for (String s : info.getResults()) {
            return s;
        }
        return "";
    }

    default String getFailResults(RequestInfo info) {
        StringBuilder str = new StringBuilder();
        if (info.getFailResults().size() <= 0) {
            return "";
        }
        for (String s : info.getFailResults()) {
            str.append(s).append("\n");
        }
        return str.toString();
    }

    default String getResultStr(RequestInfo info) {
        StringBuilder str = new StringBuilder();
        info.getResults().forEach(s -> str.append(s).append("\n"));
        return str.toString();
    }
}
