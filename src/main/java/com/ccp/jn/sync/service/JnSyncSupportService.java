package com.ccp.jn.sync.service;

import static com.ccp.constantes.CcpConstants.DO_NOTHING;
import static com.ccp.jn.sync.business.JnProcessStatus.loginTokenIsMissing;
import static com.ccp.jn.sync.business.JnProcessStatus.requestAlreadyAnswered;
import static com.ccp.jn.sync.business.JnProcessStatus.requestDoesNotExist;
import static com.ccp.jn.sync.business.JnProcessStatus.thisUserIsNotAllowedToDoSupport;
import static com.jn.commons.utils.JnConstants.PUT_EMAIL_TOKEN;
import static com.jn.commons.utils.JnTopic.requestTokenAgain;
import static com.jn.commons.utils.JnTopic.requestUnlockToken;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CcpGetEntityId;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.business.JnCommonsBusinessGetMessage;
import com.jn.commons.entities.JnEntityEmailParametersToSend;
import com.jn.commons.entities.JnEntityEmailTemplateMessage;
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
			
			CcpMapDecorator result = this.answerSupport(chatId, email, PUT_EMAIL_TOKEN, responsibleEntity, answerEntity, requestEntity, requestUnlockToken);
			
			return result;
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
	}
	;
	
	public abstract CcpMapDecorator execute(Long chatId, String email);
	
	public CcpMapDecorator getUnlockTokenMessage(Long chatId, String email) {
		
		
		CcpMapDecorator result = this.answerSupport(chatId, email, PUT_EMAIL_TOKEN, new JnEntityRequestUnlockTokenResponsible(), new JnEntityRequestUnlockTokenAnswered(),
				new JnEntityRequestUnlockToken(), requestUnlockToken);
		
		return result;
	}

	public CcpMapDecorator getRequestTokenAgainMessage(Long chatId, String email) {
		
		
		CcpMapDecorator result = this.answerSupport(chatId, email, DO_NOTHING, new JnEntityRequestTokenAgainResponsible(), new JnEntityRequestTokenAgainAnswered(),
				new JnEntityRequestTokenAgain(), requestTokenAgain);
		
		return result;
	}

	protected CcpMapDecorator answerSupport(
			Long chatId, String email, Function<CcpMapDecorator, CcpMapDecorator> transform, CcpEntity responsibleEntity,
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
		
		CcpMapDecorator result = new CcpGetEntityId(valores)
			.toBeginProcedureAnd()
				.ifThisIdIsNotPresentInEntity(responsibleEntity).returnStatus(thisUserIsNotAllowedToDoSupport).and()
				.ifThisIdIsNotPresentInEntity(new JnEntityLoginToken()).returnStatus(loginTokenIsMissing).and()
				.ifThisIdIsNotPresentInEntity(requestEntity).returnStatus(requestDoesNotExist).and()
				.ifThisIdIsPresentInEntity(answerEntity).returnStatus(requestAlreadyAnswered).and()
				.executeAction(action).andFinally()
			.endThisProcedureRetrievingTheResultingData();
		return result;
	}
	
}
