package com.jn.business.commons.resume.download;

import static com.jn.business.commons.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.IF_IS_A_CONSULTING_RECRUITER_THEN;
import static com.jn.business.commons.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.IF_IS_A_FREELANCER_RECRUITER_THEN;
import static com.jn.business.commons.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.IF_IS_NOT_THE_OWNER_WHO_IS_DOWNLOADING_THIS_RESUME_THEN;
import static com.jn.business.commons.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.IF_IT_IS_AN_ACTIVE_RESUME_THEN;
import static com.jn.business.commons.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.IF_THERE_IS_NOT_RESTRINCTION_TO_THIS_RECRUITER_TO_VIEW_THIS_RESUME_THEN;
import static com.jn.business.commons.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.IF_THE_OWNER_IS_WHO_IS_DOWNLOADING_THIS_RESUME_THEN;
import static com.jn.business.commons.resume.download.StepList_DownloadThisResumeToAllowedRecruiter.IF_THIS_RESUME_HAS_A_VALID_ALIAS_THEN;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpBusiness;
import com.ccp.dependency.injection.CcpEspecification;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.process.CcpStepResult;
@CcpBusiness
public class Business_DownloadThisResumeToAllowedRecruiter {

	@CcpEspecification
	private CcpFileBucket fileBucket;
	
	
	public CcpMapDecorator downloadTheResumeToAllowedRecruiter(String recruiter, String professionalAlias, String viewMode) {
		String businessName = Business_DownloadThisResumeToAllowedRecruiter.class.getName();
		
		Step_CheckActiveResume checkActiveResume = new Step_CheckActiveResume(businessName);
		Step_CheckWhoBelongsThisResume checkWhoBelongsThisResume = new Step_CheckWhoBelongsThisResume(businessName);
		Step_GetResumeFromBucket getThisResumeFromBucket = new Step_GetResumeFromBucket(businessName, this.fileBucket);
		Step_CheckExistenceOfThisRecruiter checkExistenceOfThisRecruiter = new Step_CheckExistenceOfThisRecruiter(businessName);
		Step_CheckExistenceOfProfessionalAlias checkExistenceOfProfessionalAlias = new Step_CheckExistenceOfProfessionalAlias(businessName);
		Step_CheckRecruiterPermissionToViewThisResume checkRecruiterPermissionToViewThisResume = 
				new Step_CheckRecruiterPermissionToViewThisResume(businessName);
		
		checkExistenceOfProfessionalAlias.addStep(IF_THIS_RESUME_HAS_A_VALID_ALIAS_THEN, checkWhoBelongsThisResume);
		checkWhoBelongsThisResume.addStep(IF_THE_OWNER_IS_WHO_IS_DOWNLOADING_THIS_RESUME_THEN, getThisResumeFromBucket);
		checkWhoBelongsThisResume.addStep(IF_IS_NOT_THE_OWNER_WHO_IS_DOWNLOADING_THIS_RESUME_THEN, checkActiveResume);
		checkActiveResume.addStep(IF_IT_IS_AN_ACTIVE_RESUME_THEN, checkExistenceOfThisRecruiter);
		checkExistenceOfThisRecruiter.addStep(IF_IS_A_FREELANCER_RECRUITER_THEN, getThisResumeFromBucket);
		checkExistenceOfThisRecruiter.addStep(IF_IS_A_CONSULTING_RECRUITER_THEN, checkRecruiterPermissionToViewThisResume);
		checkRecruiterPermissionToViewThisResume.addStep(IF_THERE_IS_NOT_RESTRINCTION_TO_THIS_RECRUITER_TO_VIEW_THIS_RESUME_THEN,
				getThisResumeFromBucket);
		
		CcpMapDecorator dataToProcess = 
				new CcpMapDecorator()
				.put("viewMode", viewMode)
				.put("recruiter", recruiter)
				.put("professionalAlias", professionalAlias)
				;
		
		CcpStepResult response = checkExistenceOfProfessionalAlias.moveToTheNextStep(dataToProcess);
		
		return response.data;
	}
	
	
}
