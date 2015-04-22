/*
 * Copyright 2005-2013 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.LockModeType;
import javax.servlet.http.HttpServletRequest;

import net.shopxx.Message;
import net.shopxx.Principal;
import net.shopxx.Setting;
import net.shopxx.dao.DepositDao;
import net.shopxx.dao.MemberDao;
import net.shopxx.dao.PointDao;
import net.shopxx.entity.Admin;
import net.shopxx.entity.Deposit;
import net.shopxx.entity.Member;
import net.shopxx.entity.Point;
import net.shopxx.entity.Point.BizType;
import net.shopxx.service.MemberService;
import net.shopxx.util.SettingUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Service - 会员
 * 
 * @author SHOP++ Team
 * @version 3.0
 */
@Service("memberServiceImpl")
public class MemberServiceImpl extends BaseServiceImpl<Member, Long> implements MemberService {

	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;
	@Resource(name = "depositDaoImpl")
	private DepositDao depositDao;
	@Resource(name = "pointDaoImpl")
	private PointDao pointDao;

	@Resource(name = "memberDaoImpl")
	public void setBaseDao(MemberDao memberDao) {
		super.setBaseDao(memberDao);
	}

	@Transactional(readOnly = true)
	public boolean usernameExists(String username) {
		return memberDao.usernameExists(username);
	}

	@Transactional(readOnly = true)
	public boolean usernameDisabled(String username) {
		Assert.hasText(username);
		Setting setting = SettingUtils.get();
		if (setting.getDisabledUsernames() != null) {
			for (String disabledUsername : setting.getDisabledUsernames()) {
				if (StringUtils.containsIgnoreCase(username, disabledUsername)) {
					return true;
				}
			}
		}
		return false;
	}

	@Transactional(readOnly = true)
	public boolean emailExists(String email) {
		return memberDao.emailExists(email);
	}

	@Transactional(readOnly = true)
	public boolean emailUnique(String previousEmail, String currentEmail) {
		if (StringUtils.equalsIgnoreCase(previousEmail, currentEmail)) {
			return true;
		} else {
			if (memberDao.emailExists(currentEmail)) {
				return false;
			} else {
				return true;
			}
		}
	}

	public void save(Member member, Admin operator) {
		Assert.notNull(member);
		memberDao.persist(member);
		if (member.getBalance().compareTo(new BigDecimal(0)) > 0) {
			Deposit deposit = new Deposit();
			deposit.setType(operator != null ? Deposit.Type.adminRecharge : Deposit.Type.memberRecharge);
			deposit.setCredit(member.getBalance());
			deposit.setDebit(new BigDecimal(0));
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMember(member);
			depositDao.persist(deposit);
		}
	}

	public void update(Member member, Integer modifyPoint, BigDecimal modifyBalance, String depositMemo, Admin operator) {
		Assert.notNull(member);

		memberDao.lock(member, LockModeType.PESSIMISTIC_WRITE);

		if (modifyPoint != null && modifyPoint != 0 && member.getPoint() + modifyPoint >= 0) {
			member.setPoint(member.getPoint() + modifyPoint);
		}

		if (modifyBalance != null && modifyBalance.compareTo(new BigDecimal(0)) != 0 && member.getBalance().add(modifyBalance).compareTo(new BigDecimal(0)) >= 0) {
			member.setBalance(member.getBalance().add(modifyBalance));
			Deposit deposit = new Deposit();
			if (modifyBalance.compareTo(new BigDecimal(0)) > 0) {
				deposit.setType(operator != null ? Deposit.Type.adminRecharge : Deposit.Type.memberRecharge);
				deposit.setCredit(modifyBalance);
				deposit.setDebit(new BigDecimal(0));
			} else {
				deposit.setType(operator != null ? Deposit.Type.adminChargeback : Deposit.Type.memberPayment);
				deposit.setCredit(new BigDecimal(0));
				deposit.setDebit(modifyBalance);
			}
			deposit.setBalance(member.getBalance());
			deposit.setOperator(operator != null ? operator.getUsername() : null);
			deposit.setMemo(depositMemo);
			deposit.setMember(member);
			depositDao.persist(deposit);
		}
		memberDao.merge(member);
	}

	@Transactional(readOnly = true)
	public Member findByUsername(String username) {
		return memberDao.findByUsername(username);
	}

	@Transactional(readOnly = true)
	public List<Member> findListByEmail(String email) {
		return memberDao.findListByEmail(email);
	}

	@Transactional(readOnly = true)
	public List<Object[]> findPurchaseList(Date beginDate, Date endDate, Integer count) {
		return memberDao.findPurchaseList(beginDate, endDate, count);
	}

	@Transactional(readOnly = true)
	public boolean isAuthenticated() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			if (principal != null) {
				return true;
			}
		}
		return false;
	}

	@Transactional(readOnly = true)
	public Member getCurrent() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			if (principal != null) {
				return memberDao.find(principal.getId());
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	public String getCurrentUsername() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME);
			if (principal != null) {
				return principal.getUsername();
			}
		}
		return null;
	}

	/**
	 * 根据邀请码查找会员
	 * @author keyruong
	 * @param referrerCode
	 *            邀请码
	 * @return 会员，若不存在则返回null
	 */
	@Transactional(readOnly = true)
	public Member findByReferrerCode(String referrerCode){
		return memberDao.findByReferrerCode(referrerCode);
	}
	
	@Transactional
	public Message save(Member member,String referrerCode) {
		if(StringUtils.isNotBlank(referrerCode)){
			Member referrer= memberDao.findByReferrerCode(referrerCode);
			member.setReferrer(referrer);
			if(referrer==null){//根据推荐码查无此会员，则返回提示
				return Message.error("shop.common.invalidReferrer");
			}else{
				//给推荐人加上会员等级的推荐积分
				Long point=referrer.getMemberRank().getPoint();
				if(point!=null){
					referrer.setPoint(referrer.getPoint()+point);
				}else{//默认10分
					referrer.setPoint(referrer.getPoint()+10);
				}
				//生成新会员的邀请码
				member.setInviteCode(generateInviteCode());
				memberDao.persist(member);
				memberDao.merge(referrer);
				//保存推荐人推荐积分记录
				Point points=new Point();
				points.setPoint(point==null?10:point);
				points.setType(Point.Type.in);
				points.setMember(referrer);
				points.setSourceMember(member);
				points.setBizType(BizType.RECOM_MEMBER);
				points.setIntro(Point.getOrderPointIntro(BizType.RECOM_MEMBER,null));
				points.setTotalPoint(referrer.getPoint());
				points.setStatus(Point.Status.valid);
				pointDao.persist(points);
				return Message.success("成功");
			}
		}else{
			//生成新会员的邀请码
			member.setInviteCode(generateInviteCode());
		    memberDao.persist(member);
		    return Message.success("成功");
		}
	}
	
	/**
	* @Title: generateInviteCode
	* @Description: 生成新用户邀请码
	* 获取当前会员总数，补位6位，加上2位秒   防止冲突
	* @return
	* @return String    返回类型
	* @throws
	* @author keyurong
	 */
	public String generateInviteCode(){
		Long count= memberDao.findMemberCount();
		Integer code=count.intValue()+1;
		NumberFormat formatter = NumberFormat.getNumberInstance(); 
		formatter.setMinimumIntegerDigits(5); 
		formatter.setGroupingUsed(false); 
		String inviteCode= formatter.format(code); 
		Date date=new Date();
		int random=(int)(Math.random()*10);
		String seconds =String.valueOf(date.getSeconds());
		return inviteCode+seconds+String.valueOf(random);
	}
}