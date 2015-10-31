$(document).ready(function(){
	var params = {
				admin: getAdminUser(),
				password: getAdminPass(),
				username: localStorage.getItem("usernameReportes")
			};
	ajaxRequest("/admin/reports", "GET", params, function(response){
		var responseJSON = JSON.parse(response);
		if(responseJSON.error == true){
			mostrarAlerta('Error', responseJSON.msj);
		} else {
			var reportesArray = JSON.parse(responseJSON.msj);
			$('#reportes').append('\
				<div class="col-lg-4">\
					<div class="panel panel-default">\
						<div class="panel-heading">\
							<h3 class="panel-title"><i class="fa fa-pencil fa-fw"></i>Reportes</h3>\
						</div>\
						<div class="panel-body">\
							<div id="lista-reportes" class="list-group">\
							</div>\
						</div>\
					</div>\
				</div>');

			for(var i = 0; i<reportesArray.length; i++){
				var reporte = reportesArray[i];
				if(reporte.hasOwnProperty("id")){
					$('#lista-reportes').append('\
						<a href="#" class="list-group-item">\
							<span class="badge">'+reporte.fecha+'</span>\
							<i class="fa fa-fw fa-mobile-phone"></i> '+reporte.mensaje+'\
						</a>');
				} else if(reporte.hasOwnProperty("idRepost")){
					var post = reporte.post;
					$('#lista-reportes').append('\
						<a href="#" class="list-group-item">\
						<span style="font-size:12px">reposte&oacute; de <span style="font-weight:bold;">'+post.voluntario.nombre+'</span></span>\
							<br>\
							<span class="badge">'+reporte.fecha+'</span>\
							<i class="fa fa-fw fa-mobile-phone"></i> '+post.mensaje+'\
						</a>');
				}
			}
		}
	});
});