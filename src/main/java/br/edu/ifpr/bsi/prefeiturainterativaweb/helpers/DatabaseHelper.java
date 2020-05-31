package br.edu.ifpr.bsi.prefeiturainterativaweb.helpers;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

public class DatabaseHelper {

	public static Firestore getDatabase() throws Exception {
		return FirebaseHelper.getDatabase();
	}

	public static <T> ApiFuture<WriteResult> inserirAtualizar(DocumentReference reference, T objeto) {
		return reference.set(objeto);
	}

	public static ApiFuture<DocumentSnapshot> get(DocumentReference reference){
		return reference.get();
	}

	public static ApiFuture<QuerySnapshot> getAll(CollectionReference reference) {
		return reference.get();
	}

	public static ApiFuture<QuerySnapshot> getQuery(Query query) {
		return query.get();
	}
}
