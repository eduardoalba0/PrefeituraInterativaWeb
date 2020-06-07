package br.edu.ifpr.bsi.prefeiturainterativaweb.dao;

import java.util.List;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.Query.Direction;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Atendimento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.DatabaseHelper;

public class AtendimentoDAO {

	private static CollectionReference reference;

	private static void init() {
		if (reference == null)
			reference = DatabaseHelper.getDatabase().collection("Atendimentos");
	}

	public static boolean merge(Atendimento atendimento) {
		init();
		return DatabaseHelper.merge(reference.document(atendimento.get_ID()), atendimento) != null;
	}

	public static boolean remove(Atendimento atendimento) {
		init();
		return DatabaseHelper.remove(reference.document(atendimento.get_ID())) != null;
	}

	public static boolean remove(String atendimento_ID) {
		init();
		return DatabaseHelper.remove(reference.document(atendimento_ID)) != null;
	}

	public static Atendimento get(String _ID) {
		init();
		DocumentSnapshot objeto = DatabaseHelper.get(reference.document(_ID));
		if (objeto == null)
			return null;
		else
			return objeto.toObject(Atendimento.class);
	}
	
	public static List<Atendimento> getAll() {
		init();
		QuerySnapshot lista = DatabaseHelper.getAll(reference.orderBy("data", Direction.DESCENDING));
		if (lista == null)
			return null;
		else
			return lista.toObjects(Atendimento.class);
	}
}
