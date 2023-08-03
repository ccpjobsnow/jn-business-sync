package com.ccp.jn.sync.resumes.tasks.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class GetAsyncTaskById {

	public CcpMapDecorator apply(String asyncTaskId) {
		CcpMapDecorator asyncTask = JnEntity.async_task.getOneById(asyncTaskId);
		String topicName = asyncTask.getAsString("topic");
		JnTopic topic = JnTopic.valueOf(topicName);
		CcpMapDecorator translatedAsyncTaskResult = topic.getTranslatedAsyncTaskResult(asyncTask);
		return translatedAsyncTaskResult;
	}
	
}
