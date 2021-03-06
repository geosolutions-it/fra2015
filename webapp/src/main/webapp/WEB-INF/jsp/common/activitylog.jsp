<%@ include file="../common/includes/taglibs.jsp"%>
<div>
  <div class="container">
    <div class="row">
      <div class="span12">
        <form:form id="filterActivityLogForm" commandName="logFilter"  method="post" action="filter">
        <table id="userTable" class="table table-bordered table-hover table-condensed table-striped">
          <thead>
            <tr>
              <th><spring:message code="timestamp"></spring:message></th>
              <th><spring:message code="username"></spring:message></th>
              <th><spring:message code="country"></spring:message></th>
              <th><spring:message code="question"></spring:message></th>
              <th><spring:message code="content"></spring:message></th>
            </tr>
             <tr>
              <th>
	              <div id="datetimepickerFrom" class="input-prepend input-append date">
	                <span class="add-on" style="width:40px"><spring:message code="from"></spring:message></span>
							    <form:input path="fromDate" data-format="dd/MM/yyyy hh:mm:ss" type="text" class="span2 input-small"/>
							    <span class="add-on picker">
							        <i data-time-icon="icon-time" data-date-icon="icon-calendar"> </i>
							    </span>
							    <span class="add-on">
							    <i id="filter_from_clear_btn" class="icon-remove-sign"> </i>
							    </span>
							  </div>
							  <div id="datetimepickerTo" class="input-prepend input-append date">
							    <span class="add-on" style="width:40px"><spring:message code="to"></spring:message></span>
                  <form:input path="toDate" data-format="dd/MM/yyyy hh:mm:ss" type="text" class="span2 input-small"/>
                    <span class="add-on picker">
                      <i data-time-icon="icon-time" data-date-icon="icon-calendar"> </i>
                  </span>
                  <span class="add-on">
                  <i id="filter_to_clear_btn" class="icon-remove-sign"> </i>
                  </span>
                </div>    						              
              </th>
              <th>
                <div class="input-append">
                <form:input path="username" class="span2 input-small ui-autocomplete-input" type="text" autocomplete="off" />
                  <span class="add-on">
                  <i id="filter_username_clear_btn" class="icon-remove-sign"> </i>
                  </span>
                </div>
              </th>
              <th>
                <div class="input-append">
                <input id="filter_countries" type="text" class="span2 input-small ui-autocomplete-input" autocomplete="off"/>     
                <form:input path="country" name="country" type="hidden" id="country"/>           
                <span class="add-on">
                  <i id="filter_country_clear_btn" class="icon-remove-sign"> </i>
                </span>
                </div>
              </th>
              <th>
                <div class="input-append">
				  <form:select path="question_id" id="questionId" name="questionId" class="span1 input-small ui-autocomplete-input" autocomplete="off">
                  	<form:option value="" label=""/>
                  	<c:if test="${showStatusChange}">
                  		<form:option value="STATUS_CHANGED" label="StateChanges"/>
                  	</c:if>
                  	<form:options items="${questions}" />
                  </form:select>                <span class="add-on">
                  <i id="filter_questionId_clear_btn" class="icon-remove-sign"> </i>
                </span>
                </div>
              </th>
              <th>
                 <div class="input-append">
                  <form:input path="content" class="span2 input-small ui-autocomplete-input" type="text" autocomplete="off" />
                <span class="add-on">
                  <i id="filter_content_clear_btn" class="icon-remove-sign"> </i>
                </span>
                </div>
              </th>
            </tr>
          </thead>
          <thead>
            <tr>
              <th colspan="5" style="text-align: right;">
                <button class="btn" type="button" id="activityLogFilterBtn"><i class="icon-filter"></i><spring:message code="filter"></spring:message></button>
              </th>
            </tr>
          </thead>
          <tbody>
             <c:forEach items='${activityLogList}' var='activityLogEntry' varStatus='rowItem'>
              <tr class="rowItem">                
                <td class="span2"><div>${activityLogEntry.date}</div></td>
                <td class="span2"><div>${activityLogEntry.username}</div></td>
                <td class="span1"><div>${activityLogEntry.country}</div></td>
                <td class="span1"><div>
                	<c:if test="${activityLogEntry.question_id=='STATUS_CHANGED'}">
							<spring:message code="statuschange" text="Status Change"></spring:message>
						</c:if>
						<c:if test="${activityLogEntry.question_id!='STATUS_CHANGED'}">
							${activityLogEntry.question_id}
						</c:if></div></td>
                <td class="span2"><div>
                		<c:if test="${activityLogEntry.question_id=='STATUS_CHANGED'}">
							
							<c:forEach items="${fra:formatStatusChange(activityLogEntry.content)}" var="attr">
								<c:if test="${ attr.value!='null' &&(attr.key=='message' || attr.key=='status')}">
								<dt>
									<spring:message code="${attr.key}" text="${attr.key}"></spring:message>
								</dt>
								<dd>
								
									<c:choose>
    									
   										<c:when test="${ attr.key =='status'}">
   										<spring:message code="survey.status.${attr.value}" text="${attr.value}"></spring:message>
   										</c:when>
   										<c:when test="${ attr.key =='message'}">
   											${attr.value}
   										</c:when>
   									 <c:otherwise>
										
									 </c:otherwise>
									 </c:choose>
								</dd>
								</c:if>
							</c:forEach>
						</c:if>
						<c:if test="${activityLogEntry.question_id!='STATUS_CHANGED'}">
							${activityLogEntry.content}
						</c:if></div></td>
              </tr>
            </c:forEach>  
            <c:forEach var="i" begin="1" end="${10 - (fn:length(activityLogList))}" step="1">
              <tr class="rowItem">                
                <td class="span2"><div>&#160;</div></td>
                <td class="span2"><div>&#160;</div></td>
                <td class="span1"><div>&#160;</div></td>
                <td class="span1"><div>&#160;</div></td>
                <td class="span2"><div>&#160;</div></td>
            </c:forEach>              
          </tbody>
        </table>
        </form:form>
        <div class="pagination pagination-centered">
          <ul>

            <c:if test="${not empty pagination.firstPage}">
            <li><a href="${pagination.firstPage}"><spring:message code="first"></spring:message></a></li>
            </c:if>
            <c:if test="${empty pagination.firstPage}">
              <li class="disabled"><a href="#"><spring:message code="first"></spring:message></a></li>
            </c:if>
            
            <c:if test="${not empty pagination.prev1}">
            <li><a href="${pagination.prev1}"><spring:message code="prev"></spring:message></a></li>
            </c:if>
            <c:if test="${empty pagination.prev1}">
              <li class="disabled"><a href="#"><spring:message code="prev"></spring:message></a></li>
            </c:if>
            
            <c:if test="${not empty pagination.prev2}">
            <li><a href="${pagination.prev2}">${pagination.prev2+1}</a></li>
            </c:if>
            <c:if test="${empty pagination.prev2}">
              <li class="disabled"><a href="#"> - </a></li>
            </c:if>
            
            <c:if test="${not empty pagination.prev1}">
            <li><a href="${pagination.prev1}">${pagination.prev1+1}</a></li>
            </c:if>
            <c:if test="${empty pagination.prev1}">
              <li class="disabled"><a href="#"> - </a></li>
            </c:if>
            
            <li class="disabled"><a href="#" style="background-color: #0088CC;color:white;">${pagination.currentPage+1}</a></li>
            
            <c:if test="${not empty pagination.next1}">
              <li><a href="${pagination.next1}">${pagination.next1+1}</a></li>
            </c:if>
            <c:if test="${empty pagination.next1}">
              <li class="disabled"><a href="#"> - </a></li>
            </c:if>
            
            <c:if test="${not empty pagination.next2}">
              <li><a href="${pagination.next2}">${pagination.next2+1}</a></li>
            </c:if>
            <c:if test="${empty pagination.next2}">
              <li class="disabled"><a href="#"> - </a></li>
            </c:if>
            
            <c:if test="${not empty pagination.next1}">
              <li><a href="${pagination.next1}"><spring:message code="next"></spring:message></a></li>
            </c:if>
            <c:if test="${empty pagination.next1}">
              <li class="disabled"><a href="#"><spring:message code="next"></spring:message></a></li>
            </c:if>
            
            <c:if test="${not empty pagination.lastPage}">
              <li><a href="${pagination.lastPage}"><spring:message code="last"></spring:message></a></li>
            </c:if>
            <c:if test="${empty pagination.lastPage}">
              <li class="disabled"><a href="#"><spring:message code="last"></spring:message></a></li>
            </c:if>            

          </ul>
        </div>
      </div>
    </div>
	</div>
</div>