package com.ccp.jn.sync.business.commons;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class TransferData extends CcpNextStep {

	private final JnBusinessEntity origin;
	private final JnBusinessEntity target;
	
	public TransferData(JnBusinessEntity origin, JnBusinessEntity target) {
		this.origin = origin;
		this.target = target;
	}

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		
		CcpMapDecorator tables = values.getInternalMap("_tables");

		boolean doNothing = tables.getInternalMap(this.origin.name()).isEmpty() ;
		
		if(doNothing) {
			return new CcpStepResult(values, 200, this);
		}
		
		CcpMapDecorator remove = this.origin.remove(values);
		this.target.save(remove);
		CcpMapDecorator renameKey = tables.renameKey(this.origin.name(), this.target.name());
		CcpMapDecorator put = values.put("_tables", renameKey);
		return new CcpStepResult(put, 200, this);
	}

}
