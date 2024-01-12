package com.ccp.jn.sync.business;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.exceptions.process.CcpAsyncProcess;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.entities.JnEntityAsyncTask;
import com.jn.commons.utils.JnTopics;

public class JnSyncBusinessResetEntity extends CcpNextStep{

	
	private final CcpEntity[] entities;
	
	private final String fieldName;
	
	private final Integer limit;

	public JnSyncBusinessResetEntity(String fieldName, Integer limit, CcpEntity... entities) {
		this.fieldName = fieldName;
		this.entities = entities;
		this.limit = limit;
	}

	public CcpStepResult executeThisStep(CcpJsonRepresentation values) {
		CcpJsonRepresentation entities = values.getInnerJson("_entities");
		List<String> entidades = Arrays.asList(this.entities).stream().map(x -> x.getClass().getSimpleName()).collect(Collectors.toList());
		CcpJsonRepresentation packageToRemoveTries = values.put("fieldName", this.fieldName).put("limit", this.limit).put("entities", entidades);
		new CcpAsyncProcess().send(packageToRemoveTries, JnTopics.removeTries.getTopicName(), new JnEntityAsyncTask());
		
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON;
		for (String entityName : entidades) {
			CcpJsonRepresentation removeKey = entities.removeKey(entityName);
			put = values.put("_entities", removeKey);
		}
		return new CcpStepResult(put, 200, this);
	}

}
