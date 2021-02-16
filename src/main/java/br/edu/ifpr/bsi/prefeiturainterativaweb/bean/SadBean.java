package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.hbar.HorizontalBarChartDataSet;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;
import org.primefaces.model.charts.optionconfig.legend.Legend;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.SolicitacaoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.sad.Fato_DemandasLocalDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.sad.Fato_QualidadeAtendimentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Fato_DemandasLocal;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Fato_QualidadeAtendimento;

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
	private List<Fato_QualidadeAtendimento> qualidadeAtendimentoList;
	private List<ModelAvaliacao> avMediaDepartamento;
	private List<ModelAvaliacao> avMediaFuncionario;
	private BarChartModel modelDepartamentos;
	private HorizontalBarChartModel modelFuncionarios;

	private Fato_DemandasLocalDAO demandasDAO;
	private List<Fato_DemandasLocal> demandasLocalList;
	private List<ModelDemandaLocal> qtCategoria;
	private List<ModelDemandaLocal> qtDepartamento;

	@PostConstruct
	public void init() {
		showStatusDialog();
		if (qualidadeDAO == null) {
			qualidadeDAO = new Fato_QualidadeAtendimentoDAO();
		}
		if (demandasDAO == null) {
			demandasDAO = new Fato_DemandasLocalDAO();
		}

		stage();
		listar();
		hideStatusDialog();
	}

	public void stage() {
		List<Fato_QualidadeAtendimento> mergeListQualidade = new ArrayList<>();
		List<Fato_DemandasLocal> mergeListLocal = new ArrayList<>();
		// solicitacoes.removeIf(solicitacao -> solicitacao.isStaged());
		solicitacoes.forEach(solicitacao -> {
			if (solicitacao.getFuncionarioConclusao_ID() != null)
				solicitacao.setLocalFuncionarioConclusao(
						funcionarios.get(funcionarios.indexOf(new Usuario(solicitacao.getFuncionarioConclusao_ID()))));
			solicitacao.setLocalDepartamento(
					departamentos.get(departamentos.indexOf(new Departamento(solicitacao.getDepartamento_ID()))));
			mergeListQualidade.add(new Fato_QualidadeAtendimento(solicitacao));

			solicitacao.getCategorias().forEach(categoria -> {
				if (categorias != null && categorias.contains(new Categoria(categoria)))
					mergeListLocal.add(new Fato_DemandasLocal(solicitacao,
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

	public List<Fato_QualidadeAtendimento> listar() {
		qualidadeAtendimentoList = qualidadeDAO.getAll();
		demandasLocalList = demandasDAO.getAll();

		if (qualidadeAtendimentoList == null || demandasLocalList == null) {
			qualidadeAtendimentoList = new ArrayList<Fato_QualidadeAtendimento>();
			demandasLocalList = new ArrayList<Fato_DemandasLocal>();
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		}
		filtrarQualidadeAtendimento();
		filtrarDemandasLocal();
		hideStatusDialog();
		return qualidadeAtendimentoList;
	}

	public void filtrarQualidadeAtendimento() {
		avMediaDepartamento = new ArrayList<ModelAvaliacao>();
		avMediaFuncionario = new ArrayList<ModelAvaliacao>();
		qualidadeAtendimentoList.removeIf(aux -> aux.getAvaliacao() == null);
		qualidadeAtendimentoList.forEach(val -> {
			ModelAvaliacao departamento = new ModelAvaliacao();
			departamento._ID = val.getDepartamento().get_ID();
			if (avMediaDepartamento.contains(departamento)) {
				avMediaDepartamento.get(avMediaDepartamento.indexOf(departamento))
						.addNota(val.getAvaliacao().getNota());
			} else {
				departamento._ID = val.getDepartamento().get_ID();
				departamento.nome = val.getDepartamento().getDescricao();
				departamento.addNota(val.getAvaliacao().getNota());
				avMediaDepartamento.add(departamento);
			}

			if (val.getFuncionario() != null) {
				ModelAvaliacao funcionario = new ModelAvaliacao();
				funcionario._ID = val.getFuncionario().get_ID();
				if (avMediaFuncionario.contains(funcionario)) {
					avMediaFuncionario.get(avMediaFuncionario.indexOf(funcionario))
							.addNota(val.getAvaliacao().getNota());
				} else {
					funcionario._ID = val.getFuncionario().get_ID();
					funcionario.nome = val.getFuncionario().getNome();
					funcionario.addNota(val.getAvaliacao().getNota());
					avMediaFuncionario.add(funcionario);
				}
			}
		});
	}

	public void filtrarDemandasLocal() {
		qtCategoria = new ArrayList<SadBean.ModelDemandaLocal>();
		qtDepartamento = new ArrayList<SadBean.ModelDemandaLocal>();
		demandasLocalList.forEach(val -> {
			if (val.getCategoria() != null) {
				ModelDemandaLocal categoria = new ModelDemandaLocal();
				categoria._ID = val.getCategoria().get_ID();
				if (qtCategoria.contains(categoria)) {
					qtCategoria.get(qtCategoria.indexOf(categoria)).quantidadeSolicitacoes++;
				}
			}
			if (val.getDepartamento() != null) {
				ModelDemandaLocal departamento = new ModelDemandaLocal();
				departamento._ID = val.getDepartamento().get_ID();
				if (qtDepartamento.contains(departamento)) {
					qtDepartamento.get(qtDepartamento.indexOf(departamento)).quantidadeSolicitacoes++;
				}
			}
		});
	}

	public BarChartModel getModelDepartamentos() {
		modelDepartamentos = new BarChartModel();
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

		for (int i = 0; i < avMediaDepartamento.size(); i++) {
			ModelAvaliacao departamento = avMediaDepartamento.get(i);
			values.add(departamento.media);
			labels.add(departamento.nome);

			if (i % 7 == 0)
				bgColor.add("#6200EA");
			else if (i % 6 == 0)
				bgColor.add("#0091EA");
			else if (i % 5 == 0)
				bgColor.add("#00BFA5");
			else if (i % 4 == 0)
				bgColor.add("#64DD17");
			else if (i % 3 == 0)
				bgColor.add("#FFD600");
			else if (i % 2 == 0)
				bgColor.add("#FF6D00");
			else if (i % 1 == 0)
				bgColor.add("#D50000");
		}
		dataSet.setData(values);
		dataSet.setBackgroundColor(bgColor);
		dataSet.setBorderColor(bgColor);
		dataSet.setBorderWidth(1);
		data.setLabels(labels);
		data.addChartDataSet(dataSet);

		ticks.setBeginAtZero(true);
		linearAxes.setOffset(true);
		linearAxes.setTicks(ticks);
		cScales.addXAxesData(linearAxes);
		legend.setDisplay(false);
		options.setScales(cScales);
		options.setLegend(legend);

		modelDepartamentos.setData(data);
		modelDepartamentos.setOptions(options);
		return modelDepartamentos;
	}

	public HorizontalBarChartModel getModelFuncionarios() {
		modelFuncionarios = new HorizontalBarChartModel();
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

		for (int i = 0; i < avMediaFuncionario.size(); i++) {
			ModelAvaliacao funcionario = avMediaFuncionario.get(i);
			values.add(funcionario.media);
			labels.add(funcionario.nome);

			if (i % 7 == 0)
				bgColor.add("#6200EA");
			else if (i % 6 == 0)
				bgColor.add("#0091EA");
			else if (i % 5 == 0)
				bgColor.add("#00BFA5");
			else if (i % 4 == 0)
				bgColor.add("#64DD17");
			else if (i % 3 == 0)
				bgColor.add("#FFD600");
			else if (i % 2 == 0)
				bgColor.add("#FF6D00");
			else if (i % 1 == 0)
				bgColor.add("#D50000");
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
		linearAxes.setTicks(ticks);
		cScales.addXAxesData(linearAxes);
		options.setScales(cScales);
		options.setLegend(legend);

		modelFuncionarios.setData(data);
		modelFuncionarios.setOptions(options);

		return modelFuncionarios;
	}

	public void setModelAvaliacaos(BarChartModel modelDepartamentos) {
		this.modelDepartamentos = modelDepartamentos;

	}

	public void setModelAvaliacaos(HorizontalBarChartModel modelFuncionarios) {
		this.modelFuncionarios = modelFuncionarios;
	}

	private class ModelAvaliacao {
		private String _ID;
		private String nome;
		private float media;
		private List<Float> notas;

		public ModelAvaliacao() {
			notas = new ArrayList<Float>();
		}

		public void addNota(float nota) {
			notas.add(nota);
			media = 0;
			notas.forEach(aux -> {
				media += aux;
			});
			media /= notas.size();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ModelAvaliacao other = (ModelAvaliacao) obj;
			return Objects.equals(_ID, other._ID);
		}
	}

	private class ModelDemandaLocal {
		private String _ID;
		private String descricao;
		private int quantidadeSolicitacoes;

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ModelDemandaLocal other = (ModelDemandaLocal) obj;
			return Objects.equals(_ID, other._ID);
		}
	}
}
