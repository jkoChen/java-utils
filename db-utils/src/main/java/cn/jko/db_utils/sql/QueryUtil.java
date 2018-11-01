package cn.jko.db_utils.sql;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

/**
 * 查询工具
 * <p>
 * 因为是查询 不涉及事务
 * 连接可以保持住
 *
 * 为什么只能查询
 * 因为防止生产环境误操作啊
 *
 * @author j.chen@91kge.com  create on 2018/10/31
 */
@Slf4j
public class QueryUtil extends SqlUtil {


    QueryUtil(ConnectionConf conf) {
        connection = conf.getNewConnection();
    }

    public List<Map<String, String>> selectList(String sql) {
        return selectList(sql, null);
    }

    public List<Map<String, String>> selectList(String sql, List<Object> params) {
        List<Map<String, String>> list = new ArrayList<>();
        select(sql, params, (r) -> {
            try {
                ResultSetMetaData metaData = r.getMetaData();
                while (r.next()) {
                    Map<String, String> result = new LinkedHashMap<>();
                    for (int i = 0; i < metaData.getColumnCount(); i++) {
                        // resultSet数据下标从1开始
                        String columnName = metaData.getColumnLabel(i + 1);
                        result.put(columnName, r.getString(i + 1));
                    }
                    list.add(result);
                }
            } catch (Exception e) {

            }
            return null;
        });
        return list;
    }

    public Map<String, String> selectOne(String sql) {
        return selectOne(sql, null);
    }

    public Map<String, String> selectOne(String sql, List<Object> params) {
        List<Map<String, String>> list = selectList(sql, params);
        return list != null && list.size() > 0 ? list.get(0) : new HashMap<>();
    }

    /**
     * 查询
     *
     * @param sql
     * @param params
     * @param function
     * @param <T>
     * @return
     */
    protected <T> T select(String sql, List<Object> params, Function<ResultSet, T> function) {
        if (sql != null) {
            String searchStart = "select";
            String value = sql.trim().substring(0, searchStart.length());
            if (!value.equalsIgnoreCase(searchStart)) {
                throw new RuntimeException("the sql is not a searchSql:" + sql);
            }
        }
        log.info("select sql start: {}", sql);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i + 1, params.get(i));
                }
            }
            resultSet = preparedStatement.executeQuery();
            return function.apply(resultSet);
        } catch (Exception e) {
            log.error("select sql error: {}", sql);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }


}
