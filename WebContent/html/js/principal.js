//agregar el header, y el menu

$(document).ready(function(){
	$('#wrapper').prepend('\
		<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">\
	            <!-- Brand and toggle get grouped for better mobile display -->\
	            <div class="navbar-header">\
	                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">\
	                    <span class="sr-only">Toggle navigation</span>\
	                    <span class="icon-bar"></span>\
	                    <span class="icon-bar"></span>\
	                    <span class="icon-bar"></span>\
	                </button>\
	                <a class="navbar-brand">Administrador</a>\
	            </div>\
	            <!-- Top Menu Items -->\
	            <ul class="nav navbar-right top-nav">\
	                <li class="dropdown">\
	                    <a id="nombreAdmin" href="#" class="dropdown-toggle" data-toggle="dropdown">'+
	                    localStorage.getItem("adminNombre") + " " + localStorage.getItem("adminApellido")+'<b class="caret"></b></a>\
	                    <ul class="dropdown-menu">\
	                        <li>\
	                            <a href="profile.html"><i class="fa fa-fw fa-user"></i> Perfil</a>\
	                        </li>\
	                        <li>\
	                            <a href="#"><i class="fa fa-fw fa-gear"></i> Config</a>\
	                        </li>\
	                        <li class="divider"></li>\
	                        <li>\
	                            <a id="cerrarSesion" href=""><i class="fa fa-fw fa-power-off"></i> Cerrar Sesi&oacute;n</a>\
	                        </li>\
	                    </ul>\
	                </li>\
	            </ul>\
	            <!-- Sidebar Menu Items - These collapse to the responsive navigation menu on small screens -->\
	            <div class="collapse navbar-collapse navbar-ex1-collapse">\
	                <ul class="nav navbar-nav side-nav">\
	                <li class="opcion" id="charts" data-url="estadisticas.html">\
	                        <a href=""><i class="fa fa-fw fa-bar-chart-o"></i> Estad&iacute;sticas</a>\
	                    </li>\
	                    <li class="opcion" id="dashboard" data-url="admin.html">\
	                        <a href=""><i class="fa fa-fw fa-pencil"></i> Reportes</a>\
	                    </li>\
	                    <li class="opcion" id="voluntarios" data-url="voluntarios.html">\
	                        <a href=""><i class="fa fa-fw fa-user"></i> Voluntarios</a>\
	                    </li>\
	                    <li class="opcion" id="map" data-url="mapa.html">\
	                        <a href=""><i class="fa fa-fw fa-map-marker"></i> Mapa</a>\
	                    </li>\
	                    <li>\
                        <a id="adminSection" href="javascript:;" data-toggle="collapse" data-target="#adminDD"><i class="fa fa-fw fa-arrows-v"></i> Administrador <i class="fa fa-fw fa-caret-down"></i></a>\
                        <ul id="adminDD" class="collapse">\
                            <li>\
                                <a href="agregarAdministrador.html">Agregar</a>\
                            </li>\
                            <li>\
                                <a href="profile.html">Modificar</a>\
                            </li>\
                            <li>\
                                <a href="eliminarAdministrador.html">Eliminar</a>\
                            </li>\
                        </ul>\
                    </li>\
	                </ul>\
	            </div>\
	            <!-- /.navbar-collapse -->\
	    	</nav>');


	$('#dashboard').click(function (e) {
		e.preventDefault();
		//obtenemos el nombre de la pagina
		var pagePathName = window.location.pathname;
		var pageName = pagePathName.substring(pagePathName.lastIndexOf("/") + 1);
		console.log(pageName);
		if(pageName != "admin.html"){
			window.open("admin.html", "_self");
		}
	});


	$('#cerrarSesion').click(function (e) {
		e.preventDefault();
		ajaxRequest("/admin/logout", "POST", {name: getAdminUser, accessToken: getAccessToken()}, function(response){
			var response = JSON.parse(response);
			if(response.error == true){
				mostrarAlerta("Error", "Hubo un error al cerrar la sesi&oacute;n. Int&eacute;ntelo m&aacute;s tarde.");
			} else {
				localStorage.clear();
				window.open("login.html", "_self");
			}
		});
		
	})

	$('.opcion').click(function (event) {
		event.preventDefault();
		/*for(key in localStorage){
			if(key != "admiPass" && key != "adminApellido" && key != "adminName" && key != "adminNombre" && key != "adminUser" && key != "accessToken"){
				localStorage.removeItem(key);
			}
		}*/
		removeDataFromLocalStorage();
		//abrimos la vista correspondiente
		var url = $(this).data("url");
		window.open(url, "_self");

	});
});

function removeDataFromLocalStorage(){
	for(key in localStorage){
		if(key != "admiPass" && key != "adminApellido" && key != "adminName" && key != "adminNombre" && key != "adminUser"  && key != "accessToken"){
			localStorage.removeItem(key);
		}
	}
}