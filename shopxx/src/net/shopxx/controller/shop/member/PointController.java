package net.shopxx.controller.shop.member;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Member;
import net.shopxx.service.MemberService;
import net.shopxx.service.PointService;

/**
 * 
* @Title: PointController.java
* @Package net.shopxx.controller.shop.member
* @Description: 积分记录控制层
* @author keyurong
* @date 2015年4月13日 下午12:02:37
* @version V1.0
 */
@Controller("shopMemberPointController")
@RequestMapping("/member/point")
public class PointController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;
	
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	
	@Resource(name = "pointServiceImpl")
	private PointService pointService;
	
	
	/**
	* @Title: list
	* @Description: 获得会员积分记录
	* @param pageNumber
	* @param model
	* @return
	* @return String    返回类型
	* @throws
	* @author keyurong
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Integer pageNumber, ModelMap model) {
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		model.addAttribute("page", pointService.findPage(member, pageable));
		return "shop/member/point/list";
	}
	
	/**
	* @Title: recomList
	* @Description: 获取会员所推荐会员的消费积分
	* @param pageNumber
	* @param model
	* @return
	* @return String    返回类型
	* @throws
	* @author keyurong
	 */
	@RequestMapping(value = "/recom_list", method = RequestMethod.GET)
	public String recomList(Integer pageNumber, ModelMap model) {
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		model.addAttribute("page", pointService.findRecomMemberCostPage(member, pageable));
		model.addAttribute("total", pointService.findSumMemberCost(member));
		return "shop/member/point/recom_list";
	}
}
