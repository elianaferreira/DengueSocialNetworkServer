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

			$('#adminDD').addClass("active");


			//ocultar la vista de error cuando se haga click fuera de el
			$(document).mouseup(function (e) {
			    var container = $(".alert");

			    if (!container.is(e.target) // if the target of the click isn't the container...
			        && container.has(e.target).length === 0) // ... nor a descendant of the container
			    {
			        container.remove();
			    }
			});

			$('#btnEnviar').click(function(event){
				event.preventDefault();

				var errorForm = false;
				var mensajeErrorForm = [];
				
				var paramsNuevoAdmin;

				$('.rowNuevoAdmin').each(function() {
					var username = $(this).find("td").children('input.input-nuevo-usuario').val();
					var nombre = $(this).find("td").children('input.input-nuevo-nombre').val();
					var apellido = $(this).find("td").children('input.input-nuevo-apellido').val();
					var password = $(this).find("td").children('input.input-nuevo-contrasenha').val();
					var passConfirm = $(this).find("td").children('input.input-nuevo-confirmacion').val();
					var ci = $(this).find("td").children('input.input-nuevo-ci').val();
					var email = $(this).find("td").children('input.input-nuevo-email').val();
					var direccion = $(this).find("td").children('input.input-nuevo-direccion').val();
					var telefono = $(this).find("td").children('input.input-nuevo-telefono').val();

					if(username.trim() == ""){
						mensajeErrorForm.push('El nombre de usuario no puede estar vac&iacute;o');
						errorForm = true;
						return;
					} else if(!isValidUsername(username)){
						mensajeErrorForm.push("El nombre de usuario debe tener un formato v&aacute;lido");
						errorForm = true;
						return;
					} else if(password.trim() == ""){
						mensajeErrorForm.push('La contrase&ntilde;a no puede estar vac&iacute;a');
						errorForm = true;
						return;
					} else if(password.length < 6){
						mensajeErrorForm.push('La contrase&ntilde;a debe contar con al menos 6 caracteres');
						errorForm = true;
						return;
					} else if(password != passConfirm){
						mensajeErrorForm.push('La confirmaci&oacute;n de la contrase&ntilde;a no coincide');
						errorForm = true;
						return;
					} else if(nombre.trim() == ""){
						mensajeErrorForm.push('El nombre no puede estar vac&iacute;o');
						errorForm = true;
						return;
					} else if(apellido.trim() == ""){
						mensajeErrorForm.push('El apellido no puede estar vac&iacute;o');
						errorForm = true;
						return;
					} else if(ci.trim() == ""){
						mensajeErrorForm.push("La C&eacute;dula de Identidad no puede estar vac&iacute;a");
						errorForm = true;
						return;
					} else if(!isValidNumericInput(ci)){
						mensajeErrorForm.push("La C&eacute;dula de Identidad debe ser num&eacute;rico.");
						errorForm = true;
						return;
					} else if(email != "" && !isValidEmailFormat(email)){
						mensajeErrorForm.push("El Email debe tener un formato v&aacute;lido");
						errorForm = true;
						return;
					}

					var md5Pass = CryptoJS.MD5(password).toString();
					var jsonTemp = {}

					paramsNuevoAdmin = {
						admin: getAdminUser(),
						accessToken: getAccessToken(),
						adminName: username,
						name: nombre,
						lastname: apellido,
						password: md5Pass,
						ci: ci
					}

					if(email.trim() != ""){
						paramsNuevoAdmin.email = email;
					}
					if(direccion.trim() != ""){
						paramsNuevoAdmin.address = direccion;
					}
					if(telefono.trim() != ""){
						paramsNuevoAdmin.phone = telefono;
					}


				 });

				 

				if(mensajeErrorForm.length > 0){
					//mostrarAlerta('Error', mensajeErrorForm[0]);
					$('#tabla-nuevo-admin').append('\
						<div class="alert alert-danger">\
		                    <strong>Error!</strong> '+mensajeErrorForm[0]+'\
		                </div>');
				} else {
					ajaxRequest("/admin/new", "POST", paramsNuevoAdmin, function (response){
						response = JSON.parse(response);
						if(response.error == true){
							mostrarAlerta("Error", response.msj);
						} else {
							//borramos la lista y todos los datos
							mostrarAlerta("Error", response.msj);
							$('input').val("");
						}
					});
				}
				
			});
		}
	});
	
});