package com.ccp.jn.sync.service;

import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.commons.entities.JnEntityAsyncTask;
import com.jn.commons.utils.JnTopic;

public class JnSyncAsyncTaskService {

	public CcpJsonRepresentation apply(String asyncTaskId) {
		CcpJsonRepresentation asyncTask = new JnEntityAsyncTask().getOneById(asyncTaskId);
		String topicName = asyncTask.getAsString("topic");
		JnTopic topic = JnTopic.valueOf(topicName);
		CcpJsonRepresentation translatedAsyncTaskResult = topic.getTranslatedAsyncTaskResult(asyncTask);
		return translatedAsyncTaskResult;
	}
	
}
