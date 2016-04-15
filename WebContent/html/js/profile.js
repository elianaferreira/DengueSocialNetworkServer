var datosCambiados = {};

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

			$('#adminSection').addClass("active");

			$('#confirmPassDiv').css('display', 'block');
			$('#confirmPassDiv').css('visibility', 'hidden');


			var params = {
				admin: getAdminUser(),
				accessToken: getAccessToken(),
			}

			ajaxRequest("/admin/getInfo", "GET", params, function(responseInfo){

				responseInfo = JSON.parse(responseInfo);
				if(responseInfo.error == true){
					mostrarAlerta("Error", responseInfo.msj);
				} else {
					var datosAdmin = JSON.parse(responseInfo.msj);

					$('#usernameInput').val(datosAdmin.admin);
					$('#passInput').val(datosAdmin.password);
					$('#nombreInput').val(datosAdmin.name);
					$('#apellidoInput').val(datosAdmin.lastname);

					if(datosAdmin.hasOwnProperty("ci")) {
						$('#ciInput').val(datosAdmin.ci);
					}
					if(datosAdmin.hasOwnProperty("address")) {
						$('#addressInput').val(datosAdmin.address);
					}
					if(datosAdmin.hasOwnProperty("phone")) {
						$('#phoneInput').val(datosAdmin.phone);
					}
					if (datosAdmin.hasOwnProperty("email")) {
						$('#emailInput').val(datosAdmin.email);
					}
				}

				$(window).on('beforeunload', function (e) {
					e.preventDefault();
					if($('fieldset').attr('disabled') == undefined){
						return ("Est\u00e1 a punto de dejar la p\u00e1gina.");
					}
				});

				//ocultar la vista de error cuando se haga click fuera de el
				$(document).mouseup(function (e) {
				    var container = $(".alert");

				    if (!container.is(e.target) // if the target of the click isn't the container...
				        && container.has(e.target).length === 0) // ... nor a descendant of the container
				    {
				        container.remove();
				    }
				});


				var formHasChanged = false;


				$('#btnEditar').click(function(e){
					e.preventDefault();
					$('fieldset').removeAttr('disabled');
					$('#btnConfirmar').removeAttr('disabled');
					$('#confirmPassDiv').css('visibility', 'visible');
				});


				$('#btnConfirmar').click(function(e){
					e.preventDefault();
					if($('fieldset').attr('disabled') == undefined){
						if(formHasChanged){
							mostrarAlertaConfirmacion("Atenci&oacute;n", "Est&aacute; seguro que desea guardar los cambios?", "Guardar cambios", function(e){
								e.preventDefault();
								borrarVestigiosModal();
								//validamos todos los campos que han sido cambiados
								for(key in datosCambiados){
									if(key == "newAdmin"){
										if(!isValidUsername(datosCambiados[key])){
											appendErrorDiv("usernameInput", "El Nombre de Usuario debe tener un formato v&aacute;lido.");
											return;
										}
									}

									//si cambio el password
									if(key == "password"){
										//verificamos que conste de 6 caracteres como minimo
										if(datosCambiados[key].length < 6){
											appendErrorDiv("passInput", "La Contrase&ntilde;a debe contar con al menos 6 caracteres.");
											return;
										}

										//verificamos el contenido del input, el password pudo ser editado pero ser el mismos que el anterior
										var confirm = $('passConfirmInput').val();
										if(datosCambiados[key] != confirm){
											appendErrorDiv("passConfirmInput", "La Confirmaci&oacute;n no coincide.");
											return;
										}
									}

									if(key == "name"){
										if(!isValidUsername(datosCambiados[key].replace(/\s+/g,""))){
											appendErrorDiv("nombreInput", "El Nombre debe tener un formato v&aacute;lido.");
											return;
										}
									}

									if(key == "lastname"){
										//validamos sin los espacios en blanco
										if(!isValidUsername(datosCambiados[key].replace(/\s+/g,""))){
											appendErrorDiv("apellidoInput", "EL Apellido debe tener un formato v&aacute;lido.");
											return;
										}
									}

									if(key == "ci"){
										if(!isValidNumericInput(datosCambiados[key])){
											appendErrorDiv("ciInput", "La C&eacute;dula de Identidad debe tener un formato v&aacute;lido.");
											return;
										}
									}

									if(key == "email"){
										if(!isValidEmailFormat(datosCambiados[key])){
											appendErrorDiv("emailInput", "El email debe tener un formato v&aacute;lido.");
											return;
										}
									}
								}

								var params = datosCambiados;
								if(params.hasOwnProperty("password")){
									var md5Pass = CryptoJS.MD5(params.password).toString();
									params.password = md5Pass;
								}
								if(params.hasOwnProperty("passConfirm")){
									var md5Pass = CryptoJS.MD5(params.passConfirm).toString();
									params.passConfirm = md5Pass;
								}

								params["accessToken"] = getAccessToken();
								console.log("ENVIAR!");
								ajaxRequest("/admin/update/"+getAdminUser(), "POST", params, function(response){
									response = JSON.parse(response);
									if(response.error == true){
										mostrarAlerta("Error", response.msj);
									} else {
										//recargamos la pagina para que traiga los nuevos valores
										$('fieldset').attr('disabled', true);
										$('#btnConfirmar').attr('disabled', true);
										location.reload();
									}
								});



								
							});
						} else {
							mostrarAlerta("Atenci&oacute;n", "Nada que guardar");
							$('#confirmPassDiv').css('display', 'block');
							$('#confirmPassDiv').css('visibility', 'hidden');
							$('fieldset').attr('disabled', true);
							$('#btnConfirmar').attr('disabled', true);
							formHasChanged = false;
						}
					}
				});

				/*$('#usernameInput').on('input',function(e){
					formHasChanged = true;
				});*/
				$('#passInput').on('input',function(e){
					formHasChanged = true;
					if($('#passInput').val() != $('#passConfirmInput').val()){
						$('#confirmPassDiv').addClass('has-error');
					} else {
						$('#confirmPassDiv').removeClass('has-error');
					}
				});
				//verificamos que sea igual al passInput
				$('#passConfirmInput').on('input',function(e){
					formHasChanged = true;
					if($('#passInput').val() != $('#passConfirmInput').val()){
						$('#confirmPassDiv').addClass('has-error');
					} else {
						$('#confirmPassDiv').removeClass('has-error');
					}
					
				});
				

				//cargamos los valores
				$('#usernameInput').focusout(function (event){
					event.stopPropagation();
					datosCambiados["newAdmin"] = $('usernameInput').val();
					/*if(!isValidUsername($('usernameInput').val())){
						mostrarAlerta("Error", "El nombre de usuario tiene un formato inv&aacute;lido.");
						return;
					} else {
						datosCambiados["username"] = $('usernameInput').val();
					}*/
				});

				$('#passInput').focusout(function (event){
					event.stopPropagation();
					datosCambiados["password"] = $('#passInput').val();
					/*if(password == ""){
						mostrarAlerta("Error", "Se necesita la contrase&ntilde;a.");
						return;
					} else {
						var confirmPass = $('#passConfirmInput').val();
						if(confirmPass == ""){
							mostrarAlerta("Error", "Se necesita la confirmaci&oacute; de la contrase&ntilde;a.");
							return;
						}

						if(password != confirmPass){
							mostrarAlerta("Error", "La confirmaci&oacute;n de la contrase&ntilde;a no coincide.");
							return;
						}
					}*/
				});

				$('#passConfirmInput').focusout(function (event){
					datosCambiados["passConfirm"] = $('#passConfirmInput').val();
					formHasChanged = true;
				});

				$('#nombreInput').focusout(function(e){
					datosCambiados["name"] = $("#nombreInput").val();
					formHasChanged = true;
				});
				$('#apellidoInput').focusout(function(e){
					datosCambiados["lastname"] = $("#apellidoInput").val();
					formHasChanged = true;
				});
				$('#ciInput').focusout(function(e){
					datosCambiados["ci"] = $('#ciInput').val();
					formHasChanged = true;
				});
				$('#phoneInput').focusout(function(e){
					datosCambiados["phone"] = $('#phoneInput').val();
					formHasChanged = true;
				});
				$('#addressInput').focusout(function(e){
					datosCambiados["address"] = $('#addressInput').val();
					formHasChanged = true;
				});
				$('#emailInput').focusout(function(e){
					datosCambiados["email"] = $('#emailInput').val();
					formHasChanged = true;
				});

			});
		}
	});
});