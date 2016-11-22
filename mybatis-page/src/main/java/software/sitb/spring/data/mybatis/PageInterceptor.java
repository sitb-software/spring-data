package software.sitb.spring.data.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import software.sitb.spring.data.mybatis.dialect.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * mybatis 分页拦截器，结合Spring data Page 和 Pageable 使用
 *
 * @author 田尘殇Sean(sean.snow@live.com) createAt 2016/11/21
 */
@Intercepts(@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
public class PageInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageInterceptor.class);

    private Dialect dialect;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final Object[] args = invocation.getArgs();
        for (Object arg : args) {
            if (arg instanceof Pageable) {
                LOGGER.debug("发现Spring分页参数,执行分页查询.");
                Pageable pageable = (Pageable) arg;
                MappedStatement ms = (MappedStatement) args[0];
                final Object parameter = args[1];
                final BoundSql boundSql = ms.getBoundSql(parameter);
                String sql = boundSql.getSql().trim().replaceAll(";$", "");

                Long total = executeCountQuery(sql, ms, boundSql);

                // 获取分页SQL，并完成参数准备
                String limitSql = dialect.getLimitString(sql, pageable.getOffset(), pageable.getPageSize());
                args[2] = RowBounds.DEFAULT;
                args[0] = copyFromNewSql(ms, boundSql, limitSql);

                Object result = invocation.proceed();

                Page<?> page = new PageImpl<>((List<?>) result, pageable, total);

                List<Page<?>> tmp = new ArrayList<>(1);
                tmp.add(page);

                return tmp;
            }
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
        Database database = Database.valueOf(properties.getProperty("database").toUpperCase());
        LOGGER.debug("database -> {}", database);
        switch (database) {
            case DB2:
                break;
            case DERBY:
                break;
            case H2:
                this.dialect = new H2Dialect();
                break;
            case HSQL:
                break;
            case INFORMIX:
                break;
            case MYSQL:
                this.dialect = new MySQLDialect();
                break;
            case ORACLE:
                this.dialect = new OracleDialect();
                break;
            case POSTGRESQL:
                this.dialect = new PostgreSQLDialect();
                break;
            case SQL_SERVER:
                break;
            case SYBASE:
                break;
        }
    }

    private Long executeCountQuery(String sql, MappedStatement mappedStatement, BoundSql boundSql) throws SQLException {
        Connection connection = null;
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            connection = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
            String countSql = this.dialect.getCountString(sql);
            LOGGER.debug("Count Query ==> {}", countSql);
            countStmt = connection.prepareStatement(countSql);
            BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
            setParameters(countStmt, mappedStatement, countBoundSql, boundSql.getParameterObject());
            rs = countStmt.executeQuery();
            Long totalCount = 0L;
            if (rs.next()) {
                totalCount = rs.getLong(1);
            }

            LOGGER.debug("total size -> {}", totalCount);

            return totalCount;
        } catch (SQLException e) {
            LOGGER.error("查询总记录数出错", e);
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    LOGGER.error("exception happens when doing: ResultSet.close()", e);
                }
            }
            if (countStmt != null) {
                try {
                    countStmt.close();
                } catch (SQLException e) {
                    LOGGER.error("exception happens when doing: PreparedStatement.close()", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.error("exception happens when doing: Connection.close()", e);
                }
            }
        }
    }

    /**
     * 对SQL参数(?)设值
     */
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
        parameterHandler.setParameters(ps);
    }

    private MappedStatement copyFromNewSql(MappedStatement ms, BoundSql boundSql, String sql) {
        BoundSql newBoundSql = copyFromBoundSql(ms, boundSql, sql);
        return copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
    }

    private BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String sql) {
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        return newBoundSql;
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {

        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }


        //setStatementTimeout()
        builder.timeout(ms.getTimeout());

        //setStatementResultMap()
        builder.parameterMap(ms.getParameterMap());

        //setStatementResultMap()
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());


        //setStatementCache()
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();

    }

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public static class BoundSqlSqlSource implements SqlSource {

        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }

    }

}
