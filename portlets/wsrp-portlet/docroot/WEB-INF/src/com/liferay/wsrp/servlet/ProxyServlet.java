/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.wsrp.servlet;

import com.liferay.compat.portal.kernel.servlet.HttpHeaders;
import com.liferay.compat.portal.kernel.util.StringUtil;
import com.liferay.compat.portal.kernel.util.Validator;
import com.liferay.compat.portal.util.PortalUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.wsrp.util.PortletPropsValues;
import com.liferay.wsrp.util.WSRPURLUtil;
import com.liferay.wsrp.util.WebKeys;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 */
public class ProxyServlet extends HttpServlet {

	@Override
	public void service(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		try {
			URL url = getAllowedURL(request);

			if (url != null) {
				proxyURL(request, response, url);
			}
		}
		catch (Exception e) {
			_log.error(e, e);

			response.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	protected URL getAllowedURL(HttpServletRequest request) throws Exception {
		long companyId = PortalUtil.getCompanyId(request);
		String urlString = ParamUtil.getString(request, "url");

		String expectedWSRPAuth = WSRPURLUtil.encodeWSRPAuth(
			companyId, urlString);

		String actualWSRPAuth = ParamUtil.getString(request, WebKeys.WSRP_AUTH);

		if (!expectedWSRPAuth.equals(actualWSRPAuth)) {
			return null;
		}

		URL url = new URL(urlString);

		String protocol = url.getProtocol();

		if (!protocol.equals(Http.HTTP) && !protocol.equals(Http.HTTPS)) {
			return null;
		}

		String[] allowedIps = PortletPropsValues.PROXY_URL_IPS_ALLOWED;

		if (allowedIps.length == 0) {
			return url;
		}

		String domain = url.getHost();

		int pos = domain.indexOf(CharPool.COLON);

		if (pos != -1) {
			domain = domain.substring(0, pos);
		}

		InetAddress inetAddress = InetAddress.getByName(domain);

		String hostAddress = inetAddress.getHostAddress();

		String serverIp = PortalUtil.getComputerAddress();

		boolean serverIpIsHostAddress = serverIp.equals(hostAddress);

		for (String ip : allowedIps) {
			if ((serverIpIsHostAddress && ip.equals("SERVER_IP")) ||
				ip.equals(hostAddress)) {

				return url;
			}
		}

		return null;
	}

	protected void proxyURL(
			HttpServletRequest request, HttpServletResponse response, URL url)
		throws Exception {

		URLConnection urlConnection = url.openConnection();

		urlConnection.setIfModifiedSince(
			request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE));

		HttpSession session = request.getSession();

		String cookie = (String)session.getAttribute(WebKeys.COOKIE);

		if (Validator.isNotNull(cookie)) {
			urlConnection.setRequestProperty(HttpHeaders.COOKIE, cookie);
		}

		boolean useCaches = true;

		Enumeration<String> enumeration = request.getHeaderNames();

		while (enumeration.hasMoreElements()) {
			String headerName = enumeration.nextElement();

			if (StringUtil.equalsIgnoreCase(headerName, HttpHeaders.COOKIE) ||
				StringUtil.equalsIgnoreCase(
					headerName, HttpHeaders.IF_MODIFIED_SINCE)) {

				continue;
			}

			String headerValue = request.getHeader(headerName);

			if (Validator.isNotNull(headerValue)) {
				if (StringUtil.equalsIgnoreCase(
						headerName, HttpHeaders.CACHE_CONTROL) &&
					headerValue.contains(
						HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE)) {

					useCaches = false;
				}

				urlConnection.setRequestProperty(headerName, headerValue);
			}
		}

		urlConnection.setUseCaches(useCaches);

		urlConnection.connect();

		response.setContentLength(urlConnection.getContentLength());
		response.setContentType(urlConnection.getContentType());

		Map<String, List<String>> headers = urlConnection.getHeaderFields();

		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			String headerName = entry.getKey();

			if (Validator.isNotNull(headerName) &&
				!response.containsHeader(headerName)) {

				response.setHeader(
					headerName, urlConnection.getHeaderField(headerName));
			}
		}

		if (urlConnection instanceof HttpURLConnection) {
			HttpURLConnection httpURLConnection =
				(HttpURLConnection)urlConnection;

			response.setStatus(httpURLConnection.getResponseCode());
		}

		ServletResponseUtil.write(response, urlConnection.getInputStream());
	}

	private static Log _log = LogFactoryUtil.getLog(ProxyServlet.class);

}