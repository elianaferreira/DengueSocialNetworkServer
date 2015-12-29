
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
					</a>');

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
					</a>');

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
						</a>');

				}
				$('#btnCargarMasReportes').show();
			}
		});
	});
	
});