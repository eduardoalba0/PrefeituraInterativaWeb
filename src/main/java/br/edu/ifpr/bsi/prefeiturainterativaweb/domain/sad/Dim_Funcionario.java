package br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;

@Entity
@Table(name = "tb_dim_funcionario")
@SuppressWarnings("serial")
public class Dim_Funcionario extends GenericDomain {

	@Column(nullable = false)
	private String nome;

	@Column
	private String cargo;

	public Dim_Funcionario() {

	}

	public Dim_Funcionario(Usuario usuario) {
		this.set_ID(usuario.get_ID());
		this.nome = usuario.getNome();
		this.cargo = usuario.getDadosFuncionais().getCargo();
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

}