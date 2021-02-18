package br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "tb_dim_tempo")
@SuppressWarnings("serial")
public class Dim_Tempo extends GenericDomain {

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	public Dim_Tempo() {

	}

	public Dim_Tempo(Date date) {
		this.set_ID(UUID.randomUUID().toString());
		this.setDate(date);
	}


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}