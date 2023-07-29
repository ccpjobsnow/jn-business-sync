package com.ccp.jn.sync.common.business;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class ResetEntity extends CcpNextStep{

	
	private final JnEntity[] entities;
	
	private final String fieldName;
	
	private final Integer limit;

	public ResetEntity(String fieldName, Integer limit, JnEntity... entities) {
		this.fieldName = fieldName;
		this.entities = entities;
		this.limit = limit;
	}

	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		CcpMapDecorator entities = values.getInternalMap("_entities");
		List<String> entidades = Arrays.asList(this.entities).stream().map(x -> x.name()).collect(Collectors.toList());
		CcpMapDecorator packageToRemoveTries = values.put("fieldName", this.fieldName).put("limit", this.limit).put("entities", entities);
		JnTopic.removeTries.send(packageToRemoveTries);
		
		CcpMapDecorator put = new CcpMapDecorator();
		for (String entityName : entidades) {
			CcpMapDecorator removeKey = entities.removeKey(entityName);
			put = values.put("_entities", removeKey);
		}
		return new CcpStepResult(put, 200, this);
	}

}
