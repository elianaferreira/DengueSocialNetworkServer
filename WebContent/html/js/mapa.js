$(document).ready(function(){

	//representa al mapa
	var map;

	var x = document.getElementById("page-wrapper");
	if(navigator.geolocation){

		var mapProp = {
		    zoom:12,
		    mapTypeId:google.maps.MapTypeId.ROADMAP
		};
	  	map = new google.maps.Map(document.getElementById("googleMap"), mapProp);


		navigator.geolocation.getCurrentPosition(function(position) {
    
	        var geolocate = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
	        
	        var infowindow = new google.maps.InfoWindow({
	            map: map,
	            position: geolocate,
	            content:'<span>Usted esta aqui</span>'
	        });
	        
	        map.setCenter(geolocate);
	    });
	} else {
		x.innerHTML = "Geolocaclizaci&oacute; no es soportada por este navegador.";
		//TODO desactivar el boton
	}


	//traemos todos los reportes y los posicionamos sobre el mapa
	$('#btnAcceder').click(function(event){
		event.stopPropagation();

		var params = {
			admin: getAdminUser(),
			password: getAdminPass()
		};

		ajaxRequest("/admin/allPosts", "GET", params, function(response){
			var responseJSON = JSON.parse(response);
			if(responseJSON.error == true) {
				mostrarAlerta('Error', responseJSON.msj);
			} else {
				var reportesArray = JSON.parse(responseJSON.msj);
				for(var i=0; i<reportesArray.length; i++){
					var reporte = reportesArray[i];
					//mostramos solo aquellos que tienen posicion, ya que en el fake hay quienes no tienen
					if(reporte.hasOwnProperty("latitud")){
						//agregamos el marker
						var marker = new google.maps.Marker({
						    position: {lat: reporte["latitud"], lng: reporte["longitud"]},
						    title:reporte["nombre"]
						});
						// To add the marker to the map, call setMap();
						marker.setMap(map);
					}
				}
			}
		});
	});
});