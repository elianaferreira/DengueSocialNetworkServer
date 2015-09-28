$(document).ready(function(){

	$('#charts').addClass("active");

	new Morris.Line({
		// ID of the element in which to draw the chart.
		element: 'cantUsuarios',
		// Chart data records -- each entry in this array corresponds to a point on
		// the chart.
		data: [
			{ year: '2008', value: 17 },
			{ year: '2009', value: 18 },
			{ year: '2010', value: 5 },
			{ year: '2011', value: 10 },
			{ year: '2012', value: 20 }
		],
		// The name of the data record attribute that contains x-values.
		xkey: 'year',
		// A list of names of data record attributes that contain y-values.
		ykeys: ['value'],
		// Labels for the ykeys -- will be displayed when you hover over the
		// chart.
		labels: ['Value'],
		lineColors: ["#1B5E20"]
	});


	//postsPorFecha

	Morris.Bar({
		element: 'postsPorFecha',
		axes: false,
		data: [
			{x: '2011 Q1', y: 3},
			{x: '2011 Q2', y: 2},
			{x: '2011 Q3', y: 4},
			{x: '2011 Q4', y: 1},
			{x: '2011 Q4', y: 7}
		],
		xkey: 'x',
		ykeys: ['y'],
		labels: ['posts'],
		barColors: ["#1B5E20"],
		xLabelFormat : function (x) { return x.toString(); }
	});


	Morris.Bar({
		element: 'solucionadosPorFecha',
		axes: false,
		data: [
			{x: '2011 Q1', y: 7, z: 2},
			{x: '2011 Q2', y: 5, z: 3},
			{x: '2011 Q3', y: 10, z: 7},
			{x: '2011 Q4', y: 9, z: 6},
			{x: '2011 Q4', y: 7, z: 4}
		],
		xkey: 'x',
		ykeys: ['y', 'z'],
		labels: ['posts', 'solucionados'],
		barColors: function (row, series, type) {
		    if (type === 'bar') {
		      var red = Math.ceil(255 * row.y / this.ymax);
		      return 'rgb(' + red + ',0,0)';
		    }
		    else {
		      return '#000';
		    }
  		}
	});
	
});