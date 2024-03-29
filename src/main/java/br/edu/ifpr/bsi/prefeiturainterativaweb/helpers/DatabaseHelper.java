package br.edu.ifpr.bsi.prefeiturainterativaweb.helpers;

import java.util.HashMap;
import java.util.Map;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;

public class DatabaseHelper {

	public static Firestore getDatabase() {
		return FirebaseHelper.getDatabase();
	}

	public static <T> WriteResult merge(DocumentReference reference, T objeto) {
		try {
			return reference.set(objeto).get();
		} catch (Exception ex) {
			return null;
		}
	}

	public static WriteResult remove(DocumentReference reference) {
		try {
			return reference.delete().get();
		} catch (Exception ex) {
			return null;
		}
	}

	public static WriteResult update(DocumentReference reference, String campo, Object valor) {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put(campo, valor);
			return reference.set(map, SetOptions.merge()).get();
		} catch (Exception ex) {
			return null;
		}
	}

	public static DocumentSnapshot get(DocumentReference reference) {
		try {
			return reference.get().get();
		} catch (Exception ex) {
			return null;
		}
	}

	public static QuerySnapshot getAll(CollectionReference reference) {
		try {
			return reference.get().get();
		} catch (Exception ex) {
			return null;
		}
	}

	public static QuerySnapshot getAll(Query query) {
		try {
			return query.get().get();
		} catch (Exception ex) {
			return null;
		}
	}

}
