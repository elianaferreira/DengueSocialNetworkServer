$(document).ready(function(){

	$('#voluntarios').addClass("active");

	var pathActivateInvalidate;
	var msjActivateInvalidate;
	var alertActivateInvalidate;

	var elementoSeleccionado;

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
	                	<a class="activateInvalidate" data-username="'+usuarioJSON.username+'" style="cursor:pointer; padding-left:16px; font-weight: bold;">'+msjActivateInvalidate+'</a>\
	                	<a class="sendAlert" data-username="'+usuarioJSON.username+'" style="cursor:pointer; padding-left:16px; margin-left:25px; font-weight: bold;">enviar alerta</a>\
	                	<div data-username="'+usuarioJSON.username+'" class="perfil panel-heading">\
	                		<h3 style="cursor:pointer" class="panel-title">\
	                			<i id="iFotoPerfil_'+usuarioJSON.username+'" class="fa fa-user fa-fw fa-5x"></i>\
	                			<img id="fotoReal_'+usuarioJSON.username+'" style="display: none; width:80px; height:80px; border-radius:50%;"/>\
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


	$(document).on('click', '.activateInvalidate', function(event){
		event.preventDefault();
		var usernameSelected = $(this).data('username');
		elementoSeleccionado = $(this);
		mostrarAlertaConfirmacion("Atenci&oacute;n", alertActivateInvalidate, "S&iacute;", function(e){
			e.preventDefault();
			$('#myModal').modal('hide');
			//eliminamos el div de modal porque se puede querer volver a mostrar en el alert a exito/error
			var el2 = document.getElementById("myModal");
			el2.parentElement.removeChild(el2);

			console.log("activar o desactivar cuenta de: " + usernameSelected);

			borrarVestigiosModal();

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
					mostrarAlerta('&Eacute;xito', reponseInvalidate.msj + '<br><span>Para visualizar los cambios es necesario recargar la p&aacute;gina.</span>');
					//cambiamos el color del panel para indicar que hay que actualizar para ver los cambios
					elementoSeleccionado.parent().removeClass("panel-default");
					elementoSeleccionado.parent().addClass("panel-warning");
				}
			});
		});
	});

	$(document).on('click', '.sendAlert', function(event){
		event.preventDefault();
		var usernameSelected = $(this).data('username');
		elementoSeleccionado = $(this);
		mostrarAlertaConfirmacion("Atenci&oacute;n", "Est&aacute; seguro que desea enviar una alerta al voluntario?", "S&iacute;", function(e){
			e.preventDefault();
			$('#myModal').modal('hide');
			//eliminamos el div de modal porque se puede querer volver a mostrar en el alert a exito/error
			var el2 = document.getElementById("myModal");
			el2.parentElement.removeChild(el2);

			borrarVestigiosModal();

			var parametros = {
				adminName: getAdminUser(),
				password: getAdminPass(),
				username: usernameSelected
			}
			ajaxRequest("/admin/alert", "POST", parametros, function(reponseInvalidate){
				reponseInvalidate = JSON.parse(reponseInvalidate);
				if(reponseInvalidate.error == true){
					mostrarAlerta('Error', reponseInvalidate.msj);
				} else {
					toastr["success"]("Alerta enviado al usuario.", "&Eacute;xito");
					toastr.options = {
						"closeButton": false,
						"debug": false,
						"newestOnTop": false,
						"progressBar": false,
						"positionClass": "toast-top-center",
						"preventDuplicates": false,
						"onclick": null,
						"showDuration": "30",
						"hideDuration": "1000",
						"timeOut": "1000",
						"extendedTimeOut": "1000",
						"showEasing": "swing",
						"hideEasing": "linear",
						"showMethod": "fadeIn",
						"hideMethod": "fadeOut"
					}
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