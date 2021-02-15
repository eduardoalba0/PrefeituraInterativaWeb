package br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad;

import java.util.Calendar;
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
	private int ano;

	@Column(nullable = false)
	private int trimestre;

	@Column(nullable = false)
	private int semestre;

	@Column(nullable = false)
	private int bimestre;

	@Column(nullable = false)
	private int mes;

	@Column(nullable = false)
	private int dia;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	public Dim_Tempo() {

	}

	public Dim_Tempo(Date date) {
		this.set_ID(UUID.randomUUID().toString());
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		this.ano = cal.get(Calendar.YEAR);
		this.trimestre = (int) Math.floor((cal.get(Calendar.MONTH) + 2) / 3);
		this.bimestre = (int) Math.floor((cal.get(Calendar.MONTH) + 1) / 2);
		this.mes = cal.get(Calendar.MONTH);
		this.dia = cal.get(Calendar.DAY_OF_MONTH);
		this.setDate(date);
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getTrimestre() {
		return trimestre;
	}

	public void setTrimestre(int trimestre) {
		this.trimestre = trimestre;
	}

	public int getSemestre() {
		return semestre;
	}

	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}

	public int getBimestre() {
		return bimestre;
	}

	public void setBimestre(int bimestre) {
		this.bimestre = bimestre;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getDia() {
		return dia;
	}

	public void setDia(int dia) {
		this.dia = dia;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}