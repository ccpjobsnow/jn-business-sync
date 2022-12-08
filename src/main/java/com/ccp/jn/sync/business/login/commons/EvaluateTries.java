package com.ccp.jn.sync.business.login.commons;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class EvaluateTries extends CcpNextStep {

	private final JnBusinessEntity table;
	
	private final int regularFlow;
	
	private final int excedeedFlow;
	
	
	public EvaluateTries(JnBusinessEntity table, int regularFlow, int exceededFlow) {
		this.excedeedFlow = exceededFlow;
		this.regularFlow = regularFlow;
		this.table = table;
	}



	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		CcpMapDecorator tables = values.getInternalMap("_tables");
		CcpMapDecorator tries = tables.getInternalMap(this.table.name());
		Integer attemps = tries.getAsIntegerNumber("tries");
		if(attemps == null) {
			attemps = 0;
		}
		if(attemps >= 3) {
			return new CcpStepResult(values.put("tries", attemps), this.excedeedFlow, this);
		}
		tries = tries.put("tries", attemps + 1);
		this.table.save(tries);
		
		return new CcpStepResult(values.put("tries", attemps + 1), this.regularFlow, this);
	}

}
