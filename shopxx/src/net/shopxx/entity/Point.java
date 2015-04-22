package net.shopxx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
* @Title: Point.java
* @Package net.shopxx.entity
* @Description: 积分记录Entity
* @author keyurong
* @date 2015年4月13日 上午11:04:59
* @version V1.0
 */
@Entity
@Table(name = "xx_point")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_point_sequence")
public class Point extends BaseEntity {

	private static final long serialVersionUID = 1333130682342345435L;
	
	/** 推荐会员说明 */
	public static final String RECOMMEND_MEMBER = "推荐新会员积分";
	
	/** 消费抵用积分说明 */
	public static String ORDER_COST = "订单：?抵用积分";
	
	/** 订单获得积分抵用说明 */
	public static String ORDER_GET = "订单：?获得积分";
	
	
    /**
     *积分类型 
     */
	public enum Type{
		
		/** 支出*/
		out,
		
		/** 收入*/
		in
	}
	
	/**
     *积分状态 
     */
	public enum Status{
		
		/**无效 */
		invalid,
		
		/**有效 */
		valid,
		
		/**过期 */
		overdue
	}
	
	/**
     *积分业务类型 
     */
	public enum BizType{
		
		/** 邀请会员*/
		RECOM_MEMBER,
		
		/** 订单消费抵用*/
		ORDER_COST,
		
		/** 订单消费获得*/
		ORDER_GET
	}
	
	
	/** 类型*/
	private Type type;
	
	/** 业务类型*/
	private BizType bizType;
	
	/** 积分*/
	private Long point;
	
	/** 来源订单*/
	private Order sourceOrder;
	
	/** 来源会员*/
    private Member sourceMember;
    
    /** 所属会员*/
    private Member member;
    
    /** 简介*/
    private String intro;
    
    /** 状态*/
    private Status status;
    
    /** 总计积分*/
    private Long totalPoint;


    /**
	 * 获取来源订单
	 * 
	 * @return 该积分记录来源订单
	 */
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="source_order")
    public Order getSourceOrder() {
        return this.sourceOrder;
    }
    
	/**
	 * 设置来源订单
	 * 
	 * @param sourceOrder 该记录来源订单
	 */
    public void setSourceOrder(Order sourceOrder) {
        this.sourceOrder = sourceOrder;
    }
    
    /**
	 * 获取来源会员
	 * 
	 * @return 该积分记录来源会员（推荐、赠送）
	 */
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="source_member")
    public Member getSourceMember() {
        return this.sourceMember;
    }
    
	/**
	 * 设置来源会员
	 * 
	 * @param sourceMember 该积分来源会员
	 */
    public void setSourceMember(Member sourceMember) {
        this.sourceMember = sourceMember;
    }
    
    /**
	 * 获取所属会员
	 * 
	 * @return 该积分记录所属会员
	 */
	@ManyToOne(fetch=FetchType.LAZY)
        @JoinColumn(name="member")
    public Member getMember() {
        return this.member;
    }
    
	/**
	 * 设置所属会员
	 * 
	 * @param member 该积分来源会员
	 */
    public void setMember(Member member) {
        this.member = member;
    }
    
    /**
	 * 积分类型
	 * 
	 * @return 积分类型
	 */
    @Column(name="type")
    public Type getType() {
        return this.type;
    }
    
    /**
	 * 设置积分类型
	 * 
	 * @param type 积分类型
	 */
    public void setType(Type type) {
        this.type = type;
    }
    
    /**
	 * 积分业务类型
	 * 
	 * @return 积分业务类型
	 */
    @Column(name="biz_type")
    public BizType getBizType() {
        return this.bizType;
    }
    
    /**
	 * 设置积分业务类型
	 * 
	 * @param bizType 积分业务类型
	 */
    public void setBizType(BizType bizType) {
        this.bizType = bizType;
    }
    
    /**
	 * 变动积分
	 * 
	 * @return 变动积分
	 */
    @Column(name="point")
    public Long getPoint() {
        return this.point;
    }
    
    /**
	 * 设置变动积分
	 * 
	 * @param point 变动积分
	 */
    public void setPoint(Long point) {
        this.point = point;
    }
    
    /**
	 * 简介
	 * 
	 * @return 积分变动说明
	 */
    @Column(name="intro")
    public String getIntro() {
        return this.intro;
    }
    
    /**
	 * 设置积分简介
	 * 
	 * @param intro 积分简介
	 */
    public void setIntro(String intro) {
        this.intro = intro;
    }
    
    /**
	 * 积分状态
	 * 
	 * @return 积分状态
	 */
    @Column(name="status")
    public Status getStatus() {
        return this.status;
    }
    
    /**
	 * 设置积分状态
	 * 
	 * @param status 积分状态
	 */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
	 * 总计积分
	 * 
	 * @return 此时的积分总数
	 */
    @Column(name="total_point")
	public Long getTotalPoint() {
		return totalPoint;
	}

    /**
	 * 设置总计积分
	 * 
	 * @param totalPoint 总计积分
	 */
	public void setTotalPoint(Long totalPoint) {
		this.totalPoint = totalPoint;
	}
    
	/**
	 * 获取来源订单
	 * @param intro 说明 
	 *        Point.BizType.RECOM_MEMBER  推荐会员说明
	          Point.BizType.ORDER_COST  消费抵用积分说明
	          Point.BizType.ORDER_GET   订单获得积分抵用说明
	 * @param sn 订单号
	 * @return 该积分记录来源订单
	 */
	public static String getOrderPointIntro(BizType bizType,String sn){
		switch (bizType) {
		case RECOM_MEMBER:return RECOMMEND_MEMBER;
		case ORDER_COST:return ORDER_COST.replace("?",sn);
		case ORDER_GET:return ORDER_GET.replace("?",sn);
		default:return "";
		}
	}
	
    
}
