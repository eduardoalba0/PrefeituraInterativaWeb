package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.TipoUsuarioDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.TipoUsuario;

@Named("tipousuarioBean")
@ApplicationScoped
@SuppressWarnings("serial")
public class TipoUsuarioBean implements Serializable {

	private TipoUsuario tipousuario;

	@Produces
	@Named("tiposusuario")
	private List<TipoUsuario> tiposUsuario;

	@PostConstruct
	public void init() {
		if (tipousuario == null) {
			tipousuario = new TipoUsuario();
		}

		tiposUsuario = TipoUsuarioDAO.getAll();
		if (tiposUsuario == null) {
			tiposUsuario = new ArrayList<TipoUsuario>();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta."));
		}
	}

	public void selecionar(ActionEvent evento) {
		tipousuario = (TipoUsuario) evento.getComponent().getAttributes().get("tipoUsuarioSelecionado");
	}

	public void salvar() {
		if (TipoUsuarioDAO.merge(tipousuario)) {
			tipousuario = new TipoUsuario();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Dados gravados na nuvem."));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta."));
		}
	}

	public void remover() {
		
	}

	public TipoUsuario getTipoUsuario() {
		return tipousuario;
	}

	public void setTipoUsuario(TipoUsuario tipousuario) {
		this.tipousuario = tipousuario;
	}

	public List<TipoUsuario> getTiposUsuario() {
		return tiposUsuario;
	}

	public void setTiposUsuario(List<TipoUsuario> tiposUsuario) {
		this.tiposUsuario = tiposUsuario;
	}
}