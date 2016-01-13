
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
				accessToken: getAccessToken(),
				ultimaactualizacion: getCurrentTimestampWithFormat()
			}
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
						</div>');

				for(var i = 0; i<arrayCampanhas.length; i++){
					var c = arrayCampanhas[i];
					globalArrayCampanhas.push(c);
					appendRow(c);
				}
			}

		});
	});
});


function appendRow(campanha){
	$('#lista').append('\
		<div class="panel panel-default">\
			<div class="panel-heading">\
				<h3 class="panel-title">'+campanha.nombre+'</h3>\
			</div>\
			<div class="panel-body">\
				'+campanha.mensaje+'\
			</div>\
		</div>');
}

