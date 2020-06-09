package br.edu.ifpr.bsi.prefeiturainterativaweb.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.google.cloud.firestore.annotation.Exclude;

@SuppressWarnings("serial")
public class Departamento implements Serializable {

	public Departamento() {
	}

	public Departamento(String _ID) {
		this._ID = _ID;
	}

	private String _ID;
	private String descricao;
	private String departamentoSuperior_ID;
	private List<String> categorias;

	@Exclude
	private Departamento departamentoSuperior;

	@Exclude
	private List<String> nomeCategorias;

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

	public String getDepartamentoSuperior_ID() {
		return departamentoSuperior_ID;
	}

	public void setDepartamentoSuperior_ID(String departamentoSuperior_ID) {
		this.departamentoSuperior_ID = departamentoSuperior_ID;
	}

	public List<String> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<String> categorias) {
		this.categorias = categorias;
	}

	@Exclude
	public Departamento getDepartamentoSuperior() {
		return departamentoSuperior;
	}

	public void setDepartamentoSuperior(Departamento departamentoSuperior) {
		this.departamentoSuperior = departamentoSuperior;
	}

	@Exclude
	public List<String> getNomeCategorias() {
		return nomeCategorias;
	}

	public void setNomeCategorias(List<String> nomeCategorias) {
		this.nomeCategorias = nomeCategorias;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Departamento departamento = (Departamento) o;
		return _ID.equals(departamento._ID);
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
