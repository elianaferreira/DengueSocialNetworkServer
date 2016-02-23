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

			$('#confirmPassDiv').hide();

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

					$('#usenameInput').val(datosAdmin.admin);
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
						return ("Está a punto de dejar la página.");
					}
				});

				var formHasChanged = false;


				$('#btnEditar').click(function(e){
					e.preventDefault();
					$('fieldset').removeAttr('disabled');
					$('#btnConfirmar').removeAttr('disabled');
					$('#confirmPassDiv').show();
				});


				$('#btnConfirmar').click(function(e){
					e.preventDefault();
					if($('fieldset').attr('disabled') == undefined){
						if(formHasChanged){
							mostrarAlertaConfirmacion("Atenci&oacute;n", "Est&aacute; seguro que desea guardar los cambios?", "Guardar cambios", function(e){
								e.preventDefault();
								$('#myModal').modal('hide');
								$('#confirmPassDiv').hide();
								$('fieldset').attr('disabled', true);
								console.log("ENVIAR!");
							});
						} else {
							mostrarAlerta("Atenci&oacute;n", "Nada que guardar");
							$('#confirmPassDiv').hide();
							$('fieldset').attr('disabled', true);
							$('#btnConfirmar').attr('disabled', true);
							formHasChanged = false;
						}
					}
				});

				$('#usenameInput').on('input',function(e){
					formHasChanged = true;
				});
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
				$('#nombreInput').on('input',function(e){
					formHasChanged = true;
				});
				$('#ciInput').on('input',function(e){
					formHasChanged = true;
				});
				$('#phoneInput').on('input',function(e){
					formHasChanged = true;
				});
				$('#addressInput').on('input',function(e){
					formHasChanged = true;
				});
				$('#emailInput').on('input',function(e){
					formHasChanged = true;
				});

			});
		}
	});
});