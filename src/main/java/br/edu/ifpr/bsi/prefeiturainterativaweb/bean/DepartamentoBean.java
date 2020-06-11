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

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.DepartamentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;

@Named("departamentoBean")
@ApplicationScoped
@SuppressWarnings("serial")
public class DepartamentoBean extends AbstractBean {

	private Departamento departamento;

	private List<Departamento> departamentos;

	@Inject
	private List<Categoria> categorias;

	@Override
	@PostConstruct
	public void listar() {
		showStatusDialog();
		if (departamento == null)
			departamento = new Departamento();

		if (departamentos == null)
			departamentos = DepartamentoDAO.getAll();

		if (departamentos == null) {
			hideStatusDialog();
			departamentos = new ArrayList<Departamento>();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		} else {
			hideStatusDialog();
		}
	}

	@Override
	public void cadastrar() {
		departamento = new Departamento();
		departamento.set_ID(UUID.randomUUID().toString());
		departamento.setHabilitado(true);
	}

	public List<Departamento> preencherDepartamentos() {
		showStatusDialog();
		departamentos.forEach((aux) -> {
			List<String> strings = new ArrayList<>();
			aux.getCategorias().forEach((string) -> {
				if (categorias != null && categorias.contains(new Categoria(string))) {
					Categoria categoria = categorias.get(categorias.indexOf(new Categoria(string)));
					strings.add(categoria.getDescricao());
				}
			});
			aux.setNomeCategorias(strings);
		});

		hideStatusDialog();
		return departamentos;

	}

	@Override
	public void selecionar(ActionEvent evento) {
		departamento = (Departamento) evento.getComponent().getAttributes().get("departamentoSelecionado");
	}

	@Override
	public void salvar() {
		showStatusDialog();
		if (DepartamentoDAO.merge(departamento)) {
			hideStatusDialog();
			departamento = new Departamento();
			showSuccessMessage("Dados gravados na nuvem.");
		} else {
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta.");
		}
	}

	@Override
	public void remover(ActionEvent evento) {

	}

	public void desabilitar(ActionEvent evento) {
		departamento = (Departamento) evento.getComponent().getAttributes().get("departamentoSelecionado");
		if (departamento.isHabilitado()) {
			departamento.setHabilitado(false);
		} else {
			departamento.setHabilitado(true);
		}
		if (DepartamentoDAO.merge(departamento)) {
			departamentos.remove(departamento);
			departamentos.add(departamento);
			hideStatusDialog();
			if (departamento.isHabilitado())
				showSuccessMessage("O Departamento foi habilitado com sucesso.");
			else
				showWarningMessage(
						"Enquanto estiver desabilitado, o departamento e suas categorias não aparecerão para os usuários");
		} else {
			hideStatusDialog();
			showErrorMessage("Ocorreu um erro ao desabilitar o departamento. Consulte o suporte da ferramenta.");
		}
		departamento = new Departamento();
	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}

	@Produces
	public List<Departamento> getDepartamentos() {
		if (departamentos == null)
			listar();
		return departamentos;
	}

	public void setDepartamentos(List<Departamento> departamentos) {
		this.departamentos = departamentos;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}
}
