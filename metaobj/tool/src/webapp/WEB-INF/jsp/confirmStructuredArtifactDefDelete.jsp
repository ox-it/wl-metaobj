<%--
  ~ *********************************************************************************
  ~  $URL: https://source.sakaiproject.org/svn/content/trunk/content-api/api/src/java/org/sakaiproject/content/api/ContentCollection.java $
  ~  $Id: ContentCollection.java 8537 2006-05-01 02:13:28Z jimeng@umich.edu $
  ~ **********************************************************************************
  ~
  ~  Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
  ~
  ~  Licensed under the Educational Community License, Version 1.0 (the "License");
  ~  you may not use this file except in compliance with the License.
  ~  You may obtain a copy of the License at
  ~
  ~       http://www.opensource.org/licenses/ecl1.php
  ~
  ~  Unless required by applicable law or agreed to in writing, software
  ~  distributed under the License is distributed on an "AS IS" BASIS,
  ~  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~  See the License for the specific language governing permissions and
  ~  limitations under the License.
  ~
  ~ *********************************************************************************
  ~
  --%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "messages"/>

<div class ="chefPortletContent">

<form method="POST" action="confirmSADDelete.osp">
<osp:form/>

<spring:bind path="bean.id">
<input type="hidden" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
</spring:bind>

   <spring:hasBindErrors name="bean">
<div class="alertMessage">
   <c:forEach items="${errors.allErrors}" var="error">
      <spring:message message="${error}" htmlEscape="true"/>
   </c:forEach>
</div>
   </spring:hasBindErrors>

<fieldset>
<legend><fmt:message key="legend_confirm"/></legend>

<div class="chefPageviewTitle">
<fmt:message key="confirm_delete"/>
</div>

</fieldset>

<p class="act">
<input name="delete" type="submit" value="<fmt:message key="button_yes"/>"/>
<input name="_cancel" type="submit" value="<fmt:message key="button_no"/>"/>
</p>

</form>

</div>