
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
			
			var params = {
				admin: getAdminUser(),
				accessToken: getAccessToken()
			}
		
			ajaxRequest("/admin/listCampaigns", "GET", params, function(response){
				response = JSON.parse(response);
				if(response.error == true){
					mostrarAlerta("Error", response.msj);
				} else {
					var arrayCampanhas = JSON.parse(response.msj);
					$('#campanhas').append('\
							<div class="col-lg-6">\
								<div class="panel panel-default">\
									<div class="panel-heading">\
										<h3 class="panel-title"><i class="fa fa-pencil fa-fw"></i>Campa&ntilde;as</h3>\
									</div>\
									<div id="lista" class="list-group">\
									</div>\
									<a id="btnCargarMas" href="">\
			                            <div class="panel-footer">\
			                                <span class="pull-left">cargar m&aacute;s</span>\
			                                <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>\
			                                <div class="clearfix"></div>\
			                            </div>\
			                        </a>\
								</div>\
							</div>\
							<button id="btnCrearNuevaCampanha" type="button" class="btn btn-danger">crear</button>');

					for(var i = 0; i<arrayCampanhas.length; i++){
						var c = arrayCampanhas[i];
						globalArrayCampanhas.push(c);
						appendRow(c);
					}
				}
			});

			$(document).on('click', '#btnCargarMas', function (event){
					event.preventDefault();

					//obtenemos la ultima campanha de la lista
					var ultimaCampanha = {};
					ultimaCampanha = globalArrayCampanhas[globalArrayCampanhas.length-1];
					var params = {
						admin: getAdminUser(),
						accessToken: getAccessToken(),
						id: ultimaCampanha.id
					}
				
					ajaxRequest("/admin/listCampaigns", "GET", params, function(response){
						response = JSON.parse(response);
						if(response.error == true){
							mostrarAlerta("Error", response.msj);
						} else {
							var arrayCampanhas = JSON.parse(response.msj);
							for(var i = 0; i<arrayCampanhas.length; i++){
								var c = arrayCampanhas[i];
								globalArrayCampanhas.push(c);
								appendRow(c);
							}
						}
					});
			});

			$(document).on('click', '#btnCrearNuevaCampanha', function (event){
				event.preventDefault();
				window.open("campanha.html", "_self");
			});

			$(document).on('click', '.campClass', function (event){
				event.preventDefault();
				var idCampanha = $(this).attr('id');
				console.log(idCampanha);
				//buscamos en el array
				for(var i=0; i<globalArrayCampanhas.length; i++){
					if(globalArrayCampanhas[i].id == idCampanha){
						localStorage.setItem("campanhaSeleccionada", JSON.stringify(globalArrayCampanhas[i]));
						break;
					}
				}
				window.open("campanhaView.html", "_self");
			});
		}
	});
});


function appendRow(campanha){
	$('#lista').append('\
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
}

