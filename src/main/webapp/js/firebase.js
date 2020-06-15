var firebaseApp;
var auth;
var boolLogin;

function initFirebase() {
	if (firebaseApp == null) {
		var firebaseConfig = {
			apiKey: "AIzaSyCG8HM6i3TOTOdHAbmKU0TtZjou7hZVxms",
			authDomain: "prefeiturainterativa.firebaseapp.com",
			databaseURL: "https://prefeiturainterativa.firebaseio.com",
			projectId: "prefeiturainterativa",
			storageBucket: "prefeiturainterativa.appspot.com",
			messagingSenderId: "977874090377",
			appId: "1:977874090377:web:d96d5491f89fa72c468958",
			measurementId: "G-V5H2DKDLSH"
		};
		firebaseApp = firebase.initializeApp(firebaseConfig);
		auth = firebaseApp.auth();
		auth.onAuthStateChanged(function (user) {
			if (boolLogin && !user.emailVerified) {
				boolLogin = false;
				user.sendEmailVerification()
					.then(function () {
						PF('statusDialog').hide();
						PF('growl').renderMessage({ summary: "Atenção!", detail: "Confirme seu e-mail clicando no link que lhe foi enviado.", severity: "warn" });
					})
					.catch(function (error) {
						console.log(error);
						PF('statusDialog').hide();
						PF('growl').renderMessage({ summary: "Erro!", detail: "Ocorreu um erro ao logar. Consulte o suporte da ferramenta.", severity: "error" });
					});
			}
		});
	}
}

function login() {
	var valemail = document.getElementById("form-login:email").value;
	var valsenha = document.getElementById("form-login:senha").value;
	loginFirebase(valemail, valsenha);
}

function loginFirebase(valemail, valsenha) {
	initFirebase();
	boolLogin = true;
	auth.signInWithEmailAndPassword(valemail, valsenha)
		.catch(function (error) {
			PF('statusDialog').hide();
			switch (error.code) {
				case 'auth/wrong-password':
					PF('growl').renderMessage({ summary: "Erro!", detail: "A senha informada está inválida.", severity: "error" });
					break;
				case 'auth/user-not-found':
					PF('growl').renderMessage({ summary: "Erro!", detail: "O e-mail informado não pertence a nenhuma conta.", severity: "error" });
					break;
				default:
					PF('growl').renderMessage({ summary: "Erro!", detail: "Ocorreu um erro ao logar. Consulte o suporte da ferramenta.", severity: "error" });
			}
		});
}

function logout() {
	initFirebase();
	auth.signOut()
		.then(function () {
			PF('statusDialog').hide();
			window.location.href = "index.xhtml";
		})
		.catch(function (error) {
			console.log(error);
			PF('statusDialog').hide();
			PF('growl').renderMessage({ summary: "Erro!", detail: "Ocorreu um erro ao deslogar. Consulte o suporte do sistema.", severity: "error" });
		});
}

function redefinirSenha() {
	initFirebase();
	var valemail = document.getElementById("form-redefinir:email").value;
	auth.sendPasswordResetEmail(valemail).then(function () {
		PF('statusDialog').hide();
		PF('growl').renderMessage({ summary: "Sucesso!", detail: "Clique no link enviado para seu e-mail para redefinir a senha.", severity: "info" });
	}).catch(function (error) {
		PF('statusDialog').hide();
		switch (error.code) {
			case 'auth/user-not-found':
				PF('growl').renderMessage({ summary: "Erro!", detail: "O e-mail informado não pertence a nenhuma conta.", severity: "error" });
				break
		}
	});
}
