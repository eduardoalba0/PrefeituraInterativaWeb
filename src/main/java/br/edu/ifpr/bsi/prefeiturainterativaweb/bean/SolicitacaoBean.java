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
import org.primefaces.PrimeFaces;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.ReverseGeocodeEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.google.api.client.testing.util.TestableByteArrayInputStream;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.AtendimentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.SolicitacaoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Atendimento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Aviso;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.TipoUsuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.FirebaseHelper;

@Named("solicitacaoBean")
@ViewScoped
@SuppressWarnings("serial")
public class SolicitacaoBean extends AbstractBean {

	private Atendimento atendimento;
	private Solicitacao solicitacao;
	private List<Aviso> avisos;
	private List<Solicitacao> solicitacoes;
	private List<Categoria> tempCategorias;

	private MapModel mapModel;
	private String center;
	private Marker marker;

	@Inject
	@Named("funcionarioLogado")
	private Usuario funcionarioLogado;

	@Inject
	@Named("categorias")
	private List<Categoria> categorias;

	@Inject
	@Named("usuarios")
	private List<Usuario> usuarios;
	
	@Inject
	@Named("funcionarios")
	private List<Usuario> funcionarios;

	@Inject
	@Named("atendimentos")
	private List<Atendimento> atendimentos;

	@Inject
	@Named("departamentos")
	private List<Departamento> departamentos;

	@Override
	@PostConstruct
	public void init() {
		if (solicitacao == null)
			solicitacao = new Solicitacao();

		if (mapModel == null)
			mapModel = new DefaultMapModel();
//TODO FILTRAR SOLICITAÇÕES POR DEPARTAMENTO
		solicitacoes = SolicitacaoDAO.getAll();
		if (solicitacoes == null) {
			hideStatusDialog();
			solicitacoes = new ArrayList<>();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		}
		hideStatusDialog();
	}

	@Override
	public void cadastrar() {
		solicitacao = new Solicitacao();
		atendimento = new Atendimento();
		avisos = new ArrayList<Aviso>();
		tempCategorias = new ArrayList<Categoria>();
		solicitacao.set_ID(UUID.randomUUID().toString());
	}

	@Override
	public List<Solicitacao> listar() {
		solicitacoes.forEach((aux) -> {
			List<Categoria> localCategorias = new ArrayList<>();
			List<Atendimento> localAtendimentos = new ArrayList<>();
			aux.getCategorias().forEach((string) -> {
				if (categorias != null && categorias.contains(new Categoria(string)))
					localCategorias.add(categorias.get(categorias.indexOf(new Categoria(string))));
			});
			if (aux.getAtendimentos() != null)
				aux.getAtendimentos().forEach((string) -> {
					if (atendimentos != null && atendimentos.contains(new Atendimento(string))) {
						Atendimento obj = atendimentos.get(atendimentos.indexOf(new Atendimento(string)));
						if (obj != null) {
							obj.setLocalDepartamento(departamentos
									.get(departamentos.indexOf(new Departamento(obj.getDepartamento_ID()))));
							obj.setLocalFuncionario(
									usuarios.get(usuarios.indexOf(new Usuario(obj.getFuncionario_ID()))));
							localAtendimentos.add(obj);
						}
					}
				});
			aux.setLocalCategorias(localCategorias);
			aux.setLocalAtendimentos(localAtendimentos);
			aux.setLocalCidadao(usuarios.get(usuarios.indexOf(new Usuario(aux.getUsuario_ID()))));
			if (aux.getFuncionarioConclusao_ID() != null)
				aux.setLocalFuncionarioConclusao(
						usuarios.get(funcionarios.indexOf(new Usuario(aux.getFuncionarioConclusao_ID()))));
			aux.setLocalDepartamento(
					departamentos.get(departamentos.indexOf(new Departamento(aux.getDepartamento_ID()))));
		});

		hideStatusDialog();
		return solicitacoes;
	}

	@Override
	public void selecionar(ActionEvent evento) {
		solicitacao = (Solicitacao) evento.getComponent().getAttributes().get("solicitacaoSelecionada");
		atendimento = new Atendimento();
		if (solicitacao.getLocalCategorias() == null) {
			listar();
			solicitacao = solicitacoes.get(solicitacoes.indexOf(new Solicitacao(solicitacao.get_ID())));
		}
		tempCategorias = solicitacao.getLocalCategorias();
		double lat = solicitacao.getLocalizacao().getLatitude();
		double lon = solicitacao.getLocalizacao().getLongitude();
		PrimeFaces.current().executeScript("PF('map').reverseGeocode('" + lat + "','" + lon + "');");
		hideStatusDialog();
	}

	@Override
	public void salvarEditar() {
		boolean tasksSuccess = false;
		if (tempCategorias != null && !tempCategorias.isEmpty()
				&& !tempCategorias.equals(solicitacao.getLocalCategorias())) {
			List<String> ids = new ArrayList<String>();
			tempCategorias.forEach(categoria -> {
				ids.add(categoria.get_ID());
			});
			solicitacao.setCategorias(ids);
		}

		if (atendimento != null && atendimento.get_ID() != null)
			tasksSuccess = AtendimentoDAO.merge(atendimento) && SolicitacaoDAO.merge(solicitacao);
		else
			tasksSuccess = SolicitacaoDAO.merge(solicitacao);

		if (tasksSuccess) {
			if (avisos != null && !avisos.isEmpty())
				FirebaseHelper.enviarNotificacao(avisos);
			solicitacao = new Solicitacao();
			atendimento = new Atendimento();
			avisos = new ArrayList<Aviso>();
			listar();
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

	public void salvarCategorias() {
		List<String> ids = new ArrayList<String>();
		tempCategorias.forEach(categoria -> {
			ids.add(categoria.get_ID());
		});
	}

	public void salvarResposta() {
		avisos = new ArrayList<Aviso>();
		atendimento.set_ID(UUID.randomUUID().toString());
		atendimento.setLocalSolicitacao(solicitacao);
		List<String> stringList;
		List<Atendimento> objList;
		if (solicitacao.getAtendimentos() == null)
			stringList = new ArrayList<String>();
		else
			stringList = solicitacao.getAtendimentos();

		stringList.add(atendimento.get_ID());
		solicitacao.setAtendimentos(stringList);
		atendimento.setData(new Date());
		atendimento.setFuncionario_ID(funcionarioLogado.get_ID());
		atendimento.setLocalFuncionario(funcionarioLogado);
		atendimento.setDepartamento_ID(funcionarioLogado.getDadosFuncionais().getDepartamento_ID());
		atendimento.setLocalDepartamento(funcionarioLogado.getDadosFuncionais().getLocalDepartamento());
		if (solicitacao.getLocalAtendimentos() == null)
			objList = new ArrayList<Atendimento>();
		else
			objList = solicitacao.getLocalAtendimentos();
		objList.add(atendimento);
		solicitacao.setLocalAtendimentos(objList);

		Aviso aviso = new Aviso();
		aviso.setTitulo("Sua demanda foi respondida!");
		aviso.setCategoria(Aviso.CATEGORIA_TRAMITACAO);
		aviso.setCorpo(atendimento.getResposta());
		aviso.setSolicitacao_ID(solicitacao.get_ID());
		aviso.setData(new Date());
		aviso.setToken(solicitacao.getLocalCidadao().getToken());
		avisos.add(aviso);
		salvarEditar();
	}

	public void responderEncerrar() {
		avisos = new ArrayList<Aviso>();
		solicitacao.setConcluida(true);
		solicitacao.setFuncionarioConclusao_ID(funcionarioLogado.get_ID());
		solicitacao.setDataConclusao(new Date());
		atendimento.set_ID(UUID.randomUUID().toString());
		atendimento.setAcao("Demanda foi encerrada.");
		atendimento.setLocalSolicitacao(solicitacao);
		atendimento.setData(new Date());
		atendimento.setFuncionario_ID(funcionarioLogado.get_ID());
		atendimento.setLocalFuncionario(funcionarioLogado);
		atendimento.setDepartamento_ID(funcionarioLogado.getDadosFuncionais().getDepartamento_ID());
		atendimento.setLocalDepartamento(funcionarioLogado.getDadosFuncionais().getLocalDepartamento());

		List<String> stringList;
		List<Atendimento> objList;
		if (solicitacao.getAtendimentos() == null)
			stringList = new ArrayList<String>();
		else
			stringList = solicitacao.getAtendimentos();
		stringList.add(atendimento.get_ID());
		solicitacao.setAtendimentos(stringList);
		if (solicitacao.getLocalAtendimentos() == null)
			objList = new ArrayList<Atendimento>();
		else
			objList = solicitacao.getLocalAtendimentos();
		objList.add(atendimento);
		solicitacao.setLocalAtendimentos(objList);

		Aviso aviso = new Aviso();
		aviso.setTitulo("Sua demanda foi respondida!");
		aviso.setCategoria(Aviso.CATEGORIA_TRAMITACAO);
		aviso.setCorpo(atendimento.getResposta());
		aviso.setSolicitacao_ID(solicitacao.get_ID());
		aviso.setData(new Date());
		aviso.setToken(solicitacao.getLocalCidadao().getToken());
		avisos.add(aviso);

		aviso = new Aviso();
		aviso.setTitulo("Sua demanda foi encerrada!");
		aviso.setCategoria(Aviso.CATEGORIA_AVALIACAO);
		aviso.setSolicitacao_ID(solicitacao.get_ID());
		aviso.setData(new Date());
		aviso.setToken(solicitacao.getLocalCidadao().getToken());
		aviso.setCorpo(
				"Sua demanda foi concluida com sucesso! Por favor, avalie nosso atendimento e a solução apresentada no aplicativo.");
		avisos.add(aviso);

		salvarEditar();
	}

	public void encaminhar() {
		avisos = new ArrayList<Aviso>();
		atendimento.set_ID(UUID.randomUUID().toString());
		atendimento.setAcao("Demanda encaminhada para o (a) " + solicitacao.getLocalDepartamento().getDescricao());
		atendimento.setLocalSolicitacao(solicitacao);
		atendimento.setData(new Date());
		atendimento.setFuncionario_ID(funcionarioLogado.get_ID());
		atendimento.setLocalFuncionario(funcionarioLogado);
		atendimento.setDepartamento_ID(funcionarioLogado.getDadosFuncionais().getDepartamento_ID());
		atendimento.setLocalDepartamento(funcionarioLogado.getDadosFuncionais().getLocalDepartamento());

		List<String> stringList;
		List<Atendimento> objList;
		if (solicitacao.getAtendimentos() == null)
			stringList = new ArrayList<String>();
		else
			stringList = solicitacao.getAtendimentos();
		stringList.add(atendimento.get_ID());
		solicitacao.setAtendimentos(stringList);
		if (solicitacao.getLocalAtendimentos() == null)
			objList = new ArrayList<Atendimento>();
		else
			objList = solicitacao.getLocalAtendimentos();
		objList.add(atendimento);
		solicitacao.setLocalAtendimentos(objList);

		Aviso aviso = new Aviso();
		aviso.setTitulo("Sua demanda foi respondida!");
		aviso.setCategoria(Aviso.CATEGORIA_TRAMITACAO);
		aviso.setCorpo(atendimento.getResposta());
		aviso.setSolicitacao_ID(solicitacao.get_ID());
		aviso.setData(new Date());
		aviso.setToken(solicitacao.getLocalCidadao().getToken());
		avisos.add(aviso);

		aviso = new Aviso();
		aviso.setTitulo("Sua demanda foi encaminhada!");
		aviso.setCategoria(Aviso.CATEGORIA_TRAMITACAO);
		aviso.setSolicitacao_ID(solicitacao.get_ID());
		aviso.setData(new Date());
		aviso.setToken(solicitacao.getLocalCidadao().getToken());
		aviso.setCorpo("Sua demanda foi encaminhada para o (a) " + solicitacao.getLocalDepartamento().getDescricao()
				+ ". Por favor, aguarde um retorno do referido setor.");
		avisos.add(aviso);

		salvarEditar();
	}

	public void onMarkerSelect(OverlaySelectEvent event) {
		marker = (Marker) event.getOverlay();
		center = marker.getLatlng().getLat() + ", " + marker.getLatlng().getLng();
	}

	public void onReverseGeocode(ReverseGeocodeEvent event) {
		List<String> addresses = event.getAddresses();
		center = event.getLatlng().getLat() + ", " + event.getLatlng().getLng();
		if (center != null) {
			marker = new Marker(event.getLatlng());
			if (addresses != null && !addresses.isEmpty()) {
				marker.setData(event.getAddresses().get(0));
				marker.setTitle(event.getAddresses().get(0));
				marker.setClickable(true);
			}
			mapModel.addOverlay(marker);
		}

	}

	public Usuario getFuncionarioLogado() {
		return funcionarioLogado;
	}

	public void setFuncionarioLogado(Usuario funcionarioLogado) {
		this.funcionarioLogado = funcionarioLogado;
	}

	public Solicitacao getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Solicitacao solicitacao) {
		this.solicitacao = solicitacao;
	}

	@Produces
	@Named("solicitacoes")
	public List<Solicitacao> getSolicitacoes() {
		if (solicitacoes == null)
			init();
		return solicitacoes;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setSolicitacoes(List<Solicitacao> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public List<Atendimento> getAtendimentos() {
		return atendimentos;
	}

	public void setAtendimentos(List<Atendimento> atendimentos) {
		this.atendimentos = atendimentos;
	}

	public List<Categoria> getTempCategorias() {
		return tempCategorias;
	}

	public void setTempCategorias(List<Categoria> tempCategorias) {
		this.tempCategorias = tempCategorias;
	}

	public List<Departamento> getDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(List<Departamento> departamentos) {
		this.departamentos = departamentos;
	}

	public Atendimento getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(Atendimento atendimento) {
		this.atendimento = atendimento;
	}

	public MapModel getMapModel() {
		return mapModel;
	}

	public void setMapModel(MapModel mapModel) {
		this.mapModel = mapModel;
	}

	public String getCenter() {
		if (center == null || center.isEmpty())
			center = "-26.484335, -51.991754";
		return center;
	}

	public void setCenter(String center) {
		this.center = center;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}
}
