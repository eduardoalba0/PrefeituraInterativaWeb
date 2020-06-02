package br.edu.ifpr.bsi.prefeiturainterativaweb.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Atendimento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.DatabaseHelper;

public class AtendimentoDAO {

	private static CollectionReference reference;

	private static void init() throws Exception {
		if (reference == null)
			reference = DatabaseHelper.getDatabase().collection("Atendimentos");
	}

	public static ApiFuture<QuerySnapshot> getAll() throws Exception {
		init();
		return DatabaseHelper.getAll(reference);
	}

	public static ApiFuture<WriteResult> merge(Atendimento atendimento) throws Exception {
		init();
		return DatabaseHelper.merge(reference.document(atendimento.get_ID()), atendimento);
	}
}
