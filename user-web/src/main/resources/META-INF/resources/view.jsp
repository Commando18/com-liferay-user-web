<%@ include file="/init.jsp" %>

<%
SearchContainer usersSearchContainer = userDisplayContext.getSearchContainer();
int status = userDisplayContext.getStatus();
String navigation = userDisplayContext.getNavigation();

PortletURL portletURL = userDisplayContext.getPortletURL();

String displayStyle = ParamUtil.getString(request, "displayStyle");

String searchContainerId = ParamUtil.getString(request, "SearchContainerId", "users");

if (Validator.isNull(displayStyle)) {
	displayStyle = portalPreferences.getValue(UserWebPortletKeys.USER_WEB, "display-style", "list");
}
else {
	portalPreferences.setValue(UserWebPortletKeys.USER_WEB, "display-style", displayStyle);

	request.setAttribute(WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);
}

String toolbarItem = ParamUtil.getString(request, "toolbarItem", "view-all-users");

portletURL.setParameter("toolbarItem", toolbarItem);
portletURL.setParameter("searchContainerId", searchContainerId);
portletURL.setParameter("navigation", navigation);
portletURL.setParameter("displayStyle", displayStyle);
portletURL.setParameter("status", String.valueOf(status));

request.setAttribute("view.jsp-portletURL", portletURL);

boolean showDeleteButton = (userDisplayContext.getStatus() != WorkflowConstants.STATUS_ANY) && (userDisplayContext.isActive() || (!userDisplayContext.isActive(
		) && PropsValues.USERS_DELETE));
boolean showRestoreButton = (userDisplayContext.getStatus() != WorkflowConstants.STATUS_ANY) && !userDisplayContext.isActive();
%>

<%@ include file="/toolbar.jspf" %>

<liferay-frontend:management-bar
	includeCheckBox="<%= true %>"
	searchContainerId="<%= searchContainerId %>"
>
	<liferay-frontend:management-bar-buttons>
		<liferay-frontend:management-bar-filters>
			<liferay-frontend:management-bar-navigation>
				<portlet:renderURL var="viewActiveUsersURL">
					<portlet:param name="searchContainerId" value="<%= searchContainerId %>" />
					<portlet:param name="navigation" value="active" />
					<portlet:param name="displayStyle" value="<%= displayStyle %>" />
					<portlet:param name="status" value="<%= String.valueOf(WorkflowConstants.STATUS_APPROVED) %>" />
				</portlet:renderURL>

				<liferay-frontend:management-bar-filter-item active="<%= userDisplayContext.isActive() %>" label="active" url="<%= viewActiveUsersURL.toString() %>" />

				<portlet:renderURL var="viewInactiveUsersURL">
					<portlet:param name="searchContainerId" value="<%= searchContainerId %>" />
					<portlet:param name="navigation" value="inactive" />
					<portlet:param name="displayStyle" value="<%= displayStyle %>" />
					<portlet:param name="status" value="<%= String.valueOf(WorkflowConstants.STATUS_INACTIVE) %>" />
				</portlet:renderURL>

				<liferay-frontend:management-bar-filter-item active="<%= userDisplayContext.isInactive() %>" label="inactive" url="<%= viewInactiveUsersURL.toString() %>" />
			</liferay-frontend:management-bar-navigation>

			<liferay-frontend:management-bar-sort
				orderByCol="<%= usersSearchContainer.getOrderByCol() %>"
				orderByType="<%= usersSearchContainer.getOrderByType() %>"
				orderColumns='<%= new String[] {"first-name", "last-name", "screen-name"} %>'
				portletURL="<%= PortletURLUtil.clone(portletURL, renderResponse) %>"
			/>
		</liferay-frontend:management-bar-filters>

		<liferay-frontend:management-bar-display-buttons
			displayViews='<%= new String[] {"icon", "descriptive", "list"} %>'
			portletURL="<%= PortletURLUtil.clone(portletURL, liferayPortletResponse) %>"
			selectedDisplayStyle="<%= displayStyle %>"
		/>
	</liferay-frontend:management-bar-buttons>

	<liferay-frontend:management-bar-action-buttons>
		<c:if test="<%= showRestoreButton %>">

			<%
			String taglibOnClick = "javascript:" + renderResponse.getNamespace() + "deleteUsers('" + Constants.RESTORE + "');";
			%>

			<liferay-frontend:management-bar-button href="<%= taglibOnClick %>" iconCssClass="icon-undo" id="restoreUsers" label="restore" />
		</c:if>

		<c:if test="<%= showDeleteButton %>">

			<%
			String taglibOnClick = "javascript:" + renderResponse.getNamespace() + "deleteUsers('" + (userDisplayContext.isActive() ? Constants.DEACTIVATE : Constants.DELETE) + "');";
			%>

			<liferay-frontend:management-bar-button href="<%= taglibOnClick %>" icon="trash" id="deleteUsers" label="<%= userDisplayContext.isActive() ? Constants.DEACTIVATE : Constants.DELETE %>" />
		</c:if>
	</liferay-frontend:management-bar-action-buttons>
</liferay-frontend:management-bar>

<h1><%= searchContainerId %></h1>

<aui:form action="<%= portletURL.toString() %>" cssClass="container-fluid-1280" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "search();" %>'>
	<liferay-portlet:renderURLParams varImpl="portletURL" />
	<aui:input name="<%= Constants.CMD %>" type="hidden" />
	<aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />

	<liferay-ui:search-container
		cssClass="users-search-container"
		emptyResultsMessage="no-users-were-found"
		id="<%= searchContainerId %>"
		searchContainer="<%= usersSearchContainer %>"
	>
		<aui:input name="status" type="hidden" value="<%= status %>" />

		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.User"
			modelVar="user2"
		>
			<%@ include file="/user/search_columns.jspf" %>
		</liferay-ui:search-container-row>

		<%
		List<User> results = searchContainer.getResults();

		showDeleteButton = !results.isEmpty() && showDeleteButton;
		showRestoreButton = !results.isEmpty() && showRestoreButton;

		if (!showDeleteButton && !showRestoreButton) {
			usersSearchContainer.setRowChecker(null);
		}
		%>

		<liferay-ui:search-iterator displayStyle="<%= displayStyle %>" markupView="lexicon" />
	</liferay-ui:search-container>
</aui:form>