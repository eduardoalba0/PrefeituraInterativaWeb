package br.edu.ifpr.bsi.prefeiturainterativaweb.helpers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.net.ssl.HttpsURLConnection;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Aviso;
import br.edu.ifpr.bsi.prefeiturainterativaweb.domain.Usuario;

public class FirebaseHelper {

	private static FirebaseApp firebaseApp;
	private static FirebaseAuth auth;
	private static Firestore database;
	private static FirebaseMessaging messaging;
	private static StorageClient storage;

	private static void init() {
		try {
			if (firebaseApp == null) {
				ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
				FirebaseOptions options = new FirebaseOptions.Builder()
						.setCredentials(GoogleCredentials
								.fromStream(ec.getResourceAsStream("/WEB-INF/firebase-credentials.json")))
						.setDatabaseUrl("prefeiturainterativa.firebaseio.com")
						.setStorageBucket("prefeiturainterativa.firebaseio.com").build();
				firebaseApp = FirebaseApp.initializeApp(options);
				auth = FirebaseAuth.getInstance(firebaseApp);
				database = FirestoreClient.getFirestore(firebaseApp);
				messaging = FirebaseMessaging.getInstance(firebaseApp);
				storage = StorageClient.getInstance(firebaseApp);
			}
		} catch (Exception ex) {
			System.out.println("Falha ao iniciar o Firebase.");
		}
	}

	public static String autenticar(Usuario usuario) {
		HttpsURLConnection urlRequest = null;
		InputStreamReader inputStream = null;
		try {
			urlRequest = (HttpsURLConnection) new URL("https://www.googleapis.com/identitytoolkit/v3/relyingparty/"
					+ "verifyPassword" + "?key=" + "AIzaSyCG8HM6i3TOTOdHAbmKU0TtZjou7hZVxms").openConnection();

			urlRequest.setDoOutput(true);
			urlRequest.setRequestMethod("POST");
			urlRequest.setRequestProperty("Content-Type", "application/json");
			OutputStream os = urlRequest.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
			osw.write("{\"email\":\"" + usuario.getEmail() + "\",\"password\":\"" + usuario.getSenha()
					+ "\",\"returnSecureToken\":true}");
			osw.flush();
			osw.close();
			urlRequest.connect();
			inputStream = new InputStreamReader((InputStream) urlRequest.getContent());
			JsonObject rootobj = JsonParser.parseReader(inputStream).getAsJsonObject();
			if (rootobj != null && rootobj.get("localId") != null)
				return rootobj.get("localId").getAsString();
			else
				return null;
		} catch (Exception ex) {
			return null;
		} finally {
			urlRequest.disconnect();
		}
	}

	public static UserRecord buscarUsuario(String uid) {
		init();
		try {
			return auth.getUser(uid);
		} catch (Exception ex) {
			return null;
		}
	}

	public static UserRecord buscarUsuario(UserRecord userRecord) {
		return buscarUsuario(userRecord.getUid());
	}

	public static UserRecord buscarUsuario(Usuario usuario) {
		return buscarUsuario(usuario.get_ID());
	}

	public static UserRecord cadastrarUsuario(Usuario usuario) throws Exception {
		init();
		return auth.createUser(new CreateRequest().setEmail(usuario.getEmail()).setPassword(usuario.getSenha())
				.setDisplayName(usuario.getNome()).setPhotoUrl(usuario.getUriFoto()).setEmailVerified(false)
				.setDisabled(!usuario.isHabilitado()));
	}

	public static UserRecord alterarUsuario(Usuario usuario) throws Exception {
		init();
		return auth.updateUser(new UpdateRequest(usuario.get_ID()).setEmail(usuario.getEmail())
				.setPassword(usuario.getSenha()).setDisplayName(usuario.getNome()).setPhotoUrl(usuario.getUriFoto())
				.setEmailVerified(false).setDisabled(!usuario.isHabilitado()));
	}

	public static Blob carregarImagemUsuario(Usuario usuario) {
		try {
			init();
			return storage.bucket("Usuarios").create(usuario.get_ID(),
					Files.readAllBytes(Paths.get(usuario.getLocalUriFoto())));
		} catch (Exception ex) {
			return null;
		}

	}

	public static Blob carregarAnexos(Usuario usuario, URI uri) {
		try {
			init();
			return storage.bucket("Solicitacoes").create(usuario.get_ID(), Files.readAllBytes(Paths.get(uri)));
		} catch (Exception ex) {
			return null;
		}

	}

	public static String enviarNotificacao(Aviso aviso) {
		try {
			init();
			Notification notification = Notification.builder().setTitle(aviso.getTitulo()).setBody(aviso.getCorpo())
					.build();
			return messaging.send(Message.builder().putData("Categoria", aviso.getCategoria())
					.putData("Solicitacao", aviso.getSolicitacao_ID()).setToken(aviso.getToken())
					.setNotification(notification).build());
		} catch (Exception ex) {
			return null;
		}
	}

	protected static Firestore getDatabase() {
		init();
		return database;
	}

}
