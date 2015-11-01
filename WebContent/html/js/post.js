$(document).ready(function(){
	$('#voluntarios').addClass("active");

	var params = {
		admin: getAdminUser(),
		password: getAdminPass(),
		idPost: parseInt(localStorage.getItem("idPost"))
	};

    ajaxRequest("/admin/post", "GET", params, function(response){
    	var responseJSON = JSON.parse(response);
    	if(responseJSON.error == true){
    		mostrarAlerta('Error', responseJSON.msj);
    	} else {
    		var post = JSON.parse(responseJSON.msj);
    		var panelColor = '';
    		if(post.solucionado == true){
    			panelColor = 'panel-green';
    		} else {
    			panelColor = 'panel-red';
    		}
	    	$('.container-fluid').append('\
	    		<div class="col-sm-5">\
                    <div class="panel '+panelColor+'">\
                        <div class="panel-heading">\
                            <div class="row">\
                                <div class="col-xs-3">\
                                    <i id="iFotoPerfil" class="fa fa-user fa-5x"></i>\
                                    <img id="fotoReal" style="display: none;"/>\
                                </div>\
                                <div class="col-xs-9 text-right">\
                                    <div class="huge">'+post.voluntario.nombre+'</div>\
                                    <div>'+post.voluntario.usernamestring+'</div>\
                                </div>\
                            </div>\
                        </div>\
                        <div class="panel-body">'
                        	+post.mensaje+
                        '</div>\
                    </div>\
                </div>');


	    	//llamado para ver la foto de perfil del usuario
	    	ajaxRequest("/users/user/profilePhoto/"+post.voluntario.username, "GET", {}, function(responseProfile){
	    		var rpp = JSON.parse(responseProfile);
	    		if(rpp.error == false){
	    			//setear la foto de perfil
	    			$('#iFotoPerfil').hide();
	    			document.getElementById('fotoReal').setAttribute( 'src', 'data:image/png;base64'+responseProfile.msj);
	    			$('#fotoReal').show();
	    		}
	    	});
    	}
    });

});