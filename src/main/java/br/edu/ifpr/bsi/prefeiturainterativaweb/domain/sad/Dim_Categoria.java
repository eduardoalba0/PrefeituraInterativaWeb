package br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;

@Entity
@Table(name = "tb_dim_categoria")
@SuppressWarnings("serial")
public class Dim_Categoria extends GenericDomain {

	@Column(nullable = false)
	private String descricao;

	public Dim_Categoria() {

	}

	public Dim_Categoria(Categoria categoria) {
		this.set_ID(categoria.get_ID());
		this.descricao = categoria.getDescricao();
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}