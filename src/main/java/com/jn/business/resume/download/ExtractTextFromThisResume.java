package com.jn.business.resume.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.text.extractor.CcpTextExtractor;
import com.ccp.process.CcpNextStepFactory;
import com.ccp.process.CcpStepResult;

class ExtractTextFromThisResume extends CcpNextStepFactory {

	private final CcpTextExtractor textExtractor;
	
	
	
	public ExtractTextFromThisResume(CcpTextExtractor textExtractor, String businessName) {
		super(businessName);
		this.textExtractor = textExtractor;
	}



	@Override
	public CcpStepResult executeDecisionTree(CcpMapDecorator values) {
		CcpMapDecorator resume = values.getInternalMap("resume");
		String base64 = resume.getAsString("base64");
		String resumeText = this.textExtractor.extractText(base64);
		CcpMapDecorator data = values.put("resumeText", resumeText);
		return  new CcpStepResult(data, 200, this);
	}

}
