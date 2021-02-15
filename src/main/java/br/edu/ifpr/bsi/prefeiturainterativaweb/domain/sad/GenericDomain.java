package br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@SuppressWarnings("serial")
@MappedSuperclass
public class GenericDomain implements Serializable {

	@Id
	private String _ID;

	public String get_ID() {
		return _ID;
	}

	public void set_ID(String _ID) {
		this._ID = _ID;
	}

	@Override
	public String toString() {
		return String.format("%s[id=%s]", getClass().getSimpleName(), get_ID());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_ID == null) ? 0 : _ID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenericDomain other = (GenericDomain) obj;
		if (_ID == null) {
			if (other._ID != null)
				return false;
		} else if (!_ID.equals(other._ID))
			return false;
		return true;
	}

}
