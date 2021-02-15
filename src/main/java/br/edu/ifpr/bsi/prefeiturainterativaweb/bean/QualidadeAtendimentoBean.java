package br.edu.ifpr.bsi.prefeiturainterativaweb.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.hbar.HorizontalBarChartDataSet;
import org.primefaces.model.charts.hbar.HorizontalBarChartModel;

import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.SolicitacaoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.dao.sad.Fato_QualidadeAtendimentoDAO;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.sad.Fato_QualidadeAtendimento;

@Named("qualidadeAtendimentoBean")
@SessionScoped
@SuppressWarnings("serial")
public class QualidadeAtendimentoBean extends AbstractBean {

	@Inject
	@Named("departamentos")
	private List<Departamento> departamentos;

	@Inject
	@Named("solicitacoes")
	private List<Solicitacao> solicitacoes;

	@Inject
	@Named("funcionarios")
	private List<Usuario> funcionarios;

	private Fato_QualidadeAtendimento qualidadeAtendimento;
	private Fato_QualidadeAtendimentoDAO dao;
	private List<Fato_QualidadeAtendimento> qualidadeAtendimentoList;
	private HorizontalBarChartModel modelDepartamentos;

	@Override
	@PostConstruct
	public void init() {
		showStatusDialog();
		if (qualidadeAtendimento == null) {
			qualidadeAtendimento = new Fato_QualidadeAtendimento();
		}
		if (dao == null) {
			dao = new Fato_QualidadeAtendimentoDAO();
		}

		stage();
		listar();
		hideStatusDialog();
	}

	public void stage() {
		List<Fato_QualidadeAtendimento> mergeList = new ArrayList<>();
		solicitacoes.removeIf(solicitacao -> solicitacao.isStaged());
		solicitacoes.forEach(solicitacao -> {
			if (solicitacao.getFuncionarioConclusao_ID() != null)
				solicitacao.setLocalFuncionarioConclusao(
						funcionarios.get(funcionarios.indexOf(new Usuario(solicitacao.getFuncionarioConclusao_ID()))));
			solicitacao.setLocalDepartamento(
					departamentos.get(departamentos.indexOf(new Departamento(solicitacao.getDepartamento_ID()))));
			mergeList.add(new Fato_QualidadeAtendimento(solicitacao));
			solicitacao.setStaged(true);
		});
		if (dao.merge(mergeList)) {
			new Thread(() -> {
				solicitacoes.forEach(solicitacao -> SolicitacaoDAO.update(solicitacao, "staged", true));
			}).run();
		} else {
			System.err.println("Falha ao sincronizar dados no Data Warehouse.");
		}
	}

	@Override
	public void cadastrar() {
		qualidadeAtendimento = new Fato_QualidadeAtendimento();
		qualidadeAtendimento.set_ID(UUID.randomUUID().toString());
	}

	@Override
	public void selecionar(ActionEvent evento) {
		qualidadeAtendimento = (Fato_QualidadeAtendimento) evento.getComponent().getAttributes()
				.get("qualidadeAtendimentoSelecionado");
	}

	@Override
	public void salvarEditar() {
		if (dao.merge(qualidadeAtendimento)) {
			qualidadeAtendimento = new Fato_QualidadeAtendimento();
			hideStatusDialog();
			showSuccessMessage("Dados gravados na nuvem.");
		} else {
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao gravar os dados. Consulte o suporte da ferramenta.");
		}
	}

	@Override
	public List<Fato_QualidadeAtendimento> listar() {
		qualidadeAtendimentoList = dao.getAll();
		if (qualidadeAtendimentoList == null) {
			qualidadeAtendimentoList = new ArrayList<Fato_QualidadeAtendimento>();
			hideStatusDialog();
			showErrorMessage("Ocorreu uma falha ao listar os dados. Consulte o suporte da ferramenta.");
		}
		hideStatusDialog();
		return qualidadeAtendimentoList;
	}

	@Override
	public void removerDesabilitar(ActionEvent evento) {
	}

	public Fato_QualidadeAtendimento getFato_QualidadeAtendimento() {
		return qualidadeAtendimento;
	}

	public void setFato_QualidadeAtendimento(Fato_QualidadeAtendimento tipousuario) {
		this.qualidadeAtendimento = tipousuario;
	}

	public HorizontalBarChartModel getModelDepartamentos() {
		modelDepartamentos = new HorizontalBarChartModel();
		ChartData data = new ChartData();

		HorizontalBarChartDataSet hbarDataSet = new HorizontalBarChartDataSet();
		List<Number> values = new ArrayList<>();
		List<String> bgColor = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		for (int i = 0; i < qualidadeAtendimentoList.size(); i++) {
			Fato_QualidadeAtendimento fato = qualidadeAtendimentoList.get(i);
			if (fato.getAvaliacao() != null)
				values.add((Number) fato.getAvaliacao().getNota());
			else
				values.add(10);
			labels.add(fato.getDepartamento().getDescricao());

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
		hbarDataSet.setData(values);
		data.setLabels(labels);

		hbarDataSet.setBackgroundColor(bgColor);
		hbarDataSet.setBorderColor(bgColor);
		hbarDataSet.setBorderWidth(1);
		data.addChartDataSet(hbarDataSet);

		modelDepartamentos.setData(data);

		BarChartOptions options = new BarChartOptions();
		CartesianScales cScales = new CartesianScales();
		CartesianLinearAxes linearAxes = new CartesianLinearAxes();
		linearAxes.setOffset(true);
		CartesianLinearTicks ticks = new CartesianLinearTicks();
		ticks.setBeginAtZero(true);
		linearAxes.setTicks(ticks);
		cScales.addXAxesData(linearAxes);
		options.setScales(cScales);

		modelDepartamentos.setOptions(options);

		return modelDepartamentos;
	}

	public void setModelDepartamentos(HorizontalBarChartModel modelDepartamentos) {
		this.modelDepartamentos = modelDepartamentos;
	}
}
