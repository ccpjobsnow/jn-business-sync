package com.ccp.jn.sync.status.login;

public enum ExecuteLogin implements EndpointsLogin{
	emailInvalido(400),
	tokenBloqueado(403),
	tokenFaltando(404),
	senhaBloqueada(423),
	senhaDigitadaIncorretamente(401),
	senhaRecemBloqueada(429),
	usuarioJaLogado(409),
	faltandoCadastrarSenha(202),
	caminhoFeliz(200),
	;

	public final int status;
	
	
	
	private ExecuteLogin(int status) {
		this.status = status;
	}

	public int status() {
		return status;
	}
}
