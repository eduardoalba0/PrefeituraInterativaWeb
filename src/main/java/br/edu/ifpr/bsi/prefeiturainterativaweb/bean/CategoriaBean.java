package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.CategoriaDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.DepartamentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.sad.Dim_CategoriaDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Dim_Categoria;

@Named("categoriaBean")
@ViewScoped
@SuppressWarnings("serial")
public class CategoriaBean extends AbstractBean {

	private Categoria categoria;
	private List<Categoria> categorias;

	@Inject
	@Named("departamentos")
	private List<Departamento> departamentos;

	@PostConstruct
	public void init() {
		if (categoria == null)
			categoria = new Categoria();

		if (categorias == null)
			categorias = CategoriaDAO.getAll();

		if (categorias == null) {
			hideStatusDialog();
			categorias = new ArrayList<Categoria>();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		} else {
			hideStatusDialog();
		}
	}

	public List<Categoria> listar() {
		categorias.forEach((aux) -> {
			aux.setLocalDepartamento(
					departamentos.get(departamentos.indexOf(new Departamento(aux.getDepartamento_ID()))));
		});
		hideStatusDialog();
		return categorias;
	}

	public void cadastrar() {
		categoria = new Categoria();
		categoria.set_ID(UUID.randomUUID().toString());
		categoria.setHabilitada(true);
	}

	public void selecionar(ActionEvent evento) {
		categoria = (Categoria) evento.getComponent().getAttributes().get("categoriaSelecionada");
	}

	public void salvarEditar() {
		boolean tasksSuccess = CategoriaDAO.merge(categoria)
				&& new Dim_CategoriaDAO().merge(new Dim_Categoria(categoria));
		Departamento departamento = null;

		if (tasksSuccess) {
			departamento = DepartamentoDAO.get(categoria.getDepartamento_ID());
			tasksSuccess = departamento != null;
		}

		if (tasksSuccess) {
			List<String> listaAdicionar = departamento.getCategorias();
			if (listaAdicionar == null)
				listaAdicionar = new ArrayList<String>();
			if (!listaAdicionar.contains(categoria.get_ID())) {
				listaAdicionar.add(categoria.get_ID());
				departamento.setCategorias(listaAdicionar);
				tasksSuccess = DepartamentoDAO.merge(departamento);
			}
		}

		if (tasksSuccess) {
			departamento = categoria.getLocalDepartamento();
			if (departamento != null && departamento.getCategorias() != null) {
				List<String> listaRemover = departamento.getCategorias();
				if (listaRemover.contains(categoria.get_ID())) {
					listaRemover.remove(categoria.get_ID());
					departamento.setCategorias(listaRemover);
					departamentos.remove(departamento);
					departamentos.add(departamento);
					tasksSuccess = DepartamentoDAO.merge(categoria.getLocalDepartamento());
				}
			}
		}
		if (tasksSuccess) {
			categoria = new Categoria();
			hideStatusDialog();
			showSuccessMessage("Dados gravados na nuvem.");
		} else {
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta.");
		}
	}

	public void removerDesabilitar(ActionEvent evento) {
		categoria = (Categoria) evento.getComponent().getAttributes().get("categoriaSelecionada");
		if (categoria.isHabilitada()) {
			categoria.setHabilitada(false);
		} else {
			categoria.setHabilitada(true);
		}
		if (CategoriaDAO.merge(categoria)) {
			hideStatusDialog();
			if (categoria.isHabilitada())
				showSuccessMessage("A categoria foi habilitada com sucesso.");
			else
				showWarningMessage("Enquanto estiver desabilitada, a categoria não aparecerá para os usuários."
						+ "Os dados existentes não serão alterados e as solicitações que já foram salvas OFFLINE ainda poderão apresentar esta categoria temporariamente.");
		} else {
			hideStatusDialog();
			showErrorMessage("Ocorreu um erro ao desabilitar a categoria. Consulte o suporte da ferramenta.");
		}
		categoria = new Categoria();
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	@Produces
	@Named("categorias")
	public List<Categoria> getCategorias() {
		if (categorias == null)
			init();
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}

	public List<Departamento> getDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(List<Departamento> departamentos) {
		this.departamentos = departamentos;
	}
}
