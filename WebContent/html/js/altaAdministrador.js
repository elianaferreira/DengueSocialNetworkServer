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

			$('#confirmPassDiv').hide();

			var params = {
				admin: getAdminUser(),
				accessToken: getAccessToken(),
			}

			ajaxRequest("/admin/getAllInactivedAdmin", "GET", params, function (response){
				response = JSON.parse(response);
				if(response.error == true){
					mostrarAlerta("Error", response.msj);
				} else {
					var arrayAdministradoresActivos = JSON.parse(response.msj);
					for(var i=0; i<arrayAdministradoresActivos.length; i++){
						var fila = '';
						fila += '<tr>\
										<td><button type="button" class="close" data-dismiss="alert" aria-hidden="true" data-admin="'+arrayAdministradoresActivos[i].adminname+'">dar de alta</button></tad>\
                                        <td>'+arrayAdministradoresActivos[i].adminname+'</td>\
                                        <td>'+arrayAdministradoresActivos[i].name+'</td>\
                                        <td>'+arrayAdministradoresActivos[i].lastname+'</td>\
                                        <td>'+arrayAdministradoresActivos[i].ci+'</td>';
                        if(arrayAdministradoresActivos[i].hasOwnProperty('email')){
                        	fila += '<td>'+arrayAdministradoresActivos[i].ci+'</td>';
                        } else {
                        	fila += '<td></td>';
                        }
                        if(arrayAdministradoresActivos[i].hasOwnProperty('address')){
                        	fila += '<td>'+arrayAdministradoresActivos[i].address+'</td>';
                        } else {
                        	fila += '<td></td>';
                        }
                        if(arrayAdministradoresActivos[i].hasOwnProperty('phone')){
                        	fila += '<td>'+arrayAdministradoresActivos[i].phone+'</td>';
                        } else {
                        	fila += '<td></td>';
                        }
                       
                        fila += '</tr>';
                        $('#listaInactivos').append(fila);
					}

					$('.close').click(function (event){
						event.stopPropagation();
						var adminToEnable = $(this).data("admin");
						mostrarAlertaConfirmacion("Atenci&oacute;n", "Est&aacute; seguro que desea dar de alta a este administrador?", "S&iacute;", function (e){
							e.preventDefault();
							borrarVestigiosModal();
							
							ajaxRequest("/admin/enable/"+adminToEnable, "POST", params, function(response){
								response = JSON.parse(response);
								if(response.error == true){
									mostrarAlerta("Error", response.msj);
								} else {
									//hacemos un reload
									location.reload();
								}
							});
						});
					});
				}
			});
			
		}
	});
});