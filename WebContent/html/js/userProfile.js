
var globalArrayCampanhas = [];

$(document).ready(function(){

	$('#wrapper').hide();
	var paramValidate = {
		accessToken: getAccessToken()
	}
	ajaxRequest("/admin/validateAccessToken", "POST", paramValidate, function(responseValidateAccessToken){
		responseValidateAccessToken = JSON.parse(responseValidateAccessToken);
		if(responseValidateAccessToken.error == true){
			window.open("login.html", "_self");
		} else {
			$('#wrapper').show();

			$('#charts').addClass("active");


			var usuario = JSON.parse(localStorage.getItem("usuario"));
			$('#spanUsername').html(usuario.nombre + ": " + usuario.username);
			$('#spanReputacion').html('<i class="fa fa-star fa-fw fa-2x"></i>'+usuario.reputacion);
			$('#spanAmigos').html('<i class="fa fa-group fa-fw fa-2x"></i>'+usuario.cantAmigos);
			$('#spanReportes').html('<i class="fa fa-pencil fa-fw fa-2x"></i>'+usuario.cantReportes);


			
			var params = {
				admin: getAdminUser(),
				accessToken: getAccessToken()
			}

		}
	});
});