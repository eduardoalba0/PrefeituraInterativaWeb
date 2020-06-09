package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;

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

	@Produces
	@Named("departamentos")
	private List<Departamento> departamentos;

	@Inject
	@Named("categorias")
	private List<Categoria> categorias;

	@Override
	@PostConstruct
	public void init() {
		showStatusDialog();
		if (departamento == null)
			departamento = new Departamento();

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
	}
	public List<Departamento> preencherDepartamentos() {
		showStatusDialog();
		departamentos.forEach((aux) -> {
			List<String> strings = new ArrayList<>();
			aux.getCategorias().forEach((string) -> {
				Categoria categoria = categorias.get(categorias.indexOf(new Categoria(string)));
				strings.add(categoria.getDescricao());
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

	public void desabilitar() {

	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}

	public List<Departamento> getDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(List<Departamento> departamentos) {
		this.departamentos = departamentos;
	}
}
