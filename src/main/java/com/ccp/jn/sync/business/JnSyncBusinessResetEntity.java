package com.ccp.jn.sync.business;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.utils.JnTopic;

public class JnSyncBusinessResetEntity extends CcpNextStep{

	
	private final CcpEntity[] entities;
	
	private final String fieldName;
	
	private final Integer limit;

	public JnSyncBusinessResetEntity(String fieldName, Integer limit, CcpEntity... entities) {
		this.fieldName = fieldName;
		this.entities = entities;
		this.limit = limit;
	}

	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		CcpMapDecorator entities = values.getInternalMap("_entities");
		List<String> entidades = Arrays.asList(this.entities).stream().map(x -> x.name()).collect(Collectors.toList());
		CcpMapDecorator packageToRemoveTries = values.put("fieldName", this.fieldName).put("limit", this.limit).put("entities", entidades);
		JnTopic.removeTries.send(packageToRemoveTries);
		
		CcpMapDecorator put = new CcpMapDecorator();
		for (String entityName : entidades) {
			CcpMapDecorator removeKey = entities.removeKey(entityName);
			put = values.put("_entities", removeKey);
		}
		return new CcpStepResult(put, 200, this);
	}

}
