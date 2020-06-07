package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.CategoriaDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;

@Named("categoriaBean")
@ApplicationScoped
@SuppressWarnings("serial")
public class CategoriaBean extends AbstractBean {

	private Categoria categoria;

	@Produces
	@Named("categorias")
	private List<Categoria> categorias;

	@Inject
	@Named("departamentos")
	private List<Departamento> departamentos;

	@Override
	@PostConstruct
	public void init() {
		showStatusDialog();
		if (categoria == null) {
			categoria = new Categoria();
		}

		categorias = CategoriaDAO.getAll();
		if (categorias == null) {
			hideStatusDialog();
			categorias = new ArrayList<Categoria>();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta."));
		} else {
			categorias.forEach((aux) -> {
				aux.setLocalDepartamento(
						departamentos.get(departamentos.indexOf(new Departamento(categoria.getDepartamento_ID()))));
			});
			hideStatusDialog();
		}

	}

	@Override
	public void selecionar(ActionEvent evento) {
		categoria = (Categoria) evento.getComponent().getAttributes().get("categoriaSelecionado");
	}

	@Override
	public void salvar() {
		showStatusDialog();
		if (CategoriaDAO.merge(categoria)) {
			hideStatusDialog();
			categoria = new Categoria();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Dados gravados na nuvem."));
		} else {
			hideStatusDialog();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta."));
		}
	}

	@Override
	public void remover() {

	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}
}
