package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.sad.Fato_DemandasAreaDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Fato_DemandasArea;

@Named("demandasAreaBean")
@SessionScoped
@SuppressWarnings("serial")
public class DemandasAreaBean extends AbstractBean {

	private Fato_DemandasArea demandasArea;
	private List<Fato_DemandasArea> demandasAreaList;
	private Fato_DemandasAreaDAO dao = new Fato_DemandasAreaDAO();

	@Override
	public void init() {
		if (this.dao == null) {
			this.dao = new Fato_DemandasAreaDAO();
		}
		if (demandasArea == null) {
			demandasArea = new Fato_DemandasArea();
		}
		if (demandasAreaList == null) {
			listar();
		}
	}

	@Override
	public List<Fato_DemandasArea> listar() {
		demandasAreaList = new Fato_DemandasAreaDAO().getAll();
		if (demandasAreaList == null) {
			demandasAreaList = new ArrayList<Fato_DemandasArea>();
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		}
		hideStatusDialog();
		return demandasAreaList;
	}

	@Override
	public void cadastrar() {
		demandasArea = new Fato_DemandasArea();
		demandasArea.set_ID(UUID.randomUUID().toString());
	}

	@Override
	public void selecionar(ActionEvent evento) {
		demandasArea = (Fato_DemandasArea) evento.getComponent().getAttributes().get("demandasAreaSelecionado");
	}

	@Override
	public void salvarEditar() {
		if (dao.merge(demandasArea)) {
			demandasArea = new Fato_DemandasArea();
			hideStatusDialog();
			showSuccessMessage("Dados gravados na nuvem.");
		} else {
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta.");
		}
	}

	@Override
	public void removerDesabilitar(ActionEvent evento) {
	}

	public Fato_DemandasArea getFato_DemandasArea() {
		return demandasArea;
	}

	public void setFato_DemandasArea(Fato_DemandasArea tipousuario) {
		this.demandasArea = tipousuario;
	}

	@Produces
	@Named("demandasAreaList")
	public List<Fato_DemandasArea> getTiposUsuario() {
		if (demandasAreaList == null)
			listar();
		return demandasAreaList;
	}

	public void setTiposUsuario(List<Fato_DemandasArea> demandasAreaList) {
		this.demandasAreaList = demandasAreaList;
	}
}
