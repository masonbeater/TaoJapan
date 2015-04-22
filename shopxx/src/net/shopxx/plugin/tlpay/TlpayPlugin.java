/*
 * Copyright 2005-2013 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.plugin.tlpay;

import java.io.File;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import net.shopxx.entity.Payment;
import net.shopxx.entity.PluginConfig;
import net.shopxx.entity.Refunds;
import net.shopxx.plugin.PaymentPlugin;
import net.shopxx.util.DateUtils;
import net.shopxx.util.SettingUtils;
import net.shopxx.util.WebUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.allinpay.ets.client.PaymentResult;

/**
 * Plugin - 通联支付
 * 
 * @author SHOP++ Team
 * @version 3.0
 */
/**  
* @Title: TlpayPlugin.java
* @Package net.shopxx.plugin.tlpay
* @Description: TODO(用一句话描述该文件做什么)
* @author Administrator 
* @date 2015年4月11日 下午4:23:17
* @version V1.0  
*/ 
/**  
* @Title: TlpayPlugin.java
* @Package net.shopxx.plugin.tlpay
* @Description: TODO(用一句话描述该文件做什么)
* @author Administrator 
* @date 2015年4月11日 下午4:23:17
* @version V1.0  
*/ 
@Component("tlpayPlugin")
public class TlpayPlugin extends PaymentPlugin {
	
	
	/**
	* @Fields INPUT_CHARSET : 编码方式 1：UTF-8
	*/ 
	public static final String INPUT_CHARSET = "1";
	
	/**
	* @Fields SIGN_TYPE : 验证方式  0:MD5;1：证书验证
	*/ 
	private static final String SIGN_TYPE_MD5 = "0";
	private static final String SIGN_TYPE_CENT = "1";
	
	/**
	* @Fields PAY_TYPE : 支付方式 0：未指定
	*/ 
	public static final String PAY_TYPE = "0";
	
	/**
	* @Fields ORDER_CURRENCY : 支付币种 0：人民币
	*/ 
	public static final String ORDER_CURRENCY = "0";
	

	public String getCentPath(){
		return SettingUtils.get().getCentPath();
	}
	public String getRefundVersion() {
		return "v1.3";
	}

	@Override
	public String getName() {
		return "通联支付";
	}

	@Override
	public String getVersion() {
		return "v1.0";
	}
	
	

	@Override
	public String getAuthor() {
		return "SHOP++";
	}

	@Override
	public String getSiteUrl() {
		return "http://www.shopxx.net";
	}

	@Override
	public String getInstallUrl() {
		return "tlpay/install.jhtml";
	}

	@Override
	public String getUninstallUrl() {
		return "tlpay/uninstall.jhtml";
	}

	@Override
	public String getSettingUrl() {
		return "tlpay/setting.jhtml";
	}

	@Override
	public String getRequestUrl() {
		return "http://ceshi.allinpay.com/gateway/index.do";
	}

	@Override
	public RequestMethod getRequestMethod() {
		return RequestMethod.post;
	}

	@Override
	public String getRequestCharset() {
		return "UTF-8";
	}
	
	/* 
	* <p>Title: getParameterMap</p>
	* <p>Description: 提交付款时候，需要向通联支付提交的参数</p>
	* @param sn
	* @param description
	* @param request
	* @return
	* @see net.shopxx.plugin.PaymentPlugin#getParameterMap(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
	*/ 
	@Override
	public Map<String, Object> getSubmitParameterMap(String sn, String description, HttpServletRequest request) {
		PluginConfig pluginConfig = getPluginConfig();
		Payment payment = getPayment(sn);
		Map<String, Object> parameterMap = new LinkedHashMap<String, Object>();
		parameterMap.put("inputCharset", INPUT_CHARSET);
		parameterMap.put("receiveUrl", getNotifyUrl(sn, NotifyMethod.general));
		parameterMap.put("version", getVersion());
		parameterMap.put("signType", SIGN_TYPE_CENT);
		parameterMap.put("merchantId", pluginConfig.getAttribute("partner"));
		parameterMap.put("orderNo", sn);
		parameterMap.put("orderAmount", payment.getAmount().multiply(new BigDecimal(100)).setScale(0).toString());
		parameterMap.put("orderCurrency", ORDER_CURRENCY);
		parameterMap.put("orderDatetime", DateUtils.dateToString(payment.getOrderdate(), "yyyyMMddHHmmss"));
		parameterMap.put("payType",PAY_TYPE);
		
		StringBuilder paramTemp = new StringBuilder(); 
		for(String key :parameterMap.keySet()){
			paramTemp.append(key+"="+parameterMap.get(key)+"&");
		}
		paramTemp.append("key="+pluginConfig.getAttribute("key"));
		String requestParam = paramTemp.toString();
		parameterMap.put("signMsg", MD5Encode(requestParam));
		return parameterMap;
	}
	
	/* (非 Javadoc)
	* <p>Title: verifyNotify</p>
	* <p>Description: 付款成功后，通联支付向商户返回的数据，并通过这些返回的数据来做验证，保证安全性</p>
	* @param sn
	* @param notifyMethod
	* @param request
	* @return
	* @see net.shopxx.plugin.PaymentPlugin#verifyNotify(java.lang.String, net.shopxx.plugin.PaymentPlugin.NotifyMethod, javax.servlet.http.HttpServletRequest)
	*/ 
	@Override
	public boolean verifySubmitNotify(String sn, NotifyMethod notifyMethod, HttpServletRequest request) {
		PluginConfig pluginConfig = getPluginConfig();
		Payment payment = getPayment(sn);
		//Map<String, String[]> parameterValuesMap = WebUtils.getParameterMap(request.getQueryString(), "GBK");
		//Map<String, Object> parameterMap = new LinkedHashMap<String, Object>();
		PaymentResult paymentResult = new PaymentResult();
		String merchantId=request.getParameter("merchantId");
		String version=request.getParameter("version");
		String language=request.getParameter("language");
		String signType=request.getParameter("signType");
		String payType=request.getParameter("payType");
		String issuerId=request.getParameter("issuerId");
		String paymentOrderId=request.getParameter("paymentOrderId");
		String orderNo=request.getParameter("orderNo");
		String orderDatetime=request.getParameter("orderDatetime");
		String orderAmount=request.getParameter("orderAmount");
		String payDatetime=request.getParameter("payDatetime");
		String payAmount=request.getParameter("payAmount");
		String ext1=request.getParameter("ext1");
		String ext2=request.getParameter("ext2");
		String payResult=request.getParameter("payResult");
		String errorCode=request.getParameter("errorCode");
		String returnDatetime=request.getParameter("returnDatetime");
		String signMsg=request.getParameter("signMsg");
		
		
		paymentResult.setMerchantId(merchantId);
		paymentResult.setVersion(version);
		paymentResult.setLanguage(language);
		paymentResult.setSignType(signType);
		paymentResult.setPayType(payType);
		paymentResult.setIssuerId(issuerId);
		paymentResult.setPaymentOrderId(paymentOrderId);
		paymentResult.setOrderNo(orderNo);
		paymentResult.setOrderDatetime(orderDatetime);
		paymentResult.setOrderAmount(orderAmount);
		paymentResult.setPayDatetime(payDatetime);
		paymentResult.setPayAmount(payAmount);
		paymentResult.setExt1(ext1);
		paymentResult.setExt2(ext2);
		paymentResult.setPayResult(payResult);
		paymentResult.setErrorCode(errorCode);
		paymentResult.setReturnDatetime(returnDatetime);
		//signMsg为服务器端返回的签名值。
		paymentResult.setSignMsg(signMsg); 
		//signType为"1"时，必须设置证书路径。
		paymentResult.setCertPath(getCentPath());
		//验证签名：返回true代表验签成功；否则验签失败。
		boolean verifyResult = paymentResult.verify();
		//验签成功，还需要判断订单状态，为"1"表示支付成功。
		boolean paySuccess = verifyResult && payResult.equals("1");
		if (paySuccess&&pluginConfig.getAttribute("partner").equals(merchantId) && sn.equals(orderNo)) {
			return true;
		}
		return false;
	}

	@Override
	public String getNotifyMessage(String sn, NotifyMethod notifyMethod, HttpServletRequest request) {
		if (SIGN_TYPE_CENT.equals(WebUtils.getParameter(request.getQueryString(), "UTF-8", "signType"))) {
			return "success";
		}
		return null;
	}

	@Override
	public Integer getTimeout() {
		return 21600;
	}
	
	/**
	* @Title: MD5Encode
	* @Description: TODO(MD5加密)
	* @param @param aData
	* @param @return
	* @param @throws SecurityException    设定文件
	* @return String    返回类型
	* @throws
	* @author Administrator 
	*/ 
	public String MD5Encode(String aData) throws SecurityException {
		String resultString = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = bytes2HexString(md.digest(aData.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
			throw new SecurityException("MD5运算失败");
		}
		return resultString;
	}
	
	public static String bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	@Override
	public Map<String, Object> getRefundParameterMap(String sn, String description, Refunds refunds) {
		PluginConfig pluginConfig = getPluginConfig();
		Payment payment = getPayment(sn);
		Map<String, Object> parameterMap = new LinkedHashMap<String, Object>();
		parameterMap.put("version", getRefundVersion());
		parameterMap.put("signType", SIGN_TYPE_MD5);
		parameterMap.put("merchantId", pluginConfig.getAttribute("partner"));
		parameterMap.put("orderNo", sn);
		parameterMap.put("refundAmount", refunds.getAmount().multiply(new BigDecimal(100)).setScale(0).toString());
		parameterMap.put("orderDatetime", DateUtils.dateToString(payment.getOrderdate(), "yyyyMMddHHmmss"));
		
		StringBuilder paramTemp = new StringBuilder(); 
		for(String key :parameterMap.keySet()){
			paramTemp.append(key+"="+parameterMap.get(key)+"&");
		}
		paramTemp.append("key="+pluginConfig.getAttribute("key"));
		String requestParam = paramTemp.toString();
		parameterMap.put("signMsg", MD5Encode(requestParam));
		return parameterMap;
	}

	@Override
	public boolean verifyRefundNotify(String sn, String response) {
		PluginConfig pluginConfig = getPluginConfig();
		Map map = new HashMap();
		PaymentResult paymentResult = new PaymentResult();
		String[] msg =response.split("&");
		String [] paramPair=null;
		for(int i=0;i<msg.length;i++){
			paramPair=msg[i].split("=");
        	map.put(paramPair[0], paramPair[1]); 
        	System.out.println(paramPair[0]+":"+map.get(paramPair[0]));
		}					
		String merchantId=map.get("merchantId").toString();
		String version=map.get("version").toString();
		String signType=map.get("signType").toString();
		String orderNo=map.get("orderNo").toString();
		String orderAmount=map.get("orderAmount").toString();
		String orderDatetime=map.get("orderDatetime").toString();
		String refundAmount=map.get("refundAmount").toString();
		String refundDatetime=map.get("refundDatetime").toString();
		String refundResult=map.get("refundResult").toString();
		String returnDatetime=map.get("returnDatetime").toString();
		String signMsg=map.get("signMsg").toString();
		
		paymentResult.setMerchantId(merchantId);
		paymentResult.setVersion(version);
		paymentResult.setSignType(signType);
		paymentResult.setOrderNo(orderNo);
		paymentResult.setOrderAmount(orderAmount);
		paymentResult.setOrderDatetime(orderDatetime);
		paymentResult.setRefundAmount(refundAmount);
		paymentResult.setRefundDatetime(refundDatetime);
		paymentResult.setRefundResult(refundResult);
		paymentResult.setReturnDatetime(returnDatetime);
		paymentResult.setSignMsg(signMsg);
		paymentResult.setKey(pluginConfig.getAttribute("key"));
		//验证签名：返回true代表验签成功；否则验签失败。
		boolean verifyResult = paymentResult.verify();
		//验签成功，还需要判断订单状态，为"1"表示支付成功。
		if (verifyResult&&pluginConfig.getAttribute("partner").equals(merchantId)) {
			return true;
		}
		return false;
	}

}