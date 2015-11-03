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


		var pathActivateInvalidate;
		var msjActivateInvalidate;
		var alertActivateInvalidate;

		if(usuarioJSON.activo == true){
			pathActivateInvalidate = "/admin/invalidateUser";
			msjActivateInvalidate = "desactivar cuenta de usuario";
			alertActivateInvalidate = "Est&aacute; seguro que desea invalidar la cuenta del voluntario?";

		} else {
			pathActivateInvalidate = "/admin/activateUser";
			msjActivateInvalidate = "activar cuenta de usuario";
			alertActivateInvalidate = "Est&aacute; seguro que desea activar la cuenta del voluntario?";
		}

		$('#perfiles').append('\
				<div class="col-lg-4">\
	                <div class="panel panel-default">\
	                	<a class="activateInvalidate" data-username="'+usuarioJSON.username+'" style="cursor:pointer;">'+msjActivateInvalidate+'</a>\
	                	<div data-username="'+usuarioJSON.username+'" class="perfil panel-heading">\
	                		<h3 style="cursor:pointer" class="panel-title">\
	                			<i id="iFotoPerfil_'+usuarioJSON.username+'" class="fa fa-user fa-fw fa-5x"></i>\
	                			<img id="fotoReal_'+usuarioJSON.username+'" style="display: none; width:60px; height:80px;"/>\
	                			<span style="vertical-align: bottom">'+ usuarioJSON.nombre + '</span>\
	                		</h3>\
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

			//llamado para ver la foto de perfil del usuario
	    	ajaxRequest("/users/user/profilePhoto/"+usuarioJSON.username, "GET", {}, function(responseProfile){
	    		var rpp = JSON.parse(responseProfile);
	    		if(rpp.error == false){
	    			//setear la foto de perfil
	    			$('#iFotoPerfil_'+usuarioJSON.username).hide();
	    			document.getElementById('fotoReal_'+usuarioJSON.username).setAttribute( 'src', 'data:image/png;base64,'+rpp.msj);
	    			$('#fotoReal_'+usuarioJSON.username).show();
	    		}
	    	});
	}


	$(document).on('click', '.activateInvalidate', function(){
		var usernameSelected = $('.activateInvalidate').data('username');
		mostrarAlertaConfirmacion("Atenci&oacute;n", alertActivateInvalidate, "S&iacute;", function(e){
			e.preventDefault();
			$('#myModal').modal('hide');
			//$('#myModal').hide();
			console.log("invalidar a: " + usernameSelected);

			var parametros = {
				admin: getAdminUser(),
				password: getAdminPass(),
				username: usernameSelected
			}
			ajaxRequest(pathActivateInvalidate, "POST", parametros, function(reponseInvalidate){
				reponseInvalidate = JSON.parse(reponseInvalidate);
				if(reponseInvalidate.error == true){
					mostrarAlerta('Error', reponseInvalidate.msj);
				} else {
					mostrarAlerta('&Eacute;xito', reponseInvalidate.msj);
				}
			});
		});
	});


	$(document).on('click', '.perfil', function () {
		event.stopPropagation();
		var usernameSelected = $(this).data("username");
		localStorage.setItem("usernameReportes", usernameSelected);
		window.open("reportes.html", "_self");
	});


	
});