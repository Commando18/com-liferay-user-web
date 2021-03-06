package com.liferay.users.web.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.users.web.constants.UserWebPortletKeys;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author alessio
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + UserWebPortletKeys.USER_WEB,
		"mvc.command.name=/", "mvc.command.name=/bundle_web/view"
	},
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		return "/view.jsp";
	}

}