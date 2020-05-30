package br.edu.ifpr.bsi.prefeiturainterativaweb.model;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
public class TipoUsuario implements Serializable {

    private String _ID;
    private String descricao;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipoUsuario tipoUsuario = (TipoUsuario) o;
        return _ID.equals(tipoUsuario._ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_ID);
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
