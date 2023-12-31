package com.ccp.jn.sync.service;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.dao.CcpGetEntityId;
import com.ccp.especifications.db.utils.CcpEntityTransferData;
import com.ccp.jn.sync.business.JnProcessStatus;
import com.ccp.jn.sync.business.JnSyncBusinessCreateLogin;
import com.ccp.jn.sync.business.JnSyncBusinessEvaluatePasswordStrength;
import com.ccp.jn.sync.business.JnSyncBusinessEvaluatePreRegistration;
import com.ccp.jn.sync.business.JnSyncBusinessEvaluateToken;
import com.ccp.jn.sync.business.JnSyncBusinessResetEntity;
import com.ccp.jn.sync.business.JnSyncBusinessSavePassword;
import com.ccp.jn.sync.business.JnSyncBusinessValidatePassword;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.ccp.process.CcpSuccessStatus;
import com.jn.commons.business.JnCommonsBusinessEvaluateTries;
import com.jn.commons.business.JnCommonsBusinessSaveEntity;
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
import com.jn.commons.utils.JnTopic;

public class JnSyncLoginService{
	
	public CcpJsonRepresentation executeLogin(Map<String, Object> json){
		
		 Function<CcpJsonRepresentation, CcpJsonRepresentation> decisionTree = values -> {
			
			JnCommonsBusinessSaveEntity lockPassword = new JnEntityLockedPassword().getSaver(JnProcessStatus.exceededTries);
			CcpNextStep executeLogin = new JnSyncBusinessResetEntity("tries", 3, new JnEntityPasswordTries()).addMostExpectedStep(new JnSyncBusinessCreateLogin());

			CcpNextStep evaluateTries = new JnCommonsBusinessEvaluateTries(new JnEntityPasswordTries(), JnProcessStatus.wrongPassword, JnProcessStatus.exceededTries)
					.addAlternativeStep(JnProcessStatus.exceededTries, lockPassword);
			
			CcpNextStep validatePassword = new JnSyncBusinessValidatePassword(new JnEntityPassword())
					.addAlternativeStep(JnProcessStatus.wrongPassword, evaluateTries)
					.addMostExpectedStep(executeLogin);
			
			return validatePassword.goToTheNextStep(values).values.put("sessionToken", "{valorDoToken}").put("words", Arrays.asList(CcpConstants.EMPTY_JSON.put("word", "java").put("type", "IT")));
		};

		CcpJsonRepresentation values = new CcpJsonRepresentation(json);

		CcpJsonRepresentation findById =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(new JnEntityFailedUnlockToken()).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsPresentInEntity(new JnEntityRequestUnlockToken()).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.loadThisIdFromEntity(new JnEntityUserStats()).andSo()
			.ifThisIdIsPresentInEntity(new JnEntityLockedToken()).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityLoginToken()).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsPresentInEntity(new JnEntityLockedPassword()).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsPresentInEntity(new JnEntityLogin()).returnStatus(JnProcessStatus.loginInUse).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityPreRegistration()).returnStatus(JnProcessStatus.preRegistrationIsMissing).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityPassword()).returnStatus(JnProcessStatus.passwordIsMissing).and()
			.ifThisIdIsPresentInEntity(new JnEntityPassword()).executeAction(decisionTree).andFinallyReturningThisFields("words", "wordsToken", "sessionToken")
		.endThisProcedureRetrievingTheResultingData()
		;
		
		
		return findById;
	}
	
	public CcpJsonRepresentation createLoginToken (String email, String language){
		
		CcpJsonRepresentation values = CcpConstants.EMPTY_JSON.put("email", email).put("language", language);
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnTopic.sendUserToken.send(valores);

		CcpJsonRepresentation result = new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(new JnEntityFailedUnlockToken()).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsPresentInEntity(new JnEntityRequestUnlockToken()).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(new JnEntityLockedToken()).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsPresentInEntity(new JnEntityLockedPassword()).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityLoginToken()).executeAction(action).and()
			.ifThisIdIsPresentInEntity(new JnEntityLogin()).returnStatus(JnProcessStatus.loginInUse).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityPassword()).returnStatus(JnProcessStatus.passwordIsMissing).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityPreRegistration()).returnStatus(JnProcessStatus.preRegistrationIsMissing).andFinallyReturningThisFields("asyncTaskId")
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}
	
	public void existsLoginToken (String email){
		
		CcpJsonRepresentation values = new CcpJsonRepresentation(CcpConstants.EMPTY_JSON.put("email", email));

		 new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(new JnEntityFailedUnlockToken()).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsPresentInEntity(new JnEntityRequestUnlockToken()).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(new JnEntityLockedToken()).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsPresentInEntity(new JnEntityLockedPassword()).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityLoginToken()).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsPresentInEntity(new JnEntityLogin()).returnStatus(JnProcessStatus.loginInUse).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityPassword()).returnStatus(JnProcessStatus.passwordIsMissing).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityPreRegistration()).returnStatus(JnProcessStatus.preRegistrationIsMissing).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}
	
	public void executeLogout (String email){
		
		CcpJsonRepresentation values = CcpConstants.EMPTY_JSON.put("email", email);
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = x -> new CcpEntityTransferData(new JnEntityLogin(), new JnEntityLogout()).goToTheNextStep(x).values;
		 new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(new JnEntityLogin()).returnStatus(JnProcessStatus.unableToExecuteLogout).and()
			.ifThisIdIsPresentInEntity(new JnEntityLogin()).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}

	public CcpJsonRepresentation requestTokenAgain (String email, String language){
		
		CcpJsonRepresentation values = CcpConstants.EMPTY_JSON.put("email", email).put("language", language);

		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> {
			new JnEntityRequestTokenAgain().create(valores);
			return JnTopic.requestTokenAgain.send(valores);
		};
	
		CcpJsonRepresentation result =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(new JnEntityFailedUnlockToken()).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsPresentInEntity(new JnEntityRequestUnlockToken()).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(new JnEntityLockedToken()).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityLoginToken()).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsPresentInEntity(new JnEntityRequestTokenAgain()).returnStatus(JnProcessStatus.tokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(new JnEntityRequestTokenAgainAnswered()).returnStatus(JnProcessStatus.tokenAlreadySent).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityRequestTokenAgain()).executeAction(action).andFinallyReturningThisFields("asyncTaskId")
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}
	
	public CcpJsonRepresentation requestUnlockToken (String email, String language){
		
		CcpJsonRepresentation values = CcpConstants.EMPTY_JSON.put("email", email).put("language", language);
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnTopic.requestUnlockToken.send(valores);
		CcpJsonRepresentation result =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(new JnEntityFailedUnlockToken()).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityLoginToken()).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityLockedToken()).returnStatus(JnProcessStatus.unableToRequestUnLockToken).and()
			.ifThisIdIsPresentInEntity(new JnEntityRequestUnlockToken()).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(new JnEntityRequestUnlockTokenAnswered()).returnStatus(JnProcessStatus.unlockTokenAlreadyAnswered).and()
			.ifThisIdIsPresentInEntity(new JnEntityFailedUnlockToken()).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityRequestUnlockToken()).executeAction(action).andFinallyReturningThisFields("asyncTaskId")
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}
	
	public void savePreRegistration (CcpJsonRepresentation values){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> new JnEntityPreRegistration().createOrUpdate(valores);
		 new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(new JnEntityFailedUnlockToken()).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsPresentInEntity(new JnEntityRequestUnlockToken()).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(new JnEntityLogin()).returnStatus(JnProcessStatus.loginInUse).and()
			.ifThisIdIsPresentInEntity(new JnEntityLockedToken()).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsPresentInEntity(new JnEntityLockedPassword()).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityLoginToken()).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityPreRegistration()).executeAction(action).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityPassword()).returnStatus(JnProcessStatus.passwordIsMissing).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}

	public CcpJsonRepresentation saveWeakPassword (CcpJsonRepresentation parameters){
		JnSyncBusinessSavePassword passwordHandler = new JnSyncBusinessSavePassword();
		
		 Function<CcpJsonRepresentation, CcpJsonRepresentation> decisionTree = values -> {
			 
			 JnSyncBusinessCreateLogin saveLogin = new JnSyncBusinessCreateLogin();
			 CcpNextStep createPasswordAndExecuteLogin = passwordHandler.addMostExpectedStep(saveLogin);
			 CcpNextStep createWeakPassword = new JnEntityWeakPassword().getSaver().addMostExpectedStep(createPasswordAndExecuteLogin);
			 CcpNextStep deleteWeakPasswordIfExists = new JnEntityWeakPassword().getDeleter().addMostExpectedStep(createPasswordAndExecuteLogin);
			 CcpNextStep evaluatePasswordStrength = new JnSyncBusinessEvaluatePasswordStrength().addMostExpectedStep(deleteWeakPasswordIfExists).addAlternativeStep(JnProcessStatus.weakPassword, createWeakPassword);
			 CcpStepResult goToTheNextStep = evaluatePasswordStrength.goToTheNextStep(values);
			 
			 return goToTheNextStep.values;
		 };
		 
		CcpJsonRepresentation values = new CcpGetEntityId(parameters)
			.toBeginProcedureAnd()
				.loadThisIdFromEntity(new JnEntityUserStats())
				.andSo()	
					.ifThisIdIsPresentInEntity(new JnEntityFailedUnlockToken()).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
					.ifThisIdIsPresentInEntity(new JnEntityRequestUnlockToken()).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
					.ifThisIdIsPresentInEntity(new JnEntityLockedToken()).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
					.ifThisIdIsNotPresentInEntity(new JnEntityLoginToken()).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
					.ifThisIdIsNotPresentInEntity(new JnEntityPreRegistration()).returnStatus(JnProcessStatus.preRegistrationIsMissing).and()
					.executeAction(decisionTree)
				.andFinallyReturningThisFields()
			.endThisProcedureRetrievingTheResultingData();
		 
		return values;
		
	}

	
	public CcpJsonRepresentation unlockToken (CcpJsonRepresentation parameters){
		Function<CcpJsonRepresentation, CcpJsonRepresentation> decisionTree = values ->{
			
			return new JnSyncBusinessValidatePassword(new JnEntityRequestUnlockTokenAnswered(), JnProcessStatus.invalidPasswordToUnlockToken, "password")
					.addMostExpectedStep(new JnSyncBusinessResetEntity("tries", 3, new JnEntityUnlockTokenTries())
							.addMostExpectedStep(new CcpEntityTransferData(new JnEntityLockedToken(), new JnEntityUnlockedToken())
									)
							)
					.addAlternativeStep(JnProcessStatus.invalidPasswordToUnlockToken, new JnCommonsBusinessEvaluateTries(new JnEntityUnlockTokenTries(), JnProcessStatus.invalidPasswordToUnlockToken, JnProcessStatus.unlockTokenHasFailed)
							.addAlternativeStep(JnProcessStatus.unlockTokenHasFailed, new JnEntityFailedUnlockToken().getSaver(JnProcessStatus.unlockTokenHasFailed))
							)
					.goToTheNextStep(values).values;
			
		};
		
		CcpJsonRepresentation result = new CcpGetEntityId(parameters)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(new JnEntityLoginToken()).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityLockedToken()).returnStatus(JnProcessStatus.tokenIsNotLocked).and()
			.ifThisIdIsPresentInEntity(new JnEntityFailedUnlockToken()).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityRequestUnlockTokenAnswered()).returnStatus(JnProcessStatus.waitingForSupport).and()
			.executeAction(decisionTree).andFinallyReturningThisFields()
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}



	
	public CcpJsonRepresentation updatePassword (CcpJsonRepresentation values){
		Function<CcpJsonRepresentation, CcpJsonRepresentation> decisionTree = valores ->{
			JnSyncBusinessSavePassword passwordHandler = new JnSyncBusinessSavePassword();
			CcpNextStep evaluatePreRegistration = new JnSyncBusinessEvaluatePreRegistration(new JnSyncBusinessCreateLogin());
			CcpNextStep savePassword = passwordHandler.addMostExpectedStep(evaluatePreRegistration);
			CcpNextStep evaluatePasswordStrength = new JnSyncBusinessEvaluatePasswordStrength().addMostExpectedStep(savePassword);//TODO todo mundo passar o most expected por construtor
			CcpNextStep unLockPassword = new CcpEntityTransferData(new JnEntityLockedPassword(), new JnEntityUnlockedPassword()).addMostExpectedStep(evaluatePasswordStrength);
			CcpNextStep solveLoginConflict = new CcpEntityTransferData(new JnEntityLoginConflict(), new JnEntityLoginConflictSolved()).addMostExpectedStep(unLockPassword);
			JnEntityTokenTries entity = new JnEntityTokenTries();
			CcpNextStep removeTokenTries = new JnSyncBusinessResetEntity("tries", 3, entity).addMostExpectedStep(solveLoginConflict);
			CcpNextStep saver = new JnEntityLockedToken().getSaver(JnProcessStatus.loginTokenIsLocked).addEmptyStep();
			CcpNextStep evaluateTokenTries = new JnCommonsBusinessEvaluateTries(entity, JnProcessStatus.wrongToken, JnProcessStatus.exceededTries).addAlternativeStep(JnProcessStatus.exceededTries, saver);
			CcpNextStep evaluateToken = new JnSyncBusinessEvaluateToken().addAlternativeStep(JnProcessStatus.wrongToken, evaluateTokenTries).addAlternativeStep(new CcpSuccessStatus(), removeTokenTries);
			
			CcpStepResult goToTheNextStep = evaluateToken.goToTheNextStep(valores);
			return goToTheNextStep.values.put("sessionToken", "{valorDoToken}").put("words", Arrays.asList(CcpConstants.EMPTY_JSON.put("word", "java").put("type", "IT")));
			
		};
		/*
		 *TODO Salvar senha desbloqueada???
		 */
		CcpJsonRepresentation result =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(new JnEntityUserStats()).andSo()
			.ifThisIdIsPresentInEntity(new JnEntityFailedUnlockToken()).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsPresentInEntity(new JnEntityRequestUnlockToken()).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(new JnEntityLockedToken()).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsNotPresentInEntity(new JnEntityLoginToken()).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.executeAction(decisionTree).andFinallyReturningThisFields("words", "wordsToken", "sessionToken")	
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}

}

