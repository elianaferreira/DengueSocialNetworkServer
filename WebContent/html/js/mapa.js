$(document).ready(function(){

	var x = document.getElementById("page-wrapper");
	if(navigator.geolocation){

		var mapProp = {
		    zoom:15,
		    mapTypeId:google.maps.MapTypeId.ROADMAP
		};
	  	var map=new google.maps.Map(document.getElementById("googleMap"), mapProp);


		navigator.geolocation.getCurrentPosition(function(position) {
    
        var geolocate = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
        
        var infowindow = new google.maps.InfoWindow({
            map: map,
            position: geolocate,
            content:
                '<span>Usted está aquí</span>'
        });
        
        map.setCenter(geolocate);
    });
	} else {
		x.innerHTML = "Geolocaclizaci&oacute; no es soportada por este navegador.";
	}

});