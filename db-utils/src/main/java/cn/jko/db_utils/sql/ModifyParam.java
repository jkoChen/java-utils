package cn.jko.db_utils.sql;

import java.util.List;

/**
 * @author j.chen@91kge.com  create on 2018/10/31
 */
public class ModifyParam {
    private String sql;
    private List<Object> param;


    public ModifyParam(String sql, List<Object> param) {
        this.sql = sql;
        this.param = param;
    }

    public String getSql() {
        return sql;
    }

    public ModifyParam setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public List<Object> getParam() {
        return param;
    }

    public ModifyParam setParam(List<Object> param) {
        this.param = param;
        return this;
    }
}
