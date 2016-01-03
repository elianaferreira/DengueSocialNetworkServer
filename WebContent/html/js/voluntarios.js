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

			function agregarFilaNuevoVoluntario(){
				$('#bodyNuevoVoluntario').append('\
					<tr class="rowNuevoVoluntario">\
						<td><input class="input-nuevo-usuario form-control"></td>\
						<td><input class="input-nuevo-nombre form-control"></td>\
						<td><input class="input-nuevo-contrasenha form-control" style="-webkit-text-security:disc;"></td>\
						<td><input class="input-nuevo-ci form-control"></td>\
						<td><input class="input-nuevo-email form-control"></td>\
						<td><input class="input-nuevo-direccion form-control"></td>\
						<td><input class="input-nuevo-telefono form-control"></td>\
					</tr>');
			}


			$('#btnAddRow').click(function(event){
				event.preventDefault();
				agregarFilaNuevoVoluntario();
				
			});


			$('#btnEnviarNuevosVoluntarios').click(function(event){
				event.preventDefault();

				var errorForm = false;
				var mensajeErrorForm = [];

				var listaVoluntarios = [];

				$('.rowNuevoVoluntario').each(function() {
					var username = $(this).find("td").children('input.input-nuevo-usuario').val();
					var nombre = $(this).find("td").children('input.input-nuevo-nombre').val();
					var password = $(this).find("td").children('input.input-nuevo-contrasenha').val();
					var ci = $(this).find("td").children('input.input-nuevo-ci').val();
					var email = $(this).find("td").children('input.input-nuevo-email').val();
					var direccion = $(this).find("td").children('input.input-nuevo-direccion').val();
					var telefono = $(this).find("td").children('input.input-nuevo-telefono').val();

					if(username.trim() == ""){
						mensajeErrorForm.push('El nombre de usuario no puede estar vacio');
						errorForm = true;
						return;
					} else if(nombre.trim() == ""){
						mensajeErrorForm.push('El nombre no puede estar vacio');
						errorForm = true;
						return;
					} else if(password.trim() == ""){
						mensajeErrorForm.push('La contraseña no puede estar vacia');
						errorForm = true;
						return;
					}

					var jsonTemp = {}
					jsonTemp["username"] = username;
					jsonTemp["password"] = password;
					jsonTemp["nombre"] = nombre;
					jsonTemp["ci"] = ci;
					jsonTemp["email"] = email;
					jsonTemp["direccion"] = direccion;
					jsonTemp["telefono"] = telefono;

					listaVoluntarios.push(jsonTemp);
				 });
				//console.log(JSON.stringify(listaVoluntarios));
				var params = {
					admin: getAdminUser(),
					password: getAdminPass(),
					voluntarios: JSON.stringify(listaVoluntarios)
				}

				if(mensajeErrorForm.length > 0){
					mostrarAlerta('Error', mensajeErrorForm[0]);
				} else {
					ajaxRequest("/admin/volunteers", "POST", params, function(response){
						var respuestaJson = JSON.parse(response);
						if(respuestaJson.error == true){
							mostrarAlerta('Error', respuestaJson.msj);
						} else {
							//msj es un JSONArray
							var noGuardadosArray = JSON.parse(respuestaJson.msj);
							if(noGuardadosArray.length > 0){
								mostrarAlerta('Alerta', 'Los siguientes usuarios no han sido agregado, verifique que no tengan el mismo nombre de usuario.');
								//eliminamos todas las filas y agregamos por cada elemento del array
								$('#bodyNuevoVoluntario').empty();
								for(var j=0; j<noGuardadosArray.length; j++){
									$('#bodyNuevoVoluntario').append('\
										<tr class="rowNuevoVoluntario">\
											<td><input class="input-nuevo-usuario form-control" value="'+noGuardadosArray[j]["username"]+'"></td>\
											<td><input class="input-nuevo-nombre form-control" value="'+noGuardadosArray[j]["nombre"]+'"></td>\
											<td><input class="input-nuevo-contrasenha form-control" style="-webkit-text-security:disc;" value="'+noGuardadosArray[j]["password"]+'"></td>\
											<td><input class="input-nuevo-ci form-control" value="'+noGuardadosArray[j]["ci"]+'"></td>\
											<td><input class="input-nuevo-email form-control" value="'+noGuardadosArray[j]["email"]+'"></td>\
											<td><input class="input-nuevo-direccion form-control" value="'+noGuardadosArray[j]["direccion"]+'"></td>\
											<td><input class="input-nuevo-telefono form-control" value="'+noGuardadosArray[j]["telefono"]+'"></td>\
										</tr>'); 
									//TODO hacer un .val() para meter el dato del JSON dentro del input
								}
							} else {
								//borramos los datos
								$('#bodyNuevoVoluntario').empty();
								//agregamos una fila
								agregarFilaNuevoVoluntario();
							}
						}
					});
				}
				
			});
		}
	});
	
});