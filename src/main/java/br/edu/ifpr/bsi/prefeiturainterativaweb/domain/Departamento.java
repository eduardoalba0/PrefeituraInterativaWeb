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
	private boolean habilitado;
	private boolean staged;
	private List<String> categorias;

	@Exclude
	private List<Categoria> localCategorias;

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

	public List<String> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<String> categorias) {
		this.categorias = categorias;
	}

	public boolean isHabilitado() {
		return habilitado;
	}

	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}

	
	public boolean isStaged() {
		return staged;
	}

	public void setStaged(boolean staged) {
		this.staged = staged;
	}

	@Exclude
	public List<Categoria> getLocalCategorias() {
		return localCategorias;
	}

	public void setLocalCategorias(List<Categoria> localCategorias) {
		this.localCategorias = localCategorias;
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
