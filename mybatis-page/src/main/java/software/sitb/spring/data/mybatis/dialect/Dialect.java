package software.sitb.spring.data.mybatis.dialect;

import org.springframework.data.domain.Sort;

public abstract class Dialect {


    /**
     * 返回分页sql，无占位符，limit和 offset 直接写死在sql中
     */

    public abstract String getLimitString(String sql, int offset, int limit);

    public String getSortSql(Sort sort) {
        if (null == sort) {
            return "";
        }


        StringBuilder sortStr = new StringBuilder(" ORDER BY ");

        for (Sort.Order order : sort) {
            sortStr.append(order.getProperty()).append(" ").append(order.getDirection()).append(",");
        }
        return sortStr.toString();
    }

    /**
     * 将sql转换为总记录数SQL
     *
     * @param sql SQL语句
     * @return 总记录数的sql
     */

    public String getCountString(String sql) {
        return "select count(1) from (" + sql + ") tmp_count";
    }

}