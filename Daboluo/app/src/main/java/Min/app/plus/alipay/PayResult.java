package Min.app.plus.alipay;

/**
 * 作者：daboluo on 2023/8/28 00:03
 * Email:daboluo719@gmail.com
 */

import android.text.TextUtils;

/**
 * author：congge
 * date: 2018/8/28 17:53
 * desc:解析支付宝支付返回string
 * 返回例子： resultStatus={9000};memo={};result={{"alipay_trade_app_pay_response":{"code":"10000","msg":"Success","app_id":"2016091300503896","auth_app_id":"2016091300503896","charset":"utf-8","timestamp":"2018-08-28 17:51:11","out_trade_no":"nVElbd74TW6WnEyxQwvX8A","total_amount":"0.01","trade_no":"2018082821001004680500208879","seller_id":"2088102175487650"},"sign":"W0Hg9k4GxL8Oaxymvqk2i65WNDQxYp6HGve32ek6VjSRnymmI3GQTjpQVbZuDzvjcwQ/HIkM97PoBGAVlTmi/wiJcqDgSSDzDY7AFnNN0OcK0ehWGwKQINA4IDGh51A7yY/vYKmR0VW+2OwGhlRPPMMZtQOEqh8a9/aIijzT6ZLwy9Hl4ayG/fVKhdC1VdckF6+C25BFNp3fIxarg5tfEunm7N9iWngKCUsnP+IZz05OHdvynimgYPcBnbBERHG97GVqRT/EdBWTQyIDMc0LemScAYxJixTVgXDkRddQjzWZ7HgLdBfjs0nXY24puHudT76ERxVY+8NkoKle/QI+FA==","sign_type":"RSA2"}}
 *
 **/
public class PayResult {
    private String resultStatus;
    private String result;
    private String memo;

    public PayResult(String rawResult) {

        if (TextUtils.isEmpty(rawResult))
            return;

        String[] resultParams = rawResult.split(";");
        for (String resultParam : resultParams) {
            if (resultParam.startsWith("resultStatus")) {
                resultStatus = gatValue(resultParam, "resultStatus");
            }
            if (resultParam.startsWith("result")) {
                result = gatValue(resultParam, "result");

            }
            if (resultParam.startsWith("memo")) {
                memo = gatValue(resultParam, "memo");
            }
        }
    }

    @Override
    public String toString() {
        return "resultStatus={" + resultStatus + "};memo={" + memo
                + "};result={" + result + "}";
    }

    private String gatValue(String content, String key) {
        String prefix = key + "={";
        return content.substring(content.indexOf(prefix) + prefix.length(),
                content.lastIndexOf("}"));
    }

    public String outOrder() {
        String order = "\"out_trade_no\"";
//        String begin = result.substring(result.indexOf(order) + order.length());
//        String outOrder = begin.substring(0, begin.indexOf("\""));
        if (result.contains(order)) {
            String begin = result.substring(result.indexOf(order));
            String ss = begin.split(",")[0];
            String newS = ss.replace("\"", "")
                    .replace("}", "")
                    .replace(":", "")
                    .replace("out_trade_no", "");
            try {
                return newS;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * @return the resultStatus
     */
    public String getResultStatus() {
        return resultStatus;
    }

    /**
     * @return the memo
     */
    public String getMemo() {
        return memo;
    }

    /**
     * @return the result
     */
    public String getResult() {
        return result;
    }
}