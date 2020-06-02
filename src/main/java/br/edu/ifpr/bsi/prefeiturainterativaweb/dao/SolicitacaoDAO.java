package br.edu.ifpr.bsi.prefeiturainterativaweb.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Solicitacao;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.DatabaseHelper;

public class SolicitacaoDAO {

	private static CollectionReference reference;

	private static void init() throws Exception {
		if (reference == null)
			reference = DatabaseHelper.getDatabase().collection("Solicitacoes");
	}

	public static ApiFuture<QuerySnapshot> getAll() throws Exception {
		init();
		return DatabaseHelper.getAll(reference);
	}

	public static ApiFuture<WriteResult> merge(Solicitacao solicitacao) throws Exception {
		init();
		return DatabaseHelper.merge(reference.document(solicitacao.get_ID()), solicitacao);
	}

}
