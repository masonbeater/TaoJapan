<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("shop.member.couponCode.list")}[#if systemShowPowered] - Powered By SHOP++[/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/css/member.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/js/common.js"></script>
<script type="text/javascript">
$().ready(function() {
	
	[@flash_message /]

});
</script>
</head>
<body>
	[#assign current = "recomMemberList" /]
	[#include "/shop/include/header.ftl" /]
	<div class="container member">
		[#include "/shop/member/include/navigation.ftl" /]
		<div class="span18 last">
			<div class="list">
				<div class="title">${message("shop.member.point.recomMember")}</div>
				消费积分总计：${total}
				<table class="list">
					<tr>
						<th>
							${message("shop.member.point.member")}
						</th>
						<th>
							${message("shop.member.point.regDate")}
						</th>
						<th>
							${message("shop.member.point.cost")}
						</th>
					</tr>
					[#list page.content as point]
						<tr[#if !coupon_has_next] class="last"[/#if]>
							<td>
								${point.sourceMember.username}
							</td>
							<td>
								${point.sourceMember.createDate}
							</td>
							<td>
								${point.sourceMember.payPoint}
							</td>
						</tr>
					[/#list]
				</table>
				[#if !page.content?has_content]
					<p>${message("shop.member.noResult")}</p>
				[/#if]
			</div>
			[@pagination pageNumber = page.pageNumber totalPages = page.totalPages pattern = "?pageNumber={pageNumber}"]
				[#include "/shop/include/pagination.ftl"]
			[/@pagination]
		</div>
	</div>
	[#include "/shop/include/footer.ftl" /]
</body>
</html>