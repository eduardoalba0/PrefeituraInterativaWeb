package br.edu.ifpr.bsi.prefeiturainterativaweb.domain;

import java.io.Serializable;

import javax.validation.constraints.Digits;

import com.google.cloud.firestore.annotation.Exclude;

@SuppressWarnings("serial")
public class DadosFuncionais implements Serializable {
	
	@Digits(integer = 10, fraction = 0, message = "São aceitos apenas números na matrícula.")
	private String matricula;
	private String cargo;
	private String departamento_ID;

	@Exclude
	private Departamento departamento;

//---------------------- Encapsulamento ----------------------

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getDepartamento_ID() {
		return departamento_ID;
	}

	public void setDepartamento_ID(String departamento_ID) {
		this.departamento_ID = departamento_ID;
	}

	@Exclude
	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}

}
