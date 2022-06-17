package com.jn.business.resume.download;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpBusiness;
import com.ccp.dependency.injection.CcpEspecification;
import com.ccp.especifications.text.extractor.CcpTextExtractor;
import com.ccp.exceptions.process.CcpStepResult;

import static com.jn.business.resume.download.Steps.*;
@CcpBusiness
public class DownloadTheResumeToAllowedRecruiter {

	@CcpEspecification
	private CcpTextExtractor textExtractor;
	
	public CcpMapDecorator downloadTheResumeToAllowedRecruiter(String recruiter, String professionalAlias, String viewMode) {
		String businessName = DownloadTheResumeToAllowedRecruiter.class.getName();
		
		
		DownloadTheResume downloadTheResume = new DownloadTheResume(businessName);
		VerifyResumeStatus verifyResumeStatus = new VerifyResumeStatus(businessName);
		VerifyRecruiterShip verifyRecruiterShip = new VerifyRecruiterShip(businessName);
		VerifyWhoBelongsThisResume verifyWhoBelongsResume = new VerifyWhoBelongsThisResume(businessName);
		VerifyProfessionalAlias verifyProfessionalAlias = new VerifyProfessionalAlias(businessName);
		ExtractTextFromResume extractTextFromResume = new ExtractTextFromResume(this.textExtractor, businessName);
		VerifyPermissionToViewResume verifyPermissionToViewResume = new VerifyPermissionToViewResume(businessName);
		
		verifyProfessionalAlias.addStep(THIS_RESUME_HAS_A_VALID_ALIAS, verifyWhoBelongsResume);
		verifyWhoBelongsResume.addStep(THE_OWNER_IS_WHO_IS_DOWNLOADING_THIS_RESUME, downloadTheResume);
		verifyWhoBelongsResume.addStep(IS_NOT_THE_OWNER_WHO_IS_DOWNLOADING_THIS_RESUME, verifyResumeStatus);
		verifyResumeStatus.addStep(IT_IS_AN_ACTIVE_RESUME, verifyRecruiterShip);
		verifyRecruiterShip.addStep(IS_A_FREELANCER_RECRUITER, downloadTheResume);
		verifyRecruiterShip.addStep(IS_A_CONSULTING_RECRUITER, verifyPermissionToViewResume);
		verifyPermissionToViewResume.addStep(THERE_IS_NOT_RESTRINCTION_TO_THIS_RECRUITER_TO_VIEW_THIS_RESUME, downloadTheResume);
		verifyPermissionToViewResume.addStep(IT_IS_A_TEXT_MODE_TO_VIEW_THIS_RESUME, extractTextFromResume);
		
		CcpMapDecorator dataToProcess = 
				new CcpMapDecorator()
				.put("viewMode", viewMode)
				.put("recruiter", recruiter)
				.put("professionalAlias", professionalAlias)
				;
		
		CcpStepResult response = verifyProfessionalAlias.executeAllSteps(dataToProcess);
		
		return response.data;
	}
	
	
}
