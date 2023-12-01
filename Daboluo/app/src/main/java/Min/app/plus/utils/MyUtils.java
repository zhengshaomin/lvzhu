package Min.app.plus.utils;

/**
 * 作者：daboluo on 2023/9/1 00:12
 * Email:daboluo719@gmail.com
 */
public class MyUtils {

    //隐藏手机号
    public static String maskPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
}
