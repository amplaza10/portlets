/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.twitter.model;

import com.liferay.portal.kernel.bean.AutoEscape;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.AuditedModel;
import com.liferay.portal.model.BaseModel;
import com.liferay.portal.model.CacheModel;
import com.liferay.portal.service.ServiceContext;

import com.liferay.portlet.expando.model.ExpandoBridge;

import java.io.Serializable;

import java.util.Date;

/**
 * The base model interface for the Feed service. Represents a row in the &quot;Twitter_Feed&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This interface and its corresponding implementation {@link com.liferay.twitter.model.impl.FeedModelImpl} exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link com.liferay.twitter.model.impl.FeedImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see Feed
 * @see com.liferay.twitter.model.impl.FeedImpl
 * @see com.liferay.twitter.model.impl.FeedModelImpl
 * @generated
 */
public interface FeedModel extends AuditedModel, BaseModel<Feed> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. All methods that expect a feed model instance should use the {@link Feed} interface instead.
	 */

	/**
	 * Returns the primary key of this feed.
	 *
	 * @return the primary key of this feed
	 */
	public long getPrimaryKey();

	/**
	 * Sets the primary key of this feed.
	 *
	 * @param primaryKey the primary key of this feed
	 */
	public void setPrimaryKey(long primaryKey);

	/**
	 * Returns the feed ID of this feed.
	 *
	 * @return the feed ID of this feed
	 */
	public long getFeedId();

	/**
	 * Sets the feed ID of this feed.
	 *
	 * @param feedId the feed ID of this feed
	 */
	public void setFeedId(long feedId);

	/**
	 * Returns the company ID of this feed.
	 *
	 * @return the company ID of this feed
	 */
	public long getCompanyId();

	/**
	 * Sets the company ID of this feed.
	 *
	 * @param companyId the company ID of this feed
	 */
	public void setCompanyId(long companyId);

	/**
	 * Returns the user ID of this feed.
	 *
	 * @return the user ID of this feed
	 */
	public long getUserId();

	/**
	 * Sets the user ID of this feed.
	 *
	 * @param userId the user ID of this feed
	 */
	public void setUserId(long userId);

	/**
	 * Returns the user uuid of this feed.
	 *
	 * @return the user uuid of this feed
	 * @throws SystemException if a system exception occurred
	 */
	public String getUserUuid() throws SystemException;

	/**
	 * Sets the user uuid of this feed.
	 *
	 * @param userUuid the user uuid of this feed
	 */
	public void setUserUuid(String userUuid);

	/**
	 * Returns the user name of this feed.
	 *
	 * @return the user name of this feed
	 */
	@AutoEscape
	public String getUserName();

	/**
	 * Sets the user name of this feed.
	 *
	 * @param userName the user name of this feed
	 */
	public void setUserName(String userName);

	/**
	 * Returns the create date of this feed.
	 *
	 * @return the create date of this feed
	 */
	public Date getCreateDate();

	/**
	 * Sets the create date of this feed.
	 *
	 * @param createDate the create date of this feed
	 */
	public void setCreateDate(Date createDate);

	/**
	 * Returns the modified date of this feed.
	 *
	 * @return the modified date of this feed
	 */
	public Date getModifiedDate();

	/**
	 * Sets the modified date of this feed.
	 *
	 * @param modifiedDate the modified date of this feed
	 */
	public void setModifiedDate(Date modifiedDate);

	/**
	 * Returns the twitter user ID of this feed.
	 *
	 * @return the twitter user ID of this feed
	 */
	public long getTwitterUserId();

	/**
	 * Sets the twitter user ID of this feed.
	 *
	 * @param twitterUserId the twitter user ID of this feed
	 */
	public void setTwitterUserId(long twitterUserId);

	/**
	 * Returns the twitter user uuid of this feed.
	 *
	 * @return the twitter user uuid of this feed
	 * @throws SystemException if a system exception occurred
	 */
	public String getTwitterUserUuid() throws SystemException;

	/**
	 * Sets the twitter user uuid of this feed.
	 *
	 * @param twitterUserUuid the twitter user uuid of this feed
	 */
	public void setTwitterUserUuid(String twitterUserUuid);

	/**
	 * Returns the twitter screen name of this feed.
	 *
	 * @return the twitter screen name of this feed
	 */
	@AutoEscape
	public String getTwitterScreenName();

	/**
	 * Sets the twitter screen name of this feed.
	 *
	 * @param twitterScreenName the twitter screen name of this feed
	 */
	public void setTwitterScreenName(String twitterScreenName);

	/**
	 * Returns the last status ID of this feed.
	 *
	 * @return the last status ID of this feed
	 */
	public long getLastStatusId();

	/**
	 * Sets the last status ID of this feed.
	 *
	 * @param lastStatusId the last status ID of this feed
	 */
	public void setLastStatusId(long lastStatusId);

	public boolean isNew();

	public void setNew(boolean n);

	public boolean isCachedModel();

	public void setCachedModel(boolean cachedModel);

	public boolean isEscapedModel();

	public Serializable getPrimaryKeyObj();

	public void setPrimaryKeyObj(Serializable primaryKeyObj);

	public ExpandoBridge getExpandoBridge();

	public void setExpandoBridgeAttributes(ServiceContext serviceContext);

	public Object clone();

	public int compareTo(Feed feed);

	public int hashCode();

	public CacheModel<Feed> toCacheModel();

	public Feed toEscapedModel();

	public String toString();

	public String toXmlString();
}