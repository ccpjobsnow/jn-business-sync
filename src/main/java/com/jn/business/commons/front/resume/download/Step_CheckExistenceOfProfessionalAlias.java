package com.jn.business.commons.front.resume.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.exceptions.db.CcpRecordNotFound;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;
import static  com.jn.business.commons.front.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.*;

class Step_CheckExistenceOfProfessionalAlias extends CcpNextStep {

	
	public Step_CheckExistenceOfProfessionalAlias(String businessName) {
		super(businessName);
	}

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
	
		String fileType = values.getAsString("fileType");
		String alias = values.getAsString("alias");
		String hash = values.getAsString("hash");
		
		CcpMapDecorator first;
		
		try {
			first = JnBusinessEntity.professional_alias.get(alias);
		} catch (CcpRecordNotFound e) {
			return new CcpStepResult(values, 404, this);
		}
		
		
		String professional = first.getAsString("id");

		int status = first.getAsIntegerNumber("status");
		CcpMapDecorator put = values
				.put("professional", professional)
				.put("fileType", fileType)
				.put("status", status)
				.put("hash", hash)
				.removeKey("alias");

		return  new CcpStepResult(put, IF_THIS_RESUME_HAS_A_VALID_ALIAS_THEN, this);
	}

}
