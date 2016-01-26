
var globalArrayReportes = [];

var URL_TIMELINE = "/admin/timeline";
var URL_SOLUCIONADOS = "/admin/solucionados";
var URL_NO_SOLUCIONADOS = "/admin/noSolucionados";
var URL_CERRADOS = "/admin/cerrados";

var vistaActual = "timeline";

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
			$('#dashboard').addClass("active");
			$('#btnCargarMasReportes').hide();
			
			var params = {
				admin: getAdminUser(),
				accessToken: getAccessToken(),
				ultimaActualizacion: getCurrentTimestampWithFormat()
			}
			ajaxRequest("/admin/timeline", "GET", params, function(responseTimeline){
				var responseJSON = JSON.parse(responseTimeline);
				if(responseJSON.error == false){
					var reportesArray = JSON.parse(responseJSON.msj);

					vistaActual = "timeline";

					for(var i = 0; i<reportesArray.length; i++){
						var reporte = reportesArray[i];
						globalArrayReportes.push(reporte);
						//post
						appendRow(reporte);
						/*$('#listaReportes').append('\
								<a id="'+reporte.id+'" class="list-group-item">\
									<span class="badge">'+reporte.fecha+'</span>\
									<i class="fa fa-fw fa-mobile-phone"></i> '+reporte.mensaje+'\
									<div class="row">\
		                                <div id="div_antes'+reporte.id+'" class="col-lg-4">\
		                                </div>\
		                                <div id="div_despues'+reporte.id+'" class="col-lg-4">\
		                                </div>\
		                            </div>\
								</a>');
						loadPhoto(reporte.id, true);
						loadPhoto(reporte.id, false);*/
						//document.getElementById("1251_antes").setAttribute( 'src', 'data:image/png;base64,'+dataIMGSRC);
					}
					$('#btnCargarMasReportes').show();
				}
			});


			var params = {
				admin: getAdminUser(),
				accessToken: getAccessToken()
			}

			ajaxRequest("/admin/reportesRelevantes", "GET", params, function(responseRelevantes){
				var responseJSON = JSON.parse(responseRelevantes);
				if(responseJSON.error == false){
					var reportesArray = JSON.parse(responseJSON.msj);

					for(var i = 0; i<reportesArray.length; i++){
						var reporte = reportesArray[i];
						//post
						$('#listaRelevantes').append('\
							<a id="'+reporte.id+'" class="list-group-item">\
									<span class="badge">'+reporte.fecha+'</span>\
									<i class="fa fa-fw fa-mobile-phone"></i> '+reporte.mensaje+'\
									<div class="row">\
		                                <div id="div_antes'+reporte.id+'" class="col-lg-4">\
		                                </div>\
		                                <div id="div_despues'+reporte.id+'" class="col-lg-4">\
		                                </div>\
		                            </div>\
								</a>');
						loadPhoto(reporte.id, true);
						loadPhoto(reporte.id, false);
					}
				}
			});


			$('#btnCargarMasReportes').click(function (event){
				event.preventDefault();

				//obtenemos el ultimo reporte de la lista
				var ultimoReporte = {};
				ultimoReporte = globalArrayReportes[globalArrayReportes.length-1];

				var params = {
					admin: getAdminUser(),
					accessToken: getAccessToken(),
					ultimaActualizacion: ultimoReporte.fecha
				}

				var urlRequets = "";
				if(vistaActual == "noSolucionados"){
					urlRequets = URL_NO_SOLUCIONADOS;
				} else if(vistaActual == "solucionados"){
					urlRequets = URL_SOLUCIONADOS;
				} else if(vistaActual == "cerrados"){
					urlRequets = URL_CERRADOS;
				} else {
					urlRequets = URL_TIMELINE;
				}

				ajaxRequest(urlRequets, "GET", params, function(responseTimeline){
					var responseJSON = JSON.parse(responseTimeline);
					if(responseJSON.error == false){
						var reportesArray = JSON.parse(responseJSON.msj);

						for(var i = 0; i<reportesArray.length; i++){
							var reporte = reportesArray[i];
							globalArrayReportes.push(reporte);
							//post
							appendRow(reporte);
						}
					}
				});
			});
		}
	});

	$('#listaReportesNoSolucionados').click(function (event){
		event.preventDefault();

		globalArrayReportes = [];
		vistaActual = "noSolucionados";

		var params = {
			admin: getAdminUser(),
			accessToken: getAccessToken(),
			ultimaActualizacion: getCurrentTimestampWithFormat()
		}
		ajaxRequest("/admin/noSolucionados", "GET", params, function(responseTimeline){
			var responseJSON = JSON.parse(responseTimeline);
			if(responseJSON.error == false){
				$('#listaReportes').empty();
				$('#titulo').html('<i class="fa fa-pencil fa-fw"></i>Todos los Reportes No Solucionados');
				var reportesArray = JSON.parse(responseJSON.msj);

				for(var i = 0; i<reportesArray.length; i++){
					var reporte = reportesArray[i];
					globalArrayReportes.push(reporte);
					//post
					appendRow(reporte);
				}
			}
		});
	});

	$('#listaReportesSolucionados').click(function (event){
		event.preventDefault();

		globalArrayReportes = [];
		vistaActual = "solucionados";

		var params = {
			admin: getAdminUser(),
			accessToken: getAccessToken(),
			ultimaActualizacion: getCurrentTimestampWithFormat()
		}
		ajaxRequest("/admin/solucionados", "GET", params, function(responseTimeline){
			var responseJSON = JSON.parse(responseTimeline);
			if(responseJSON.error == false){
				$('#listaReportes').empty();
				$('#titulo').html('<i class="fa fa-pencil fa-fw"></i>Todos los Reportes Solucionados');
				var reportesArray = JSON.parse(responseJSON.msj);

				for(var i = 0; i<reportesArray.length; i++){
					var reporte = reportesArray[i];
					globalArrayReportes.push(reporte);
					//post
					appendRow(reporte);
				}
			}
		});
	});

	$('#listaReportesCerrados').click(function (event){
		event.preventDefault();

		globalArrayReportes = [];
		vistaActual = "cerrados";

		var params = {
			admin: getAdminUser(),
			accessToken: getAccessToken(),
			ultimaActualizacion: getCurrentTimestampWithFormat()
		}
		ajaxRequest("/admin/cerrados", "GET", params, function(responseTimeline){
			var responseJSON = JSON.parse(responseTimeline);
			if(responseJSON.error == false){
				$('#listaReportes').empty();
				$('#titulo').html('<i class="fa fa-pencil fa-fw"></i>Todos los Reportes Cerrados');
				var reportesArray = JSON.parse(responseJSON.msj);

				for(var i = 0; i<reportesArray.length; i++){
					var reporte = reportesArray[i];
					globalArrayReportes.push(reporte);
					//post
					appendRow(reporte);
				}
			}
		});
	});
	

});


function loadPhoto(idPostInt, antesBoolean){
	var photoParams = {
		idPost: idPostInt,
		antes: antesBoolean
	}

	ajaxRequest("/statuses/photo", "GET", photoParams, function(responsePhoto){
		var responseJSON = JSON.parse(responsePhoto);
		if(responseJSON.error == false){
			var stringFlagAntes = "";
			if(antesBoolean){
				stringFlagAntes = "_antes";
			} else {
				stringFlagAntes = "_despues";
			}
			$('#div'+stringFlagAntes+idPostInt).append('<img id="'+idPostInt+stringFlagAntes+'" style="border-radius: 0.5rem;">');

			document.getElementById(idPostInt+stringFlagAntes).setAttribute( 'src', 'data:image/png;base64,'+responseJSON.msj);
		}
	});
}


function appendRow(reporte){
	var badge = '<span class="badge" style="background-color:#d9534f;">'+reporte.fecha+'</span>';
	if(reporte.solucionado == true){
		badge = '<span class="badge" style="background-color:#5cb85c;">'+reporte.fecha+'</span>';
	}
	if(reporte.cerrado == true){
		badge = '<span class="badge" style="background-color:#337ab7;">'+reporte.fecha+'</span>';
	}

	$('#listaReportes').append('\
			<a id="'+reporte.id+'" class="list-group-item">'+
				badge+'\
				<i class="fa fa-fw fa-mobile-phone"></i> '+reporte.mensaje+'\
				<div class="row">\
	                <div id="div_antes'+reporte.id+'" class="col-lg-4">\
	                </div>\
	                <div id="div_despues'+reporte.id+'" class="col-lg-4">\
	                </div>\
	            </div>\
			</a>');
	loadPhoto(reporte.id, true);
	loadPhoto(reporte.id, false);
}