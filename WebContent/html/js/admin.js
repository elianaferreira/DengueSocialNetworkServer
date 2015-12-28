$(document).ready(function(){

	$('#dashboard').addClass("active");

	var params = {
		admin: getAdminUser(),
		password: getAdminPass(),
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
	
});