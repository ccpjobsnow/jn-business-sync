package com.ccp.jn.sync.status.login;

import com.ccp.process.CcpProcessStatus;

public enum CreateLoginToken implements CcpProcessStatus{
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
