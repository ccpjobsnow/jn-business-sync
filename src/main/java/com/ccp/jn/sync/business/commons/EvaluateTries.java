package com.ccp.jn.sync.business.commons;

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
		CcpMapDecorator tries = values.getInternalMap("_tables").getInternalMap(this.table.name());
		Integer tentativas = tries.getAsIntegerNumber("tries");
		if(tentativas == null) {
			tentativas = 0;
		}
		if(tentativas >= 3) {
			return new CcpStepResult(values.put("tries", tentativas), this.excedeedFlow, this);
		}
		tries = tries.put("tries", tentativas + 1);
		this.table.save(tries);
		
		return new CcpStepResult(values.put("tries", tentativas + 1), this.regularFlow, this);
	}

}
