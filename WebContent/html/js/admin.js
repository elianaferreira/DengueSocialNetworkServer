$(document).ready(function(){

	$('#dashboard').addClass("active");

	$('#nombreAdmin').html(localStorage.getItem("adminNombre") + " " + localStorage.getItem("adminApellido"));
	
});