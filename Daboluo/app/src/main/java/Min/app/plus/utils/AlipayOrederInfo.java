package Min.app.plus.utils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;


/**
 * 作者：daboluo on 2023/8/28 16:14
 * Email:daboluo719@gmail.com
 */
public class AlipayOrederInfo {
    public static String addPay(String out_trade_no,int total_amount,String subject,String product_code,String privateKey,String alipayPublic,String serverUrl,String signType,String appid) throws AlipayApiException {

                AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appid,privateKey, "json", "utf-8", alipayPublic, signType);
                AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
                request.setNotifyUrl("");
                JSONObject bizContent = new JSONObject();
                bizContent.put("out_trade_no", out_trade_no);//订单号
                bizContent.put("total_amount", total_amount);//订单总金额
                bizContent.put("subject", subject);//商品说明
                bizContent.put("product_code", product_code);//产品码
//bizContent.put("time_expire", "2022-08-01 22:00:00");

//// 商品明细信息，按需传入
//        JSONArray goodsDetail = new JSONArray();
//        JSONObject goods1 = new JSONObject();
//        goods1.put("goods_id", "goodsNo1");
//        goods1.put("goods_name", "子商品1");
//        goods1.put("quantity", 1);
//        goods1.put("price", 0.01);
//        goodsDetail.add(goods1);
//        bizContent.put("goods_detail", goodsDetail);

//// 扩展信息，按需传入
//JSONObject extendParams = new JSONObject();
//extendParams.put("sys_service_provider_id", "2088511833207846");
//bizContent.put("extend_params", extendParams);

                request.setBizContent(bizContent.toString());

                    AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
                    if (response.isSuccess()) {
//            System.out.println(response.getBody());
                        System.out.println("调用成功");
                        return response.getBody();
                    } else {
                        System.out.println("调用失败");
                        return "500";
                    }
    }
}