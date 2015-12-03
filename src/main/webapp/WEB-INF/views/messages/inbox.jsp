<%@ include file="../layout/taglib.jsp" %>
<%@ taglib prefix="tag" uri="/WEB-INF/taglibs/customTaglib.tld"%>

		<div class="jumbotron">
			<h1 class="text-center">Message Inbox</h1>
		</div>
	     <c:choose>
				<c:when test="${isEmpty}">
				<h3 class="text-center">There are no messages received!</h3>
 	      </c:when>
			 <c:otherwise>
			 <table class="table table-striped">	
		<c:forEach items="${messages}" var="message">
		<tr>
			<c:choose>
				<c:when test="${message.isNew=='0'}">
					<td>From: <i>${message.sender.username}</i></td>
					<td>Subject: <i><a href="<c:url value="/${currUser}/messages/${message.message_id}/reply" />">${message.header}</a></i></td>
					<td>Date: <i><fmt:formatDate pattern="yyyy-MM-dd, hh:mm a" value="${message.date}" /></i></td>			
					<td>Status: <i>Read</i></td>
			</c:when>
			 <c:otherwise>		
					<td><b>From: <i>${message.sender.username}</i></b></td>
					<td><b>Subject: <i><a href="<c:url value="/${currUser}/messages/${message.message_id}/reply" />">${message.header}</a></i></b></td>
					<td><b>Date: <i><fmt:formatDate pattern="yyyy-MM-dd, hh:mm a" value="${message.date}" /></i></b></td>
					<td><b>Status: <i>Unread</i></b></td>
						</c:otherwise>		
				</c:choose>
				</tr>
			<br />
		</c:forEach>
		</table>
		</c:otherwise>
		</c:choose>
			<!-- pagination -->		
				<div class="text-center">	
					<tag:paginate max="15" offset="${offset}" count="${count}"
   						uri="../messages/inbox" next="&raquo;" previous="&laquo;" /> 
   				</div>
   			<!-- end of pagination -->	
