package software.sitb.spring.data.mybatis.dialect;


public class OracleDialect extends Dialect {

    /**
     * 分页sql
     */
    final static String LIMIT_SQL_PATTERN = "select * from ( select row__.*, rownum rownum__ from ( %s ) row__ where rownum <=  %s ) where rownum__ > %s ";


    /**
     * 分页sql首页
     */
    final static String LIMIT_SQL_PATTERN_FIRST = "select * from ( %s ) where rownum <= %s";


    @Override
    public String getLimitString(String sql, int offset, int limit) {
        sql = sql.trim();

        // no supports "for update", kill it
        sql = sql.replaceAll("for\\s+update", "");


        if (offset == 0) {
            return String.format(LIMIT_SQL_PATTERN_FIRST, sql, limit);
        }

        return String.format(LIMIT_SQL_PATTERN, sql, offset + limit, offset);
    }
}