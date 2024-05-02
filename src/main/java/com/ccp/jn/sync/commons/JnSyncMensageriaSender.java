package com.ccp.jn.sync.commons;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.entities.JnEntityAsyncTask;

public class JnSyncMensageriaSender {
	private final CcpMensageriaSender mensageriaSender = CcpDependencyInjection.getDependency(CcpMensageriaSender.class);
	
	public static final JnSyncMensageriaSender INSTANCE = new JnSyncMensageriaSender();
	
	private JnSyncMensageriaSender() {
		
	}
	
	private CcpJsonRepresentation send(CcpJsonRepresentation values, String topic, CcpEntity entity) {
		
		String token = new CcpStringDecorator(CcpConstants.CHARACTERS_TO_GENERATE_TOKEN).text().generateToken(20).content;
		String formattedCurrentDateTime = new CcpTimeDecorator().getFormattedCurrentDateTime("dd/MM/yyyy HH:mm:ss");
	
		CcpJsonRepresentation messageDetails = CcpConstants.EMPTY_JSON
				.put("started", System.currentTimeMillis())
				.put("data", formattedCurrentDateTime)
				.put("request", values)
				.put("topic", topic)
				.put("id", token)
				.putAll(values)
				;
		
		String asyncTaskId = entity.getId(messageDetails);
		entity.createOrUpdate(messageDetails, asyncTaskId);
		
		this.mensageriaSender.send(topic, messageDetails);
		return messageDetails.put("asyncTaskId", asyncTaskId);
	}

	public CcpJsonRepresentation send(CcpJsonRepresentation values, Enum<?> topic) {
		String topicName = topic.name();
		CcpJsonRepresentation send = this.send(values, topicName, JnEntityAsyncTask.INSTANCE);
		return send;
	}
}
