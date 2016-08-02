package software.sitb.spring.data.commons.utils;

import org.springframework.data.domain.Sort;

/**
 * @author 田尘殇Sean(sean.snow@live.com) Create At 16/8/1
 */
public class QueryUtils {

    /**
     * Sort 转换为字符串
     *
     * @param alias 表别名
     * @param sort  排序参数
     * @return 排序字符串
     */
    public static String toOrderStr(String alias, Sort sort) {
        StringBuilder orderStr = new StringBuilder(" ORDER BY ");
        sort.forEach(order -> {
            orderStr.append(alias).append(".").append(order.getProperty());
            orderStr.append(" ").append(order.getDirection());
        });
        return orderStr.toString();
    }

}
