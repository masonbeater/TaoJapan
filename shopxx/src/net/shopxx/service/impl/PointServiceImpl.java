package net.shopxx.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.PointDao;
import net.shopxx.entity.Member;
import net.shopxx.entity.Order;
import net.shopxx.entity.Point;
import net.shopxx.service.PointService;
/**
* @Title: PointServiceImpl.java
* @Package net.shopxx.service.impl
* @Description: 积分记录Service实现类
* @author keyurong
* @date 2015年4月13日 下午1:45:01
* @version V1.0
 */
@Service("pointServiceImpl")
public class PointServiceImpl extends BaseServiceImpl<Point,Long> implements PointService {

	@Resource(name = "pointDaoImpl")
	private PointDao pointDao;
	
	@Resource(name = "pointDaoImpl")
	public void setBaseDao(PointDao pointDao) {
		super.setBaseDao(pointDao);
	}
	
	@Transactional(readOnly = true)
	public Page<Point> findPage(Member member, Pageable pageable) {
		return pointDao.findPage(member, pageable);
	}
	
	
	@Transactional(readOnly = true)
	public Page<Point> findRecomMemberCostPage(Member member, Pageable pageable) {
		return pointDao.findRecomPage(member, pageable);
	}
	
	@Transactional(readOnly = true)
	public Long findSumMemberCost(Member member){
		return pointDao.findSumMemberCost(member);
	}
}
