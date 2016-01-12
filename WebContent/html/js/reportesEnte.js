
var globalArrayReportes = [];

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
				accessToken: getAccessToken(),
				ultimaactualizacion: getCurrentTimestampWithFormat()
			}
			ajaxRequest("/admin/pendingSolutions/"+localStorage.getItem("ente"), "GET", params, function(response){
				var responseJSON = JSON.parse(response);
				if(responseJSON.error == true){
					mostrarAlerta('Error', responseJSON.msj);
				} else {
					var reportesArray = JSON.parse(responseJSON.msj);
					$('#reportes').append('\
						<div class="col-lg-6">\
							<div class="panel panel-default">\
								<div class="panel-heading">\
							<h3 class="panel-title"><i class="fa fa-pencil fa-fw"></i>Reportes que debe solucionar '+localStorage.getItem("ente")+'</h3>\
								</div>\
								<div id="lista-reportes" class="list-group">\
								</div>\
								<a id="btnCargarMasReportes" href="">\
		                            <div class="panel-footer">\
		                                <span class="pull-left">cargar m&aacute;s</span>\
		                                <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>\
		                                <div class="clearfix"></div>\
		                            </div>\
		                        </a>\
							</div>\
						</div>');

					for(var i = 0; i<reportesArray.length; i++){
						var reporte = reportesArray[i];
						globalArrayReportes.push(reporte);
						appendRow(reporte);
					}
				}
			});

			$(document).on('click', 'a', function (event) {
				event.stopPropagation();
				var idPost = $(this).attr("id");
				localStorage.setItem("idPost", idPost);
				window.open("post.html", "_self");
			});


			$('#btnCargarMasReportes').click(function (event){
				event.preventDefault();

				//obtenemos el ultimo reporte de la lista
				var ultimoReporte = {};
				ultimoReporte = globalArrayReportes[globalArrayReportes.length-1];

				var params = {
					admin: getAdminUser(),
					accessToken: getAccessToken(),
					ultimaactualizacion: ultimoReporte.fecha
				};
				ajaxRequest("/admin/pendingSolutions/"+localStorage.getItem("ente"), "GET", params, function(response){
					var responseJSON = JSON.parse(response);
					if(responseJSON.error == true){
						mostrarAlerta('Error', responseJSON.msj);
					} else {
						var reportesArray = JSON.parse(responseJSON.msj);

						for(var i = 0; i<reportesArray.length; i++){
							var reporte = reportesArray[i];
							globalArrayReportes.push(reporte);

							appendRow(reporte);
						}
					}
				});
			});
		}

		$(document).on('click', '.cerrar', function (event) {
			event.stopPropagation();
			var idReporteCerrar = $(this).data("id");
			console.log("cerrar el reporte " + idReporteCerrar);
			var params = {
				admin: getAdminUser(),
				accessToken: getAccessToken()
			}
			ajaxRequest("/admin/resolveReport/"+idReporteCerrar, "POST", params, function(responseCerrar){
				var responseJSON = JSON.parse(responseCerrar);
				if(responseJSON.error == true){
					mostrarAlerta("Error", responseJSON.msj);
				} else {
					$('#btnCerrar'+idReporteCerrar).hide();
					$('#cerrado'+idReporteCerrar).show();
				}
			});
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
	$('#lista-reportes').append('\
			<div style="display: table;">\
				<div style="display: table-cell; width:75%">\
					<a id="'+reporte.id+'" class="list-group-item">\
						<i class="fa fa-fw fa-mobile-phone"></i> '+reporte.mensaje+'\
						<div class="row">\
	                        <div id="div_antes'+reporte.id+'" class="col-lg-4">\
	                        </div>\
	                        <div id="div_despues'+reporte.id+'" class="col-lg-4">\
	                        </div>\
	                    </div>\
					</a>\
				</div>\
				<div style="display: table-cell; width:15%">\
					<span class="badge">'+reporte.fecha+'</span>\
					<br>\
					<button id="btnCerrar'+reporte.id+'" data-id="'+reporte.id+'" type="button" class="cerrar btn btn-xs btn-link">cerrar reporte</button>\
					<div id="cerrado'+reporte.id+'" class="alert alert-success" style="margin-top: 0.5rem; margin-right: 0.5rem;">\
                    	<strong>Cerrado.</strong>\
                	</div>\
				</div>\
			</div>');
	if(reporte.cerrado == true){
		$('#btnCerrar'+reporte.id).hide();
		$('#cerrado'+reporte.id).show();
	} else {
		$('#btnCerrar'+reporte.id).show();
		$('#cerrado'+reporte.id).hide();
	}

	loadPhoto(reporte.id, true);
	loadPhoto(reporte.id, false);
}