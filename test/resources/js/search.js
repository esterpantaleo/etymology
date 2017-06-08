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
	    d3.select("#message").html("");
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
            d3.select("#tree-container") 
		.append("p")
                .attr("id", "message") 
                .attr("align", "center");	    
	    if (graph.length == 0){
		d3.select("#message")
		    .html("This word is not available in the database");
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
                       console.log(element);
			nodes[element] = new Node(element);
		    });
		}
	    }) 
	    if (debug) {
		console.log(nodes); 
	    }

            // Create a dagre
	    var g = new dagreD3.graphlib.Graph().setGraph({});  
	    var counter = 0;   
	    var m = null;
	    for (var n in nodes){
		g.setNode(n,
			  { label: nodes[n].word,
			    language: nodes[n].lang,
			    iso: nodes[n].iso,
			    shape: "rect",  
			    id: n,
			    number: counter,
			    style: "fill: #ffb380; stroke: lightBlue;" 
			  }); 
                if (m != null){
		    g.setEdge(n, 
			      m, 
			      { label: "", style: "stroke-width: 0"})
		}
		m = n;
		counter ++;
	    }

	    //style nodes
	    g.nodes().forEach(function(v) {
		var node = g.node(v);
		node.rx = node.ry = 7;
	    });

	    var svg = d3.select("#tree-container").append("svg") 
		.attr("id", "tree-overlay") 
		.attr("width", width)     
		.attr("height", height)
		.on("click", function(){
		    d3.select("#myPopup")   
			.style("display", "none"); 
		});

	    var inner = svg.append("g"); 

	    // Set up zoom support
	    var zoom = d3.behavior.zoom().on("zoom", function() {
		inner.attr("transform", "translate(" + d3.event.translate + ")" +
			   "scale(" + d3.event.scale + ")");
		});
	    svg.call(zoom);

	    // Create the renderer    
	    var render = new dagreD3.render();  

	    // Run the renderer. This is what draws the final graph.
	    render(inner, g);

	    //append language tag to nodes
	    inner.selectAll("g.node") 
		.append("text")  
		.attr("id", "isotext") 
		.style("width", "auto")
		.style("height", "auto") 
		.style("display", "inline") 
		.attr("y", "2em") 
		.html(function(v) { return g.node(v).iso; }); 

	     inner.selectAll("g.node")
		.append("rect")
//                .attr("class", "iso")
		.attr("y", "1.1em")
		.attr("width", function(v) { return g.node(v).iso.length/1.7 + "em"; })
		.attr("height", "1em")
		.attr("fill-opacity", 0)  
		.on("mouseover", function(d) {  
		    d3.select(this).style("cursor", "pointer");  
		})
		.on("click", function(d) { 
		    d3.select("#myPopup")
			.style("display", "none");  
		    d3.select("#myPopup").html(""); 
		    d3.select(this)    
			.append("a")
			.attr("href", "#myPopup") 
			.attr("data-rel", "popup") 
			.attr("data-transition", "pop");
		    d3.select("#myPopup") 
			.style("width", "auto")
			.style("height", "auto") 
			.style("display", "inline"); 
		    d3.select("#myPopup").html(function() { 
			   return g.node(d).language; 
		        })
			.style("left", (d3.event.pageX) + "px")   
			.style("top", (d3.event.pageY - 28) + "px");
		    d3.event.stopPropagation();
		})
		.on("mousedown", function() { d3.event.stopPropagation(); });

	    //showtooltip on click on nodes 
	    var touchtime = 0;
	    inner.selectAll("g.node") 
		.on("mousedown", function() { d3.event.stopPropagation(); })  
		.on('click', function(d) {
		    if(touchtime == 0) {
			//set first click
			touchtime = new Date().getTime();
		    } else {
			//compare first click to this click and see if they occurred within double click threshold
			if(((new Date().getTime())-touchtime) < 800) {
			    //double click occurred
			    d3.select("#myPopup").style("display", "none");
			    loadTree(g.node(d).id);
			    touchtime = 0;
			} else {
			    d3.select("#myPopup").html("");
			    d3.select(this)
				.append("a")
				.attr("href", "#myPopup")
				.attr("data-rel", "popup")
				.attr("data-transition", "pop");
			    d3.select("#myPopup")
				.style("width", "auto")
				.style("height", "auto")
				.style("display", "inline");
			    nodes[g.node(d).id].showTooltip(true);
			    d3.event.stopPropagation();
			    //it is not a double click so set as a new first click
			    touchtime = new Date().getTime();
			}
		    }                 
		})

	    d3.selectAll(".edgePath").remove();

	    var initialScale = 0.75;
	    d3.select("svg").on("dblclick.zoom", null);
	    zoom.translate([(width - g.graph().width * initialScale) / 2, 20])
		.scale(initialScale)
		.event(svg);
	}
    })
}
	    
