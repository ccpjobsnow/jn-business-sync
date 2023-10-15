package com.ccp.jn.sync.service;

import static com.ccp.constantes.CcpConstants.DO_NOTHING;
import static com.ccp.jn.sync.business.JnProcessStatus.loginTokenIsMissing;
import static com.ccp.jn.sync.business.JnProcessStatus.requestAlreadyAnswered;
import static com.ccp.jn.sync.business.JnProcessStatus.requestToUnlockDoesNotExist;
import static com.ccp.jn.sync.business.JnProcessStatus.thisUserIsNotAllowedToDoSupport;
import static com.jn.commons.utils.JnConstants.PUT_PASSWORD;
import static com.jn.commons.utils.JnTopic.requestTokenAgain;
import static com.jn.commons.utils.JnTopic.requestUnlockToken;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CcpDaoProcedure;
import com.ccp.especifications.db.dao.CcpGetEntityId;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.sync.business.JnProcessStatus;
import com.jn.commons.business.JnCommonsBusinessGetMessage;
import com.jn.commons.entities.JnEntityEmailParametersToSend;
import com.jn.commons.entities.JnEntityEmailTemplateMessage;
import com.jn.commons.entities.JnEntityFailedUnlockToken;
import com.jn.commons.entities.JnEntityLockedToken;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.entities.JnEntityRequestTokenAgain;
import com.jn.commons.entities.JnEntityRequestTokenAgainAnswered;
import com.jn.commons.entities.JnEntityRequestTokenAgainResponsible;
import com.jn.commons.entities.JnEntityRequestUnlockToken;
import com.jn.commons.entities.JnEntityRequestUnlockTokenAnswered;
import com.jn.commons.entities.JnEntityRequestUnlockTokenResponsible;
import com.jn.commons.utils.JnTopic;

public enum JnSyncSupportService {
	unlockToken {
		@Override
		public CcpMapDecorator execute(Long chatId, String email) {
			
			JnEntityRequestUnlockTokenResponsible responsibleEntity = new JnEntityRequestUnlockTokenResponsible();
			JnEntityRequestUnlockTokenAnswered answerEntity = new JnEntityRequestUnlockTokenAnswered();
			JnEntityRequestUnlockToken requestEntity = new JnEntityRequestUnlockToken();
			
			CcpMapDecorator result = this.answerSupport(chatId, email, PUT_PASSWORD, responsibleEntity, answerEntity, requestEntity, requestUnlockToken);
			
			return result;
		}

		@Override
		public CcpDaoProcedure getValidations(CcpMapDecorator valores, CcpEntity responsibleEntity, CcpEntity answerEntity, CcpEntity requestEntity) {
			CcpDaoProcedure rules = new CcpGetEntityId(valores)
					.toBeginProcedureAnd()
						.ifThisIdIsPresentInEntity(new JnEntityFailedUnlockToken()).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
						.ifThisIdIsNotPresentInEntity(responsibleEntity).returnStatus(thisUserIsNotAllowedToDoSupport).and()
						.ifThisIdIsNotPresentInEntity(new JnEntityLoginToken()).returnStatus(loginTokenIsMissing).and()
						.ifThisIdIsPresentInEntity(answerEntity).returnStatus(requestAlreadyAnswered).and()
						.ifThisIdIsNotPresentInEntity(requestEntity).returnStatus(requestToUnlockDoesNotExist).and();
			return rules;
		}
	},
	resendToken {
		@Override
		public CcpMapDecorator execute(Long chatId, String email) {
			
			JnEntityRequestTokenAgainResponsible responsibleEntity = new JnEntityRequestTokenAgainResponsible();
			JnEntityRequestTokenAgainAnswered answerEntity = new JnEntityRequestTokenAgainAnswered();
			JnEntityRequestTokenAgain requestEntity = new JnEntityRequestTokenAgain();
			
			CcpMapDecorator result = this.answerSupport(chatId, email, DO_NOTHING, responsibleEntity, answerEntity, requestEntity, requestTokenAgain);
			
			return result;
		}

		@Override
		public CcpDaoProcedure getValidations(CcpMapDecorator valores, CcpEntity responsibleEntity, CcpEntity answerEntity, CcpEntity requestEntity) {
			CcpDaoProcedure validations = new CcpGetEntityId(valores)
					.toBeginProcedureAnd()
						.ifThisIdIsPresentInEntity(new JnEntityLockedToken()).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
						.ifThisIdIsPresentInEntity(new JnEntityRequestUnlockToken()).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
						.ifThisIdIsPresentInEntity(new JnEntityFailedUnlockToken()).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
						.ifThisIdIsNotPresentInEntity(responsibleEntity).returnStatus(thisUserIsNotAllowedToDoSupport).and()
						.ifThisIdIsNotPresentInEntity(new JnEntityLoginToken()).returnStatus(loginTokenIsMissing).and()
						.ifThisIdIsPresentInEntity(answerEntity).returnStatus(requestAlreadyAnswered).and()
						.ifThisIdIsNotPresentInEntity(requestEntity).returnStatus(requestToUnlockDoesNotExist).and();
			return validations;
		}
	}
	;
	
	public abstract CcpMapDecorator execute(Long chatId, String email);
	
	public abstract CcpDaoProcedure getValidations(CcpMapDecorator valores, CcpEntity responsibleEntity, CcpEntity answerEntity, CcpEntity requestEntity);
	
	protected CcpMapDecorator answerSupport(Long chatId, String email, Function<CcpMapDecorator, CcpMapDecorator> transform, CcpEntity responsibleEntity,
			CcpEntity answerEntity, CcpEntity requestEntity, JnTopic topic) {
		
		CcpMapDecorator valores = new CcpMapDecorator().put("email", email).put("chatId", chatId);
		CcpMapDecorator transformed = valores.getTransformed(transform);
		
		Function<CcpMapDecorator, CcpMapDecorator> action = values -> {
			CcpMapDecorator entities = values.getInternalMap("_entities");
			String language = entities.getInternalMap(requestEntity.name()).getAsString("language");
			JnCommonsBusinessGetMessage gm = new JnCommonsBusinessGetMessage();
			JnEntityEmailTemplateMessage messageEntity = new JnEntityEmailTemplateMessage();
			JnEntityEmailParametersToSend parameterEntity = new JnEntityEmailParametersToSend();
			gm
			.addFlow(DO_NOTHING, parameterEntity, messageEntity)
			.execute(topic, answerEntity, transformed, language);
			requestEntity.delete(valores);
			return values;
			
		};
		
		CcpDaoProcedure validations = this.getValidations(valores, responsibleEntity, answerEntity, requestEntity);
		
		CcpMapDecorator result = validations
				.executeAction(action).andFinally()
			.endThisProcedureRetrievingTheResultingData();
		return result;
	}
	
}
