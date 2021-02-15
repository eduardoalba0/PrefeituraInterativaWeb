package br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;

@Entity
@Table(name = "tb_dim_departamento")
@SuppressWarnings("serial")
public class Dim_Departamento extends GenericDomain {

	@Column(nullable = false)
	private String descricao;

	public Dim_Departamento() {

	}

	public Dim_Departamento(Departamento departamento) {
		this.set_ID(departamento.get_ID());
		this.descricao = departamento.getDescricao();
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}