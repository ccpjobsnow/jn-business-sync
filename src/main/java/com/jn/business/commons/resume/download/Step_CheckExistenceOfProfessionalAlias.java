package com.jn.business.commons.resume.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.exceptions.db.CcpRecordNotFound;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

class Step_CheckExistenceOfProfessionalAlias  extends CcpNextStep {

	
	public Step_CheckExistenceOfProfessionalAlias(String businessName) {
		super(businessName);
	}

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
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
