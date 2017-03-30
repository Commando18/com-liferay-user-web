<%@ include file="/init.jsp" %>

<%
String redirect = themeDisplay.getURLCurrent();

ResultRow row = (ResultRow)liferayPortletRequest.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

User user2 = (User)row.getObject();

long userId = user2.getUserId();
%>

<liferay-ui:icon-menu direction="left-side" icon="<%= StringPool.BLANK %>" markupView="lexicon" message="<%= StringPool.BLANK %>"
	showWhenSingleIcon="<%= true %>">

	<portlet:renderURL var="editUserURL">
		<portlet:param name="mvcRenderCommandName" value="/edit_user" />
		<portlet:param name="redirect" value="<%= redirect %>" />
		<portlet:param name="p_u_i_d" value="<%= String.valueOf(userId) %>" />
	</portlet:renderURL>

	<liferay-ui:icon
		message="edit"
		url="<%= editUserURL %>"
	/>
</liferay-ui:icon-menu>