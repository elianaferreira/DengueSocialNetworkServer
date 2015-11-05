$(document).ready(function(){

	var COLOR_ZONA_TRES = "#FF0000";
	var COLOR_ZONA_DOS = "#FF8000";
	var COLOR_ZONA_UNO = "#FFBF00";


	$('#map').addClass("active");
	$('#btnVerZonas').hide();

	//representa al mapa
	var map;

	var coordenadasZonaTres = [];
	var coordenadasZonaDos = [];
	var coordenadasZonaUno = [];

	var x = document.getElementById("page-wrapper");
	if(navigator.geolocation){

		var mapProp = {
		    zoom:16,
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

				//agregamos el boton para ver las zonas
				$('#btnVerZonas').show();
				var reportesArray = JSON.parse(responseJSON.msj);
				for(var i=0; i<reportesArray.length; i++){
					var reporte = reportesArray[i];
					//mostramos solo aquellos que tienen posicion, ya que en el fake hay quienes no tienen
					if(reporte.hasOwnProperty("latitud")){
						var jsonTemp = {};
						jsonTemp["lat"] = reporte["latitud"];
						jsonTemp["lng"] = reporte["longitud"];
						
						if(reporte["ranking"] == 3){
							coordenadasZonaTres.push(jsonTemp);
						} else if(reporte["ranking"] == 2){
							coordenadasZonaDos.push(jsonTemp);
						} else {
							coordenadasZonaUno.push(jsonTemp);
						}


						//agregamos el marker
						var marker = new google.maps.Marker({
							id: reporte["id"],
							map: map,
						    position: {lat: reporte["latitud"], lng: reporte["longitud"]},
						    title:reporte["nombre"]
						});
						//marker en verde solo en caso de que sea un marker solucionado
						if(reporte["solucionado"] == true){
							marker.setIcon('http://maps.google.com/mapfiles/ms/icons/green-dot.png');
						}

						//marker.addListener('click', showReport(marker));
						// To add the marker to the map, call setMap();
						//marker.setMap(map);

						mostrarID(marker);
					}
				}

				/*/ Construct the polygon.
				var bermudaTriangle = new google.maps.Polygon({
					paths: coordenadas,
					strokeColor: '#FF0000',
					strokeOpacity: 0.8,
					strokeWeight: 2,
					fillColor: '#FF0000',
					fillOpacity: 0.35
				});
				bermudaTriangle.setMap(map);*/
			}
		});
	});

	$('#btnVerZonas').click(function(event){
		event.stopPropagation();
		drawZones();
	});



	function mostrarID(marker){
		marker.addListener('click', function() {
			//alert(marker.id);
			localStorage.setItem("idPost", marker.id);
			window.open("post.html", "_self");
		});
	}


	function drawZones(){
		//Zona 3
		if(coordenadasZonaTres.length > 0){
			var zonaTres = new google.maps.Polygon({
				paths: coordenadasZonaTres,
				strokeColor: COLOR_ZONA_TRES,
				strokeOpacity: 0.8,
				strokeWeight: 2,
				fillColor: COLOR_ZONA_TRES,
				fillOpacity: 0.35
			});
			zonaTres.setMap(map);
		}

		if(coordenadasZonaDos.length > 0){
			var zonaTres = new google.maps.Polygon({
				paths: coordenadasZonaDos,
				strokeColor: COLOR_ZONA_DOS,
				strokeOpacity: 0.8,
				strokeWeight: 2,
				fillColor: COLOR_ZONA_DOS,
				fillOpacity: 0.35
			});
			zonaTres.setMap(map);
		}

		if(coordenadasZonaUno.length > 0){
			var zonaTres = new google.maps.Polygon({
				paths: coordenadasZonaUno,
				strokeColor: COLOR_ZONA_UNO,
				strokeOpacity: 0.8,
				strokeWeight: 2,
				fillColor: COLOR_ZONA_UNO,
				fillOpacity: 0.35
			});
			zonaTres.setMap(map);
		}
	}
	
});