package br.edu.ifpr.bsi.prefeiturainterativaweb.domain;

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import com.google.cloud.firestore.annotation.Exclude;

@SuppressWarnings("serial")
public class Usuario implements Serializable {

	public Usuario() {
	}
	
	public Usuario(String _ID) {
		this._ID = _ID;
	}
	
	private String _ID;
	@Length(min = 8, message = "Seu nome está inválido.")
	private String nome;
	@CPF(message = "O CPF informado está inválido.")
	private String cpf;
	@Email(message = "O endereço de E-mail informado está inválido.")
	private String email;
	@NotNull(message = "Informe o tipo do usuário.")
	private String tipoUsuario_ID;

	private String token;
	private String uriFoto;
	private String motivoDesabilitacao;
	private boolean habilitado;
	private DadosFuncionais dadosFuncionais;

	@Exclude
	private URI localUriFoto;
	@Exclude
	private String senha;
	@Exclude
	private TipoUsuario localTipoUsuario;

//---------------------- Encapsulamento ----------------------

	public String get_ID() {
		return _ID;
	}

	public void set_ID(String _ID) {
		this._ID = _ID;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Exclude
	public URI getLocalUriFoto() {
		return localUriFoto;
	}

	public void setLocalUriFoto(URI urlFoto) {
		this.localUriFoto = urlFoto;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Exclude
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getTipoUsuario_ID() {
		return tipoUsuario_ID;
	}

	public void setTipoUsuario_ID(String tipoUsuario_ID) {
		this.tipoUsuario_ID = tipoUsuario_ID;
	}

	public String getUriFoto() {
		return uriFoto;
	}

	public void setUriFoto(String uriFoto) {
		this.uriFoto = uriFoto;
	}

	@Exclude
	public TipoUsuario getLocalTipoUsuario() {
		return localTipoUsuario;
	}

	public void setLocalTipoUsuario(TipoUsuario tipoUsuario) {
		if (tipoUsuario != null)
			this.tipoUsuario_ID = tipoUsuario.get_ID();
		this.localTipoUsuario = tipoUsuario;
	}

	public String getMotivoDesabilitacao() {
		return motivoDesabilitacao;
	}

	public void setMotivoDesabilitacao(String motivoDesabilitacao) {
		this.motivoDesabilitacao = motivoDesabilitacao;
	}

	public boolean isHabilitado() {
		return habilitado;
	}

	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public DadosFuncionais getDadosFuncionais() {
		return dadosFuncionais;
	}

	public void setDadosFuncionais(DadosFuncionais dadosFuncionais) {
		this.dadosFuncionais = dadosFuncionais;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Usuario usuario = (Usuario) o;
		return _ID.equals(usuario._ID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_ID);
	}

	@Nonnull
	@Override
	public String toString() {
		return this._ID;
	}
}
