package com.ccp.jn.sync.mensageria;

import java.util.UUID;
import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.especifications.db.utils.decorators.CcpEntityExpurgableOptions;
import com.ccp.validation.CcpJsonFieldsValidations;
import com.jn.commons.entities.JnEntityAsyncTask;
import com.jn.commons.utils.JnTopic;

public class JnSyncPackageMessage implements Function<CcpJsonRepresentation,CcpJsonRepresentation>{

	
	public static final JnSyncPackageMessage INSTANCE = new JnSyncPackageMessage();
	
	private JnSyncPackageMessage() {
		
	}
	
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		JnTopic topic = json.getAsObject(JnEntityAsyncTask.Fields.topic.name());
		String topicName = topic.name();
		Class<? extends JnTopic> validationClass = (Class<? extends JnTopic>) topic.validationClass();
		CcpJsonFieldsValidations.validate(validationClass, json.content, topicName);
		CcpTimeDecorator ccpTimeDecorator = new CcpTimeDecorator();
		String formattedCurrentDateTime = ccpTimeDecorator.getFormattedDateTime(CcpEntityExpurgableOptions.second.format);
		
		CcpJsonRepresentation messageDetails = CcpOtherConstants.EMPTY_JSON
				.put(JnEntityAsyncTask.Fields.started.name(), System.currentTimeMillis())
				.put(JnEntityAsyncTask.Fields.data.name(), formattedCurrentDateTime)
				.put(JnEntityAsyncTask.Fields.messageId.name(), UUID.randomUUID())
				.put(JnEntityAsyncTask.Fields.topic.name(), topicName)
				.put(JnEntityAsyncTask.Fields.request.name(), json)
				.putAll(json)
				;
		return messageDetails;
	}

}
