package br.edu.ifpr.bsi.prefeiturainterativaweb.helpers;

import java.util.Objects;

public class ChartHolder {
	private String _ID;
	private Number value;
	private String label;

	public String get_ID() {
		return _ID;
	}

	public void set_ID(String _ID) {
		this._ID = _ID;
	}

	public Number getValue() {
		return value;
	}

	public void setValue(Number value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_ID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChartHolder other = (ChartHolder) obj;
		return Objects.equals(_ID, other._ID);
	}

}
