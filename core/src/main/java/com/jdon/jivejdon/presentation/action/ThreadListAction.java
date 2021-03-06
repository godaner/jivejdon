/*
 * Copyright 2003-2005 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.jdon.jivejdon.presentation.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.jdon.controller.WebAppUtil;
import com.jdon.controller.model.PageIterator;
import com.jdon.jivejdon.model.Forum;
import com.jdon.jivejdon.model.ForumThread;
import com.jdon.jivejdon.model.query.ResultSort;
import com.jdon.jivejdon.model.query.specification.ThreadListSpec;
import com.jdon.jivejdon.presentation.action.util.ForumEtagFilterList;
import com.jdon.jivejdon.service.ForumMessageQueryService;
import com.jdon.jivejdon.service.ForumMessageService;
import com.jdon.jivejdon.service.ForumService;
import com.jdon.strutsutil.ModelListForm;
import com.jdon.util.Debug;
import com.jdon.util.UtilValidate;

/**
 * @author <a href="mailto:banq@163.com">banq</a>
 * 
 */
public class ThreadListAction extends ForumEtagFilterList {
	private final static String module = ThreadListAction.class.getName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jdon.strutsutil.ModelListAction#getPageIterator(javax.servlet.http
	 *      .HttpServletRequest, int, int)
	 */
	public PageIterator getPageIterator(HttpServletRequest request, int start, int count) {
		ForumMessageQueryService forumMessageQueryService = (ForumMessageQueryService) WebAppUtil.getService("forumMessageQueryService", request);
		String forumId = request.getParameter("forum");

		ResultSort resultSort = new ResultSort();
		if (request.getParameterMap().containsKey("ASC")) {// DESC ASC
			resultSort.setOrder_ASCENDING();
			request.setAttribute("ASC", "ASC");// for display
		} else {
			resultSort.setOrder_DESCENDING();
		}
		ThreadListSpec threadListSpec = new ThreadListSpec();
		threadListSpec.setResultSort(resultSort);

		if (forumId == null)
			forumId = request.getParameter("forumId");
		if ((forumId == null) || !UtilValidate.isInteger(forumId)) {
			return forumMessageQueryService.getThreads(start, count, threadListSpec);
		} else
			return forumMessageQueryService.getThreads(new Long(forumId), start, count, resultSort);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jdon.strutsutil.ModelListAction#findModelByKey(javax.servlet.http
	 *      .HttpServletRequest, java.lang.Object)
	 */
	public Object findModelIFByKey(HttpServletRequest request, Object key) {
		ForumMessageService forumMessageService = (ForumMessageService) WebAppUtil.getService("forumMessageService", request);
		Debug.logVerbose(" key calss type = " + key.getClass().getName(), module);
		ForumThread thread = null;
		try {
			thread = forumMessageService.getThread((Long) key);
		} catch (Exception e) {
			Debug.logError("getThread error:" + e, module);
		}
		return thread;
	}

	public void customizeListForm(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, ModelListForm modelListForm)
			throws Exception {
		ForumService forumService = (ForumService) WebAppUtil.getService("forumService", request);
		String forumId = request.getParameter("forum");
		if (forumId == null)
			forumId = request.getParameter("forumId");

		Forum forum = null;
		if ((forumId == null) || !UtilValidate.isInteger(forumId)) {
			forum = new Forum();
			forum.setName("主题总表");
		} else {
			forum = forumService.getForum(new Long(forumId));
		}
		if (forum == null)
			throw new Exception("forum is null forumid=" + forumId);
		modelListForm.setOneModel(forum);
	}

}
