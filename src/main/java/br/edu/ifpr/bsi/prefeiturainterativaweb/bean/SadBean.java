package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.sad.Fato_PerfilDemandasDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.sad.Fato_QualidadeAtendimentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Dim_Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Dim_Funcionario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Fato_PerfilDemandas;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Fato_QualidadeAtendimento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.chartHolders.AvaliacaoHolder;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.chartHolders.DepartamentoHolder;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.chartHolders.FuncionarioHolder;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.chartHolders.GenericHolder;

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

	private Fato_QualidadeAtendimentoDAO qualidadeDAO;
	private List<Fato_QualidadeAtendimento> avaliacaoList;
	private List<Fato_QualidadeAtendimento> avaliacaoListFiltered;

	private Departamento departamentoSelecionado;
	private Usuario funcionarioSelecionado;

	private Fato_PerfilDemandasDAO demandasDAO;
	private List<Fato_PerfilDemandas> perfilDemandasList;

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
		List<Fato_QualidadeAtendimento> mergeListQualidade = new ArrayList<>();
		List<Fato_PerfilDemandas> mergeListLocal = new ArrayList<>();
		solicitacoes.removeIf(solicitacao -> solicitacao.isStaged());
		solicitacoes.forEach(solicitacao -> {
			if (solicitacao.getFuncionarioConclusao_ID() != null) {
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

	public void selecionarAvDepartamento(ActionEvent evento) {
		departamentoSelecionado = (Departamento) evento.getComponent().getAttributes().get("departamentoSelecionado");
		filtrarQualidadeAtendimento();
		filtrarPerfilDemandas();
	}

	public void selecionarAvFuncionario(ActionEvent evento) {
		funcionarioSelecionado = (Usuario) evento.getComponent().getAttributes().get("funcionarioSelecionado");
		filtrarQualidadeAtendimento();
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

	public List<Fato_QualidadeAtendimento> listar() {
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
		return avaliacaoListFiltered;
	}

	public void filtrarQualidadeAtendimento() {
		avaliacaoListFiltered = new ArrayList<Fato_QualidadeAtendimento>();
		avaliacaoList.forEach(val -> {
			avaliacaoListFiltered.add(val);
		});

		avaliacaoListFiltered.removeIf(aux -> aux.getAvaliacao() == null);

		if (dataInicio != null)
			avaliacaoListFiltered.removeIf(aux -> aux.getDataConclusao().getDate().before(dataInicio));
		if (dataFim != null)
			avaliacaoListFiltered.removeIf(aux -> aux.getDataConclusao().getDate().after(dataFim));
	}

	public void filtrarPerfilDemandas() {

	}

	public HorizontalBarChartModel modelMediaDepartamentos() {
		List<DepartamentoHolder> holderList = new ArrayList<DepartamentoHolder>();
		Map<Dim_Departamento, Float[]> mapNotas = new HashMap<Dim_Departamento, Float[]>();

		avaliacaoListFiltered.forEach(val -> {
			if (departamentoSelecionado == null || val.getDepartamento().get_ID() != departamentoSelecionado.get_ID()) {
				Dim_Departamento key = val.getDepartamento();
				Float[] notas;
				if (mapNotas.containsKey(key)) {
					notas = mapNotas.get(key);
					notas[0] += val.getAvaliacao().getNota();
					notas[1] += (float) 1.0;
				} else {
					notas = new Float[2];
					notas[0] = val.getAvaliacao().getNota();
					notas[1] = (float) 1.0;
				}
				mapNotas.put(key, notas);
			}
		});

		mapNotas.forEach((key, val) -> {
			DepartamentoHolder holder = new DepartamentoHolder();
			holder.set_ID(key.get_ID());
			holder.setLabel(key.getDescricao());
			holder.setValue(val[0] / val[1]);
			holderList.add(holder);
		});

		holderList.sort((GenericHolder o1, GenericHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderMediaChart(holderList);
	}

	public HorizontalBarChartModel modelMediaFuncionarios() {
		List<FuncionarioHolder> holderList = new ArrayList<FuncionarioHolder>();
		Map<Dim_Funcionario, Float[]> mapNotas = new HashMap<Dim_Funcionario, Float[]>();

		avaliacaoListFiltered.forEach(val -> {
			if ((departamentoSelecionado == null || val.getDepartamento().get_ID() != departamentoSelecionado.get_ID())
					&& (funcionarioSelecionado == null
							|| val.getFuncionario().get_ID() != funcionarioSelecionado.get_ID())) {
				Dim_Funcionario key = val.getFuncionario();
				Float[] notas;
				if (mapNotas.containsKey(key)) {
					notas = mapNotas.get(key);
					notas[0] += val.getAvaliacao().getNota();
					notas[1] += (float) 1.0;
				} else {
					notas = new Float[2];
					notas[0] = val.getAvaliacao().getNota();
					notas[1] = (float) 1.0;
				}
				mapNotas.put(key, notas);
			}
		});

		mapNotas.forEach((key, val) -> {
			FuncionarioHolder holder = new FuncionarioHolder();
			holder.set_ID(key.get_ID());
			holder.setLabel(key.getNome());
			holder.setValue(val[0] / val[1]);
			holderList.add(holder);
		});
		holderList.sort((GenericHolder o1, GenericHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderMediaChart(holderList);
	}

	public BarChartModel modelNotasDepartamentos() {
		List<AvaliacaoHolder> holderList = new ArrayList<AvaliacaoHolder>();
		Map<Float, Integer> mapNotas = new HashMap<Float, Integer>();

		mapNotas.put((float) 0.0, 0);
		mapNotas.put((float) 0.5, 0);
		mapNotas.put((float) 1.0, 0);
		mapNotas.put((float) 1.5, 0);
		mapNotas.put((float) 2.0, 0);
		mapNotas.put((float) 2.5, 0);
		mapNotas.put((float) 3.0, 0);
		mapNotas.put((float) 3.5, 0);
		mapNotas.put((float) 4.0, 0);
		mapNotas.put((float) 4.5, 0);
		mapNotas.put((float) 5.0, 0);

		avaliacaoListFiltered.forEach(val -> {
			if (departamentoSelecionado == null || val.getDepartamento().get_ID() != departamentoSelecionado.get_ID()) {
				Float key = val.getAvaliacao().getNota();
				int quantidade;
				if (mapNotas.containsKey(key)) {
					quantidade = mapNotas.get(key);
					quantidade++;
				} else {
					quantidade = 1;
				}
				mapNotas.put(key, quantidade);
			}
		});

		mapNotas.forEach((key, val) -> {
			AvaliacaoHolder holder = new AvaliacaoHolder();
			holder.set_ID(key + "");
			holder.setLabel(key + " estrela(s)");
			holder.setValue(val);
			holderList.add(holder);
		});

		holderList.sort((GenericHolder o1, GenericHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderNotasChart(holderList);
	}

	public BarChartModel modelNotasFuncionarios() {
		List<AvaliacaoHolder> holderList = new ArrayList<AvaliacaoHolder>();
		Map<Float, Integer> mapNotas = new HashMap<Float, Integer>();

		mapNotas.put((float) 0.0, 0);
		mapNotas.put((float) 0.5, 0);
		mapNotas.put((float) 1.0, 0);
		mapNotas.put((float) 1.5, 0);
		mapNotas.put((float) 2.0, 0);
		mapNotas.put((float) 2.5, 0);
		mapNotas.put((float) 3.0, 0);
		mapNotas.put((float) 3.5, 0);
		mapNotas.put((float) 4.0, 0);
		mapNotas.put((float) 4.5, 0);
		mapNotas.put((float) 5.0, 0);

		avaliacaoListFiltered.forEach(val -> {
			if ((departamentoSelecionado == null || val.getDepartamento().get_ID() != departamentoSelecionado.get_ID())
					&& (funcionarioSelecionado == null
							|| val.getFuncionario().get_ID() != funcionarioSelecionado.get_ID())) {
				Float key = val.getAvaliacao().getNota();
				int quantidade;
				if (mapNotas.containsKey(key)) {
					quantidade = mapNotas.get(key);
					quantidade++;
				} else {
					quantidade = 1;
				}
				mapNotas.put(key, quantidade);
			}
		});

		mapNotas.forEach((key, val) -> {
			AvaliacaoHolder holder = new AvaliacaoHolder();
			holder.setLabel(key + " estrela(s)");
			holder.setValue(val);
			holderList.add(holder);
		});

		holderList.sort((GenericHolder o1, GenericHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderNotasChart(holderList);
	}

	public PieChartModel modelSolucaoDepartamentos() {
		List<AvaliacaoHolder> holderList = new ArrayList<AvaliacaoHolder>();
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
			AvaliacaoHolder holder = new AvaliacaoHolder();
			holder.setLabel(key ? "Demandas Solucionadas" : "Demandas não Solucionadas");
			holder.setValue(val);
			holderList.add(holder);
		});

		holderList.sort((GenericHolder o1, GenericHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderSolucaoChart(holderList);
	}

	public PieChartModel modelSolucaoFuncionarios() {
		List<AvaliacaoHolder> holderList = new ArrayList<AvaliacaoHolder>();
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
			AvaliacaoHolder holder = new AvaliacaoHolder();
			holder.setLabel(key ? "Demandas Solucionadas" : "Demandas não Solucionadas");
			holder.setValue(val);
			holderList.add(holder);
		});

		holderList.sort((GenericHolder o1, GenericHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderSolucaoChart(holderList);
	}

	public LineChartModel modelEvolucaoAvDepartamentos() {
		List<AvaliacaoHolder> holderList = new ArrayList<AvaliacaoHolder>();
		Map<String, Float[]> mapNotas = new HashMap<String, Float[]>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy MMMM", new Locale("pt", "BR"));

		avaliacaoListFiltered.forEach(val -> {
			if (departamentoSelecionado == null || val.getDepartamento().get_ID() != departamentoSelecionado.get_ID()) {
				String key = df.format(val.getDataConclusao().getDate());
				Float[] notas;
				if (mapNotas.containsKey(key)) {
					notas = mapNotas.get(key);
					notas[0] += val.getAvaliacao().getNota();
					notas[1] += (float) 1.0;
				} else {
					notas = new Float[2];
					notas[0] = val.getAvaliacao().getNota();
					notas[1] = (float) 1.0;
				}
				mapNotas.put(key, notas);
			}
		});

		mapNotas.forEach((key, val) -> {
			AvaliacaoHolder holder = new AvaliacaoHolder();
			holder.set_ID(key);
			holder.setLabel(key);
			holder.setValue(val[0] / val[1]);
			holderList.add(holder);
		});

		holderList.sort((GenericHolder o1, GenericHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderEvolucaoChart(holderList);
	}

	public LineChartModel modelEvolucaoAvFuncionarios() {
		List<AvaliacaoHolder> holderList = new ArrayList<AvaliacaoHolder>();
		Map<String, Float[]> mapNotas = new HashMap<String, Float[]>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy MMMM", new Locale("pt", "BR"));

		avaliacaoListFiltered.forEach(val -> {
			if ((departamentoSelecionado == null || val.getDepartamento().get_ID() != departamentoSelecionado.get_ID())
					&& (funcionarioSelecionado == null
							|| val.getFuncionario().get_ID() != funcionarioSelecionado.get_ID())) {
				String key = df.format(val.getDataConclusao().getDate());
				Float[] notas;
				if (mapNotas.containsKey(key)) {
					notas = mapNotas.get(key);
					notas[0] += val.getAvaliacao().getNota();
					notas[1] += (float) 1.0;
				} else {
					notas = new Float[2];
					notas[0] = val.getAvaliacao().getNota();
					notas[1] = (float) 1.0;
				}
				mapNotas.put(key, notas);
			}
		});

		mapNotas.forEach((key, val) -> {
			AvaliacaoHolder holder = new AvaliacaoHolder();
			holder.set_ID(key);
			holder.setLabel(key);
			holder.setValue(val[0] / val[1]);
			holderList.add(holder);
		});
		holderList.sort((GenericHolder o1, GenericHolder o2) -> o1.getLabel().compareTo(o2.getLabel()));
		return renderEvolucaoChart(holderList);
	}

	public HorizontalBarChartModel renderMediaChart(List<? extends GenericHolder> list) {
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
			GenericHolder model = list.get(i);
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

	public BarChartModel renderNotasChart(List<? extends GenericHolder> list) {
		BarChartModel modelDepartamentos = new BarChartModel();
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
			GenericHolder model = list.get(i);
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

		modelDepartamentos.setData(data);
		modelDepartamentos.setOptions(options);
		return modelDepartamentos;

	}

	public PieChartModel renderSolucaoChart(List<? extends GenericHolder> list) {
		PieChartModel modelDepartamentos = new PieChartModel();
		PieChartDataSet dataSet = new PieChartDataSet();
		ChartData data = new ChartData();
		PieChartOptions options = new PieChartOptions();
		Legend legend = new Legend();

		List<Number> values = new ArrayList<>();
		List<String> bgColor = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		for (int i = 0; i < list.size(); i++) {
			GenericHolder model = list.get(i);
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
		data.setLabels(labels);
		data.addChartDataSet(dataSet);

		legend.setPosition("left");
		options.setLegend(legend);

		modelDepartamentos.setOptions(options);
		modelDepartamentos.setData(data);
		return modelDepartamentos;
	}

	public LineChartModel renderEvolucaoChart(List<? extends GenericHolder> list) {
		LineChartModel modelDepartamentos = new LineChartModel();
		LineChartDataSet dataSet = new LineChartDataSet();
		ChartData data = new ChartData();
		LineChartOptions options = new LineChartOptions();
		Legend legend = new Legend();

		List<Object> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		for (int i = 0; i < list.size(); i++) {
			GenericHolder model = list.get(i);
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

		modelDepartamentos.setOptions(options);
		modelDepartamentos.setData(data);
		return modelDepartamentos;

	}

	public Departamento getDepartamentoSelecionado() {
		return departamentoSelecionado;
	}

	public Usuario getFuncionarioSelecionado() {
		return funcionarioSelecionado;
	}

	public List<Departamento> getDepartamentos() {
		return departamentos;
	}

	public List<Usuario> getFuncionarios() {
		return funcionarios;
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
