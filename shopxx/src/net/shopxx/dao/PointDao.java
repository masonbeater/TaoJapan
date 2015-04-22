package net.shopxx.dao;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Member;
import net.shopxx.entity.Point;
/**
* @Title: PointDao.java
* @Package net.shopxx.dao
* @Description: 积分记录Dao接口
* @author keyurong
* @date 2015年4月13日 下午1:54:36
* @version V1.0
 */
public interface PointDao extends BaseDao<Point, Long> {

	/**
	* @Title: findPage
	* @Description: 积分列表
	* @param member 所属会员
	* @param pageable 分页对象
	* @return
	* @return Page<Point>    返回类型
	* @throws
	* @author keyurong
	 */
	public Page<Point> findPage(Member member, Pageable pageable);
	
	/**
	* @Title: findRecomPage
	* @Description: 获得推荐会员积分列表
	* @param member 所属会员
	* @param pageable 分页对象
	* @return
	* @return Page<Point>    返回类型
	* @throws
	* @author keyurong
	 */
	public Page<Point> findRecomPage(Member member, Pageable pageable);
	
	/**
	* @Title: findSumMemberCost
	* @Description: 统计会员推荐的所有会员的消费积分
	* @param member 所属会员
	* @return
	* @return Long    返回类型
	* @throws
	* @author keyurong
	 */
	public Long findSumMemberCost(Member member);
}
