package com.jn.business.resume.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.exceptions.db.CcpRecordNotFound;
import com.ccp.process.CcpNextStepFactory;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

class CheckProfessionalAlias  extends CcpNextStepFactory {

	
	public CheckProfessionalAlias(String businessName) {
		super(businessName);
	}

	@Override
	public CcpStepResult executeDecisionTree(CcpMapDecorator values) {
		String alias = values.getAsString("alias");
		CcpMapDecorator first;
		try {
			first = JnBusinessEntity.professional_alias.get(alias);
		} catch (CcpRecordNotFound e) {
			return new CcpStepResult(values, 404, this);
		}
		String professional = first.getAsString("id");
		int status = first.getAsIntegerNumber("status");
		CcpMapDecorator put = values.put("professional", professional).put("status", status);
		return  new CcpStepResult(put, 200, this);
	}

}
