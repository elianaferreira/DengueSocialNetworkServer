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

			ajaxRequest("/admin/getAllActivedAdmin", "GET", params, function (response){
				response = JSON.parse(response);
				if(response.error == true){
					mostrarAlerta("Error", response.msj);
				} else {
					var arrayAdministradoresActivos = JSON.parse(response.msj);
					for(var i=0; i<arrayAdministradoresActivos.length; i++){
						var fila = '';
						fila += '<tr>\
                                        <td>'+arrayAdministradoresActivos[i].adminname+'</td>\
                                        <td>'+arrayAdministradoresActivos[i].name+'</td>\
                                        <td>'+arrayAdministradoresActivos[i].lastname+'</td>';
                        if(arrayAdministradoresActivos[i].hasOwnProperty('email')){
                        	fila += '<td>'+arrayAdministradoresActivos[i].ci+'</td>';
                        } else {
                        	'<td></td>';
                        }
                        if(arrayAdministradoresActivos[i].hasOwnProperty('address')){
                        	fila += '<td>'+arrayAdministradoresActivos[i].address+'</td>';
                        } else {
                        	'<td></td>';
                        }
                        if(arrayAdministradoresActivos[i].hasOwnProperty('phone')){
                        	fila += '<td>'+arrayAdministradoresActivos[i].phone+'</td>';
                        } else {
                        	'<td></td>';
                        }
                        if(arrayAdministradoresActivos[i].hasOwnProperty('ci')){
                        	fila += '<td>'+arrayAdministradoresActivos[i].ci+'</td>';
                        } else {
                        	'<td></td>';
                        }

                        fila += '</tr>';
                        $('#listaActivos').append(fila);
					}
				}
			});
			
		}
	});
});