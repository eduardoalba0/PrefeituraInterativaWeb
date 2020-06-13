package br.edu.ifpr.bsi.prefeiturainterativaweb.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.ServerTimestamp;

@SuppressWarnings("serial")
public class Atendimento implements Serializable {

	private String _ID;
	private String resposta;
	private String acao;
	private String departamento_ID;
	private String funcionario_ID;
	private String solicitacao_ID;

	@ServerTimestamp
	private Date data;
	@Exclude
	private Usuario funcionario;

//---------------------- Encapsulamento ----------------------

	public String get_ID() {
		return _ID;
	}

	public void set_ID(String _ID) {
		this._ID = _ID;
	}

	public String getResposta() {
		return resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getFuncionario_ID() {
		return funcionario_ID;
	}

	public void setFuncionario_ID(String funcionario_ID) {
		this.funcionario_ID = funcionario_ID;
	}

	public String getSolicitacao_ID() {
		return solicitacao_ID;
	}

	public void setSolicitacao_ID(String solicitacao_ID) {
		this.solicitacao_ID = solicitacao_ID;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Exclude
	public Usuario getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Usuario funcionario) {
		if (funcionario != null && funcionario.get_ID() != null)
			this.setFuncionario_ID(funcionario.get_ID());
		this.funcionario = funcionario;
	}

	public String getDepartamento_ID() {
		return departamento_ID;
	}

	public void setDepartamento_ID(String departamento_ID) {
		this.departamento_ID = departamento_ID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Atendimento atendimento = (Atendimento) o;
		return _ID.equals(atendimento._ID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_ID);
	}

	@Override
	public String toString() {
		return this.resposta;
	}
}
