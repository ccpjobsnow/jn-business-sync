package com.ccp.jn.sync.common.business;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class ResetTable extends CcpNextStep{

	private final CcpMensageriaSender mensageriaSender;
	
	private final JnEntity[] entities;
	
	private final String fieldName;
	
	private final Integer limit;

	public ResetTable(CcpMensageriaSender mensageriaSender, String fieldName, Integer limit, JnEntity... entities) {
		this.mensageriaSender = mensageriaSender;
		this.fieldName = fieldName;
		this.entities = entities;
		this.limit = limit;
	}



	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		CcpMapDecorator tables = values.getInternalMap("_tables");
		List<String> entities = Arrays.asList(this.entities).stream().map(x -> x.name()).collect(Collectors.toList());
		CcpMapDecorator packageToRemoveTries = values.put("fieldName", this.fieldName).put("limit", this.limit).put("entities", entities);
		this.mensageriaSender.send(packageToRemoveTries, JnTopic.removeTries);
		
		CcpMapDecorator put = new CcpMapDecorator();
		for (String entityName : entities) {
			CcpMapDecorator removeKey = tables.removeKey(entityName);
			put = values.put("_tables", removeKey);
		}
		return new CcpStepResult(put, 200, this);
	}

}
