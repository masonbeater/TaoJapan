/*
 * Copyright 2005-2013 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import net.shopxx.entity.Refunds;

/**
 * Dao - 退款单
 * 
 * @author SHOP++ Team
 * @version 3.0
 */
public interface RefundsDao extends BaseDao<Refunds, Long> {
	/**
	 * 根据退款单编号查找退款单
	 * 
	 * @param sn
	 *            退款单编号(忽略大小写)
	 * @return 若不存在则返回null
	 */
	Refunds findBySn(String sn);
}