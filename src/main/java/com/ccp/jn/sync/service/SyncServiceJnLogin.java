package com.ccp.jn.sync.service;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.dao.CcpGetEntityId;
import com.ccp.especifications.db.utils.CcpEntityTransferData;
import com.ccp.jn.sync.business.JnProcessStatus;
import com.ccp.jn.sync.business.SyncBusinessJnCreateLogin;
import com.ccp.jn.sync.business.SyncBusinessJnEvaluatePasswordStrength;
import com.ccp.jn.sync.business.SyncBusinessJnEvaluatePreRegistration;
import com.ccp.jn.sync.business.SyncBusinessJnEvaluateToken;
import com.ccp.jn.sync.business.SyncBusinessJnResetEntity;
import com.ccp.jn.sync.business.SyncBusinessJnSavePassword;
import com.ccp.jn.sync.business.SyncBusinessJnValidatePassword;
import com.ccp.jn.sync.business.utils.JnSyncMensageriaSender;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.ccp.process.CcpSuccessStatus;
import com.jn.commons.business.steps.CommonsBusinessStepEvaluateTries;
import com.jn.commons.business.steps.CommonsBusinessStepSaveEntity;
import com.jn.commons.entities.JnEntityFailedUnlockToken;
import com.jn.commons.entities.JnEntityLockedPassword;
import com.jn.commons.entities.JnEntityLockedToken;
import com.jn.commons.entities.JnEntityLogin;
import com.jn.commons.entities.JnEntityLoginConflict;
import com.jn.commons.entities.JnEntityLoginConflictSolved;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.entities.JnEntityLogout;
import com.jn.commons.entities.JnEntityPassword;
import com.jn.commons.entities.JnEntityPasswordTries;
import com.jn.commons.entities.JnEntityPreRegistration;
import com.jn.commons.entities.JnEntityRequestTokenAgain;
import com.jn.commons.entities.JnEntityRequestTokenAgainAnswered;
import com.jn.commons.entities.JnEntityRequestUnlockToken;
import com.jn.commons.entities.JnEntityRequestUnlockTokenAnswered;
import com.jn.commons.entities.JnEntityTokenTries;
import com.jn.commons.entities.JnEntityUnlockTokenTries;
import com.jn.commons.entities.JnEntityUnlockedPassword;
import com.jn.commons.entities.JnEntityUnlockedToken;
import com.jn.commons.entities.JnEntityUserStats;
import com.jn.commons.entities.JnEntityWeakPassword;
import com.jn.commons.utils.JnAsyncBusiness;

public class SyncServiceJnLogin{
	
	public CcpJsonRepresentation executeLogin(Map<String, Object> json){
		
		 Function<CcpJsonRepresentation, CcpJsonRepresentation> decisionTree = values -> {
			
			CommonsBusinessStepSaveEntity lockPassword = JnEntityLockedPassword.INSTANCE.getSaver(JnProcessStatus.exceededTries);
			CcpNextStep executeLogin = new SyncBusinessJnResetEntity("tries", 3, JnEntityPasswordTries.INSTANCE).addMostExpectedStep(new SyncBusinessJnCreateLogin());

			CcpNextStep evaluateTries = new CommonsBusinessStepEvaluateTries(JnEntityPasswordTries.INSTANCE, JnProcessStatus.wrongPassword, JnProcessStatus.exceededTries)
					.addAlternativeStep(JnProcessStatus.exceededTries, lockPassword);
			
			CcpNextStep validatePassword = new SyncBusinessJnValidatePassword(JnEntityPassword.INSTANCE)
					.addAlternativeStep(JnProcessStatus.wrongPassword, evaluateTries)
					.addMostExpectedStep(executeLogin);
			
			return validatePassword.goToTheNextStep(values).values.put("sessionToken", "{valorDoToken}").put("words", Arrays.asList(CcpConstants.EMPTY_JSON.put("word", "java").put("type", "IT")));
		};

		CcpJsonRepresentation values = new CcpJsonRepresentation(json);

		CcpJsonRepresentation findById =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityFailedUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsPresentInEntity(JnEntityRequestUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.loadThisIdFromEntity(JnEntityUserStats.INSTANCE).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedPassword.INSTANCE).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(JnProcessStatus.loginInUse).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPreRegistration.INSTANCE).returnStatus(JnProcessStatus.preRegistrationIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPassword.INSTANCE).returnStatus(JnProcessStatus.passwordIsMissing).and()
			.ifThisIdIsPresentInEntity(JnEntityPassword.INSTANCE).executeAction(decisionTree).andFinallyReturningThisFields("words", "wordsToken", "sessionToken")
		.endThisProcedureRetrievingTheResultingData()
		;
		
		
		return findById;
	}
	
	public CcpJsonRepresentation createLoginToken (String email, String language){
		
		CcpJsonRepresentation values = CcpConstants.EMPTY_JSON.put("email", email).put("language", language);
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnSyncMensageriaSender.INSTANCE.send(valores, JnAsyncBusiness.sendUserToken);

		CcpJsonRepresentation result = new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityFailedUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsPresentInEntity(JnEntityRequestUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedPassword.INSTANCE).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.INSTANCE).executeAction(action).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(JnProcessStatus.loginInUse).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPassword.INSTANCE).returnStatus(JnProcessStatus.passwordIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPreRegistration.INSTANCE).returnStatus(JnProcessStatus.preRegistrationIsMissing).andFinallyReturningThisFields("asyncTaskId")
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}
	
	public void existsLoginToken (String email){
		
		CcpJsonRepresentation values = new CcpJsonRepresentation(CcpConstants.EMPTY_JSON.put("email", email));

		 new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityFailedUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsPresentInEntity(JnEntityRequestUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedPassword.INSTANCE).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(JnProcessStatus.loginInUse).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPassword.INSTANCE).returnStatus(JnProcessStatus.passwordIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPreRegistration.INSTANCE).returnStatus(JnProcessStatus.preRegistrationIsMissing).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}
	
	public void executeLogout (String email){
		
		CcpJsonRepresentation values = CcpConstants.EMPTY_JSON.put("email", email);
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = x -> new CcpEntityTransferData(JnEntityLogin.INSTANCE, JnEntityLogout.INSTANCE).goToTheNextStep(x).values;
		 new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(JnProcessStatus.unableToExecuteLogout).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}

	public CcpJsonRepresentation requestTokenAgain (String email, String language){
		
		CcpJsonRepresentation values = CcpConstants.EMPTY_JSON.put("email", email).put("language", language);

		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> {
			JnEntityRequestTokenAgain.INSTANCE.create(valores);
			return JnSyncMensageriaSender.INSTANCE.send(valores, JnAsyncBusiness.requestTokenAgain);
		};
	
		CcpJsonRepresentation result =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityFailedUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsPresentInEntity(JnEntityRequestUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsPresentInEntity(JnEntityRequestTokenAgain.INSTANCE).returnStatus(JnProcessStatus.tokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(JnEntityRequestTokenAgainAnswered.INSTANCE).returnStatus(JnProcessStatus.tokenAlreadySent).and()
			.ifThisIdIsNotPresentInEntity(JnEntityRequestTokenAgain.INSTANCE).executeAction(action).andFinallyReturningThisFields("asyncTaskId")
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}
	
	public CcpJsonRepresentation requestUnlockToken (String email, String language){
		
		CcpJsonRepresentation values = CcpConstants.EMPTY_JSON.put("email", email).put("language", language);
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnSyncMensageriaSender.INSTANCE.send(valores, JnAsyncBusiness.requestUnlockToken);
		CcpJsonRepresentation result =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			//FIXME VERIFICAR DUPLICIDADE
			.ifThisIdIsPresentInEntity(JnEntityFailedUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(JnProcessStatus.unableToRequestUnLockToken).and()
			.ifThisIdIsPresentInEntity(JnEntityRequestUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(JnEntityRequestUnlockTokenAnswered.INSTANCE).returnStatus(JnProcessStatus.unlockTokenAlreadyAnswered).and()
			.ifThisIdIsPresentInEntity(JnEntityFailedUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsNotPresentInEntity(JnEntityRequestUnlockToken.INSTANCE).executeAction(action).andFinallyReturningThisFields("asyncTaskId")
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}
	
	public void savePreRegistration (CcpJsonRepresentation values){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnEntityPreRegistration.INSTANCE.createOrUpdate(valores);
		 new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityFailedUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsPresentInEntity(JnEntityRequestUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(JnProcessStatus.loginInUse).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedPassword.INSTANCE).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPreRegistration.INSTANCE).executeAction(action).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPassword.INSTANCE).returnStatus(JnProcessStatus.passwordIsMissing).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}

	public CcpJsonRepresentation saveWeakPassword (CcpJsonRepresentation parameters){
		SyncBusinessJnSavePassword passwordHandler = new SyncBusinessJnSavePassword();
		
		 Function<CcpJsonRepresentation, CcpJsonRepresentation> decisionTree = values -> {
			 
			 SyncBusinessJnCreateLogin saveLogin = new SyncBusinessJnCreateLogin();
			 CcpNextStep createPasswordAndExecuteLogin = passwordHandler.addMostExpectedStep(saveLogin);
			 CcpNextStep createWeakPassword = JnEntityWeakPassword.INSTANCE.getSaver().addMostExpectedStep(createPasswordAndExecuteLogin);
			 CcpNextStep deleteWeakPasswordIfExists = JnEntityWeakPassword.INSTANCE.getDeleter().addMostExpectedStep(createPasswordAndExecuteLogin);
			 CcpNextStep evaluatePasswordStrength = new SyncBusinessJnEvaluatePasswordStrength().addMostExpectedStep(deleteWeakPasswordIfExists).addAlternativeStep(JnProcessStatus.weakPassword, createWeakPassword);
			 CcpStepResult goToTheNextStep = evaluatePasswordStrength.goToTheNextStep(values);
			 
			 return goToTheNextStep.values;
		 };
		 
		CcpJsonRepresentation values = new CcpGetEntityId(parameters)
			.toBeginProcedureAnd()
				.loadThisIdFromEntity(JnEntityUserStats.INSTANCE)
				.and()	
					.ifThisIdIsPresentInEntity(JnEntityFailedUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
					.ifThisIdIsPresentInEntity(JnEntityRequestUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
					.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
					.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
					.ifThisIdIsNotPresentInEntity(JnEntityPreRegistration.INSTANCE).returnStatus(JnProcessStatus.preRegistrationIsMissing).and()
					.executeAction(decisionTree)
				.andFinallyReturningThisFields()
			.endThisProcedureRetrievingTheResultingData();
		 
		return values;
		
	}

	
	public CcpJsonRepresentation unlockToken (CcpJsonRepresentation parameters){
		Function<CcpJsonRepresentation, CcpJsonRepresentation> decisionTree = values ->{
			
			return new SyncBusinessJnValidatePassword(JnEntityRequestUnlockTokenAnswered.INSTANCE, JnProcessStatus.invalidPasswordToUnlockToken, "password")
					.addMostExpectedStep(new SyncBusinessJnResetEntity("tries", 3, JnEntityUnlockTokenTries.INSTANCE)
							.addMostExpectedStep(new CcpEntityTransferData(JnEntityLockedToken.INSTANCE, JnEntityUnlockedToken.INSTANCE)
									)
							)
					.addAlternativeStep(JnProcessStatus.invalidPasswordToUnlockToken, new CommonsBusinessStepEvaluateTries(JnEntityUnlockTokenTries.INSTANCE, JnProcessStatus.invalidPasswordToUnlockToken, JnProcessStatus.unlockTokenHasFailed)
							.addAlternativeStep(JnProcessStatus.unlockTokenHasFailed, JnEntityFailedUnlockToken.INSTANCE.getSaver(JnProcessStatus.unlockTokenHasFailed))
							)
					.goToTheNextStep(values).values;
			
		};
		
		CcpJsonRepresentation result = new CcpGetEntityId(parameters)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(JnProcessStatus.tokenIsNotLocked).and()
			.ifThisIdIsPresentInEntity(JnEntityFailedUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsNotPresentInEntity(JnEntityRequestUnlockTokenAnswered.INSTANCE).returnStatus(JnProcessStatus.waitingForSupport).and()
			.executeAction(decisionTree).andFinallyReturningThisFields()
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}



	
	public CcpJsonRepresentation updatePassword (CcpJsonRepresentation values){
		Function<CcpJsonRepresentation, CcpJsonRepresentation> decisionTree = valores ->{
			SyncBusinessJnSavePassword passwordHandler = new SyncBusinessJnSavePassword();
			CcpNextStep evaluatePreRegistration = new SyncBusinessJnEvaluatePreRegistration(new SyncBusinessJnCreateLogin());
			CcpNextStep savePassword = passwordHandler.addMostExpectedStep(evaluatePreRegistration);
			CcpNextStep evaluatePasswordStrength = new SyncBusinessJnEvaluatePasswordStrength().addMostExpectedStep(savePassword);//TODO todo mundo passar o most expected por construtor
			CcpNextStep unLockPassword = new CcpEntityTransferData(JnEntityLockedPassword.INSTANCE, JnEntityUnlockedPassword.INSTANCE).addMostExpectedStep(evaluatePasswordStrength);
			CcpNextStep solveLoginConflict = new CcpEntityTransferData(JnEntityLoginConflict.INSTANCE, JnEntityLoginConflictSolved.INSTANCE).addMostExpectedStep(unLockPassword);
			CcpNextStep removeTokenTries = new SyncBusinessJnResetEntity("tries", 3, JnEntityTokenTries.INSTANCE).addMostExpectedStep(solveLoginConflict);
			CcpNextStep saver = JnEntityLockedToken.INSTANCE.getSaver(JnProcessStatus.loginTokenIsLocked).addEmptyStep();
			CcpNextStep evaluateTokenTries = new CommonsBusinessStepEvaluateTries(JnEntityTokenTries.INSTANCE, JnProcessStatus.wrongToken, JnProcessStatus.exceededTries).addAlternativeStep(JnProcessStatus.exceededTries, saver);
			CcpNextStep evaluateToken = new SyncBusinessJnEvaluateToken().addAlternativeStep(JnProcessStatus.wrongToken, evaluateTokenTries).addAlternativeStep(new CcpSuccessStatus(), removeTokenTries);
			
			CcpStepResult goToTheNextStep = evaluateToken.goToTheNextStep(valores);
			return goToTheNextStep.values.put("sessionToken", "{valorDoToken}").put("words", Arrays.asList(CcpConstants.EMPTY_JSON.put("word", "java").put("type", "IT")));
			
		};
		CcpJsonRepresentation result =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntityUserStats.INSTANCE).and()
			.ifThisIdIsPresentInEntity(JnEntityFailedUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsPresentInEntity(JnEntityRequestUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.executeAction(decisionTree).andFinallyReturningThisFields("words", "wordsToken", "sessionToken")	
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}

}

