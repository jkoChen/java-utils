package cn.jko.db_utils.sql;

/**
 * 数据库相关工具类
 *
 * @author j.chen@91kge.com  create on 2018/11/1
 */
public class SqlUtils {

    /**
     * 返回一个仅供查询的工具
     *
     * @param conf
     * @return
     */
    public static QueryUtil createQueryUtil(ConnectionConf conf) {
        return new QueryUtil(conf);
    }

    /**
     * 返回一个可供修改的工具
     *
     * @param conf
     * @return
     */
    public static ModifyUtil createModifyUtil(ConnectionConf conf) {
        return new ModifyUtil(conf);
    }

    public static ModifyUtil createModifyUtil(ConnectionConf conf, boolean isWait) {
        return new ModifyUtil(conf, isWait);
    }
}
