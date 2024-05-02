package com.ccp.jn.sync.status.login;

import com.ccp.process.CcpProcessStatus;

public enum ExecuteLogout implements CcpProcessStatus{
	emailInvalido(400),
	usuarioNaoLogado(404),
	caminhoFeliz(200),
	;

	public final int status;
	
	
	
	private ExecuteLogout(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
