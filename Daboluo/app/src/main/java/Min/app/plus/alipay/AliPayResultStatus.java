package Min.app.plus.alipay;

/**
 * 作者：daboluo on 2023/8/28 00:00
 * Email:daboluo719@gmail.com
 */
public class AliPayResultStatus {
    /**
     * 订单支付成功,唯一肯定是支付成功的
     */
    public static final String PAY_SUCCESS = "9000";
    /**
     * 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
     */
    public static final String PAY_PROCESSING = "8000";
    /**
     * 订单支付失败
     */
    public static final String PAY_FAIL = "4000";
    /**
     * 重复请求
     */
    public static final String PAY_REPEAT = "5000";
    /**
     * 用户中途取消
     */
    public static final String PAY_PROCESS_CANCEL = "6001";
    /**
     * 网络连接出错
     */
    public static final String PAY_NET_ERROR = "6002";
    /**
     * 支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
     */
    public static final String PAY_UNKNOWN = "6004";
}