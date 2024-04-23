package com.ccp.jn.sync.service;

import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.commons.entities.JnEntityAsyncTask;

public class SyncServiceJnAsyncTask {

	public CcpJsonRepresentation apply(String asyncTaskId) {
		JnEntityAsyncTask jnEntityAsyncTask = new JnEntityAsyncTask();
		CcpJsonRepresentation asyncTask = jnEntityAsyncTask.getOneById(asyncTaskId);
		return asyncTask;
	}
	
}
