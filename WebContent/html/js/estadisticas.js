$(document).ready(function(){

	$('#wrapper').hide();
	var paramValidate = {
		accessToken: getAccessToken()
	}
	ajaxRequest("/admin/validateAccessToken", "POST", paramValidate, function(responseValidateAccessToken){
		responseValidateAccessToken = JSON.parse(responseValidateAccessToken);
		if(responseValidateAccessToken.error == true){
			window.open("login.html", "_self");
		} else {
			$('#wrapper').show();
			$('#charts').addClass("active");

			var params = {
				admin: getAdminUser(),
				accessToken: getAccessToken(),
			}

			ajaxRequest("/admin/subtotalesReportes", "GET", params, function(responseSubtotales){
				responseSubtotales = JSON.parse(responseSubtotales);
				if(responseSubtotales.error == true){
					//TODO mostrar un mensaje de error solo en el div correspondiente al chart
				} else {
					var datosJSON = JSON.parse(responseSubtotales.msj);
					var data = [{
				        label: "Solucionados",
				        data: datosJSON.solucionados,
				        color: "#66BB6A"
				    }, {
				        label: "No Solucionados",
				        data: datosJSON.noSolucionados,
				        color: "#FF5252"
				    }];

				    //mostrar el porcentaje: content: "%p.0%, %s", // show percentages, rounding to 2 decimal places
				    //mostrar el valor: http://stackoverflow.com/a/24413739/4173916
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
				            content: function(label,x,y){
		                        return y+" "+label;
		                    },
				            shifts: {
				                x: 20,
				                y: 0
				            },
				            defaultTheme: false
				        }
				    });
				}
			});

			//subtotalesNoSolucionadosPie

			ajaxRequest("/admin/quienDebeSolucionar", "GET", params, function(responseSubtotales){
				responseSubtotales = JSON.parse(responseSubtotales);
				if(responseSubtotales.error == true){
					//TODO mostrar un mensaje de error solo en el div correspondiente al chart
				} else {
					var datosJSON = JSON.parse(responseSubtotales.msj);
					var dataArray = [];
					for(key in datosJSON){
						var jsonTemp = {};
						jsonTemp["label"] = key;
						jsonTemp["data"] = datosJSON[key];
						dataArray.push(jsonTemp);
					}

				    //mostrar el porcentaje: content: "%p.0%, %s", // show percentages, rounding to 2 decimal places
				    //mostrar el valor: http://stackoverflow.com/a/24413739/4173916
				    var plotObj = $.plot($("#subtotalesNoSolucionadosPie"), dataArray, {
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
				            content: function(label,x,y){
		                        return y+" "+label;
		                    },
				            shifts: {
				                x: 20,
				                y: 0
				            },
				            defaultTheme: false
				        }
				    });
				}
			});


			ajaxRequest("/admin/usuariosPorMes", "GET", params, function(responseUsers){
				responseUsers = JSON.parse(responseUsers);
				if(responseUsers.error == true){
					//no hacer nada
				} else {
					var datos = JSON.parse(responseUsers.msj);
					var dataArray = [];
					for(key in datos){
						var jsonTemp = {};
						jsonTemp["fecha"] = key;
						jsonTemp["cantidad"] = datos[key];
						dataArray.push(jsonTemp);
					}

					new Morris.Line({
						// ID of the element in which to draw the chart.
						element: 'cantUsuarios',
						// Chart data records -- each entry in this array corresponds to a point on
						// the chart.
						data: dataArray,
						// The name of the data record attribute that contains x-values.
						xkey: 'fecha',
						// A list of names of data record attributes that contain y-values.
						ykeys: ['cantidad'],
						// Labels for the ykeys -- will be displayed when you hover over the
						// chart.
						labels: ['Usuarios registrados'],
						lineColors: ["#337ab7"],
						parseTime:false
					});
				}
			});

			ajaxRequest("/admin/activeCampaigns", "GET", params, function(responseCampaings){
				responseCampaings = JSON.parse(responseCampaings);
				if(responseCampaings.error == true){
					//no hacer nada
				} else {
					var arrayData = JSON.parse(responseCampaings.msj);

					new Morris.Bar({
				        element: 'bar-chart-campaigns',
				        data: arrayData,
				        xkey: 'nombre',
				        ykeys: ['cantInvitados', 'cantAdheridos'],
				        labels: ['Invitados', 'Adheridos'],
				        barRatio: 0.4,
				        xLabelAngle: 35,
				        hideHover: 'auto',
				        resize: true
	    			});
				}
			});
		}
	});
});