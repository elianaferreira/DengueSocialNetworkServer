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
	                    <li id="dashboard">\
	                        <a href="admin.html"><i class="fa fa-fw fa-dashboard"></i> Dashboard</a>\
	                    </li>\
	                    <li id="charts">\
	                        <a href="estadisticas.html"><i class="fa fa-fw fa-bar-chart-o"></i> Estad&iacute;sticas</a>\
	                    </li>\
	                    <li id="voluntarios">\
	                        <a href="voluntarios.html"><i class="fa fa-fw fa-user"></i> Voluntarios</a>\
	                    </li>\
	                    <li id="map">\
	                        <a href="mapa.html"><i class="fa fa-fw fa-map-marker"></i> Mapa</a>\
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
		//TODO enviar post al server
		window.open("login.html", "_self");
	})
});