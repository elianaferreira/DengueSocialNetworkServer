$(document).ready(function(){

	$('#voluntarios').addClass("active");

	$('#btnBuscar').click(function(event){
		event.stopPropagation();

		var usuarioBuscar = $('#inputBuscarUsuario').val();
		//si es igual a la cadena vacia no hacemos nada
		if(usuarioBuscar.trim() != ""){
			var params = {
				admin: getAdminUser(),
				password: getAdminPass(),
				criterio: usuarioBuscar
			}

			ajaxRequest("/admin/search", "GET", params, function(response){
				var respuestaJson = JSON.parse(response);
				if(respuestaJson.error == true){
					mostrarAlerta('Error', respuestaJson.msj);
				} else {
					$('#perfiles').empty();
					var arrayUsuarios = JSON.parse(respuestaJson.msj);
					for(var i=0; i<arrayUsuarios.length; i++){
						agregarVistaUsuario(arrayUsuarios[i]);
					}
				}
			});
		}
	});



	function agregarVistaUsuario(usuarioJSON){
		var ci = '';
		if(usuarioJSON.hasOwnProperty("ci")){
			ci += '<div class="form-group">\
						<label>C&eacute;dula</label>\
						<input class="form-control" type="text" placeholder="" value="'+usuarioJSON.ci+'" >\
					</div>';
		}
		var telefono = '';
		if(usuarioJSON.hasOwnProperty("telefono")){
			telefono += '<div class="form-group">\
						<label>Tel&eacute;fono</label>\
						<input class="form-control" type="text" placeholder="" value="'+usuarioJSON.telefono+'" >\
					</div>';
		}
		var email = '';
		if(usuarioJSON.hasOwnProperty("email")){
			email += '<div class="form-group">\
						<label>Email</label>\
						<input class="form-control" type="text" placeholder="" value="'+usuarioJSON.email+'" >\
					</div>';
		}
		var direccion = '';
		if(usuarioJSON.hasOwnProperty("telefono")){
			direccion += '<div class="form-group">\
						<label>Direcci&oacute;n</label>\
						<input class="form-control" type="text" placeholder="" value="'+usuarioJSON.direccion+'" >\
					</div>';
		}


		$('#perfiles').append('\
				<div class="col-lg-4">\
	                <div class="panel panel-default">\
	                	<div data-username="'+usuarioJSON.username+'" class="perfil panel-heading">\
	                		<h3 class="panel-title"><i class="fa fa-user fa-fw"></i> ' + usuarioJSON.nombre + '</h3>\
	                	</div>\
	                	<div class="panel panel-body">\
	                		<form role="form">\
	                			<fieldset disabled="">\
	                				<div class="form-group">\
	                					<label>Username</label>\
	                					<input class="form-control" type="text" placeholder="" value="'+usuarioJSON.usernamestring+'" >\
	                				</div>\
	                				<div class="form-group">\
	                					<label>Reputaci&oacute;n</label>\
	                					<input class="form-control" type="text" placeholder="" value="'+usuarioJSON.reputacion+'" >\
	                				</div>'+
	                				email+
	                				telefono+
	                				ci+'\
	                				<div class="form-group">\
	                					<label>Cantidad Amigos</label>\
	                					<input class="form-control" type="text" placeholder="" value="'+usuarioJSON.cantAmigos+'" >\
	                				</div>\
	                				<div class="form-group">\
	                					<label>Cantidad de Reportes</label>\
	                					<input class="form-control" type="text" placeholder="" value="'+usuarioJSON.cantReportes+'" >\
	                				</div>\
	                			</fieldset>\
	                		</form>\
	                	</div>\
	                </div>\
	            </div>');
	}

	$(document).on( 'click', '.perfil', function () {
		event.stopPropagation();
		var usernameSelected = $(this).data("username");
		localStorage.setItem("usernameReportes", usernameSelected);
		window.open("reportes.html", "_self");
	});
});