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

package com.liferay.wsrp.proxy;

import com.liferay.compat.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.wsrp.axis.WSRPHTTPSender;
import com.liferay.wsrp.client.PasswordCallback;
import com.liferay.wsrp.util.PortletPropsValues;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.net.URL;

import oasis.names.tc.wsrp.v1.wsdl.WSRPServiceLocator;
import oasis.names.tc.wsrp.v2.wsdl.WSRP_v2_ServiceLocator;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.SimpleChain;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.handlers.LogHandler;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.transport.http.HTTPTransport;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.ws.axis.security.WSDoAllSender;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.message.token.UsernameToken;

/**
 * @author Michael Young
 */
public class ServiceHandler implements InvocationHandler {

	public ServiceHandler(
		String forwardCookies, String forwardHeaders, String userToken,
		boolean v2) {

		_engineConfiguration = getEngineConfiguration(
			forwardCookies, forwardHeaders, userToken);

		_v2 = v2;

		if (_v2) {
			_serviceLocator = new WSRP_v2_ServiceLocator(_engineConfiguration);
			_version = "v2";
		}
		else {
			_serviceLocator = new WSRPServiceLocator(_engineConfiguration);
			_version = "v1";
		}

		_serviceLocator.setMaintainSession(true);
	}

	public Object doInvoke(Object proxy, Method method, Object[] args)
		throws Exception {

		String methodName = method.getName();

		if (_v2 && methodName.equals("getWSRP_v2_Markup_Service")) {
			WSRP_v2_Markup_Binding_SOAPStub markupService =
				new WSRP_v2_Markup_Binding_SOAPStub(
					(URL)args[0], _serviceLocator);

			WSRP_v2_ServiceLocator wsrpV2ServiceLocator =
				(WSRP_v2_ServiceLocator)_serviceLocator;

			String markupServiceName =
				wsrpV2ServiceLocator.getWSRP_v2_Markup_ServiceWSDDServiceName();

			markupService.setPortName(markupServiceName);

			return markupService;
		}

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		URL bindingURL = (URL)args[0];

		int x = methodName.indexOf("_v2_") + 4;
		int y = methodName.lastIndexOf("_Service");

		String serviceName = methodName.substring(x, y);

		StringBundler sb = new StringBundler(7);

		sb.append(_OASIS_PACKAGE);
		sb.append(_version);
		sb.append(".bind.WSRP_");
		sb.append(_version);
		sb.append(StringPool.UNDERLINE);
		sb.append(serviceName);
		sb.append("_Binding_SOAPStub");

		Class<?> clazz = contextClassLoader.loadClass(sb.toString());

		args = new Object[] {bindingURL, getService()};

		Object stub = ConstructorUtils.invokeConstructor(clazz, args);

		sb = new StringBundler(5);

		sb.append("getWSRP_");
		sb.append(_version);
		sb.append(StringPool.UNDERLINE);
		sb.append(serviceName);
		sb.append("_ServiceWSDDServiceName");

		Object serviceWSDDServiceName = MethodUtils.invokeMethod(
			_serviceLocator, sb.toString(), null);

		MethodUtils.invokeMethod(stub, "setPortName", serviceWSDDServiceName);

		if (_v2) {
			return stub;
		}

		sb.setIndex(0);

		sb.append(_OASIS_PACKAGE);
		sb.append("v2.intf.WSRP_v2_");
		sb.append(serviceName);
		sb.append("_PortType");

		Class<?> proxyInterface = contextClassLoader.loadClass(sb.toString());

		sb.setIndex(0);

		sb.append(_WSRP_PROXY_PACKAGE);
		sb.append(serviceName);
		sb.append("ServiceHandler");

		clazz = contextClassLoader.loadClass(sb.toString());

		InvocationHandler invocationHandler =
			(InvocationHandler)ConstructorUtils.invokeConstructor(clazz, stub);

		return ProxyUtil.newProxyInstance(
			ServiceHandler.class.getClassLoader(),
			new Class[] {proxyInterface, Stub.class}, invocationHandler);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		try {
			return doInvoke(proxy, method, args);
		}
		catch (InvocationTargetException ite) {
			throw ite.getTargetException();
		}
	}

	protected EngineConfiguration getEngineConfiguration(
		String forwardCookies, String forwardHeaders, String userToken) {

		SimpleChain requestSimpleChain = new SimpleChain();

		Handler logHandler = new LogHandler();

		if (PortletPropsValues.SOAP_DEBUG) {
			requestSimpleChain.addHandler(logHandler);
		}

		if (Validator.isNotNull(userToken)) {
			Handler wsDoAllSenderHandler = new WSDoAllSender();

			wsDoAllSenderHandler.setOption(
				WSHandlerConstants.ACTION, "UsernameToken");
			wsDoAllSenderHandler.setOption(
				WSHandlerConstants.MUST_UNDERSTAND, "false");
			wsDoAllSenderHandler.setOption(
				UsernameToken.PASSWORD_TYPE, WSConstants.PW_NONE);
			wsDoAllSenderHandler.setOption(
				WSHandlerConstants.PW_CALLBACK_CLASS,
				PasswordCallback.class.getName());
			wsDoAllSenderHandler.setOption(WSHandlerConstants.USER, userToken);

			requestSimpleChain.addHandler(wsDoAllSenderHandler);
		}

		SimpleChain responseSimpleChain = new SimpleChain();

		if (PortletPropsValues.SOAP_DEBUG) {
			responseSimpleChain.addHandler(logHandler);
		}

		SimpleProvider simpleProvider = new SimpleProvider();

		HTTPSender httpSender = new WSRPHTTPSender(
			forwardCookies, forwardHeaders);

		simpleProvider.deployTransport(
			HTTPTransport.DEFAULT_TRANSPORT_NAME,
			new SimpleTargetedChain(
				requestSimpleChain, httpSender, responseSimpleChain));

		return simpleProvider;
	}

	protected Service getService() {
		Service service = new Service(_engineConfiguration);

		service.setMaintainSession(true);

		return service;
	}

	private static final String _OASIS_PACKAGE = "oasis.names.tc.wsrp.";

	private static final String _WSRP_PROXY_PACKAGE = "com.liferay.wsrp.proxy.";

	private EngineConfiguration _engineConfiguration;
	private Service _serviceLocator;
	private boolean _v2;
	private String _version;

}