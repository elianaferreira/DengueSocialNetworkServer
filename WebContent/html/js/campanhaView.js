
var globalJsonUsers = {};

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
				accessToken: getAccessToken()
			}

			ajaxRequest("/admin/campaign/invitados/"+campanha.id, "GET", params, function(responseInvitados){
				responseInvitados = JSON.parse(responseInvitados);
				if(responseInvitados.error == true){
					mostrarAlerta("Error", responseInvitados.msj);
				} else {
					var arrayInvitados = JSON.parse(responseInvitados.msj);
					for(var i=0; i<arrayInvitados.length; i++){
						if(!globalJsonUsers.hasOwnProperty(arrayInvitados[i]["username"])){
							globalJsonUsers[arrayInvitados[i]["username"]] = arrayInvitados[i];
						}
						appendUserRow(arrayInvitados[i], true);
					}
				}
			});

			ajaxRequest("/admin/campaign/adheridos/"+campanha.id, "GET", params, function(responseAdheridos){
				responseAdheridos = JSON.parse(responseAdheridos);
				if(responseAdheridos.error == true){
					mostrarAlerta("Error", responseAdheridos.msj);
				} else {
					var arrayAdheridos = JSON.parse(responseAdheridos.msj);
					for(var i=0; i<arrayAdheridos.length; i++){
						if(!globalJsonUsers.hasOwnProperty(arrayAdheridos[i]["username"])){
							globalJsonUsers[arrayAdheridos[i]["username"]] = arrayAdheridos[i];
						}
						appendUserRow(arrayAdheridos[i], false);
					}
				}
			});

			$(document).on('click', 'tr', function(event){
				var selected = $(this).data("username");
				console.log(selected);
				localStorage.setItem("usuario", JSON.stringify(globalJsonUsers[selected]));
				localStorage.setItem("usernameReportes", selected);
				window.open("userProfile.html", "_self");
			});
		}
	});
});


function appendUserRow(usuario, esInvitado){
	var idToAppend = "#listaInvitados";
	if(!esInvitado){
		idToAppend = "#listaAdheridos";
	}

	$(idToAppend).append('\
		<tr data-username="'+usuario.username+'">\
			<td>'+usuario.nombre+'</td>\
			<td>'+usuario.usernamestring+'</td>\
			<td>'+usuario.reputacion+'</td>\
		</tr>');
}