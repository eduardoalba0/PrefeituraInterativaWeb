package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.UsuarioDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Funcionario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.FirebaseHelper;

@Named("loginBean")
@SessionScoped
@SuppressWarnings("serial")
public class LoginBean implements Serializable {

	private Usuario usuario;
	private Funcionario funcionarioLogado;

	@Inject
	public void init() {
		usuario = new Usuario();

	}

	public void logar() {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Autenticando dados...", "Por favor, aguarde."));
		try {
			String usuarioId = FirebaseHelper.logar(usuario);
			if (usuarioId != null && !usuarioId.isEmpty()) {
				funcionarioLogado = UsuarioDAO.get(usuarioId).get().toObject(Funcionario.class);
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Bem vindo! ", funcionarioLogado.getNome()));
				context.getExternalContext().getFlash().setKeepMessages(true);
				context.getExternalContext().redirect("/PrefeituraInterativa/view/solicitacoes.xhtml");
			} else
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Dados inválidos!",
						"verifique seu e-mail e/ou senha"));
		} catch (Exception ex) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Dados inválidos!",
					"verifique seu e-mail e/ou senha"));
		} finally {
			usuario = new Usuario();
		}
	}

	public void redefinirSenha() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			if (FirebaseHelper.redefinirSenha(usuario)) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso! ",
						"Clique no link enviado para seu e-mail para redefinir sua senha."));
				context.getExternalContext().getFlash().setKeepMessages(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "E-mail não encontrado!",
					"verifique se digitou corretamente seu e-mail."));
		}

	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setFuncionarioLogado(Funcionario funcionarioLogado) {
		this.funcionarioLogado = funcionarioLogado;
	}

	public Funcionario getFuncionarioLogado() {
		return funcionarioLogado;
	}

}
