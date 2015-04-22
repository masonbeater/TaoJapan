package net.shopxx.service;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Member;
import net.shopxx.entity.Point;
/**
* @Title: PointService.java
* @Package net.shopxx.service
* @Description: 积分记录Service
* @author keyurong
* @date 2015年4月13日 下午1:44:01
* @version V1.0
 */
public interface PointService extends BaseService<Point, Long> {
	
    /**
    * @Title: findPage
    * @Description: 获取积分列表
    * @param member 所属会员
    * @param pageable 分页对象
    * @return
    * @return Page<Point>    返回类型
    * @throws
    * @author keyurong
     */
	public Page<Point> findPage(Member member, Pageable pageable);
	
	/**
	* @Title: findRecomMemberCostPage
	* @Description: 获取推荐新会员积分记录
	* @param member
	* @param pageable
	* @return
	* @return Page<Point>    返回类型
	* @throws
	* @author keyurong
	 */
	public Page<Point> findRecomMemberCostPage(Member member, Pageable pageable);
	
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
