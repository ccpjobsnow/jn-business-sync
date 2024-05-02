package com.ccp.jn.sync.status.login;

import com.ccp.process.CcpProcessStatus;

public enum SavePreRegistration implements CcpProcessStatus{
	emailInvalido(400),
	tokenBloqueado(403),
	tokenFaltando(404),
	senhaBloqueada(401),
	usuarioJaLogado(409),
	faltandoCadastrarSenha(202),
	caminhoFeliz(200),
	;

	public final int status;
	
	
	
	private SavePreRegistration(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
