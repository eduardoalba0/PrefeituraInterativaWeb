package br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;

@Entity
@Table(name = "tb_dim_funcionario")
@SuppressWarnings("serial")
public class Dim_Funcionario extends GenericDomain {

	@Column(nullable = false)
	private String nome;

	@Column
	private String cargo;

	@Transient
	@JoinColumn
	@ManyToOne
	private Dim_Departamento departamento;

	public Dim_Funcionario() {

	}

	public Dim_Funcionario(Usuario usuario) {
		this.set_ID(usuario.get_ID());
		this.nome = usuario.getNome();
		this.cargo = usuario.getDadosFuncionais().getCargo();
		this.departamento = new Dim_Departamento(usuario.getDadosFuncionais().getLocalDepartamento());
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	@Transient
	public Dim_Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Dim_Departamento departamento) {
		this.departamento = departamento;
	}

}