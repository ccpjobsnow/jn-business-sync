package com.ccp.jn.sync.mensageria;

import java.util.Map;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.entities.JnEntityAsyncTask;
import com.jn.commons.utils.JnTopic;

public class JnSyncMensageriaSender implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {
	
	private final CcpMensageriaSender mensageriaSender = CcpDependencyInjection.getDependency(CcpMensageriaSender.class);
	
	private final JnTopic topic;
	
	public JnSyncMensageriaSender(JnTopic topic) {
		this.topic = topic;
	}

	public Map<String, Object> apply(Map<String, Object> map) {
		CcpJsonRepresentation json = new CcpJsonRepresentation(map);
		CcpJsonRepresentation response = this.apply(json);
		return response.content;
	}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {

		CcpJsonRepresentation put = json.put("topic", this.topic);
		
		CcpJsonRepresentation responseFromTopic = JnSyncPackageMessage.INSTANCE.apply(put);
		
		JnEntityAsyncTask.ENTITY.createOrUpdate(responseFromTopic);
		
		this.mensageriaSender.send(this.topic.name(), responseFromTopic);

		return responseFromTopic;
	}
	
	public String toString() {
		return this.topic.name();
	}
	
	
}
