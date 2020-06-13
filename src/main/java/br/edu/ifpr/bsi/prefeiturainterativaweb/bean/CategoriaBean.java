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
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;

@Named("categoriaBean")
@ViewScoped
@SuppressWarnings("serial")
public class CategoriaBean extends AbstractBean {

	private Categoria categoria;
	private List<Categoria> categorias;

	@Inject
	@Named("departamentos")
	private List<Departamento> departamentos;

	@Override
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
	
	@Override
	public List<Categoria> listar() {
		categorias.forEach((aux) -> {
			aux.setLocalDepartamento(
					departamentos.get(departamentos.indexOf(new Departamento(aux.getDepartamento_ID()))));
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
	public void salvarEditar() {
		boolean tasksSuccess = CategoriaDAO.merge(categoria);
		Departamento departamento = null;

		// Se a operação foi concluida com êxito, continua.
		if (tasksSuccess) {
			departamento = DepartamentoDAO.get(categoria.getDepartamento_ID());
			tasksSuccess = departamento != null;
		}

		// Se a operação foi concluida com êxito, continua.
		if (tasksSuccess) {
			List<String> listaAdicionar = departamento.getCategorias();
			// Caso o departamento não possua nenhuma, precisa ser criada uma lista.
			if (listaAdicionar == null)
				listaAdicionar = new ArrayList<String>();

			// Caso seja uma categoria nova, ela precisa ser associada à um departamento.
			if (!listaAdicionar.contains(categoria.get_ID())) {
				listaAdicionar.add(categoria.get_ID());
				departamento.setCategorias(listaAdicionar);
				tasksSuccess = DepartamentoDAO.merge(departamento);
			}
		}

		// Se a operação foi concluida com êxito, continua.
		if (tasksSuccess) {
			// Caso o departamento for alterado, ela precisa ser desassociada
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
		// Se a operação foi concluida com êxito, mostra mensagem de sucesso.
		if (tasksSuccess) {
			categoria = new Categoria();
			hideStatusDialog();
			showSuccessMessage("Dados gravados na nuvem.");
			// Se ocorreu erro em qualquer etapa, mostra mensagem de erro.
		} else {
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta.");
		}
	}

	@Override
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
