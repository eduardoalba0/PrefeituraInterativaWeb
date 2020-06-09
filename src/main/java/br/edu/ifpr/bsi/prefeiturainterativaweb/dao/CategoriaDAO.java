package br.edu.ifpr.bsi.prefeiturainterativaweb.dao;

import java.util.List;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Categoria;
import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.DatabaseHelper;

public class CategoriaDAO {

	private static CollectionReference reference;

	private static void init() {
		if (reference == null)
			reference = DatabaseHelper.getDatabase().collection("Categorias");
	}

	public static boolean merge(Categoria categoria) {
		init();
		return DatabaseHelper.merge(reference.document(categoria.get_ID()), categoria) != null;
	}

	public static boolean remove(Categoria categoria) {
		init();
		return DatabaseHelper.remove(reference.document(categoria.get_ID())) != null;
	}

	public static boolean remove(String categoria_ID) {
		init();
		return DatabaseHelper.remove(reference.document(categoria_ID)) != null;
	}

	public static Categoria get(String _ID) {
		init();
		DocumentSnapshot objeto = DatabaseHelper.get(reference.document(_ID));
		if (objeto == null)
			return null;
		else
			return objeto.toObject(Categoria.class);
	}

	public static List<Categoria> getAll() {
		init();
		QuerySnapshot lista = DatabaseHelper.getAll(reference.orderBy("descricao"));
		if (lista == null)
			return null;
		else
			return lista.toObjects(Categoria.class);
	}

	public static List<Categoria> getAll(List<String> ids) {
		init();
		QuerySnapshot lista = DatabaseHelper.getAll(reference.orderBy("descricao").whereIn("_ID", ids));
		if (lista == null)
			return null;
		else
			return lista.toObjects(Categoria.class);
	}

	public static boolean isAssociada(String categoria_ID) {
		init();
		if(!DepartamentoDAO.getAllPorCategoria(categoria_ID).isEmpty())
			return true;
		if(!SolicitacaoDAO.getAllPorCategoria(categoria_ID).isEmpty())
			return true;
		return false;
	}
}
