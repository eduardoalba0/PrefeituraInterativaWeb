var firebaseApp;
var auth;

function initFirebase() {
	if (firebaseApp == null){
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
	}
}

function login() {
	initFirebase();
	var  valemail = document.getElementById("form-login:email").value;
	var  valsenha = document.getElementById("form-login:senha").value;
	auth.signInWithEmailAndPassword(valemail, valsenha)
		.then(function(user){
		if(!user.emailVerified)
			logout();
			window.location.href = "index.xhtml";
			user.sendEmailVerification().catch(function(error){
				console.log(error);
				PF('growl').removeAll();
				PF('growl').renderMessage({summary:"Erro!", detail: "Ocorreu um erro ao logar. Consulte o suporte da ferramenta.", severity: "error"});
			});
		})
		.catch(function(error) {
			PF('statusDialog').hide();
			switch(error.code){
			case 'auth/wrong-password':	
				PF('growl').removeAll();
				PF('growl').renderMessage({summary:"Erro!", detail: "A senha informada está inválida.", severity: "error"});
				break;
			case 'auth/user-not-found':
				PF('growl').removeAll();
				PF('growl').renderMessage({summary:"Erro!", detail: "O e-mail informado não pertence a nenhuma conta.", severity: "error"});
				break;
			default:
				PF('growl').removeAll();
				PF('growl').renderMessage({summary:"Erro!", detail: "Ocorreu um erro ao logar. Consulte o suporte da ferramenta.", severity: "error"});
			}
		});
}

function logout(){
	initFirebase();
	auth.signOut()
	.then(function(){
		PF('statusDialog').hide();
		window.location.href = "index.xhtml";
	})
	.catch(function(error) {
		console.log(error);
		PF('statusDialog').hide();
		PF('growl').removeAll();
		PF('growl').renderMessage({summary:"Erro!", detail: "Ocorreu um erro ao deslogar. Consulte o suporte do sistema.", severity: "error"});
	});
}

function redefinirSenha() {
	initFirebase();
	var valemail = document.getElementById("form-redefinicao:email").value;
	auth.sendPasswordResetEmail(valemail).then(function(){
		PF('statusDialog').hide();
		PF('growl').removeAll();
		PF('growl').renderMessage({summary:"Sucesso!", detail: "Clique no link enviado para seu e-mail para redefinir a senha.", severity: "info"});
	}).catch(function(error){
		PF('statusDialog').hide();
		switch(error.code){
		case 'auth/user-not-found':
			PF('growl').removeAll();
			PF('growl').renderMessage({summary:"Erro!", detail: "O e-mail informado não pertence a nenhuma conta.", severity: "error"});
			break
		}
	});
}
