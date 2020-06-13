package br.edu.ifpr.bsi.prefeiturainterativaweb.domain;

import javax.validation.constraints.Digits;

import com.google.cloud.firestore.annotation.Exclude;

@SuppressWarnings("serial")
public class Funcionario extends Usuario {

	public Funcionario() {
	}

	public Funcionario(Usuario usuario) {
		this.set_ID(usuario.get_ID());
		this.setNome(usuario.getNome());
		this.setCpf(usuario.getCpf());
		this.setHabilitado(usuario.isHabilitado());
		this.setEmail(usuario.getEmail());
		this.setSenha(usuario.getSenha());
		this.setUriFoto(usuario.getUriFoto());
		this.setToken(usuario.getToken());
		this.setTipoUsuario_ID(usuario.getTipoUsuario_ID());
		this.setMotivoDesabilitacao(usuario.getMotivoDesabilitacao());
		this.setLocalTipoUsuario(usuario.getLocalTipoUsuario());
		this.setLocalUriFoto(usuario.getLocalUriFoto());
	}
	
	@Digits(integer = 10, fraction = 0, message = "São aceitos apenas números inteiros na matrícula.")
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
