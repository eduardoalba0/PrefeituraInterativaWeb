package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.io.Serializable;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.PrimeFaces;

import com.google.common.util.concurrent.MoreExecutors;

@SuppressWarnings("serial")
public abstract class AbstractBean implements Serializable {

	@PostConstruct
	public abstract void init();

	public abstract void selecionar(ActionEvent event);

	public abstract void salvar();

	public abstract void remover();

	public final void redirect(String pagina) {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha no redirecionamento da página. Consulte o suporte da ferramenta."));
		}
	}

	public final void redirect(String pagina, FacesMessage mensagem) {
		try {
			FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
			FacesContext.getCurrentInstance().addMessage(null, mensagem);
			FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha no redirecionamento da página. Consulte o suporte da ferramenta."));
		}
	}

	public final void showStatusDialog() {
		PrimeFaces.current().executeScript("PF('statusDialog').show();");
	}

	public final void hideStatusDialog() {
		PrimeFaces.current().executeScript("PF('statusDialog').hide();");
	}

	public final Executor getExecutor() {
		return MoreExecutors.directExecutor();
	}

}
