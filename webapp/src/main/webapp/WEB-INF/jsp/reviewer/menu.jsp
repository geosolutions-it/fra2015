<%@ include file="../common/includes/taglibs.jsp"%>
<ul class="nav">
	<li class="${context=='surveylist' ? 'active' : '' }"><a
		href="${pageContext.request.contextPath}/surveylist/0" class="tab" data-i18n="toolbar_surveys"><spring:message
				code="toolbar.surveys" /></a></li>
	<li class="${context=='activitylog' ? 'active' : '' }"><a
		href="${pageContext.request.contextPath}/revieweractivitylog" class="tab"> <spring:message
				code="toolbar.activity_log" /></a></li>
	<li><a class="${context=='export' ? 'active' : '' }" href="${pageContext.request.contextPath}/revexport"
		data-i18n="toolbar_export"><spring:message
				code="toolbar.export" /></a></li>
</ul>