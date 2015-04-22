package net.shopxx.dao.impl;


import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.PointDao;
import net.shopxx.entity.Member;
import net.shopxx.entity.Order;
import net.shopxx.entity.OrderItem;
import net.shopxx.entity.Point;
import net.shopxx.entity.Product;

@Repository("pointDaoImpl")
public class PointDaoImpl extends BaseDaoImpl<Point, Long> implements PointDao {


	public Page<Point> findPage(Member member, Pageable pageable) {
		if (member == null) {
			return new Page<Point>(Collections.<Point> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Point> criteriaQuery = criteriaBuilder.createQuery(Point.class);
		Root<Point> root = criteriaQuery.from(Point.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("member"), member));
		return super.findPage(criteriaQuery, pageable);
	}
	
	public Page<Point> findRecomPage(Member member, Pageable pageable) {
		if (member == null) {
			return new Page<Point>(Collections.<Point> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Point> criteriaQuery = criteriaBuilder.createQuery(Point.class);
		Root<Point> root = criteriaQuery.from(Point.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
	    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
	    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("bizType"), Point.BizType.RECOM_MEMBER));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
	
	public Long findSumMemberCost(Member member){
		Long count=(long) 0;
		try {
		  String jpql = "select sum(point.sourceMember.payPoint) from Point point   where point.member=:member and point.bizType = :type ";
		  count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("member", member).setParameter("type",Point.BizType.RECOM_MEMBER).getSingleResult();
		} catch (NoResultException e) {
			count=(long) 0;
		}
		return count;
	}
}
