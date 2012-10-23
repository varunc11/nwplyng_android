/*
 * IHttpListener interface
 * 
 * Author : Thomas Antony
 * Company : MindHelix Technosol Pvt. Ltd
 * Last Updated : 01-Apr-2012
 * 
 */
package com.mindhelix.helpers.http;

public interface IHttpListener<T> {
	void RequestComplete(HttpResult<T> result);
}
