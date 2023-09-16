package com.ccp.jn.sync.service;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.entities.JnEntity;
import com.jn.commons.utils.JnTopic;

public class JnSyncAsyncTaskService {

	public CcpMapDecorator apply(String asyncTaskId) {
		CcpMapDecorator asyncTask = JnEntity.async_task.getOneById(asyncTaskId);
		String topicName = asyncTask.getAsString("topic");
		JnTopic topic = JnTopic.valueOf(topicName);
		CcpMapDecorator translatedAsyncTaskResult = topic.getTranslatedAsyncTaskResult(asyncTask);
		return translatedAsyncTaskResult;
	}
	
}
