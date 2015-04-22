/*
 * Copyright 2005-2013 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service;

import javax.persistence.LockModeType;

import net.shopxx.entity.Admin;
import net.shopxx.entity.Order;
import net.shopxx.entity.Payment;
import net.shopxx.entity.Refunds;

/**
 * Service - 退款单
 * 
 * @author SHOP++ Team
 * @version 3.0
 */
public interface RefundsService extends BaseService<Refunds, Long> {
	/**
	 * 根据退款单编号查找退款单
	 * 
	 * @param sn
	 *            退款单编号(忽略大小写)
	 * @return 若不存在则返回null
	 */
	Refunds findBySn(String sn);
	
	/**
	 * 讲数据库中数据更新到实体entity中，并加锁
	 * 
	 * @param refunds
	 *            
	 */
	public void refresh(Refunds refunds,LockModeType lockModeType);
	
	/**
	 * 退款成功后操需要做的操作
	 * 
	 * @param refunds operator
	 *            
	 */
	public void handle(Refunds refunds,Admin operator);
}