package br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;

@Entity
@Table(name = "tb_dim_local")
@SuppressWarnings("serial")
public class Dim_Local extends GenericDomain {

	@Column(nullable = false)
	private String bairro;

	public Dim_Local() {

	}

	public Dim_Local(Solicitacao solicitacao) {
		this.set_ID(solicitacao.get_ID());
		this.bairro = solicitacao.getLocalizacao().getBairro();
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

}