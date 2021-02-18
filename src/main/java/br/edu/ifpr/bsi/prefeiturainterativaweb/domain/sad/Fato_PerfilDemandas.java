package br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;

@Entity
@Table(name = "tb_fato_perfildemandas")
@SuppressWarnings("serial")
public class Fato_PerfilDemandas extends GenericDomain {

	@Column
	private boolean solicitacaoConcluida;

	@JoinColumn(nullable = true)
	@OneToOne(cascade = CascadeType.ALL)
	private Dim_Tempo dataAbertura;

	@JoinColumn(nullable = true)
	@ManyToOne(cascade = CascadeType.ALL)
	private Dim_Departamento departamento;

	@JoinColumn(nullable = true)
	@ManyToOne(cascade = CascadeType.ALL)
	private Dim_Categoria categoria;

	@JoinColumn(nullable = true)
	@OneToOne(cascade = CascadeType.ALL)
	private Dim_Local local;

	public Fato_PerfilDemandas() {

	}

	public Fato_PerfilDemandas(Solicitacao solicitacao, Categoria categoria) {
		this.set_ID(solicitacao.get_ID());
		this.solicitacaoConcluida = solicitacao.isConcluida();
		this.dataAbertura = new Dim_Tempo(solicitacao.getDataAbertura());
		this.departamento = new Dim_Departamento(solicitacao.getLocalDepartamento());
		this.categoria = new Dim_Categoria(categoria);
		this.local = new Dim_Local(solicitacao);
	}

	public boolean isSolicitacaoConcluida() {
		return solicitacaoConcluida;
	}

	public void setSolicitacaoConcluida(boolean solicitacaoConcluida) {
		this.solicitacaoConcluida = solicitacaoConcluida;
	}

	public Dim_Tempo getDataAbertura() {
		return dataAbertura;
	}

	public void setDataAbertura(Dim_Tempo dataAbertura) {
		this.dataAbertura = dataAbertura;
	}

	public Dim_Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Dim_Departamento departamento) {
		this.departamento = departamento;
	}

	public Dim_Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Dim_Categoria categoria) {
		this.categoria = categoria;
	}

	public Dim_Local getLocal() {
		return local;
	}

	public void setLocal(Dim_Local local) {
		this.local = local;
	}

}
