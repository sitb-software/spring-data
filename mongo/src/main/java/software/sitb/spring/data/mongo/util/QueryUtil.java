package software.sitb.spring.data.mongo.util;

import java.util.Date;

/**
 * @author Sean createAt 2021/7/6
 */
public class QueryUtil {

    /**
     * 机器码和自增号
     */
    private static final String MACHINE_INC = "0000000000000000";

    public static String date2ObjectId() {
        return date2ObjectId(new Date());
    }

    public static String date2ObjectId(Date date) {
        int time = (int) (date.getTime() / 1000);
        return Integer.toHexString(time) + MACHINE_INC;
    }

    public static void main(String[] args) {
        System.out.println(date2ObjectId());
    }
}
