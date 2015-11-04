$(document).ready(function(){

	$('#voluntarios').addClass("active");

	var params = {
		admin: getAdminUser(),
		password: getAdminPass(),
	}
	ajaxRequest("/admin/allNodeContacts", "GET", params, function(response){
		response = JSON.parse(response);

		if(response.error == true){
			mostrarAlerta('Error', response.msj);
		} else {
			//es necesario setear las coordenadas de los nodos
			var jsonData = JSON.parse(response.msj);

			//console.log(jsonData);
			//var randomnumber = Math.floor(Math.random() * (RANDOM_MAXIMO- -RANDOM_MINIMO+ 1)) + -RANDOM_MINIMO;
			for(var i=0; i<jsonData.nodes.length; i++){
				if(i==0){
					jsonData.nodes[i]["x"] = 0;
					jsonData.nodes[i]["y"] = 0;
				} else {
					jsonData.nodes[i]["x"] = Math.floor(Math.random() * (RANDOM_MAXIMO - RANDOM_MINIMO+ 1)) + RANDOM_MINIMO;
					jsonData.nodes[i]["y"] = Math.floor(Math.random() * (RANDOM_MAXIMO - RANDOM_MINIMO+ 1)) + RANDOM_MINIMO;
				}
				jsonData.nodes[i]["size"] = 1;
			}

			//var jsonData = {"nodes":[{"id":"n0","label":"A node","x":0,"y":0,"size":3},{"id":"n1","label":"Another node","x":-3,"y":1,"size":2},{"id":"n2","label":"And a last one","x":1,"y":-5,"size":1}],"edges":[{"id":"e0","source":"n0","target":"n1"},{"id":"e1","source":"n1","target":"n2"}]};
			s = new sigma({ 
		        graph: jsonData,
		        container: document.getElementById('graph-container'),
		        settings: {
		            defaultNodeColor: '#ec5148'
		        }
			});

			s.bind('clickNode doubleClickNode rightClickNode', function(e) {
				console.log(e.type, e.data.node.label, e.data.node.id, e.data.captor);
			});
		}
	});
});