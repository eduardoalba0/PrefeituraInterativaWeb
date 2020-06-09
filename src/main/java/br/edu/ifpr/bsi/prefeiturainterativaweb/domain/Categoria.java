package br.edu.ifpr.bsi.prefeiturainterativaweb.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.google.cloud.firestore.annotation.Exclude;

@SuppressWarnings("serial")
public class Categoria implements Serializable {

	public Categoria() {

	}

	public Categoria(String _ID) {
		this._ID = _ID;
	}

	private String _ID;
	private String descricao;
	private String departamento_ID;
	private boolean habilitada;

	@Exclude
	private boolean selecionada;

	@Exclude
	private String nomeDepartamento;

//---------------------- Encapsulamento ----------------------

	public String get_ID() {
		return _ID;
	}

	public void set_ID(String _ID) {
		this._ID = _ID;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDepartamento_ID() {
		return departamento_ID;
	}

	public void setDepartamento_ID(String departamento_ID) {
		this.departamento_ID = departamento_ID;
	}

	public boolean isHabilitada() {
		return habilitada;
	}

	public void setHabilitada(boolean habilitada) {
		this.habilitada = habilitada;
	}

	@Exclude
	public String getNomeDepartamento() {
		return nomeDepartamento;
	}

	public void setNomeDepartamento(String nomeDepartamento) {
		this.nomeDepartamento = nomeDepartamento;
	}

	@Exclude
	public boolean isSelecionada() {
		return selecionada;
	}

	public void setSelecionada(boolean selecionada) {
		this.selecionada = selecionada;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Categoria categoria = (Categoria) o;
		return _ID.equals(categoria._ID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_ID);
	}

	@Nonnull
	@Override
	public String toString() {
		return this.descricao;
	}
}
