package br.edu.ifpr.bsi.prefeiturainterativaweb.dao;

import java.util.List;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Departamento;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.DatabaseHelper;

public class DepartamentoDAO {

	private static CollectionReference reference;

	private static void init() {
		if (reference == null)
			reference = DatabaseHelper.getDatabase().collection("Departamentos");
	}

	public static boolean merge(Departamento departamento) {
		init();
		return DatabaseHelper.merge(reference.document(departamento.get_ID()), departamento) != null;
	}

	public static boolean remove(Departamento departamento) {
		init();
		return DatabaseHelper.remove(reference.document(departamento.get_ID())) != null;
	}

	public static boolean remove(String departamento_ID) {
		init();
		return DatabaseHelper.remove(reference.document(departamento_ID)) != null;
	}

	public static Departamento get(String _ID) {
		init();
		DocumentSnapshot objeto = DatabaseHelper.get(reference.document(_ID));
		if (objeto == null)
			return null;
		else
			return objeto.toObject(Departamento.class);
	}

	public static List<Departamento> getAll() {
		init();
		QuerySnapshot lista = DatabaseHelper.getAll(reference.orderBy("descricao"));
		if (lista == null)
			return null;
		else
			return lista.toObjects(Departamento.class);
	}

}
