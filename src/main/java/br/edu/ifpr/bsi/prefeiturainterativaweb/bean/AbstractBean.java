package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

@SuppressWarnings("serial")
public class AbstractBean implements Serializable {
	
	public final void redirect(String pagina) {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
		} catch (Exception ex) {
			ex.printStackTrace();
			showErrorMessage("Ocorreu uma falha no redirecionamento da página. Consulte o suporte da ferramenta.");
		}
	}

	public final void redirect(String pagina, String mensagem) {
		try {
			FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
			showSuccessMessage(mensagem);
			FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
		} catch (Exception ex) {
			ex.printStackTrace();
			showErrorMessage("Ocorreu uma falha no redirecionamento da página. Consulte o suporte da ferramenta.");
		}
	}

	public final void showErrorMessage(String mensagem) {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", mensagem));
	}

	public final void showSuccessMessage(String mensagem) {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", mensagem));

	}

	public final void showWarningMessage(String mensagem) {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", mensagem));

	}

	public final void showStatusDialog() {
		PrimeFaces.current().executeScript("PF('statusDialog').show();");
	}

	public final void hideStatusDialog() {
		PrimeFaces.current().executeScript("PF('statusDialog').hide();");
	}

}
