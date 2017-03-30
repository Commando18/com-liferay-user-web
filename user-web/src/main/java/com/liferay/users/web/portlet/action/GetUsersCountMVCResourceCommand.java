package com.liferay.users.web.portlet.action;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.users.web.constants.UserWebPortletKeys;

import java.io.PrintWriter;

import java.util.LinkedHashMap;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author alessio
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + UserWebPortletKeys.USER_WEB,
		"mvc.command.name=/bundle_web/get_users_count"
	},
	service = MVCResourceCommand.class
)
public class GetUsersCountMVCResourceCommand implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			PrintWriter printWriter = resourceResponse.getWriter();

			printWriter.write(getText(resourceRequest, resourceResponse));

			return true;
		}
		catch (Exception e) {
			throw new PortletException(e);
		}
	}

	protected String getText(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		HttpServletRequest request = _portal.getOriginalServletRequest(
			_portal.getHttpServletRequest(resourceRequest));

		long companyId = _portal.getCompanyId(request);

		String className = ParamUtil.getString(request, "className");
		long[] ids = StringUtil.split(ParamUtil.getString(request, "ids"), 0L);
		int status = ParamUtil.getInteger(request, "status");

		int count = 0;

		if (className.equals(User.class.getName())) {
			count = getUsersCount(companyId, ids, status);
		}

		return String.valueOf(count);
	}

	protected int getUsersCount(long companyId, long[] userGroupIds, int status)
		throws Exception {

		int count = 0;

		for (long userGroupId : userGroupIds) {
			LinkedHashMap<String, Object> params = new LinkedHashMap<>();

			params.put("usersUserGroups", userGroupId);

			count += _userLocalService.searchCount(
				companyId, null, status, params);
		}

		return count;
	}

	@Reference(unbind = "-")
	protected void setUserLocalService(UserLocalService userLocalService) {
		_userLocalService = userLocalService;
	}

	@Reference
	private Portal _portal;

	private UserLocalService _userLocalService;

}