/*
 * Copyright 2005-2013 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.persistence.LockModeType;

import net.shopxx.dao.OrderDao;
import net.shopxx.dao.OrderLogDao;
import net.shopxx.dao.RefundsDao;
import net.shopxx.entity.Admin;
import net.shopxx.entity.Order;
import net.shopxx.entity.OrderLog;
import net.shopxx.entity.Payment;
import net.shopxx.entity.Refunds;
import net.shopxx.entity.Order.PaymentStatus;
import net.shopxx.entity.OrderLog.Type;
import net.shopxx.service.RefundsService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 退款单
 * 
 * @author SHOP++ Team
 * @version 3.0
 */
@Service("refundsServiceImpl")
public class RefundsServiceImpl extends BaseServiceImpl<Refunds, Long> implements RefundsService {
	
	@Resource(name = "refundsDaoImpl")
	private RefundsDao refundsDao;
	@Resource(name = "orderDaoImpl")
	private OrderDao orderDao;
	@Resource(name = "orderLogDaoImpl")
	private OrderLogDao orderLogDao;
	
	@Resource(name = "refundsDaoImpl")
	public void setBaseDao(RefundsDao refundsDao) {
		super.setBaseDao(refundsDao);
	}
	
	@Transactional(readOnly = true)
	public Refunds findBySn(String sn) {
		return refundsDao.findBySn(sn);
	}
	@Transactional(readOnly = true)
	public void refresh(Refunds refunds,LockModeType lockModeType) {
		refundsDao.refresh(refunds, lockModeType);
	}
	@Transactional
	public void handle(Refunds refunds,Admin operator){
		refundsDao.refresh(refunds, LockModeType.PESSIMISTIC_WRITE);
		refunds.setStatus(Refunds.Status.success);
		refundsDao.merge(refunds);
		
		Order order = refunds.getOrder();
		order.setAmountPaid(order.getAmountPaid().subtract(refunds.getAmount()));
		order.setExpire(null);
		if (order.getAmountPaid().compareTo(new BigDecimal(0)) == 0) {
			order.setPaymentStatus(PaymentStatus.refunded);
		} else if (order.getAmountPaid().compareTo(new BigDecimal(0)) > 0) {
			order.setPaymentStatus(PaymentStatus.partialRefunds);
		}
		orderDao.merge(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(Type.refunds);
		orderLog.setOperator(operator != null ? operator.getUsername() : null);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);
	}
}