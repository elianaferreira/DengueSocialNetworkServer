//var URL_BASE = "http://localhost:8080/RestServerSNGlass/rest";
var URL_BASE = "http://jbossas-eferreiran.rhcloud.com/api/ws";


function ajaxRequest(pUrl, pType, pData, pSuccessCallback){
	var url = URL_BASE + pUrl;
	console.log("Request to: " + url + " with params: " + JSON.stringify(pData));
        $.ajax({
            type : pType,
            url  : url,
            contentType: 'application/x-www-form-urlencoded',
            data : pData,
            timeout:(1000 * 120)
        })
        .done(function(response){
            var res = JSON.stringify(response);
            console.log("Response from "+url+": "+res);
            pSuccessCallback(response); //tanto si tiene error true or false
        }).fail(function (error) {
            mostrarAlerta("Error", "Ha ocurrido un error, intentalo más tarde.");
        });

}