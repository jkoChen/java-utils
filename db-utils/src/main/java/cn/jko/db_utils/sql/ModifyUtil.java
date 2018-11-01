package cn.jko.db_utils.sql;

import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * 数据库修改工具
 * <p>
 * 涉及到修改 所以具有事务
 *
 * @author j.chen@91kge.com  create on 2018/10/31
 */
@Slf4j
public class ModifyUtil extends QueryUtil {
    private ConnectionConf connectionConf;

    private final boolean isWait;

    ModifyUtil(ConnectionConf conf) {
        super(conf);
        this.connectionConf = conf;
        this.isWait = false;
    }

    ModifyUtil(ConnectionConf conf, boolean isWait) {
        super(conf);
        this.connectionConf = conf;
        this.isWait = isWait;
    }

    public int modify(String sql, List<Object> params) {
        try {
            return modify(sql, params, null);
        } catch (Exception e) {
            return 0;
        }
    }


    public void batchModify(ModifyParam... params) {
        boolean flag = false;
        try {
            flag = connection.getAutoCommit();
            if (flag) {
                UUID transactionId = UUID.randomUUID();
                try {
                    connection.setAutoCommit(false);
                    log.info("事务[{}]开启...", transactionId);
                    for (ModifyParam modifyParam : params) {
                        modify(modifyParam.getSql(), modifyParam.getParam(), transactionId);
                    }
                    connection.commit();
                    log.info("事务[{}]提交...", transactionId);
                } catch (Exception e) {
                    connection.rollback();
                    log.info("事务[{}]回滚... {}", transactionId, e.getLocalizedMessage());
                } finally {
                    connection.setAutoCommit(true);
                    log.info("事务[{}]关闭...", transactionId);
                }
            } else {
                log.error("事务已经开启,无法执行");
            }

        } catch (SQLException e) {
            log.error("获取事务状态出错。");

        }
    }


    /**
     * 更新或删除
     *
     * @param sql
     * @param params
     * @return
     */
    private int modify(String sql, List<Object> params, UUID transactionId) {
        try {
            boolean autoCommit = connection.getAutoCommit();
            if (transactionId == null && !autoCommit) {
                if (isWait) {
                    log.info("wait a minute");
                    Thread.sleep(50);
                    return modify(sql, params, null);
                } else {
                    log.info("new modifyUtil");
                    //该实例开启了事务 但是这个更新没有事务id 所以新建一个 来执行这条语句
                    ModifyUtil modifyUtil = new ModifyUtil(connectionConf);
                    int ret = modifyUtil.modify(sql, params, null);
                    modifyUtil.close();
                    return ret;
                }
            }

        } catch (SQLException e) {
            return 0;
        } catch (InterruptedException e) {
            return 0;
        }

        final String deleteStart = "delete";
        final String updateStart = "update";
        final String insertStart = "insert";
        String type = updateStart;
        if (sql != null) {
            type = sql.trim().substring(0, updateStart.length());
            if (!type.equalsIgnoreCase(updateStart) && !type.equalsIgnoreCase(deleteStart) && !type.equalsIgnoreCase(insertStart)) {
                throw new RuntimeException("the sql is not a modifySql:" + sql);
            }
        }
        if (transactionId != null) {
            log.info("{}[{}] sql start: {} {}", type, transactionId, sql, params);
        } else {
            log.info("{} sql start: {} {}", type, sql, params);
        }
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i + 1, params.get(i));
                }
            }
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            if (transactionId != null) {
                log.info("{}[{}] sql error: {} {}", type, transactionId, sql, params);
            } else {
                log.info("{} sql error: {} {}", type, sql, params);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
