package br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.gson.Gson;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;

@Entity
@Table(name = "tb_fato_qualidadeatendimento")
@SuppressWarnings("serial")
public class Fato_QualidadeAtendimento extends GenericDomain {

	@Column
	private boolean solicitacaoSolucionada;

	@Column
	private boolean solicitacaoConcluida;

	@JoinColumn(nullable = true)
	@ManyToOne(cascade = CascadeType.ALL)
	private Dim_Avaliacao avaliacao;

	@JoinColumn(nullable = true)
	@ManyToOne(cascade = CascadeType.ALL)
	private Dim_Tempo dataAbertura;

	@JoinColumn(nullable = true)
	@ManyToOne(cascade = CascadeType.ALL)
	private Dim_Tempo dataConclusao;

	@JoinColumn(nullable = true)
	@ManyToOne(cascade = CascadeType.ALL)
	private Dim_Departamento departamento;

	@JoinColumn(nullable = true)
	@ManyToOne(cascade = CascadeType.ALL)
	private Dim_Funcionario funcionario;

	public Fato_QualidadeAtendimento() {

	}

	public Fato_QualidadeAtendimento(Solicitacao solicitacao) {
		this.set_ID(solicitacao.get_ID());
		this.solicitacaoConcluida = solicitacao.isConcluida();
		this.avaliacao = new Dim_Avaliacao(solicitacao);
		this.dataAbertura = new Dim_Tempo(solicitacao.getDataAbertura());
		this.dataConclusao = new Dim_Tempo(solicitacao.getDataConclusao());
		this.departamento = new Dim_Departamento(solicitacao.getLocalDepartamento());
		if (solicitacao.getAvaliacao() != null)
			this.solicitacaoSolucionada = solicitacao.getAvaliacao().isSolucionada();
		if (solicitacao.getLocalFuncionarioConclusao() != null)
			this.funcionario = new Dim_Funcionario(solicitacao.getLocalFuncionarioConclusao());
	}

	public boolean isSolicitacaoSolucionada() {
		return solicitacaoSolucionada;
	}

	public void setSolicitacaoSolucionada(boolean solicitacaoSolucionada) {
		this.solicitacaoSolucionada = solicitacaoSolucionada;
	}

	public boolean isSolicitacaoConcluida() {
		return solicitacaoConcluida;
	}

	public void setSolicitacaoConcluida(boolean solicitacaoConcluida) {
		this.solicitacaoConcluida = solicitacaoConcluida;
	}

	public Dim_Avaliacao getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(Dim_Avaliacao avaliacao) {
		this.avaliacao = avaliacao;
	}

	public Dim_Tempo getDataAbertura() {
		return dataAbertura;
	}

	public void setDataAbertura(Dim_Tempo dataAbertura) {
		this.dataAbertura = dataAbertura;
	}

	public Dim_Tempo getDataConclusao() {
		return dataConclusao;
	}

	public void setDataConclusao(Dim_Tempo dataConclusao) {
		this.dataConclusao = dataConclusao;
	}

	public Dim_Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Dim_Departamento departamento) {
		this.departamento = departamento;
	}

	public Dim_Funcionario getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Dim_Funcionario funcionario) {
		this.funcionario = funcionario;
	}

}
