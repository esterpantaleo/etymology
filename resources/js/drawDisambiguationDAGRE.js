function refreshScreen1(){
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
}

function refreshScreen2(){
    d3.select("#tree-container")
        .append("p")
        .attr("id", "message")
        .attr("align", "center");
    
    d3.select("#message")
        .html("This word is not available in the database");
}

//TODO: use wheel 
function refreshScreen3(){ 
    d3.select("#tree-overlay").remove();
    d3.select("#myPopup")  
	.style("display", "none"); 
    d3.select("#message")  
	.html("Loading, please wait...");
}

function refreshScreen4(s){ 
    d3.select("#myPopup").html("");
    d3.select(s)   
	.append("a")
	.attr("href", "#myPopup")  
	.attr("data-rel", "popup")  
	.attr("data-transition", "pop"); 
    d3.select("#myPopup")    
	.style("width", "auto")    
	.style("height", "auto")  
	.style("display", "inline");
}

function drawDisambiguationDAGRE(response, width, height){
    if (response != null){
	refreshScreen1();

	var graph = JSON.parse(response).results.bindings;
	if (graph.length == 0){
            refreshScreen2();
        }
	
        var nodes = {};
        graph.forEach(function(n){
            var iris = n.et.value.split(",");
            if (iris == ""){
                nodes[n.iri.value] = new Node(n.iri.value);
            } else {
                iris.forEach(function(element) {
                    nodes[element] = new Node(element);
                });
            }
        })
	if (debug) {
            console.log(nodes);
	}

        // Create a dagre    
        var g = new dagreD3.graphlib.Graph().setGraph({});
        var m = null;
        for (var n in nodes){
            g.setNode(n, nodes[n]);
            if (m != null){
                g.setEdge(n, m, { label: "", style: "stroke-width: 0"})
            }
            m = n;
        }
	
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
		refreshScreen4(this);
                d3.select("#myPopup").html(function() {
                    return g.node(d).lang;
                })
		    .style("left", (d3.event.pageX) + "px")
                    .style("top", (d3.event.pageY - 28) + "px");
                d3.event.stopPropagation();
            })
            .on("mousedown", function() { d3.event.stopPropagation(); });
	
        //show tooltip on click on nodes                                               
        var touchtime = 0;
        inner.selectAll("g.node")
            .on("mousedown", function() { d3.event.stopPropagation(); })
            .on('click', function(d) {
                if(touchtime == 0) {
                    //set first click                                                    
                    touchtime = new Date().getTime();
                } else {
                    //compare first click to this click and see if they occurred within double click threshold     
                    var iri = g.node(d).iri;
                    if((new Date().getTime())-touchtime < 800) {
                        //double click occurred
			refreshScreen3();
			var url = ENDPOINT + "?query=" + encodeURIComponent(ancestorsSparql(iri));
			if (debug) { 
			    console.log(url); 
			}
			
			const source = get(url);
			source.subscribe(response => drawDAGRE(response, width, height),
					 error => console.error(error),
					 () => console.log('done DAGRE'));
			touchtime = 0;
                    } else {
			refreshScreen4(this);
                        nodes[iri].showTooltip(true);
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
}
