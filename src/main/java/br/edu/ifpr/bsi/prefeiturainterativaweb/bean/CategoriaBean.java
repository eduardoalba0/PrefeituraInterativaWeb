package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.CategoriaDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.DepartamentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;

@Named("categoriaBean")
@ApplicationScoped
@SuppressWarnings("serial")
public class CategoriaBean extends AbstractBean {

	private Categoria categoria;

	private List<Categoria> categorias;

	@Inject
	private List<Departamento> departamentos;

	@Override
	@PostConstruct
	public void listar() {
		showStatusDialog();
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

	public List<Categoria> preencherCategorias() {
		showStatusDialog();
		categorias.forEach((aux) -> {
			Departamento departamento = departamentos
					.get(departamentos.indexOf(new Departamento(aux.getDepartamento_ID())));
			if (departamento != null)
				aux.setNomeDepartamento(departamento.getDescricao());
		});
		hideStatusDialog();
		return categorias;
	}

	@Override
	public void cadastrar() {
		categoria = new Categoria();
		categoria.set_ID(UUID.randomUUID().toString());
		categoria.setHabilitada(true);
	}

	@Override
	public void selecionar(ActionEvent evento) {
		categoria = (Categoria) evento.getComponent().getAttributes().get("categoriaSelecionada");
	}

	@Override
	public void salvar() {
		boolean tasksSuccess = CategoriaDAO.merge(categoria);

		if (tasksSuccess) {
			Departamento departamento = DepartamentoDAO.get(categoria.getDepartamento_ID());
			List<String> lista = departamento.getCategorias();

			if (categorias.contains(categoria))
				categorias.remove(categoria);

			if (lista == null)
				lista = new ArrayList<String>();

			if (!lista.contains(categoria.get_ID())) {
				lista.add(categoria.get_ID());
				departamento.setCategorias(lista);
				tasksSuccess = DepartamentoDAO.merge(departamento);
				departamentos.remove(departamento);
				departamentos.add(departamento);
			}
		}
		if (tasksSuccess) {
			categorias.add(categoria);
			categoria = new Categoria();
			hideStatusDialog();
			showSuccessMessage("Dados gravados na nuvem.");
		} else {
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta.");
		}
	}

	@Override
	public void remover(ActionEvent evento) {
		categoria = (Categoria) evento.getComponent().getAttributes().get("categoriaSelecionada");
		if (CategoriaDAO.isAssociada(categoria.get_ID())) {
			showErrorMessage(
					"Antes de removê-la, por favor, desassocie esta categoria de todos as solicitações e departamentos que a possuem.");
		} else if (CategoriaDAO.remove(categoria)) {
			categorias.remove(categoria);
			hideStatusDialog();
			showSuccessMessage("A categoria foi removida.");
		} else {
			hideStatusDialog();
			showErrorMessage("Ocorreu um erro ao remover a categoria. Consulte o suporte da ferramenta.");
		}

	}

	public void desabilitar(ActionEvent evento) {
		categoria = (Categoria) evento.getComponent().getAttributes().get("categoriaSelecionada");
		if (categoria.isHabilitada()) {
			categoria.setHabilitada(false);
		} else {
			categoria.setHabilitada(true);
		}
		if (CategoriaDAO.merge(categoria)) {
			categorias.remove(categoria);
			categorias.add(categoria);
			hideStatusDialog();
			if (categoria.isHabilitada())
				showSuccessMessage("A categoria foi habilitada com sucesso.");
			else
				showWarningMessage("Enquanto estiver desabilitada, a categoria não aparecerá para os usuários");
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
	public List<Categoria> getCategorias() {
		if (categorias == null)
			listar();
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
