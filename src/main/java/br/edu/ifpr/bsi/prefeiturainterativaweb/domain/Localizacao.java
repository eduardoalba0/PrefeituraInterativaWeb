package br.edu.ifpr.bsi.prefeiturainterativaweb.domain;

import java.io.Serializable;

import com.google.type.LatLng;

@SuppressWarnings("serial")
public class Localizacao implements Serializable {

	private double latitude;
	private double longitude;

	public Localizacao() {
	}

	public Localizacao(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Localizacao(LatLng latLng) {
		this.latitude = latLng.getLatitude();
		this.longitude = latLng.getLongitude();
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return this.latitude + "," + this.longitude;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Localizacao other = (Localizacao) obj;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		return true;
	}
}
