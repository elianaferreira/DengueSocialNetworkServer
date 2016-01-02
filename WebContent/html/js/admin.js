
var globalArrayReportes = [];

$(document).ready(function(){

	$('#dashboard').addClass("active");
	$('#btnCargarMasReportes').hide();
	
	var params = {
		admin: getAdminUser(),
		password: getAdminPass(),
		ultimaActualizacion: getCurrentTimestampWithFormat()
	}
	ajaxRequest("/admin/timeline", "GET", params, function(responseTimeline){
		var responseJSON = JSON.parse(responseTimeline);
		if(responseJSON.error == false){
			var reportesArray = JSON.parse(responseJSON.msj);

			for(var i = 0; i<reportesArray.length; i++){
				var reporte = reportesArray[i];
				globalArrayReportes.push(reporte);
				//post
				$('#listaReportes').append('\
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
				//document.getElementById("1251_antes").setAttribute( 'src', 'data:image/png;base64,'+dataIMGSRC);
			}
			$('#btnCargarMasReportes').show();
		}
	});


	var params = {
		admin: getAdminUser(),
		password: getAdminPass()
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
			password: getAdminPass(),
			ultimaActualizacion: ultimoReporte.fecha
		}
		ajaxRequest("/admin/timeline", "GET", params, function(responseTimeline){
			var responseJSON = JSON.parse(responseTimeline);
			if(responseJSON.error == false){
				var reportesArray = JSON.parse(responseJSON.msj);

				for(var i = 0; i<reportesArray.length; i++){
					var reporte = reportesArray[i];
					globalArrayReportes.push(reporte);
					//post
					$('#listaReportes').append('\
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
				
				//$('#btnCargarMasReportes').show();
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