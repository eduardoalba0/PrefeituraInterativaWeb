package br.edu.ifpr.bsi.prefeiturainterativaweb.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.DatabaseHelper;
import br.edu.ifpr.bsi.prefeiturainterativaweb.model.Funcionario;

public class FuncionarioDAO {

	private static CollectionReference reference;

	private static void init() throws Exception {
		if (reference == null)
			reference = DatabaseHelper.getDatabase().collection("Funcionarios");
	}

	public static ApiFuture<QuerySnapshot> getAll() throws Exception {
		init();
		return DatabaseHelper.getAll(reference);
	}

	public static ApiFuture<WriteResult> merge(Funcionario funcionario) throws Exception {
		init();
		return DatabaseHelper.merge(reference.document(funcionario.get_ID()), funcionario);
	}
}
