var arrayUsuariosInvitados = [];

$(document).ready(function(){

	$('#voluntarios').addClass("active");

	var nowTemp = new Date();
	var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
	 

	//la fecha de inicio no puede ser inferior al dia actual, y la fecha fin no puede ser infereior a la fecha de inicio 
	var checkin = $('#fechaInicio').datepicker({
		onRender: function(date) {
			return date.valueOf() < now.valueOf() ? 'disabled' : '';
		}
	}).on('changeDate', function(ev) {
		if (ev.date.valueOf() > checkout.date.valueOf()) {
			var newDate = new Date(ev.date)
			newDate.setDate(newDate.getDate() + 1);
			checkout.setValue(newDate);
		}
		checkin.hide();
		$('#fechaFin')[0].focus();
	}).data('datepicker');

	var checkout = $('#fechaFin').datepicker({
		onRender: function(date) {
			return date.valueOf() <= checkin.date.valueOf() ? 'disabled' : '';
		}
	}).on('changeDate', function(ev) {
		checkout.hide();
	}).data('datepicker');


	//buscamos por nombre
	$('#btnBuscarPorNombre').click(function(event){
		event.preventDefault();

		if($('#inputBuscar').val().trim() == ""){
			mostrarAlerta("Error", "Ingrese un nombre por el cual buscar los voluntarios.");
		} else {
			//hacemos la llamada al servicio
			var paramsSearch = {
				admin: getAdminUser(),
				password: getAdminPass(),
				criterio: $('#inputBuscar').val()
			};
			ajaxRequest("/admin/search", "GET", paramsSearch, function(responseSearch){
				var respuestaJson = JSON.parse(responseSearch);
				$('#listaUsuarios').empty();
				//si es igual a true no mostramos nada
				if(respuestaJson.error == false) {
					$('#perfiles').empty();
					var arrayUsuarios = JSON.parse(respuestaJson.msj);
					for(var i=0; i<arrayUsuarios.length; i++){
						$('#listaUsuarios').append('\
							<a id="'+arrayUsuarios[i]["username"]+'" class="list-group-item">\
								<span class="badge">'+arrayUsuarios[i]["reputacion"]+'</span>\
								<i class="fa fa-fw fa-user"></i> '+arrayUsuarios[i]["nombre"] + ": " + arrayUsuarios[i]["usernamestring"]+'\
							</a>');
					}
				}
			});
		}
	});


	$('#btnBuscarPorReputacion').click(function(event){
		event.preventDefault();
		var params = {
			admin: getAdminUser(),
			password: getAdminPass()
		};
		ajaxRequest("/admin/usersByRanking", "GET", params, function(responseReputacion){
			var respuestaJson = JSON.parse(responseReputacion);
			$('#listaUsuarios').empty();
			//si es igual a true no mostramos nada
			if(respuestaJson.error == false) {
				$('#perfiles').empty();
				var arrayUsuarios = JSON.parse(respuestaJson.msj);
				for(var i=0; i<arrayUsuarios.length; i++){
					$('#listaUsuarios').append('\
						<a id="'+arrayUsuarios[i]["username"]+'" class="list-group-item">\
							<span class="badge">'+arrayUsuarios[i]["reputacion"]+'</span>\
							<i class="fa fa-fw fa-user"></i> '+arrayUsuarios[i]["nombre"] + ": " + arrayUsuarios[i]["usernamestring"]+'\
						</a>');
				}
			}
		});
	});
	

	$(document).on('click', 'a', function() {
		event.stopPropagation();
		var idUser = $(this).attr("id");
		//verificamos si el usuario ya esta en el array (pudo ser de la busqueda anterior)
		var yaExiste = false;
		for(var j=0; j<arrayUsuariosInvitados.length; j++){
			if(idUser == arrayUsuariosInvitados[j]){
				yaExiste = true;
				break;
			}
		}
		if(!yaExiste){
			arrayUsuariosInvitados.push(idUser);
			//ocultamos ese usuario de manera que no pueda ser agregado nuevamente
			$(this).hide();
			//mostrar toast
			toastr["success"]("Usuario agregado a la lista de invitados.", "Exito");
			toastr.options = {
				"closeButton": false,
				"debug": false,
				"newestOnTop": false,
				"progressBar": false,
				"positionClass": "toast-top-right",
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
		} else {
			$(this).hide();
			toastr["info"]("Este usuario ya ha sido agregado.", "Info");
			toastr.options = {
				"closeButton": false,
				"debug": false,
				"newestOnTop": false,
				"progressBar": false,
				"positionClass": "toast-top-right",
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


	$('#btnCrearCampanha').click(function(event){
		event.preventDefault();
		event.stopPropagation();

		//nombre de la campanha
		var nombreCampanha = $('#inputNombre').val();
		var mensajeCampanha = $('#inputMensaje').val();
		var fechaInicio = $('#fechaInicio').val();
		var fechaFin = $('#fechaFin').val();

		if(nombreCampanha.trim() == ""){
			$('#divNombreCamp').append('\
				<div class="alert alert-danger">\
                    <strong>Error!</strong> La campa&ntilde;a debe tener un nombre.\
                </div>');
		} else {
			if(mensajeCampanha.trim() == ""){
				$('#divMensaje').append('\
				<div class="alert alert-danger">\
                    <strong>Error!</strong> La campa&ntilde;a debe tener un mensaje a modo de descripci&oacute;n.\
                </div>');
			} else {
				if(fechaInicio.trim() == ""){
					$('#divFechas').append('\
						<div class="alert alert-danger">\
		                    <strong>Error!</strong> La campa&ntilde;a debe tener una fecha de inicio.\
		                </div>');
				} else {
					if(fechaFin.trim() == ""){
						$('#divFechas').append('\
							<div class="alert alert-danger">\
			                    <strong>Error!</strong> La campa&ntilde;a debe tener una fecha de finalizaci&oacute;n.\
			                </div>');
					} else {
						if(arrayUsuariosInvitados.length == 0){
							$('#formUsuarios').append('\
								<div class="alert alert-danger">\
				                    <strong>Error!</strong> La campa&ntilde;a debe contar con usuarios invitados.\
				                </div>');
						} else {
							/*mostrarAlertaConfirmacion("Campa&ntilde;a", "Est&aacute; seguro que desea lanzar la campa&ntilde;a?", 
								"S&iacute;, lanzar la campa&ntilde;a",
								function(event){
									//enviar campanha tal y como esta
									event.preventDefault();
									$('#myModal').modal('hide');
									borrarVestigiosModal();*/
									var params = {
										adminName: getAdminUser(),
										password: getAdminPass(),
										nombre: nombreCampanha,
										mensaje: mensajeCampanha,
										fechaLanzamiento: fechaInicio,
										fechaFinalizacion: fechaFin,
										voluntariosInvitados: JSON.stringify(arrayUsuariosInvitados)
									}

									ajaxRequest("/admin/campanha", "POST", params, function(response){
										var response = JSON.parse(response);
										borrarVestigiosModal();
										if(response.error == true){
											mostrarAlerta("Error", response.msj);
										} else {
											//mostramos los voluntarios no invitados
											var arrayNoInvitados = JSON.parse(response.msj);
											if(arrayNoInvitados.length == 0){
												mostrarAlerta("&Eacute;xito", "La campa&ntilde;a ha sido lanzada.");
											} else {
												var mensaje = "La campa&ntilde;a ha sido lanzada. Sin embargo, los siguientes voluntarios "+
														"no pudieron ser invitados.";
												var listaHTML = '<br>\
												<div class="panel-body">\
													<div class="list-group">';
												for(var k=0; k<arrayNoInvitados.length; k++){
													listaHTML += '\
														<a class="list-group-item">\
															<i class="fa fa-fw fa-user"></i> '+arrayNoInvitados[k]+'\
														</a>';
												}
												listaHTML += '</div></div>';
												mostrarAlerta("&Eacute;xito", mensaje + listaHTML);
												//limpiamos todos los datos
												$('#inputNombre').val("");
												$('#inputMensaje').val("");
												$('#fechaInicio').val("");
												$('#fechaFin').val("");
												$('#inputBuscar').val("");
												$('#listaUsuarios').empty();
											}
										}
									});
								//}
							//);
						}
					}
				}
			}
		}

	});
	
	//ocultar la vista de error cuando se haga click fuera de el
	$(document).mouseup(function (e) {
	    var container = $(".alert");

	    if (!container.is(e.target) // if the target of the click isn't the container...
	        && container.has(e.target).length === 0) // ... nor a descendant of the container
	    {
	        container.hide();
	    }
	});
});