function searchSparql(word){
    var encodedWord = word;
    var query = [
	"PREFIX dbetym: <http://kaiko.getalp.org/dbnaryetymology#>",
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
	"SELECT DISTINCT ?iri (group_concat(distinct ?ee ; separator=\",\") as ?et) ",
	"WHERE {",
	"    ?iri rdfs:label ?label . ?label bif:contains \"\'" + encodedWord + "\'\" .",
	//exclude entries that contain the searched word but include other words
	//(e.g.: search="door" label="doorbell", exclude "doorbell")  
	"    FILTER REGEX(?label, \"^" + encodedWord + "$\", 'i') .",
	"    ?iri rdf:type dbetym:EtymologyEntry .",
	"    OPTIONAL {",
	"        ?iri dbnary:refersTo ?ee .",
	"        ?ee rdf:type dbetym:EtymologyEntry .",
	"    }",
	"}"
    ];
    return query.join(" ");
}

function search(word){ 
    var url = ENDPOINT + "?query=" + encodeURIComponent(searchSparql(word));
    if (debug) {
	console.log(url);
    }

    var width = window.innerWidth
    height = $(document).height() - $(header).height();

    d3.xhr(url, MIME, function(request) { 
	if (request != null) {
	    //clean screen
	    d3.select("#tree-overlay").remove();
	    d3.select("#message").remove();
	    d3.select("#p-helpPopup").remove();
	    d3.select("#myPopup").style("display", "none");

	    //change help message
	    d3.select("#helpPopup") 
		.append("p") 
		.attr("id", "p-helpPopup")
	        .attr("class", "help")
		.html("<b>Disambiguation page</b>" +
		      "<br>Pick the word you are interested in." +
		      "<ul>" +
		      "<li>Click on a circle to display the language</li>" +
		      "<li>Click on a word to display lexical information</li>" +
		      "<li>Double click on a circle to choose a word</li>" +
		      "</ul>");   
	    
	    //perform query
	    var json = JSON.parse(request.responseText);

	    var graph = json.results.bindings; 
	    if (graph.length == 0){
		d3.select("#tree-container") 
		    .append("p") 
		    .attr("id", "message") 
		    .attr("align", "center") 
		    .html("This word is not available in the database")
		    .append("p");  
	    }

	    var nodes = {};
	    graph.forEach(function(n){ 
		var iris = n.et.value.split(",");
		if (iris == ""){ 
		    nodes[n.iri.value] = new Node(n.iri.value);
		} else { 
		    if (iris.length > 1){  
			nodes[n.iri.value] = new Node(n.iri.value);
		    }
		    iris.forEach(function(element) {  
			nodes[element] = new Node(element);
		    });
		}
	    }) 
	    if (debug) {
		console.log(nodes); 
	    }

	    var force = d3.layout.force()
		.nodes(d3.values(nodes))
		.size([width, height])   
		.charge(-700)
		.on("tick", tick)
		.start();

	    var svg = d3.select("#tree-container").append("svg")
		.attr("id", "tree-overlay")
		.attr("width", width)
		.attr("height", height) 
		.on("click", function(){
		    d3.select("#myPopup")
			.style("display", "none");
		    d3.event.stopPropagation();
		});
	    
	    var circle = svg.append("g").selectAll("circle") 
		.data(force.nodes()) 
		.enter().append("circle")
                .attr("class", "circle")
                .attr("r", 12)
		.call(force.drag)   
		.on("mouseover", function(d) {   
		    d3.select(this).style("cursor", "pointer");
		})
		.on("click", function(d) {
		    d3.selectAll("circle").attr("fill", "#ffb380"); 
		    d3.select(this)  
			.append("a") 
			.attr("href", "#myPopup")
			.attr("data-rel", "popup") 
			.attr("data-transition", "pop");
		    d3.select("#myPopup")
			.style("width", "auto") 
			.style("height", "auto")   
			.style("display", "inline");
		    d3.select("#myPopup")
			.html(d.lang)   
			.style("left", (d3.event.pageX) + "px")
			.style("top", (d3.event.pageY - 28) + "px");
		    d3.event.stopPropagation(); 
		})
		.on("dblclick", function(d) { loadTree(d.iri); });

	    var isoText = svg.append("g").selectAll("text")
		.data(force.nodes()) 
		.enter().append("text")
		.attr("y", ".31em") 
	        .attr("class", "iso")
		.text(function(d) { return d.iso; }) 
	    
	    var rectangle = svg.append("g").selectAll("rectangle")
		.data(force.nodes())
		.enter().append("rect")
		.attr("x", 20)
		.attr("y", "-.31em")  
		.attr("class", "rectangle")
		.on("mouseover", function(d) {   
		    d3.select(this).style("cursor", "pointer"); 
		})
		.on("click", function(d){  
		    d3.select(this) 
			.append("a")   
			.attr("href", "#myPopup")
			.attr("data-rel", "popup")
			.attr("data-transition", "pop");
		    d3.select("#myPopup")  
			.style("width", "auto")  
			.style("height", "auto") 
			.style("display", "inline");
		    d3.select("#myPopup").html("");
		    d3.selectAll("circle").attr("fill", "#ffb380");        
		    if (d.refersTo != undefined){ 
			d.disambiguate();
		    } else {  
			d.showTooltip(false); 
		    } 
		    d3.event.stopPropagation(); 
		});

	    var wordText = svg.append("g").selectAll("text")  
		.data(force.nodes())   
		.enter().append("text")
		.attr("x", 20)
		.attr("y", ".31em") 
		.attr("id", "word") 
		.text(function(d) { return d.word; });
	    
	    function tick() {     
		circle.attr("transform", transform);
		wordText.attr("transform", transform);
		rectangle.attr("transform", transform);
		isoText.attr("transform", transform);
	    }
	}
    })
}
	    
