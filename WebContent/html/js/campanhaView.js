
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

			//seteamos los datos de la campanha seleccionada
			var campanha = JSON.parse(localStorage.getItem("campanhaSeleccionada"));
			$('#datosCampanha').append('\
				<div id="'+campanha.id+'" class="campClass panel">\
					<div class="panel-body">\
						<h3 class="panel-title" style="font-weight: bold; color: #3c763d;">'+campanha.nombre+'</h3>\
						<span>'+campanha.mensaje+'</span>\
						</br>\
						<div style="display: table; color: #777;">\
							<div style="display: table-cell;">\
								<span><span style="font-weight:bold">fecha inicio: </span>'+campanha.fechaInicio+'</span>\
							</div>\
							<div style="display: table-cell; padding-left:2rem;">\
								<span><span style="font-weight:bold">fecha fin: </span>'+campanha.fechaFin+'</span>\
							</div>\
						</div>\
						<div style="display: table; color: #777;">\
							<div style="display: table-cell;">\
								<span><span style="font-weight:bold">invitados: </span>'+campanha.cantInvitados+'</span>\
							</div>\
							<div style="display: table-cell; padding-left:2rem;">\
								<span><span style="font-weight:bold">adheridos: </span>'+campanha.cantAdheridos+'</span>\
							</div>\
						</div>\
					</div>\
				</div>');


			
			var params = {
				admin: getAdminUser(),
				accessToken: getAccessToken(),
			}
		}
	});
});


