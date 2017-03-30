package com.liferay.users.web.display.context;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.users.admin.kernel.util.UsersAdminUtil;
import com.liferay.users.web.constants.UserWebPortletKeys;

import java.util.LinkedHashMap;
import java.util.List;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author alessio
 */
public class UserDisplayContext {

	public UserDisplayContext(
		HttpServletRequest request, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		PortletPreferences portletPreferences) {

		_request = request;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_portletPreferences = portletPreferences;

		_portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
			_request);
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_request, "keywords");

		return _keywords;
	}

	public String getNavigation() {
		if (_navigation != null) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(_request, "navigation", "active");

		return _navigation;
	}

	public String getOrderByCol() {
		if (_orderByCol != null) {
			return _orderByCol;
		}

		_orderByCol = ParamUtil.getString(_request, "orderByCol");

		if (Validator.isNull(_orderByCol)) {
			_orderByCol = _portalPreferences.getValue(
				UserWebPortletKeys.USER_WEB, "order-by-col", "first-name");
		}

		return _orderByCol;
	}

	public String getOrderByType() {
		if (_orderByType != null) {
			return _orderByType;
		}

		_orderByType = ParamUtil.getString(_request, "orderByType");

		if (Validator.isNull(_orderByType)) {
			_orderByType = _portalPreferences.getValue(
				UserWebPortletKeys.USER_WEB, "order-by-type", "asc");
		}

		return _orderByType;
	}

	public PortletURL getPortletURL() throws PortalException {
		PortletURL portletURL = _liferayPortletResponse.createRenderURL();

		String navigation = ParamUtil.getString(_request, "navigation");

		if (Validator.isNotNull(navigation)) {
			portletURL.setParameter(
				"navigation", HtmlUtil.escapeJS(getNavigation()));
		}

		String status = ParamUtil.getString(_request, "status");

		if (Validator.isNotNull(status)) {
			portletURL.setParameter("status", String.valueOf(getStatus()));
		}

		String orderByCol = getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		return portletURL;
	}

	public RowChecker getRowChecker() {
		if (_rowChecker != null) {
			return _rowChecker;
		}

		RowChecker rowChecker = new EmptyOnClickRowChecker(
			_liferayPortletResponse);

		rowChecker.setRowIds("rowIdsUser");

		_rowChecker = rowChecker;

		return _rowChecker;
	}

	public SearchContainer getSearchContainer() throws PortalException {
		if (_userSearchContainer != null) {
			return _userSearchContainer;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_request.getAttribute(
			WebKeys.THEME_DISPLAY);

		SearchContainer userSearchContainer = new SearchContainer(
			_liferayPortletRequest, getPortletURL(), null, null);

		if (!isSearch()) {
			userSearchContainer.setEmptyResultsMessageCssClass(
				"taglib-empty-result-message-header-has-plus-btn");
		}
		else {
			userSearchContainer.setSearch(true);
		}

		OrderByComparator<User> orderByComparator =
			UsersAdminUtil.getUserOrderByComparator(
				getOrderByCol(), getOrderByType());

		userSearchContainer.setOrderByCol(getOrderByCol());
		userSearchContainer.setOrderByComparator(orderByComparator);
		userSearchContainer.setOrderByType(getOrderByType());

		userSearchContainer.setRowChecker(getRowChecker());

		LinkedHashMap<String, Object> userParams = new LinkedHashMap<>();

		int total = UserLocalServiceUtil.searchCount(
			themeDisplay.getCompanyId(), getKeywords(), getStatus(),
			userParams);

		userSearchContainer.setTotal(total);

		List results = UserLocalServiceUtil.search(
			themeDisplay.getCompanyId(), getKeywords(), getStatus(), userParams,
			userSearchContainer.getStart(), userSearchContainer.getEnd(),
			userSearchContainer.getOrderByComparator());

		userSearchContainer.setResults(results);

		_userSearchContainer = userSearchContainer;

		return _userSearchContainer;
	}

	public int getStatus() {
		if (_status != null) {
			return _status;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_request.getAttribute(
			WebKeys.THEME_DISPLAY);

		int defaultStatus = WorkflowConstants.STATUS_APPROVED;

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		_status = ParamUtil.getInteger(_request, "status", defaultStatus);

		return _status;
	}

	public boolean isActive() {
		if (getNavigation().matches("active")) {
			return true;
		}

		return false;
	}

	public boolean isInactive() {
		if (!getNavigation().matches("active")) {
			return true;
		}

		return false;
	}

	public boolean isSearch() {
		if (Validator.isNotNull(getKeywords())) {
			return true;
		}

		return false;
	}

	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _navigation;
	private String _orderByCol;
	private String _orderByType;
	private final PortalPreferences _portalPreferences;
	private final PortletPreferences _portletPreferences;
	private final HttpServletRequest _request;
	private RowChecker _rowChecker;
	private Integer _status;
	private SearchContainer _userSearchContainer;

}
