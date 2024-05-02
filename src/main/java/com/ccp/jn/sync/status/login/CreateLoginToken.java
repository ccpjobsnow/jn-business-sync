package com.ccp.jn.sync.status.login;

public enum CreateLoginToken implements EndpointsLogin{
	emailInvalido(400),
	tokenBloqueado(403),
	tokenFaltando(404),
	faltandoPreRegistration(201),
	caminhoFeliz(200),
	;

	public final int status;
	
	
	
	private CreateLoginToken(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
