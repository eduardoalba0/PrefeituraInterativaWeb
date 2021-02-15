package br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tb_fato_demandasarea")
@SuppressWarnings("serial")
public class Fato_DemandasArea extends GenericDomain {

	@JoinColumn
	@ManyToOne(cascade = CascadeType.ALL)
	private Dim_Solicitacao solicitacao;

	@JoinColumn
	@ManyToOne(cascade = CascadeType.ALL)
	private Dim_Categoria categoria;

	@JoinColumn
	@ManyToOne(cascade = CascadeType.ALL)
	private Dim_Tempo tempo;

	@JoinColumn
	@ManyToOne(cascade = CascadeType.ALL)
	private Dim_Departamento departamento;

	public Dim_Solicitacao getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Dim_Solicitacao solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Dim_Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Dim_Categoria categoria) {
		this.categoria = categoria;
	}

	public Dim_Tempo getTempo() {
		return tempo;
	}

	public void setTempo(Dim_Tempo tempo) {
		this.tempo = tempo;
	}

	public Dim_Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Dim_Departamento departamento) {
		this.departamento = departamento;
	}

}
