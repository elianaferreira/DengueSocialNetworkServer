
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

			$('#voluntarios').addClass("active");
			
			var params = {
						ultimaactualizacion: getCurrentTimestampWithFormat()
					};
			ajaxRequest("/users/user/homeTimeline/"+localStorage.getItem("usernameReportes"), "GET", params, function(response){
				var responseJSON = JSON.parse(response);
				if(responseJSON.error == true){
					mostrarAlerta('Error', responseJSON.msj);
				} else {
					var reportesArray = JSON.parse(responseJSON.msj);
					$('#reportes').append('\
						<div class="col-lg-6">\
							<div class="panel panel-default">\
								<div class="panel-heading">\
									<h3 class="panel-title"><i class="fa fa-pencil fa-fw"></i>Reportes</h3>\
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

						//post
						if(reporte.hasOwnProperty("id")){
							$('#lista-reportes').append('\
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
						} else if(reporte.hasOwnProperty("idRepost")){
							//respost
							var post = reporte.post;
							$('#lista-reportes').append('\
								<a id="'+post.id+'" class="list-group-item">\
								<span style="font-size:12px">reposte&oacute; de <span style="font-weight:bold;">'+post.voluntario.nombre+'</span></span>\
									<br>\
									<span class="badge">'+reporte.fecha+'</span>\
									<i class="fa fa-fw fa-mobile-phone"></i> '+post.mensaje+'\
									<div class="row">\
		                                <div id="div_antes'+post.id+'" class="col-lg-4">\
		                                </div>\
		                                <div id="div_despues'+post.id+'" class="col-lg-4">\
		                                </div>\
		                            </div>\
								</a>');
							loadPhoto(post.id, true);
							loadPhoto(post.id, false);
						}
					}
				}
			});

			$(document).on('click', 'a', function() {
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
						ultimaactualizacion: ultimoReporte.fecha
					};
				ajaxRequest("/users/user/homeTimeline/"+localStorage.getItem("usernameReportes"), "GET", params, function(response){
					var responseJSON = JSON.parse(response);
					if(responseJSON.error == true){
						mostrarAlerta('Error', responseJSON.msj);
					} else {
						var reportesArray = JSON.parse(responseJSON.msj);
						$('#reportes').append('\
							<div class="col-lg-6">\
								<div class="panel panel-default">\
									<div class="panel-heading">\
										<h3 class="panel-title"><i class="fa fa-pencil fa-fw"></i>Reportes</h3>\
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

							//post
							if(reporte.hasOwnProperty("id")){
								$('#lista-reportes').append('\
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
							} else if(reporte.hasOwnProperty("idRepost")){
								//respost
								var post = reporte.post;
								$('#lista-reportes').append('\
									<a id="'+post.id+'" class="list-group-item">\
									<span style="font-size:12px">reposte&oacute; de <span style="font-weight:bold;">'+post.voluntario.nombre+'</span></span>\
										<br>\
										<span class="badge">'+reporte.fecha+'</span>\
										<i class="fa fa-fw fa-mobile-phone"></i> '+post.mensaje+'\
										<div class="row">\
			                                <div id="div_antes'+post.id+'" class="col-lg-4">\
			                                </div>\
			                                <div id="div_despues'+post.id+'" class="col-lg-4">\
			                                </div>\
			                            </div>\
									</a>');
								loadPhoto(post.id, true);
								loadPhoto(post.id, false);
							}
						}
					}
				});
			});
		}
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