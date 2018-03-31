package org.andy.alipay.test;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.andy.alipay.model.JsonResult;
import org.andy.alipay.util.AlipayUtil;
import org.andy.alipay.util.DatetimeUtil;
import org.andy.alipay.util.PayUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;

/**
 * 创建时间：2016年11月7日 上午11:58:55
 * 
 * @author andy
 * @version 2.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml", "classpath:rest-servlet.xml" })
public class HttpTest {

	@Test
	public void testGet() throws UnsupportedEncodingException {
		Map<String, String> param = new HashMap<>();
		// 公共请求参数
		param.put("app_id", AlipayUtil.ALIPAY_APPID);// 商户订单号
		param.put("method", "alipay.trade.app.pay");// 交易金额
		param.put("format", AlipayConstants.FORMAT_JSON);
		param.put("charset", AlipayConstants.CHARSET_UTF8);
		param.put("timestamp", DatetimeUtil.formatDateTime(new Date()));
		param.put("version", "1.0");
		param.put("notify_url", "https://www.huazhi.co/alipay/order/pay/notify.shtml"); // 支付宝服务器主动通知商户服务
		param.put("sign_type", AlipayConstants.SIGN_TYPE_RSA);

		Map<String, Object> pcont = new HashMap<>();
		// 支付业务请求参数
		pcont.put("out_trade_no", PayUtil.getTradeNo()); // 商户订单号
		pcont.put("total_amount", String.valueOf(001));// 交易金额
		pcont.put("subject", "懒人用卡-羊毛社区-付费提问"); // 订单标题
		pcont.put("body", "华智网络");// 对交易或商品的描述
		pcont.put("product_code", "QUICK_MSECURITY_PAY");// 销售产品码
		
		param.put("biz_content", JSON.toJSONString(pcont)); // 业务请求参数
		Map<String, String> payMap = new HashMap<>();
		try {
			//param.put("sign", AlipaySignature.rsaSign(PayUtil.createSign(param, true), AlipayUtil.APP_PRIVATE_KEY, AlipayConstants.CHARSET_UTF8)); // 业务请求参数
			param.put("sign", PayUtil.getSign(param, AlipayUtil.APP_PRIVATE_KEY)); // 业务请求参数
			payMap.put("orderStr", PayUtil.getSignEncodeUrl(param, true));
			System.out.println(payMap.get("orderStr"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSign(){
		Map<String, String> param = new HashMap<>();
		// 公共请求参数
		
		param.put("app_id", "2016101002078165");// 商户订单号
		param.put("auth_app_id", "2016101002078165");// 交易金额
		param.put("body", "华智网络");
		param.put("buyer_id", "2088702728710710");
		param.put("buyer_logon_id", "970***@qq.com");
		param.put("buyer_pay_amount", "0.01");
		param.put("charset", "UTF-8");
		param.put("fund_bill_list", "[{\"amount\":\"0.01\",\"fundChannel\":\"ALIPAYACCOUNT\"}]");
		param.put("gmt_create", "2016-11-16 16:39:20");
		param.put("gmt_payment", "2016-11-16 16:39:20");
		param.put("invoice_amount", "0.01");
		param.put("notify_id", "7455900d20cba119cc2df38228f7588lha");
		param.put("notify_time", "2016-11-16 16:39:21");
		try {
			boolean signVerified = AlipaySignature.rsaCheckV1(param, AlipayUtil.ALIPAY_PUBLIC_KEY,
					AlipayConstants.CHARSET_UTF8);
			if (signVerified) {
				// TODO 验签成功后
				// 按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure
			} else {
				// TODO 验签失败则记录异常日志，并在response中返回failure.
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
		} // 调用SDK验证签名
	}

	
	@Test
	public void testJsonpares(){
		String s = "{\"code\":1,\"msg\":\"订单获取成功\",\"response\":{\"cont\":{\"orderStr\":\"app_id=2016101002078165&biz_3D\"},\"list\":[]}}";
		
		JsonResult jsonResult = JSON.toJavaObject(JSON.parseObject(s), JsonResult.class);
		Map<String, String> map = (Map<String, String>) jsonResult.getResponse().getCont();
		System.out.println(map.get("orderStr"));
	}
}
