$(document).ready(function(){

	$('#dashboard').addClass("active");

	var params = {
		admin: getAdminUser(),
		password: getAdminPass(),
	}
	ajaxRequest("/admin/subtotalesReportes", "GET", params, function(responseSubtotales){
		var responseSubtotales = {"error": false, "msj":{"solucionados":2,"noSolucionados":13}};
		//responseSubtotales = JSON.parse(responseSubtotales);
		if(responseSubtotales.error == true){
			//TODO mostrar un mensaje de error solo en el div correspondiente al chart
		} else {


			var data = [{
		        label: "Solucionados",
		        data: responseSubtotales.msj.solucionados,
		        color: "#5cb85c"
		    }, {
		        label: "No Solucionados",
		        data: responseSubtotales.msj.noSolucionados,
		        color: "#d9534f"
		    }];

		    var plotObj = $.plot($("#subtotalReportesPie"), data, {
		        series: {
		            pie: {
		                show: true
		            }
		        },
		        grid: {
		            hoverable: true
		        },
		        tooltip: true,
		        tooltipOpts: {
		            content: "%p.0%, %s", // show percentages, rounding to 2 decimal places
		            shifts: {
		                x: 20,
		                y: 0
		            },
		            defaultTheme: false
		        }
		    });
		}
	});

	ajaxRequest("/admin/reportesRelevantes", "GET", params, function(responseRelevantes){
		var responseJSON = JSON.parse(responseRelevantes);
		if(responseJSON.error == false){
			var reportesArray = JSON.parse(responseJSON.msj);

			for(var i = 0; i<reportesArray.length; i++){
				var reporte = reportesArray[i];
				//post
				$('#listaRelevantes').append('\
					<a id="'+reporte.id+'" class="list-group-item">\
						<span class="badge">'+reporte.fecha+'</span>\
						<i class="fa fa-fw fa-mobile-phone"></i> '+reporte.mensaje+'\
					</a>');

			}
		}
	});
	
});