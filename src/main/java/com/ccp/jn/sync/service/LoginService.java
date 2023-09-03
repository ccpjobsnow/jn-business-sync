package com.ccp.jn.sync.service;

import java.util.Map;
import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CalculateId;
import com.ccp.especifications.db.utils.TransferDataBetweenEntities;
import com.ccp.jn.sync.common.business.CreateLogin;
import com.ccp.jn.sync.common.business.EvaluatePasswordStrength;
import com.ccp.jn.sync.common.business.EvaluatePreRegistration;
import com.ccp.jn.sync.common.business.EvaluateToken;
import com.ccp.jn.sync.common.business.JnProcessStatus;
import com.ccp.jn.sync.common.business.ResetEntity;
import com.ccp.jn.sync.common.business.SavePassword;
import com.ccp.jn.sync.common.business.ValidatePassword;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.ccp.process.SuccessStatus;
import com.jn.commons.EvaluateTries;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;
import com.jn.commons.SaveEntity;

public class LoginService{
	
	public CcpMapDecorator executeLogin(Map<String, Object> json){
		
		 Function<CcpMapDecorator, CcpMapDecorator> decisionTree = values -> {
			
			SaveEntity lockPassword = JnEntity.locked_password.getSaver(JnProcessStatus.exceededTries);
			CcpNextStep executeLogin = new ResetEntity("tries", 3, JnEntity.password_tries).addMostExpectedStep(new CreateLogin());

			CcpNextStep evaluateTries = new EvaluateTries(JnEntity.password_tries, JnProcessStatus.wrongPassword, JnProcessStatus.exceededTries)
					.addAlternativeStep(JnProcessStatus.exceededTries, lockPassword);
			
			CcpNextStep validatePassword = new ValidatePassword(JnEntity.password)
					.addAlternativeStep(JnProcessStatus.wrongPassword, evaluateTries)
					.addMostExpectedStep(executeLogin);
			
			return validatePassword.goToTheNextStep(values).values;
		};

		CcpMapDecorator values = new CcpMapDecorator(json);

		CcpMapDecorator findById =  new CalculateId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntity.user_stats).andSo()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsPresentInEntity(JnEntity.locked_password).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsPresentInEntity(JnEntity.login).returnStatus(JnProcessStatus.loginInUse).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(JnProcessStatus.preRegistrationIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.password).returnStatus(JnProcessStatus.passwordIsMissing).and()
			.ifThisIdIsPresentInEntity(JnEntity.password).executeAction(decisionTree).andFinally()
		.endThisProcedureRetrievingTheResultingData()
		;
		
		
		return findById;
	}
	
	public CcpMapDecorator createLoginToken (String email, String language){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email).put("language", language);
		
		Function<CcpMapDecorator, CcpMapDecorator> action = valores -> JnTopic.sendUserToken.send(valores);

		CcpMapDecorator result = new CalculateId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsPresentInEntity(JnEntity.locked_password).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).executeAction(action).and()
			.ifThisIdIsPresentInEntity(JnEntity.login).returnStatus(JnProcessStatus.loginInUse).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.password).returnStatus(JnProcessStatus.passwordIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(JnProcessStatus.preRegistrationIsMissing).andFinally()
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}
	
	public void existsLoginToken (String email){
		
		CcpMapDecorator values = new CcpMapDecorator(new CcpMapDecorator().put("email", email));

		 new CalculateId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsPresentInEntity(JnEntity.locked_password).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsPresentInEntity(JnEntity.login).returnStatus(JnProcessStatus.loginInUse).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.password).returnStatus(JnProcessStatus.passwordIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(JnProcessStatus.preRegistrationIsMissing).andFinally()
		.endThisProcedure()
		;
	}
	
	public void executeLogout (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		
		Function<CcpMapDecorator, CcpMapDecorator> action = x -> new TransferDataBetweenEntities(JnEntity.login, JnEntity.logout).goToTheNextStep(x).values;
		 new CalculateId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntity.login).returnStatus(JnProcessStatus.unableToExecuteLogout).and()
			.ifThisIdIsPresentInEntity(JnEntity.login).executeAction(action).andFinally()
		.endThisProcedure()
		;
	}

	public CcpMapDecorator requestTokenAgain (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);

		
		
		Function<CcpMapDecorator, CcpMapDecorator> action = valores -> {
			JnEntity.request_token_again.create(valores);
			return JnTopic.requestTokenAgain.send(valores);
		};
	
		CcpMapDecorator result =  new CalculateId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsPresentInEntity(JnEntity.request_token_again).returnStatus(JnProcessStatus.tokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(JnEntity.request_token_again_answered).returnStatus(JnProcessStatus.tokenAlreadySent).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.request_token_again).executeAction(action).andFinally()
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}
	
	public CcpMapDecorator requestUnlockToken (String email){
		
		CcpMapDecorator values = new CcpMapDecorator().put("email", email);
		Function<CcpMapDecorator, CcpMapDecorator> action = valores -> JnTopic.requestUnlockToken.send(valores);
		CcpMapDecorator result =  new CalculateId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.unableToRequestUnLockToken).and()
			.ifThisIdIsPresentInEntity(JnEntity.request_unlock_token).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
			.ifThisIdIsPresentInEntity(JnEntity.request_unlock_token_answered).returnStatus(JnProcessStatus.unlockTokenAlreadyAnswered).and()
			.ifThisIdIsPresentInEntity(JnEntity.failed_unlock_token).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.request_unlock_token).executeAction(action).andFinally()
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}
	
	public void savePreRegistration (CcpMapDecorator values){
		
		Function<CcpMapDecorator, CcpMapDecorator> action = valores -> JnEntity.pre_registration.createOrUpdate(valores);
		 new CalculateId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntity.login).returnStatus(JnProcessStatus.loginInUse).and()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsPresentInEntity(JnEntity.locked_password).returnStatus(JnProcessStatus.passwordIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).executeAction(action).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.password).returnStatus(JnProcessStatus.passwordIsMissing).andFinally()
		.endThisProcedure()
		;
	}

	public CcpMapDecorator saveWeakPassword (CcpMapDecorator parameters){
		SavePassword passwordHandler = new SavePassword();
		
		 Function<CcpMapDecorator, CcpMapDecorator> decisionTree = values -> {
			 
			 CreateLogin saveLogin = new CreateLogin();
			 CcpNextStep createPasswordAndExecuteLogin = passwordHandler.addMostExpectedStep(saveLogin);
			 CcpNextStep createWeakPassword = JnEntity.weak_password.getSaver().addMostExpectedStep(createPasswordAndExecuteLogin);
			 CcpNextStep deleteWeakPasswordIfExists = JnEntity.weak_password.getDeleter().addMostExpectedStep(createPasswordAndExecuteLogin);
			 CcpNextStep evaluatePasswordStrength = new EvaluatePasswordStrength().addMostExpectedStep(deleteWeakPasswordIfExists).addAlternativeStep(JnProcessStatus.weakPassword, createWeakPassword);
			 CcpStepResult goToTheNextStep = evaluatePasswordStrength.goToTheNextStep(values);
			 
			 return goToTheNextStep.values;
		 };
		 
		CcpMapDecorator values = new CalculateId(parameters)
			.toBeginProcedureAnd()
				.loadThisIdFromEntity(JnEntity.user_stats)
				.andSo()	
					.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
					.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
					.ifThisIdIsNotPresentInEntity(JnEntity.pre_registration).returnStatus(JnProcessStatus.preRegistrationIsMissing).and()
					.executeAction(decisionTree)
				.andFinally()
			.endThisProcedureRetrievingTheResultingData();
		 
		return values;
		
	}

	
	public CcpMapDecorator unlockToken (CcpMapDecorator parameters){
		Function<CcpMapDecorator, CcpMapDecorator> decisionTree = values ->{
			
			return new ValidatePassword(JnEntity.request_unlock_token_answered)
					.addMostExpectedStep(new ResetEntity("tries", 3, JnEntity.unlock_token_tries)
							.addMostExpectedStep(new TransferDataBetweenEntities(JnEntity.locked_token, JnEntity.unlocked_token)
									)
							)
					.addAlternativeStep(JnProcessStatus.wrongPassword, new EvaluateTries(JnEntity.unlock_token_tries, JnProcessStatus.wrongPassword, JnProcessStatus.exceededTries)
							.addAlternativeStep(JnProcessStatus.exceededTries, JnEntity.locked_password.getSaver(JnProcessStatus.exceededTries))
							)
					.goToTheNextStep(values).values;
			
		};
		
		CcpMapDecorator result = new CalculateId(parameters)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.tokenIsNotLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.request_unlock_token).returnStatus(JnProcessStatus.unlockTokenHasNotBeenRequested).and()
			.ifThisIdIsPresentInEntity(JnEntity.failed_unlock_token).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
			.ifThisIdIsPresentInEntity(JnEntity.request_unlock_token_answered).executeAction(decisionTree).andFinally()
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}



	
	public CcpMapDecorator updatePassword (CcpMapDecorator values){
		Function<CcpMapDecorator, CcpMapDecorator> decisionTree = valores ->{
			SavePassword passwordHandler = new SavePassword();
			CcpNextStep evaluatePreRegistration = new EvaluatePreRegistration(new CreateLogin());
			CcpNextStep savePassword = passwordHandler.addMostExpectedStep(evaluatePreRegistration);
			CcpNextStep evaluatePasswordStrength = new EvaluatePasswordStrength().addMostExpectedStep(savePassword);//TODO todo mundo passar o most expected por construtor
			CcpNextStep unLockPassword = new TransferDataBetweenEntities(JnEntity.locked_password, JnEntity.unlocked_password).addMostExpectedStep(evaluatePasswordStrength);
			CcpNextStep solveLoginConflict = new TransferDataBetweenEntities(JnEntity.login_conflict, JnEntity.login_conflict_solved).addMostExpectedStep(unLockPassword);
			CcpNextStep removeTokenTries = new ResetEntity("tries", 3, JnEntity.token_tries).addMostExpectedStep(solveLoginConflict);
			CcpNextStep saver = JnEntity.locked_token.getSaver(JnProcessStatus.loginTokenIsLocked).addEmptyStep();
			CcpNextStep evaluateTokenTries = new EvaluateTries(JnEntity.token_tries, JnProcessStatus.wrongToken, JnProcessStatus.exceededTries).addAlternativeStep(JnProcessStatus.exceededTries, saver);
			CcpNextStep evaluateToken = new EvaluateToken().addAlternativeStep(JnProcessStatus.wrongToken, evaluateTokenTries).addAlternativeStep(new SuccessStatus(), removeTokenTries);
			
			CcpStepResult goToTheNextStep = evaluateToken.goToTheNextStep(valores);
			return goToTheNextStep.values;
			
		};
		/*
		 *TODO Salvar senha desbloqueada
		 */
		CcpMapDecorator result =  new CalculateId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntity.user_stats).andSo()
			.ifThisIdIsPresentInEntity(JnEntity.locked_token).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
			.ifThisIdIsNotPresentInEntity(JnEntity.login_token).returnStatus(JnProcessStatus.loginTokenIsMissing).and()
			.executeAction(decisionTree).andFinally()	
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}

}

