package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.AtendimentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.SolicitacaoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Atendimento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Aviso;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;

@Named("atendimentoBean")
@ViewScoped
@SuppressWarnings("serial")
public class AtendimentoBean extends AbstractBean {

	private Atendimento atendimento;
	private Aviso aviso;
	private Solicitacao solicitacao;
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

	@Override
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

	@Override
	public void cadastrar() {
		atendimento = new Atendimento();
		atendimento.setLocalFuncionario(funcionarioLogado);
	}

	@Override
	public List<Atendimento> listar() {
		atendimentos.forEach((aux) -> {
			if (usuarios != null && !usuarios.isEmpty() && usuarios.contains(new Usuario(aux.getFuncionario_ID()))) {
				aux.setLocalFuncionario(usuarios.get(usuarios.indexOf(new Usuario(aux.getFuncionario_ID()))));
			}
			if (departamentos != null && !departamentos.isEmpty()
					&& departamentos.contains(new Departamento(aux.getDepartamento_ID())))
				aux.setLocalDepartamento(
						departamentos.get(departamentos.indexOf(new Departamento(aux.getDepartamento_ID()))));
		});
		hideStatusDialog();
		return atendimentos;
	}

	@Override
	public void selecionar(ActionEvent evento) {
		atendimento = (Atendimento) evento.getComponent().getAttributes().get("atendimentoSelecionado");
	}

	public void responder(ActionEvent evento) {
		solicitacao = (Solicitacao) evento.getComponent().getAttributes().get("solicitacaoSelecionada");
		atendimento = new Atendimento();
		aviso = new Aviso();
		atendimento.setLocalSolicitacao(solicitacao);
		aviso.setTitulo("Sua demanda foi respondida!");
		aviso.setCategoria(Aviso.CATEGORIA_TRAMITACAO);
	}

	@Override
	public void salvarEditar() {
		boolean tasksSuccess = false;
		if (atendimento.get_ID() == null) {
			atendimento.set_ID(UUID.randomUUID().toString());
			List<String> stringList;
			if (solicitacao.getAtendimentos() == null)
				stringList = new ArrayList<String>();
			else
				stringList = solicitacao.getAtendimentos();
			stringList.add(atendimento.get_ID());
			solicitacao.setAtendimentos(stringList);
			atendimento.setFuncionario_ID(funcionarioLogado.get_ID());
			atendimento.setDepartamento_ID(funcionarioLogado.getDadosFuncionais().getDepartamento_ID());
			tasksSuccess = AtendimentoDAO.merge(atendimento) && SolicitacaoDAO.merge(solicitacao);
		} else
			tasksSuccess = AtendimentoDAO.merge(atendimento);
		if (tasksSuccess) {
			aviso.setCorpo(atendimento.getResposta());
			aviso.setSolicitacao_ID(solicitacao.get_ID());
			aviso.setData(new Date());
			aviso.setToken(solicitacao.getLocalCidadao().getToken());
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

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public Solicitacao getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Solicitacao solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Aviso getAviso() {
		return aviso;
	}

	public void setAviso(Aviso aviso) {
		this.aviso = aviso;
	}
}
