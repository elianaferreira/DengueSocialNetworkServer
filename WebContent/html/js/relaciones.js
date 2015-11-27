var arrayUsuariosInvitados = [];

var existeCampanhaGuardada = false;

$(document).ready(function(){

	$('#voluntarios').addClass("active");
	$('#modalCampanha').modal('hide');
	$('#toolbarCampanha').hide();
	/*$('#btnCampanha').hide();
	$('#btnVerCampanhaGuardada').hide();
	$('#btnVerCampanhaGuardada').parent().hide();
	$('#btnEditar').hide();*/

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



	var params = {
		admin: getAdminUser(),
		password: getAdminPass(),
	}
	ajaxRequest("/admin/allNodeContacts", "GET", params, function(response){
		response = JSON.parse(response);

		if(response.error == true){
			mostrarAlerta('Error', response.msj);
		} else {
			//$('#btnCampanha').show();
			//es necesario setear las coordenadas de los nodos
			var jsonData = JSON.parse(response.msj);

			//console.log(jsonData);
			//var randomnumber = Math.floor(Math.random() * (RANDOM_MAXIMO- -RANDOM_MINIMO+ 1)) + -RANDOM_MINIMO;
			for(var i=0; i<jsonData.nodes.length; i++){
				if(i==0){
					jsonData.nodes[i]["x"] = 0;
					jsonData.nodes[i]["y"] = 0;
				} else {
					jsonData.nodes[i]["x"] = Math.floor(Math.random() * (RANDOM_MAXIMO - RANDOM_MINIMO+ 1)) + RANDOM_MINIMO;
					jsonData.nodes[i]["y"] = Math.floor(Math.random() * (RANDOM_MAXIMO - RANDOM_MINIMO+ 1)) + RANDOM_MINIMO;
				}
				//jsonData.nodes[i]["size"] = 1;
			}

			//var jsonData = {"nodes":[{"id":"n0","label":"A node","x":0,"y":0,"size":3},{"id":"n1","label":"Another node","x":-3,"y":1,"size":2},{"id":"n2","label":"And a last one","x":1,"y":-5,"size":1}],"edges":[{"id":"e0","source":"n0","target":"n1","type":"arrow"},{"id":"e1","source":"n1","target":"n2","type":"arrow"]};
			s = new sigma({ 
		        graph: jsonData,
		        container: document.getElementById('graph-container'),
		        settings: {
		            defaultNodeColor: '#ec5148',
		            maxEdgeSize: 10
		        }
			});

			s.bind('clickNode', function(e) {
				console.log(e.type, e.data.node.label, e.data.node.id, e.data.captor);

				if(arrayUsuariosInvitados.length > 0){
					$('#btnLanzar').attr("disabled", false);
				}

				//verificamos si es para agregar a una campanha
				if(existeCampanhaGuardada){
					var yaExiste = false;
					for(var j=0; j<arrayUsuariosInvitados.length; j++){
						if(e.data.node.id == arrayUsuariosInvitados[j]){
							yaExiste = true;
							break;
						}
					}
					if(!yaExiste){
						arrayUsuariosInvitados.push(e.data.node.id);
						//agregamos al preview
						$('#detalles').append('<li><span style="padding-left: 20px; padding-right: 20px">'+e.data.node.label+'</span></li>');

						//ocultamos ese usuario de manera que no pueda ser agregado nuevamente
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
				}
			});
		}

		$('#btnCampanha').click(function (event){
			event.preventDefault();
			$('#modalCampanha').modal('show');
		});

		
	});



	/*$('#btnEditar').click(function (event){
		if(localStorage.getItem("tempCampanha") != undefined){
			event.preventDefault();
			$('#modalCampanha').modal('show');
		}
	});*/


	$('#btnGuardarCampanha').click(function (e){
		e.preventDefault();

		var nombreCampanha = $('#inputNombre').val();
		var mensajeCampanha = $('#inputMensaje').val();
		var fechaInicio = $('#fechaInicio').val();
		var fechaFin = $('#fechaFin').val();

		if(nombreCampanha.trim() == ""){
			$('#divNombreCamp').addClass('has-error');
			$('#divMensaje').removeClass('has-error');
			$('.well').removeClass('has-error');
		} else {
			if(mensajeCampanha.trim() == ""){
				$('#divMensaje').addClass('has-error');
				$('#divNombreCamp').removeClass('has-error');
				$('.well').removeClass('has-error');
			} else {
				if(fechaInicio.trim() == ""){
					$('.well').addClass('has-error');
					$('#divMensaje').removeClass('has-error');
					$('#divNombreCamp').removeClass('has-error');
				} else {
					if(fechaFin.trim() == ""){
						$('.well').addClass('has-error');
						$('#divMensaje').removeClass('has-error');
						$('#divNombreCamp').removeClass('has-error');
					} else {
						$('#divNombreCamp').removeClass('has-error');
						$('#divMensaje').removeClass('has-error');
						$('.well').removeClass('has-error');

						//guardar la campanha
						var jsonTemp = {};
						jsonTemp.nombre = nombreCampanha;
						jsonTemp.mensaje = mensajeCampanha;
						jsonTemp.fechaInicio = fechaInicio;
						jsonTemp.fechaFin = fechaFin;
						localStorage.setItem("tempCampanha", JSON.stringify(jsonTemp));
						$('#modalCampanha').modal('hide');
						//$('#btnCampanha').hide();
						//$('#btnCampanha').attr("disabled", true);
						//$('#btnVerCampanhaGuardada').show();
						//$('#btnVerCampanhaGuardada').parent().show();
						//mostramos para editar
						//$('#btnEditar').show();

						//seteamos al toggle
						$('#verNombre').html(nombreCampanha);
						$('#verMensaje').html(mensajeCampanha);
						$('#verInicio').html('inicio: '+fechaInicio);
						$('#verFin').html('fin: ' + fechaFin);

						existeCampanhaGuardada = true;

						toastr["info"]("Ahora puede agregar los voluntarios invitados haciendo click en los nodos correspondientes.", "Info")
						toastr.options = {
							"closeButton": false,
							"debug": false,
							"newestOnTop": false,
							"progressBar": false,
							"positionClass": "toast-top-center",
							"preventDuplicates": false,
							"onclick": null,
							"showDuration": "300",
							"hideDuration": "1000",
							"timeOut": "5000",
							"extendedTimeOut": "1000",
							"showEasing": "swing",
							"hideEasing": "linear",
							"showMethod": "fadeIn",
							"hideMethod": "fadeOut"
						}
					}
				}
			}
		}
	});

	
	//enviar la campanha
	$('#btnLanzar').click(function (event){
		event.preventDefault();


		if(localStorage.getItem("tempCampanha") != undefined){
			if(arrayUsuariosInvitados.length == 0){
				mostrarAlerta("Error", "La campa&ntilde;a debe contar con usuarios invitados");
			} else{


		/*mostrarConfirmacionDosOpciones("Campa&ntilde;a", "Est&aacute; seguro que desea lanzar la campa&ntilde;a?", 
			"S&iacute;, lanzar la campa&ntilde;a",
			function(event){
				//enviar campanha tal y como esta
				event.preventDefault();*/
				var tempJson = {};
				tempJson = JSON.parse(localStorage.getItem("tempCampanha"));

				var params = {
					adminName: getAdminUser(),
					password: getAdminPass(),
					nombre: tempJson.nombre,
					mensaje: tempJson.mensaje,
					fechaLanzamiento: tempJson.fechaInicio,
					fechaFinalizacion: tempJson.fechaFin,
					voluntariosInvitados: JSON.stringify(arrayUsuariosInvitados)
				}

				ajaxRequest("/admin/campanha", "POST", params, function(response){
					var response = JSON.parse(response);
					borrarVestigiosModal();
					if(response.error == true){
						mostrarAlerta("Error", response.msj);
					} else {
						localStorage.removeItem("tempCampanha");
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
						}
					}
				});
			//}
		//);
			}
		}
	});
});