package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.hbar.HorizontalBarChartDataSet;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import org.primefaces.model.charts.pie.PieChartOptions;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.SolicitacaoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.sad.Dim_CategoriaDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.sad.Dim_DepartamentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.sad.Fato_PerfilDemandasDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.sad.Fato_QualidadeAtendimentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Dim_Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Dim_Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Dim_Funcionario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Fato_PerfilDemandas;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Fato_QualidadeAtendimento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.ChartHolder;

@Named("sadBean")
@ViewScoped
@SuppressWarnings("serial")
public class SadBean extends AbstractBean {

	@Inject
	@Named("departamentos")
	private List<Departamento> departamentos;

	@Inject
	@Named("solicitacoes")
	private List<Solicitacao> solicitacoes;

	@Inject
	@Named("funcionarios")
	private List<Usuario> funcionarios;

	@Inject
	@Named("categorias")
	private List<Categoria> categorias;

	private List<String> bairros;

	private Fato_QualidadeAtendimentoDAO qualidadeDAO;
	private List<Fato_QualidadeAtendimento> avaliacaoList;
	private List<Fato_QualidadeAtendimento> avaliacaoListFiltered;

	private Departamento departamentoSelecionado;
	private Usuario funcionarioSelecionado;
	private Categoria categoriaSelecionada;
	private String bairroSelecionado;

	private Fato_PerfilDemandasDAO demandasDAO;
	private List<Fato_PerfilDemandas> perfilDemandasList;
	private List<Fato_PerfilDemandas> perfilDemandasListFiltered;

	private Date dataInicio;
	private Date dataFim;

	@PostConstruct
	public void init() {
		showStatusDialog();
		if (qualidadeDAO == null) {
			qualidadeDAO = new Fato_QualidadeAtendimentoDAO();
		}
		if (demandasDAO == null) {
			demandasDAO = new Fato_PerfilDemandasDAO();
		}

		stage();
		listar();
		hideStatusDialog();
	}

	public void stage() {
		List<Dim_Departamento> mergeDepartamentos = new ArrayList<Dim_Departamento>();
		List<Dim_Categoria> mergeCategorias = new ArrayList<Dim_Categoria>();

		departamentos.forEach(aux -> {
			mergeDepartamentos.add(new Dim_Departamento(aux));
		});
		categorias.forEach(aux -> {
			mergeCategorias.add(new Dim_Categoria(aux));
		});

		new Dim_DepartamentoDAO().merge(mergeDepartamentos);
		new Dim_CategoriaDAO().merge(mergeCategorias);

		List<Fato_QualidadeAtendimento> mergeListQualidade = new ArrayList<>();
		List<Fato_PerfilDemandas> mergeListLocal = new ArrayList<>();
		solicitacoes.removeIf(solicitacao -> solicitacao.isStaged());
		solicitacoes.forEach(solicitacao -> {
			if (solicitacao.getFuncionarioConclusao_ID() != null && solicitacao.getDataConclusao() != null) {
				Usuario aux = funcionarios
						.get(funcionarios.indexOf(new Usuario(solicitacao.getFuncionarioConclusao_ID())));
				if (aux.getDadosFuncionais() != null && aux.getDadosFuncionais().getDepartamento_ID() != null)
					aux.getDadosFuncionais().setLocalDepartamento(departamentos.get(
							departamentos.indexOf(new Departamento(aux.getDadosFuncionais().getDepartamento_ID()))));
				solicitacao.setLocalFuncionarioConclusao(aux);
			}
			solicitacao.setLocalDepartamento(
					departamentos.get(departamentos.indexOf(new Departamento(solicitacao.getDepartamento_ID()))));
			mergeListQualidade.add(new Fato_QualidadeAtendimento(solicitacao));

			solicitacao.getCategorias().forEach(categoria -> {
				if (categorias != null && categorias.contains(new Categoria(categoria)))
					mergeListLocal.add(new Fato_PerfilDemandas(solicitacao,
							categorias.get(categorias.indexOf(new Categoria(categoria)))));
			});
		});
		if (qualidadeDAO.merge(mergeListQualidade) && demandasDAO.merge(mergeListLocal)) {
			new Thread(() -> {
				solicitacoes.forEach(solicitacao -> SolicitacaoDAO.update(solicitacao, "staged", true));
				solicitacoes = SolicitacaoDAO.getAll();
			}).run();
		} else {
			showErrorMessage("Falha ao sincronizar dados no Data Warehouse.");
		}
	}

	public void selecionarDepartamento(ActionEvent evento) {
		departamentoSelecionado = (Departamento) evento.getComponent().getAttributes().get("departamentoSelecionado");
		filtrarQualidadeAtendimento();
		modelMediaDepartamentos();	
	}

	public void selecionarFuncionario(ActionEvent evento) {
		funcionarioSelecionado = (Usuario) evento.getComponent().getAttributes().get("funcionarioSelecionado");
		filtrarQualidadeAtendimento();
	}

	public void selecionarCategoria(ActionEvent evento) {
		categoriaSelecionada = (Categoria) evento.getComponent().getAttributes().get("categoriaSelecionada");
		filtrarPerfilDemandas();
	}

	public void selecionarBairro(ActionEvent evento) {
		bairroSelecionado = (String) evento.getComponent().getAttributes().get("bairroSelecionado");
		filtrarPerfilDemandas();
	}

	public void selecionarData(SelectEvent<Date> event) {
		filtrarQualidadeAtendimento();
		filtrarPerfilDemandas();
	}

	public void limparData(ActionEvent evento) {
		dataInicio = null;
		dataFim = null;
		filtrarQualidadeAtendimento();
		filtrarPerfilDemandas();
	}

	public void listar() {
		avaliacaoList = qualidadeDAO.getAll();
		perfilDemandasList = demandasDAO.getAll();

		if (avaliacaoList == null || perfilDemandasList == null) {
			avaliacaoList = new ArrayList<Fato_QualidadeAtendimento>();
			perfilDemandasList = new ArrayList<Fato_PerfilDemandas>();
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		}

		filtrarQualidadeAtendimento();
		filtrarPerfilDemandas();
		hideStatusDialog();
	}

	public void filtrarQualidadeAtendimento() {
		avaliacaoListFiltered = new ArrayList<Fato_QualidadeAtendimento>();
		avaliacaoList.forEach(val -> {
			avaliacaoListFiltered.add(val);
		});

		avaliacaoListFiltered.removeIf(aux -> aux.getAvaliacao() == null);

		if (funcionarioSelecionado != null && funcionarioSelecionado.get_ID() != null)
			avaliacaoListFiltered.removeIf(aux -> aux.getFuncionario().get_ID() != funcionarioSelecionado.get_ID());
		if (departamentoSelecionado != null && departamentoSelecionado.get_ID() != null)
			avaliacaoListFiltered.removeIf(aux -> aux.getDepartamento().get_ID() != departamentoSelecionado.get_ID());

		if (dataInicio != null)
			avaliacaoListFiltered.removeIf(aux -> aux.getDataConclusao().getDate().before(dataInicio));
		if (dataFim != null)
			avaliacaoListFiltered.removeIf(aux -> aux.getDataConclusao().getDate().after(dataFim));
	}

	public void filtrarPerfilDemandas() {
		perfilDemandasListFiltered = new ArrayList<Fato_PerfilDemandas>();
		bairros = new ArrayList<String>();
		perfilDemandasList.forEach(val -> {
			perfilDemandasListFiltered.add(val);
			if (!bairros.contains(val.getLocal().getBairro())) {
				bairros.add(val.getLocal().getBairro());
			}
		});

		if (bairroSelecionado != null)
			perfilDemandasListFiltered.removeIf(aux -> aux.getLocal().getBairro() != bairroSelecionado);
		if (categoriaSelecionada != null && categoriaSelecionada.get_ID() != null)
			perfilDemandasListFiltered.removeIf(aux -> aux.getCategoria().get_ID() != categoriaSelecionada.get_ID());

		if (dataInicio != null)
			perfilDemandasListFiltered.removeIf(aux -> aux.getDataAbertura().getDate().before(dataInicio));
		if (dataFim != null)
			perfilDemandasListFiltered.removeIf(aux -> aux.getDataAbertura().getDate().after(dataFim));
	}

	public HorizontalBarChartModel modelMediaDepartamentos() {
		List<ChartHolder> holderList = new ArrayList<ChartHolder>();
		Map<Dim_Departamento, Float[]> hashMap = new HashMap<Dim_Departamento, Float[]>();

		avaliacaoListFiltered.forEach(val -> {
			if (departamentoSelecionado == null || val.getDepartamento().get_ID() != departamentoSelecionado.get_ID()) {
				Dim_Departamento key = val.getDepartamento();
				Float[] notas;
				if (hashMap.containsKey(key)) {
					notas = hashMap.get(key);
					notas[0] += val.getAvaliacao().getNota();
					notas[1] += (float) 1.0;
				} else {
					notas = new Float[2];
					notas[0] = val.getAvaliacao().getNota();
					notas[1] = (float) 1.0;
				}
				hashMap.put(key, notas);
			}
		});

		hashMap.forEach((key, val) -> {
			ChartHolder holder = new ChartHolder();
			holder.set_ID(key.get_ID());
			holder.setLabel(key.getDescricao());
			holder.setValue(val[0] / val[1]);
			holderList.add(holder);
		});

		holderList.sort((ChartHolder o1, ChartHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderHorizontalChart(holderList);
	}

	public HorizontalBarChartModel modelMediaFuncionarios() {
		List<ChartHolder> holderList = new ArrayList<ChartHolder>();
		Map<Dim_Funcionario, Float[]> hashMap = new HashMap<Dim_Funcionario, Float[]>();

		avaliacaoListFiltered.forEach(val -> {
			if ((departamentoSelecionado == null || val.getDepartamento().get_ID() != departamentoSelecionado.get_ID())
					&& (funcionarioSelecionado == null
							|| val.getFuncionario().get_ID() != funcionarioSelecionado.get_ID())) {
				Dim_Funcionario key = val.getFuncionario();
				Float[] notas;
				if (hashMap.containsKey(key)) {
					notas = hashMap.get(key);
					notas[0] += val.getAvaliacao().getNota();
					notas[1] += (float) 1.0;
				} else {
					notas = new Float[2];
					notas[0] = val.getAvaliacao().getNota();
					notas[1] = (float) 1.0;
				}
				hashMap.put(key, notas);
			}
		});

		hashMap.forEach((key, val) -> {
			ChartHolder holder = new ChartHolder();
			holder.set_ID(key.get_ID());
			holder.setLabel(key.getNome());
			holder.setValue(val[0] / val[1]);
			holderList.add(holder);
		});
		holderList.sort((ChartHolder o1, ChartHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderHorizontalChart(holderList);
	}

	public BarChartModel modelNotasDepartamentos() {
		List<ChartHolder> holderList = new ArrayList<ChartHolder>();
		Map<Float, Integer> hashMap = new HashMap<Float, Integer>();

		hashMap.put((float) 0.0, 0);
		hashMap.put((float) 0.5, 0);
		hashMap.put((float) 1.0, 0);
		hashMap.put((float) 1.5, 0);
		hashMap.put((float) 2.0, 0);
		hashMap.put((float) 2.5, 0);
		hashMap.put((float) 3.0, 0);
		hashMap.put((float) 3.5, 0);
		hashMap.put((float) 4.0, 0);
		hashMap.put((float) 4.5, 0);
		hashMap.put((float) 5.0, 0);

		avaliacaoListFiltered.forEach(val -> {
			if (departamentoSelecionado == null || val.getDepartamento().get_ID() != departamentoSelecionado.get_ID()) {
				Float key = val.getAvaliacao().getNota();
				int quantidade;
				if (hashMap.containsKey(key)) {
					quantidade = hashMap.get(key);
					quantidade++;
				} else {
					quantidade = 1;
				}
				hashMap.put(key, quantidade);
			}
		});

		hashMap.forEach((key, val) -> {
			ChartHolder holder = new ChartHolder();
			holder.set_ID(key + "");
			holder.setLabel(key + " estrela(s)");
			holder.setValue(val);
			holderList.add(holder);
		});

		holderList.sort((ChartHolder o1, ChartHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderVerticalChart(holderList);
	}

	public BarChartModel modelNotasFuncionarios() {
		List<ChartHolder> holderList = new ArrayList<ChartHolder>();
		Map<Float, Integer> hashMap = new HashMap<Float, Integer>();

		hashMap.put((float) 0.0, 0);
		hashMap.put((float) 0.5, 0);
		hashMap.put((float) 1.0, 0);
		hashMap.put((float) 1.5, 0);
		hashMap.put((float) 2.0, 0);
		hashMap.put((float) 2.5, 0);
		hashMap.put((float) 3.0, 0);
		hashMap.put((float) 3.5, 0);
		hashMap.put((float) 4.0, 0);
		hashMap.put((float) 4.5, 0);
		hashMap.put((float) 5.0, 0);

		avaliacaoListFiltered.forEach(val -> {
			if ((departamentoSelecionado == null || val.getDepartamento().get_ID() != departamentoSelecionado.get_ID())
					&& (funcionarioSelecionado == null
							|| val.getFuncionario().get_ID() != funcionarioSelecionado.get_ID())) {
				Float key = val.getAvaliacao().getNota();
				int quantidade;
				if (hashMap.containsKey(key)) {
					quantidade = hashMap.get(key);
					quantidade++;
				} else {
					quantidade = 1;
				}
				hashMap.put(key, quantidade);
			}
		});

		hashMap.forEach((key, val) -> {
			ChartHolder holder = new ChartHolder();
			holder.setLabel(key + " estrela(s)");
			holder.setValue(val);
			holderList.add(holder);
		});

		holderList.sort((ChartHolder o1, ChartHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderVerticalChart(holderList);
	}

	public PieChartModel modelSolucaoDepartamentos() {
		List<ChartHolder> holderList = new ArrayList<ChartHolder>();
		Map<Boolean, Integer> mapSolucionadas = new HashMap<Boolean, Integer>();

		avaliacaoListFiltered.forEach(val -> {
			if (departamentoSelecionado == null || val.getDepartamento().get_ID() != departamentoSelecionado.get_ID()) {
				Boolean key = val.getAvaliacao().isSolucionada();
				int quantidade;
				if (mapSolucionadas.containsKey(key)) {
					quantidade = mapSolucionadas.get(key);
					quantidade++;
				} else {
					quantidade = 1;
				}
				mapSolucionadas.put(key, quantidade);
			}
		});

		mapSolucionadas.forEach((key, val) -> {
			ChartHolder holder = new ChartHolder();
			holder.setLabel(key ? "Demandas Solucionadas" : "Demandas não Solucionadas");
			holder.setValue(val);
			holderList.add(holder);
		});

		holderList.sort((ChartHolder o1, ChartHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderPieChart(holderList);
	}

	public PieChartModel modelSolucaoFuncionarios() {
		List<ChartHolder> holderList = new ArrayList<ChartHolder>();
		Map<Boolean, Integer> mapSolucionadas = new HashMap<Boolean, Integer>();

		avaliacaoListFiltered.forEach(val -> {
			if ((departamentoSelecionado == null || val.getDepartamento().get_ID() != departamentoSelecionado.get_ID())
					&& (funcionarioSelecionado == null
							|| val.getFuncionario().get_ID() != funcionarioSelecionado.get_ID())) {
				Boolean key = val.getAvaliacao().isSolucionada();
				int quantidade;
				if (mapSolucionadas.containsKey(key)) {
					quantidade = mapSolucionadas.get(key);
					quantidade++;
				} else {
					quantidade = 1;
				}
				mapSolucionadas.put(key, quantidade);
			}
		});

		mapSolucionadas.forEach((key, val) -> {
			ChartHolder holder = new ChartHolder();
			holder.setLabel(key ? "Demandas Solucionadas" : "Demandas não Solucionadas");
			holder.setValue(val);
			holderList.add(holder);
		});

		holderList.sort((ChartHolder o1, ChartHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderPieChart(holderList);
	}

	public LineChartModel modelEvolucaoAvDepartamentos() {
		List<ChartHolder> holderList = new ArrayList<ChartHolder>();
		Map<String, Float[]> hashMap = new HashMap<String, Float[]>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM", new Locale("pt", "BR"));

		avaliacaoListFiltered.forEach(val -> {
			if (departamentoSelecionado == null || val.getDepartamento().get_ID() != departamentoSelecionado.get_ID()) {
				String key = df.format(val.getDataConclusao().getDate());
				Float[] notas;
				if (hashMap.containsKey(key)) {
					notas = hashMap.get(key);
					notas[0] += val.getAvaliacao().getNota();
					notas[1] += (float) 1.0;
				} else {
					notas = new Float[2];
					notas[0] = val.getAvaliacao().getNota();
					notas[1] = (float) 1.0;
				}
				hashMap.put(key, notas);
			}
		});

		hashMap.forEach((key, val) -> {
			ChartHolder holder = new ChartHolder();
			holder.set_ID(key);
			holder.setLabel(key);
			holder.setValue(val[0] / val[1]);
			holderList.add(holder);
		});

		holderList.sort((ChartHolder o1, ChartHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderLineChart(holderList);
	}

	public LineChartModel modelEvolucaoAvFuncionarios() {
		List<ChartHolder> holderList = new ArrayList<ChartHolder>();
		Map<String, Float[]> hashMap = new HashMap<String, Float[]>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM", new Locale("pt", "BR"));

		avaliacaoListFiltered.forEach(val -> {
			if ((departamentoSelecionado == null || val.getDepartamento().get_ID() != departamentoSelecionado.get_ID())
					&& (funcionarioSelecionado == null
							|| val.getFuncionario().get_ID() != funcionarioSelecionado.get_ID())) {
				String key = df.format(val.getDataConclusao().getDate());
				Float[] notas;
				if (hashMap.containsKey(key)) {
					notas = hashMap.get(key);
					notas[0] += val.getAvaliacao().getNota();
					notas[1] += (float) 1.0;
				} else {
					notas = new Float[2];
					notas[0] = val.getAvaliacao().getNota();
					notas[1] = (float) 1.0;
				}
				hashMap.put(key, notas);
			}
		});

		hashMap.forEach((key, val) -> {
			ChartHolder holder = new ChartHolder();
			holder.set_ID(key);
			holder.setLabel(key);
			holder.setValue(val[0] / val[1]);
			holderList.add(holder);
		});
		holderList.sort((ChartHolder o1, ChartHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderLineChart(holderList);
	}

	public HorizontalBarChartModel modelQuantidadeCategorias() {
		List<ChartHolder> holderList = new ArrayList<ChartHolder>();
		Map<Dim_Categoria, Integer> hashMap = new HashMap<Dim_Categoria, Integer>();

		perfilDemandasListFiltered.forEach(val -> {
			if ((bairroSelecionado == null || val.getLocal().getBairro() != bairroSelecionado)
					&& (categoriaSelecionada == null || val.getCategoria().get_ID() != categoriaSelecionada.get_ID())) {
				Dim_Categoria key = val.getCategoria();
				int quantidade;
				if (hashMap.containsKey(key)) {
					quantidade = hashMap.get(key);
					quantidade++;
				} else {
					quantidade = 1;
				}
				hashMap.put(key, quantidade);
			}
		});

		hashMap.forEach((key, val) -> {
			ChartHolder holder = new ChartHolder();
			holder.set_ID(key.get_ID());
			holder.setLabel(key.getDescricao());
			holder.setValue(val);
			holderList.add(holder);
		});

		holderList.sort((ChartHolder o1, ChartHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderHorizontalChart(holderList);
	}

	public HorizontalBarChartModel modelQuantidadeBairros() {
		List<ChartHolder> holderList = new ArrayList<ChartHolder>();
		Map<String, Integer> hashMap = new HashMap<String, Integer>();

		perfilDemandasListFiltered.forEach(val -> {
			if ((bairroSelecionado == null || val.getLocal().getBairro() != bairroSelecionado)
					&& (categoriaSelecionada == null || val.getCategoria().get_ID() != categoriaSelecionada.get_ID())) {
				String key = val.getLocal().getBairro();
				int quantidade;
				if (hashMap.containsKey(key)) {
					quantidade = hashMap.get(key);
					quantidade++;
				} else {
					quantidade = 1;
				}
				hashMap.put(key, quantidade);
			}
		});

		hashMap.forEach((key, val) -> {
			ChartHolder holder = new ChartHolder();
			holder.set_ID(key);
			holder.setLabel(key);
			holder.setValue(val);
			holderList.add(holder);
		});

		holderList.sort((ChartHolder o1, ChartHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderHorizontalChart(holderList);
	}

	public PieChartModel modelConclusaoCategorias() {
		List<ChartHolder> holderList = new ArrayList<ChartHolder>();
		Map<Boolean, Integer> mapSolucionadas = new HashMap<Boolean, Integer>();

		perfilDemandasListFiltered.forEach(val -> {
			if ((bairroSelecionado == null || val.getLocal().getBairro() != bairroSelecionado)
					&& (categoriaSelecionada == null || val.getCategoria().get_ID() != categoriaSelecionada.get_ID())) {
				Boolean key = val.isSolicitacaoConcluida();
				int quantidade;
				if (mapSolucionadas.containsKey(key)) {
					quantidade = mapSolucionadas.get(key);
					quantidade++;
				} else {
					quantidade = 1;
				}
				mapSolucionadas.put(key, quantidade);
			}
		});

		mapSolucionadas.forEach((key, val) -> {
			ChartHolder holder = new ChartHolder();
			holder.setLabel(key ? "Demandas Concluídas" : "Demandas Pendentes");
			holder.setValue(val);
			holderList.add(holder);
		});

		holderList.sort((ChartHolder o1, ChartHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderPieChart(holderList);
	}

	public PieChartModel modelConclusaoBairros() {
		List<ChartHolder> holderList = new ArrayList<ChartHolder>();
		Map<Boolean, Integer> mapSolucionadas = new HashMap<Boolean, Integer>();

		perfilDemandasListFiltered.forEach(val -> {
			if ((bairroSelecionado == null || val.getLocal().getBairro() != bairroSelecionado)
					&& (categoriaSelecionada == null || val.getCategoria().get_ID() != categoriaSelecionada.get_ID())) {
				Boolean key = val.isSolicitacaoConcluida();
				int quantidade;
				if (mapSolucionadas.containsKey(key)) {
					quantidade = mapSolucionadas.get(key);
					quantidade++;
				} else {
					quantidade = 1;
				}
				mapSolucionadas.put(key, quantidade);
			}
		});

		mapSolucionadas.forEach((key, val) -> {
			ChartHolder holder = new ChartHolder();
			holder.setLabel(key ? "Demandas Concluídas" : "Demandas Pendentes");
			holder.setValue(val);
			holderList.add(holder);
		});

		holderList.sort((ChartHolder o1, ChartHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderPieChart(holderList);
	}

	public LineChartModel modelEvolucaoCategorias() {
		List<ChartHolder> holderList = new ArrayList<ChartHolder>();
		Map<String, Integer> hashMap = new HashMap<String, Integer>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM", new Locale("pt", "BR"));

		perfilDemandasListFiltered.forEach(val -> {
			if ((bairroSelecionado == null || val.getLocal().getBairro() != bairroSelecionado)
					&& (categoriaSelecionada == null || val.getCategoria().get_ID() != categoriaSelecionada.get_ID())) {
				String key = df.format(val.getDataAbertura().getDate());
				int quantidade;
				if (hashMap.containsKey(key)) {
					quantidade = hashMap.get(key);
					quantidade++;
				} else {
					quantidade = 1;
				}
				hashMap.put(key, quantidade);
			}
		});

		hashMap.forEach((key, val) -> {
			ChartHolder holder = new ChartHolder();
			holder.set_ID(key);
			holder.setLabel(key);
			holder.setValue(val);
			holderList.add(holder);
		});

		holderList.sort((ChartHolder o1, ChartHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderLineChart(holderList);
	}

	public LineChartModel modelEvolucaoBairros() {
		List<ChartHolder> holderList = new ArrayList<ChartHolder>();
		Map<String, Integer> hashMap = new HashMap<String, Integer>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM", new Locale("pt", "BR"));

		perfilDemandasListFiltered.forEach(val -> {
			String key = df.format(val.getDataAbertura().getDate());
			int quantidade;
			if (hashMap.containsKey(key)) {
				quantidade = hashMap.get(key);
				quantidade++;
			} else {
				quantidade = 1;
			}
			hashMap.put(key, quantidade);
		});

		hashMap.forEach((key, val) -> {
			ChartHolder holder = new ChartHolder();
			holder.set_ID(key);
			holder.setLabel(key);
			holder.setValue(val);
			holderList.add(holder);
		});

		holderList.sort((ChartHolder o1, ChartHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderLineChart(holderList);
	}

	public HorizontalBarChartModel renderHorizontalChart(List<? extends ChartHolder> list) {
		HorizontalBarChartModel chartModel = new HorizontalBarChartModel();
		HorizontalBarChartDataSet dataSet = new HorizontalBarChartDataSet();
		ChartData data = new ChartData();
		BarChartOptions options = new BarChartOptions();
		CartesianScales cScales = new CartesianScales();
		CartesianLinearAxes linearAxes = new CartesianLinearAxes();
		CartesianLinearTicks ticks = new CartesianLinearTicks();
		Legend legend = new Legend();

		List<Number> values = new ArrayList<>();
		List<String> bgColor = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		for (int i = 0; i < list.size(); i++) {
			ChartHolder model = list.get(i);
			values.add(model.getValue());
			labels.add(model.getLabel());
		}

		for (int i = 0; i < (int) list.size() / 10 + 1; i++) {
			bgColor.add("#f44336");
			bgColor.add("#E91E63");
			bgColor.add("#673AB7");
			bgColor.add("#2196F3");
			bgColor.add("#00BCD4");
			bgColor.add("#009688");
			bgColor.add("#4CAF50");
			bgColor.add("#CDDC39");
			bgColor.add("#FFEB3B");
			bgColor.add("#FF9800");
		}
		while (bgColor.size() > list.size()) {
			bgColor.remove(bgColor.size() - 1);
		}

		dataSet.setData(values);
		dataSet.setBackgroundColor(bgColor);
		dataSet.setBorderColor(bgColor);
		dataSet.setBorderWidth(1);
		data.setLabels(labels);
		data.addChartDataSet(dataSet);

		ticks.setBeginAtZero(true);
		ticks.setMax(5);
		linearAxes.setOffset(true);
		linearAxes.setTicks(ticks);
		cScales.addXAxesData(linearAxes);
		legend.setDisplay(false);
		options.setScales(cScales);
		options.setLegend(legend);

		chartModel.setData(data);
		chartModel.setOptions(options);
		return chartModel;

	}

	public BarChartModel renderVerticalChart(List<? extends ChartHolder> list) {
		BarChartModel chartModel = new BarChartModel();
		BarChartDataSet dataSet = new BarChartDataSet();
		ChartData data = new ChartData();
		BarChartOptions options = new BarChartOptions();
		CartesianScales cScales = new CartesianScales();
		CartesianLinearAxes linearAxes = new CartesianLinearAxes();
		CartesianLinearTicks ticks = new CartesianLinearTicks();
		Legend legend = new Legend();

		List<Number> values = new ArrayList<>();
		List<String> bgColor = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		for (int i = 0; i < list.size(); i++) {
			ChartHolder model = list.get(i);
			values.add(model.getValue());
			labels.add(model.getLabel());
		}

		for (int i = 0; i < (int) list.size() / 10 + 1; i++) {
			bgColor.add("#f44336");
			bgColor.add("#E91E63");
			bgColor.add("#673AB7");
			bgColor.add("#2196F3");
			bgColor.add("#00BCD4");
			bgColor.add("#009688");
			bgColor.add("#4CAF50");
			bgColor.add("#CDDC39");
			bgColor.add("#FFEB3B");
			bgColor.add("#FF9800");
		}

		while (bgColor.size() > list.size()) {
			bgColor.remove(bgColor.size() - 1);
		}

		dataSet.setData(values);
		dataSet.setBackgroundColor(bgColor);
		dataSet.setBorderColor(bgColor);
		dataSet.setBorderWidth(1);
		data.setLabels(labels);
		data.addChartDataSet(dataSet);

		legend.setDisplay(false);

		linearAxes.setOffset(true);
		ticks.setBeginAtZero(true);
		ticks.setMax(5);
		linearAxes.setTicks(ticks);
		cScales.addXAxesData(linearAxes);
		options.setScales(cScales);
		options.setLegend(legend);

		chartModel.setData(data);
		chartModel.setOptions(options);
		return chartModel;

	}

	public PieChartModel renderPieChart(List<? extends ChartHolder> list) {
		PieChartModel chartModel = new PieChartModel();
		PieChartDataSet dataSet = new PieChartDataSet();
		ChartData data = new ChartData();
		PieChartOptions options = new PieChartOptions();
		Legend legend = new Legend();

		List<Number> values = new ArrayList<>();
		List<String> bgColor = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		for (int i = 0; i < list.size(); i++) {
			ChartHolder model = list.get(i);
			values.add(model.getValue());
			labels.add(model.getLabel());
		}

		for (int i = 0; i < (int) list.size() / 10 + 1; i++) {
			bgColor.add("#4CAF50");
			bgColor.add("#CDDC39");
			bgColor.add("#FFEB3B");
			bgColor.add("#FF9800");
			bgColor.add("#f44336");
			bgColor.add("#E91E63");
			bgColor.add("#673AB7");
			bgColor.add("#2196F3");
			bgColor.add("#00BCD4");
			bgColor.add("#009688");
		}

		while (bgColor.size() > list.size()) {
			bgColor.remove(bgColor.size() - 1);
		}

		dataSet.setData(values);
		dataSet.setBackgroundColor(bgColor);
		dataSet.setBorderColor(bgColor);
		data.setLabels(labels);
		data.addChartDataSet(dataSet);

		legend.setPosition("left");
		options.setLegend(legend);

		chartModel.setOptions(options);
		chartModel.setData(data);
		return chartModel;
	}

	public LineChartModel renderLineChart(List<? extends ChartHolder> list) {
		LineChartModel chartModel = new LineChartModel();
		LineChartDataSet dataSet = new LineChartDataSet();
		ChartData data = new ChartData();
		LineChartOptions options = new LineChartOptions();
		Legend legend = new Legend();

		List<Object> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		for (int i = 0; i < list.size(); i++) {
			ChartHolder model = list.get(i);
			values.add(model.getValue());
			labels.add(model.getLabel());
		}

		dataSet.setData(values);
		dataSet.setBackgroundColor("#E0E0E0");
		dataSet.setBorderColor("#6200EA");
		data.setLabels(labels);
		data.addChartDataSet(dataSet);

		legend.setDisplay(false);
		options.setLegend(legend);

		chartModel.setOptions(options);
		chartModel.setData(data);
		return chartModel;

	}

	public Departamento getDepartamentoSelecionado() {
		return departamentoSelecionado;
	}

	public Usuario getFuncionarioSelecionado() {
		return funcionarioSelecionado;
	}

	public Categoria getCategoriaSelecionada() {
		return categoriaSelecionada;
	}

	public String getBairroSelecionado() {
		return bairroSelecionado;
	}

	public List<Departamento> getDepartamentos() {
		return departamentos;
	}

	public List<Usuario> getFuncionarios() {
		return funcionarios;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public List<String> getBairros() {
		return bairros;
	}

	public Date getDataInicio() {
		return this.dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return this.dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

}
