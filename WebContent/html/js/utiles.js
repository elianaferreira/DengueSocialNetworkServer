var RANDOM_MINIMO = -10;
var RANDOM_MAXIMO = 10;
var EMAIL_REGEX = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$/i;	//"/i" es para que sea incasesensitive
var USERNAME_REGEX = /^[a-zA-Z0-9]*$/i;


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
		        <button type="button" class="btn btn-default" data-dismiss="modal">No</button>\
		        <button id="btnSuccess" type="button" class="btn btn-success" data-dismiss="modal">' + boton + '</button>\
		      </div>\
		    </div>\
		  </div>\
		</div>');

	$('#myModal').modal('show');

	$('#btnSuccess').click(function (e) {
		callback(e);
	});	
}


/**
*	Metodo que muestra una alerta con dos opciones personalizadas
*/
function mostrarConfirmacionDosOpciones(titulo, mensaje, primerBoton, segundoBoton, callbackPrimerBoton, callbackSegundoBoton){
	$('#wrapper').prepend('\
		<div class="modal fade" id="modalDosOpciones" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">\
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
		        <button id="btnPrimerBoton" type="button" class="btn btn-default" data-dismiss="modal">' + primerBoton + '</button>\
		        <button id="btnSegundoBoton" type="button" class="btn btn-success data-dismiss="modal">' + segundoBoton + '</button>\
		      </div>\
		    </div>\
		  </div>\
		</div>');

	$('#modalDosOpciones').modal('show');

	$('#btnPrimerBoton').click(function (e) {
		callbackPrimerBoton(e);
	});

	$('#btnSegundoBoton').click(function (e) {
		callbackSegundoBoton(e);
	});
}


function getAccessToken(){
	if(localStorage.getItem("accessToken") == undefined){
		return "";
	} else {
		return localStorage.getItem("accessToken");
	}
}

function getAdminUser(){
	return localStorage.getItem("adminUser");
}

function getAdminPass(){
	return localStorage.getItem("admiPass");
}


/**
	Metodo que borra las sombras que hayan podido quedar de los alerts
*/
function borrarVestigiosModal(){
	var list = document.getElementsByClassName("modal-backdrop");
	for(var i = list.length - 1; 0 <= i; i--){
		if(list[i] && list[i].parentElement){
			list[i].parentElement.removeChild(list[i]);
		}
	}
}


/**
	Metodo que retorna el timestamp actual en el formato yyyy-MM-dd hh:mm:ss.SSS
*/
function getCurrentTimestampWithFormat(){
	var d = new Date();
	var mes = d.getMonth()+1;
	var dateString = d.getFullYear()+'-'+mes+'-'+d.getDate()+' '+d.getHours()+':'+d.getMinutes()+':'+d.getSeconds()+'.'+d.getTimezoneOffset();
	return dateString;
}


function isValidEmailFormat(email){
	var patt = new RegExp(/^[A-Z0-9._%+-]+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$/i);
    var res = patt.test(email);

	return res;
}


function isValidUsername(username){
	var patt = new RegExp(USERNAME_REGEX);
    var res = patt.test(username);

	return res;
}


function appendErrorDiv(inputId, msj){
	var elem = document.getElementById(inputId);
	if(elem != null){
		$('#'+inputId).parent().append('\
				<div class="alert alert-danger">\
                    <strong>Error!</strong> '+msj+'\
                </div>');
	}

}