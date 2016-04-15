$(document).ready(function(){

	//acceder a la vista de admin
	$('#btnAcceder').click(function (e){
		e.preventDefault();
		var username = $('#usernameInput').val();
		var password = $('#passInput').val();
		if(username == null || username == ""){
			$('#divUsername').append('\
				<div class="alert alert-danger">\
                    <strong>Error!</strong> Se necesita su nombre de usuario.\
                </div>');
		} else if(password == null || password == ""){
			$('#divPassword').append('\
				<div class="alert alert-danger">\
                    <strong>Error!</strong> Se necesita su contrase&ntilde;a.\
                </div>');
		} else {
			var md5Pass = CryptoJS.MD5(password).toString();
			ajaxRequest("/admin/auth", "POST", {name: username, password: md5Pass}, function(response){
				var jsonResponse = JSON.parse(response);
				if(jsonResponse.error == true){
					mostrarAlerta('Error', jsonResponse.msj);
				} else {
					var adminJson = JSON.parse(jsonResponse.msj);
					localStorage.setItem("accessToken", adminJson["accessToken"]);
					localStorage.setItem("adminUser", adminJson["adminname"]);
					localStorage.setItem("adminNombre", adminJson["nombre"]);
					localStorage.setItem("adminApellido", adminJson["apellido"]);
					window.open("estadisticas.html", "_self");
				}
			});
		}
	});


	//ocultar la vista de error cuando se haga click fuera de el
	$(document).mouseup(function (e) {
	    var container = $(".alert");

	    if (!container.is(e.target) // if the target of the click isn't the container...
	        && container.has(e.target).length === 0) // ... nor a descendant of the container
	    {
	        container.remove();
	    }
	});


})