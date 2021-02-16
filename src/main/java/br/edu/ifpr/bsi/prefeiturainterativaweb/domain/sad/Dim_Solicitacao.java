package br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "tb_dim_solicitacao")
@SuppressWarnings("serial")
public class Dim_Solicitacao extends GenericDomain {

	@Column
	private String avaliacao_comentario;
	
	@Column
	private String bairro;
	
	@Column
	private float avaliacao_nota;
	
	@Column
	private boolean concluida;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date data_abertura;

	@Column(nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date data_conclusao;

	@JoinColumn
	@ManyToOne(cascade = CascadeType.ALL)
	private Dim_Funcionario funcionario;

	@JoinColumn
	@ManyToOne(cascade = CascadeType.ALL)
	private Dim_Departamento departamento;

	public Dim_Solicitacao() {
		
	}
	
	public Dim_Funcionario getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Dim_Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public Dim_Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Dim_Departamento departamento) {
		this.departamento = departamento;
	}

	public String getAvaliacao_comentario() {
		return avaliacao_comentario;
	}

	public void setAvaliacao_comentario(String avaliacao_comentario) {
		this.avaliacao_comentario = avaliacao_comentario;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public float getAvaliacao_nota() {
		return avaliacao_nota;
	}

	public void setAvaliacao_nota(float avaliacao_nota) {
		this.avaliacao_nota = avaliacao_nota;
	}

	public boolean isConcluida() {
		return concluida;
	}

	public void setConcluida(boolean concluida) {
		this.concluida = concluida;
	}

	public Date getData_abertura() {
		return data_abertura;
	}

	public void setData_abertura(Date data_abertura) {
		this.data_abertura = data_abertura;
	}

	public Date getData_conclusao() {
		return data_conclusao;
	}

	public void setData_conclusao(Date data_conclusao) {
		this.data_conclusao = data_conclusao;
	}

}