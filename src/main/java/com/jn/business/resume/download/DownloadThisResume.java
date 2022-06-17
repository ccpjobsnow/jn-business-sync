package com.jn.business.resume.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.exceptions.process.CcpStepResult;
import com.ccp.process.CcpNextStepFactory;
import static com.jn.business.resume.download.Steps.*;

class DownloadThisResume extends CcpNextStepFactory {

	public DownloadThisResume(String businessName) {
		super(businessName);
	}

	@Override
	public CcpStepResult executeDecisionTree(CcpMapDecorator values) {
		CcpMapDecorator resume = this.getResume(values);
		
		CcpMapDecorator newValues = values.put("resume", resume);
		
		String viewMode = values.getAsString("viewMode");
		
		if("TEXT".equals(viewMode)) {
			return new CcpStepResult(newValues, IT_IS_A_TEXT_MODE_TO_VIEW_THIS_RESUME, this);

		}
		return  new CcpStepResult(newValues, 200, this);
	}

	private CcpMapDecorator getResume(CcpMapDecorator values) {
		return values;
	}
}
