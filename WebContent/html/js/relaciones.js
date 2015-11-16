$(document).ready(function(){

	$('#voluntarios').addClass("active");
	//$('#btnVerCampanha').hide();
	$('#modalCampanha').modal('hide');

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
			$('#btnVerCampanha').show();

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
				jsonData.nodes[i]["size"] = 1;
			}

			//var jsonData = {"nodes":[{"id":"n0","label":"A node","x":0,"y":0,"size":3},{"id":"n1","label":"Another node","x":-3,"y":1,"size":2},{"id":"n2","label":"And a last one","x":1,"y":-5,"size":1}],"edges":[{"id":"e0","source":"n0","target":"n1"},{"id":"e1","source":"n1","target":"n2"}]};
			s = new sigma({ 
		        graph: jsonData,
		        container: document.getElementById('graph-container'),
		        settings: {
		            defaultNodeColor: '#ec5148'
		        }
			});

			s.bind('clickNode', function(e) {
				console.log(e.type, e.data.node.label, e.data.node.id, e.data.captor);
				//verificamos si es para agregar a una campanha
				if(localStorage.getItem("nuevaCampanha") != undefined){
					var campanhaTemp = JSON.parse(localStorage.getItem("nuevaCampanha"));
					var arrayUsuariosInvitados = JSON.parse(campanhaTemp.voluntariosInvitados);
					var yaExiste = false;
					for(var j=0; j<arrayUsuariosInvitados.length; j++){
						if(e.data.node.id == arrayUsuariosInvitados[j]){
							yaExiste = true;
							break;
						}
					}
					if(!yaExiste){
						arrayUsuariosInvitados.push(e.data.node.id);
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
				}
			});


			//mostramos los datos de la campanha si es que existe una guardada
			if(localStorage.getItem("nuevaCampanha") != undefined){
				$('#btnVerCampanha').show();
				var campanhaTemp = JSON.parse(localStorage.getItem("nuevaCampanha"));
				$('#nombreCampanha').html(campanhaTemp.nombre);
				$('#mensajeCampanha').html(campanhaTemp.mensaje);
				$('#fechaInicio').html('fecha de inicio: ' + campanhaTemp.fechaInicio);
				$('#fechaFin').html('fecha de finalizaci&oacute;n: ' + campanhaTemp.fechaFin);
			}
		}

		$('#btnCampanha').click(function (event){
			event.preventDefault();
			$('#modalCampanha').modal('show');
		});
	});
});