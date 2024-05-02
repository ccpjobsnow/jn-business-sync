package com.ccp.jn.sync.status.login;

import com.ccp.process.CcpProcessStatus;

public enum UpdatePassword implements CcpProcessStatus{
	emailInvalido(400),
	tokenBloqueado(403),
	tokenFaltando(404),
	tokenDigitadoIncorretamente(401),
	tokenRecemBloqueado(429),
	caminhoFeliz(200),
	;

	public final int status;
	
	
	
	private UpdatePassword(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
