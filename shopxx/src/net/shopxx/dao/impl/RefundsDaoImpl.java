/*
 * Copyright 2005-2013 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import net.shopxx.dao.RefundsDao;
import net.shopxx.entity.Order;
import net.shopxx.entity.Refunds;

import org.springframework.stereotype.Repository;

/**
 * Dao - 退款单
 * 
 * @author SHOP++ Team
 * @version 3.0
 */
@Repository("refundsDaoImpl")
public class RefundsDaoImpl extends BaseDaoImpl<Refunds, Long> implements RefundsDao {

	public Refunds findBySn(String sn) {
		if (sn == null) {
			return null;
		}
		String jpql = "select refunds from Refunds refunds where lower(refunds.sn) = lower(:sn)";
		try {
			return entityManager.createQuery(jpql, Refunds.class).setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}