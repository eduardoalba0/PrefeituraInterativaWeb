package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.auth.UserRecord;
import com.google.gson.Gson;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.UsuarioDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Funcionario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.FirebaseHelper;

@Named("loginBean")
@SessionScoped
@SuppressWarnings("serial")
public class LoginBean implements Serializable {

	@Produces
	@Named("#{funcionarioLogado}")
	private Funcionario funcionarioLogado;

	private Usuario usuario;

	@PostConstruct
	public void init() {
		if (usuario == null)
			usuario = new Usuario();
		if (funcionarioLogado != null)
			redirect("/web/view/solicitacoes.xhtml", null);

	}

	public void autenticarLogin() {
		String uid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("uid");
		UserRecord objJson = new Gson().fromJson(uid, UserRecord.class);
		if (FirebaseHelper.userEquals(objJson, FirebaseHelper.buscarUsuario(objJson))) {
			ApiFuture<DocumentSnapshot> task = UsuarioDAO.get(objJson.getUid());
			try {
				funcionarioLogado = task.get().toObject(Funcionario.class);
				if (funcionarioLogado == null) {
					redirect("/web/view/index.xhtml", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
							"Seu usuário não está cadastrado. Consulte o suporte da ferramenta."));
				} else if (funcionarioLogado.getTipoUsuario_ID().equals("6b395be8-a7c1-4971-8dc0-afa04be63a00")) {
					funcionarioLogado = null;
					redirect("/web/view/index.xhtml", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
							"Seu usuário não possuí privilégios para acessar a plataforma. Consulte o suporte da ferramenta."));
				} else {
					redirect("/web/view/solicitacoes.xhtml", new FacesMessage(FacesMessage.SEVERITY_INFO, "Bem vindo, ",
							funcionarioLogado.getNome() + "! "));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				funcionarioLogado = null;
				redirect("/web/view/solicitacoes.xhtml", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
						"Falha ao autenticar dados. Consulte o suporte da ferramenta."));
			}
		}
	}

	public void deslogar() {
		funcionarioLogado = null;
		redirect("/web/view/index.xhtml", new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!",
				"Obrigado por utilizar nossos serviços."));
	}

	private void redirect(String pagina, FacesMessage mensagem) {
		try {
			if (mensagem != null) {
				FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
				FacesContext.getCurrentInstance().addMessage(null, mensagem);
			}
			FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
		} catch (Exception ex) {
			ex.printStackTrace();
			funcionarioLogado = null;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao autenticar seus dados. Consulte o suporte da ferramenta."));
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
