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

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.AtendimentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Atendimento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;

@Named("atendimentoBean")
@ViewScoped
@SuppressWarnings("serial")
public class AtendimentoBean extends AbstractBean {

	private Atendimento atendimento;
	private List<Atendimento> atendimentos;

	@Inject
	@Named("funcionarioLogado")
	private Usuario funcionarioLogado;
	@Inject
	@Named("departamentos")
	private List<Departamento> departamentos;
	@Inject
	@Named("usuarios")
	private List<Usuario> usuarios;
	@Inject
	@Named("solicitacoes")
	private List<Solicitacao> solicitacoes;

	@PostConstruct
	public void init() {
		showStatusDialog();
		if (atendimento == null)
			atendimento = new Atendimento();

		atendimentos = AtendimentoDAO.getAll();

		if (atendimentos == null) {
			hideStatusDialog();
			atendimentos = new ArrayList<Atendimento>();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		}
		hideStatusDialog();
	}

	public void cadastrar() {
		atendimento = new Atendimento();
		atendimento.setLocalFuncionario(funcionarioLogado);
		atendimento.set_ID(UUID.randomUUID().toString());
	}

	public List<Atendimento> listar() {
		atendimentos.forEach((aux) -> {
			if (usuarios != null && !usuarios.isEmpty() && usuarios.contains(new Usuario(aux.getFuncionario_ID()))) {
				aux.setLocalFuncionario(usuarios.get(usuarios.indexOf(new Usuario(aux.getFuncionario_ID()))));
			}
			if (departamentos != null && !departamentos.isEmpty()
					&& departamentos.contains(new Departamento(aux.getDepartamento_ID())))
				aux.setLocalDepartamento(
						departamentos.get(departamentos.indexOf(new Departamento(aux.getDepartamento_ID()))));
			else
				showErrorMessage("Falha ao listar atendimentos. Consulte o suporte da ferramenta.");
			if (solicitacoes != null && !solicitacoes.isEmpty()
					&& solicitacoes.contains(new Solicitacao(aux.getSolicitacao_ID())))
				aux.setLocalSolicitacao(
						solicitacoes.get(solicitacoes.indexOf(new Solicitacao(aux.getSolicitacao_ID()))));
			else
				showErrorMessage("Falha ao listar atendimentos. Consulte o suporte da ferramenta.");
		});
		hideStatusDialog();
		return atendimentos;
	}

	public void selecionar(ActionEvent evento) {
		atendimento = (Atendimento) evento.getComponent().getAttributes().get("atendimentoSelecionado");
	}

	public void salvarEditar() {
		if (AtendimentoDAO.merge(atendimento)) {
			hideStatusDialog();
			showSuccessMessage("Dados gravados na nuvem.");
		} else {
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta.");
		}
	}

	public Usuario getFuncionarioLogado() {
		return funcionarioLogado;
	}

	public void setFuncionarioLogado(Usuario funcionarioLogado) {
		this.funcionarioLogado = funcionarioLogado;
	}

	public Atendimento getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(Atendimento atendimento) {
		this.atendimento = atendimento;
	}

	@Produces
	@Named("atendimentos")
	public List<Atendimento> getAtendimentos() {
		if (atendimentos == null)
			init();
		return atendimentos;
	}

	public void setAtendimentos(List<Atendimento> atendimentos) {
		this.atendimentos = atendimentos;
	}

	public List<Departamento> getDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(List<Departamento> departamentos) {
		this.departamentos = departamentos;
	}

	public List<Solicitacao> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(List<Solicitacao> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}
}
