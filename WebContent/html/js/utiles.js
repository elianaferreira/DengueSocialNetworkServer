
/**
*	Metodo que despliega un mensaje de alerta simple
*/
function mostrarAlerta(titulo, mensaje){
	$('#page-wrapper').prepend('\
		<div class="modal fade" id="myModal" role="dialog">\
			<div class="modal-dialog">\
			  <!-- Modal content-->\
			  <div class="modal-content">\
			  	<div class="modal-header">\
			    	<button type="button" class="close" data-dismiss="modal">&times;</button>\
			    	<h4 class="modal-title">' + titulo + '</h4>\
			    </div>\
			    <div class="modal-body">\
			    	<p>' + mensaje + '</p>\
			    </div>\
			    <div class="modal-footer">\
			    	<button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>\
			    </div>\
			  </div>\
			</div>\
	  	</div>');

	$('#myModal').modal('show');

}


/**
*	Metodo que despliega un mensaje de confirmacion
*/
function mostrarAlertaConfirmacion(titulo, mensaje, boton, callback){
	$('#wrapper').prepend('\
		<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">\
		  <div class="modal-dialog" role="document">\
		    <div class="modal-content">\
		      <div class="modal-header">\
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>\
		        <h4 class="modal-title" id="myModalLabel">' + titulo + '</h4>\
		      </div>\
		      <div class="modal-body">\
		        ' + mensaje + '\
		      </div>\
		      <div class="modal-footer">\
		        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>\
		        <button id="btnSuccess" type="button" class="btn btn-success">' + boton + '</button>\
		      </div>\
		    </div>\
		  </div>\
		</div>');

	$('#myModal').modal('show');

	$('#btnSuccess').click(function (e) {
		callback(e);
	});	
}



function getAdminUser(){
	return localStorage.getItem("adminUser");
}

function getAdminPass(){
	return localStorage.getItem("admiPass");
}


