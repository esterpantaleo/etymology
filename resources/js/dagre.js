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

function refreshScreen5(){
    d3.select("#tree-overlay").remove();
    d3.select("#myPopup")
        .style("display", "none");
    d3.select("#message").html("");
}

function refreshScreen6(){
    d3.select("#message")
        .html("Sorry, the server cannot extract etymological relationships correctly for this word.");
}

function refreshScreen7(){
    d3.select("#p-helpPopup").remove();
    d3.select("#helpPopup")
        .append("p")
        .attr("id", "p-helpPopup")
        .attr("class", "help")
        .html("Arrows go from ancestor to descendant.<ul>" +
              "<li>Click on a circle to display the language</li>" +
              "<li>Click on a word to display lexical information.</li>" +
              "</ul>");
}

function refreshScreen8(){
    d3.select("#message")
        .html("Sorry, no etymology is available for this word!");
}

function slicedQuery(myArray, mySparql, num){
    var i, j, tmpArray, url, chunk = num, sources = [];
    for (i=0, j=myArray.length; i<j; i+=chunk) {
        tmpArray = myArray.slice(i, i+chunk);
	//console.log(unionSparql(tmpArray, mySparql));
        url = ENDPOINT + "?query=" + encodeURIComponent(unionSparql(tmpArray, mySparql));
        console.log(url);
        sources.push(get(url));
    }
    const query = Rx.Observable.zip.apply(this, sources)
        .catch((err) => {
            refreshScreen6();
            //Return an empty Observable which gets collapsed in the output
            return Rx.Observable.empty();
        });
    return query;
}

function appendTooltip(inner, g, nodes){
    //show tooltip on click on nodes                    
    inner.selectAll("g.node")
        .on("click", function(d) {
            refreshScreen4(this);
	    var iri = g.node(d).iri;
            console.log(iri[0]);
	    for (var i in iri){
		nodes[iri[i]].showTooltip(d3.event.pageX, d3.event.pageY);
	    }
            d3.event.stopPropagation();
        })
        .on("mousedown", function() { 
	    d3.event.stopPropagation(); 
	})
}

function appendLanguageTagTextAndTooltip(inner, g){
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
        .attr("y", "1.1em")
        .attr("width", function(v) { return g.node(v).iso.length/1.7 + "em"; })
        .attr("height", "1em")
        .attr("fill", "red")
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
        .on("mousedown", function() { d3.event.stopPropagation(); })
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
	
	appendLanguageTagTextAndTooltip(inner, g);
	
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
			var url = ENDPOINT + "?query=" + encodeURIComponent(ancestorSparql(iri));
			if (debug) { 
			    console.log(url); 
			}
			refreshScreen3();
			const source = get(url);
			source.subscribe(response => drawDAGRE(response, width, height),
					 error => console.error(error),
					 () => console.log('done DAGRE'));
			touchtime = 0;
                    } else {
			refreshScreen4(this);
                        nodes[iri].showTooltip(d3.event.pageX, d3.event.pageY); 
			d3.event.stopPropagation(); 
			//not a double click so set as a new first click
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


function drawDAGRE(response, width, height) {
    refreshScreen5();
    if (response == null){
        refreshScreen6();
	return;
    } 
    refreshScreen7();
    var ancestorArray = [];
    JSON.parse(response).results.bindings.forEach(function(element){
	ancestorArray.push(element.ancestor1.value);
    });
    console.log("ANCESTORS");
    console.log(ancestorArray);
    const subscribe = slicedQuery(ancestorArray, descendantSparql, 8).subscribe(function(val){//8 crashes sometimes
	var dataArray = [];
	val.forEach(function(element) {
	    var tmp = JSON.parse(element).results.bindings;
	    for (var t in tmp){
                dataArray.push(tmp[t].descendant1.value);
            }
        })
	if (dataArray.length == 0){
            refreshScreen8();
        }
	const subscribe = slicedQuery(dataArray, dataSparql, 8).subscribe(function(val){
	    var graphArray = [];
	    val.forEach(function(element) {
		var tmp = JSON.parse(element).results.bindings;
                for (var t in tmp){
		    graphArray.push(tmp[t]);
                }
	    })
	    if (graphArray.length == 0){
		refreshScreen8();
	    } else {
		drawData(ancestorArray, graphArray, width, height);
	    }
        });
        
    },
										error => console.log("Error DAGREZIP"),
										() => console.log('done DAGREZIP'));
}

function drawData(ancestors, response, width, height){
    var nodes = {};
    var links = [];
    
    response.forEach(function(element){
        //save all nodes             
        if (element.s != undefined && nodes[element.s.value] == null){
            nodes[element.s.value] = new Node(element.s.value);
        }
	if (element.rel != undefined && nodes[element.rel.value] == null) {
            nodes[element.rel.value] = new Node(element.rel.value);
        }
        if (element.rel != undefined && element.eq != undefined){
            if (nodes[element.eq.value] == null) {
		nodes[element.eq.value] = new Node(element.eq.value);
	    }
	    //push to eqIri
	    nodes[element.rel.value].eqIri.push(element.eq.value);
	    nodes[element.eq.value].eqIri.push(element.rel.value);
	} 
	if (element.der != undefined){
	    if (nodes[element.der.value] == null) {
		nodes[element.der.value] = new Node(element.der.value);
	    }
            //add property der
	    nodes[element.s.value].der = true;
	}
    })

    //add property isAncestor
    ancestors.forEach(function(element) { nodes[element].isAncestor = true; });
	
    console.log("nodes");
    console.log(nodes);
    var graphNodes = {};
    var counter = 0;
    //push and merge nodes ee_door and ee_1_door into graphNodes
    //if both ee_door and ee_1_door are nodes,           
    //and there is no other node (no ee_2_door or ee_3_door)
    for (var n in nodes){
	if (nodes[n].ety == 0){
	    var tmp = [];
	    var iso = nodes[n].iso; 
	    var label = nodes[n].label;
	    for (var m in nodes) {
		if (nodes[m] != undefined) {
		    if (nodes[m].iso == iso && nodes[m].label == label){
			if (nodes[m].ety > 0){
			    tmp.push(m);
			}
		    }
		}
	    } 
	    tmp = sort_unique(tmp);
	    if (tmp.length == 1){
		var gg = new GraphNode(counter);
		//initialize all 
		gg.all.push(n);
		//define iri 
		gg.iri = nodes[tmp[0]].eqIri;
		gg.iri.push(tmp[0]);
		nodes[n].graphNode.push(counter);
		gg.iri.forEach(function(element) {
		    nodes[element].graphNode.push(counter);
		})
		
		//push to graphNodes
		graphNodes[counter] = gg;
		graphNodes[counter].iri = sort_unique(graphNodes[counter].iri);
		counter ++;
	    } 
	}
    }

    for (var n in nodes){
	if (nodes[n].graphNode.length == 0){
	    //add iri
	    var gg = new GraphNode(counter);
	    gg.iri = nodes[n].eqIri;
	    gg.iri.push(n);
	    var tmp = [];
	    gg.iri.forEach(function(element) {		
		tmp.concat(element.eqIri);
	    })
	    tmp = sort_unique(tmp);
	    gg.iri.concat(tmp);
	    gg.iri = sort_unique(gg.iri);
	    gg.iri.forEach(function(element) {
                nodes[element].graphNode.push(counter);
            })
	    graphNodes[counter] = gg;
	    counter ++;
	} else {
	    var graphNode = nodes[n].graphNode[0];
	    
	    nodes[n].eqIri.forEach(function(element){
		//add iri
		nodes[element].graphNode.push(graphNode);
		graphNodes[graphNode].iri.concat(nodes[element].eqIri);
		graphNodes[graphNode].iri = sort_unique(graphNodes[graphNode].iri);
	    })
	}
    }

    for (var gg in graphNodes){
	//define all
	graphNodes[gg].all = graphNodes[gg].all.concat(graphNodes[gg].iri);
	
	//define isAncestor
        if (graphNodes[gg].all.filter(function(element) { return nodes[element].isAncestor;}).length >0){
	    graphNodes[gg].isAncestor = true;
	}

	//define der
	var der = graphNodes[gg].all.filter(function(element) { 
	    return nodes[element].der != undefined; });
	if (der.length > 0){
	    graphNodes[gg].der = true;
	}
	//define iso, label, lang
	graphNodes[gg].iso = nodes[graphNodes[gg].all[0]].iso;
	graphNodes[gg].label = graphNodes[gg].iri.map(function(i) { return nodes[i].label;}).join(",");
	graphNodes[gg].lang = nodes[graphNodes[gg].all[0]].lang;
    }

    //define linkedToTarget and linkedToSource
    response.forEach(function(element){
	if (element.rel != undefined && element.s != undefined){
	    var source = nodes[element.rel.value].graphNode[0], target = nodes[element.s.value].graphNode[0];
	    if (source != target){
		if (!(graphNodes[source].der || graphNodes[target].der)){
		    graphNodes[source].linkedToTarget.push(target); 
		} 
		if (graphNodes[target].linkedToSource.indexOf(source) == -1){ 
		    graphNodes[target].linkedToSource.push(source);
		}
	    } 
	}
    });

    for (var gg in graphNodes){ 
	//collapse nodes that have more than 10 descendants and color them differently
	if (graphNodes[gg].linkedToTarget.length > 10){
	    console.log("the following node has more than 10 targets: collapsing:");
	    console.log(graphNodes[gg]);
	    graphNodes[gg].linkedToTarget.map(function(e){
		console.log("derived node="); 
		console.log(graphNodes[e]);
		if (graphNodes[e].linkedToSource.length == 1) {
		    graphNodes[e].der = true; 
		}
	    });  
	    //    graphNodes[gg].linkedToTarget.map(function(e){  graphNodes[e].der = true; });
	    graphNodes[gg].style = "fill: sandyBrown; stroke: lightBlue";
	}
    }

    response.forEach(function(element){
        if (element.rel != undefined && element.s != undefined){
            var source = nodes[element.rel.value].graphNode[0], 
	    target = nodes[element.s.value].graphNode[0];
            if (source != target){
                if (graphNodes[target].isAncestor || !(graphNodes[source].der || graphNodes[target].der)){
                    var Link = {"source": source, "target": target};
                    if (graphNodes[target].linkedToSourceCopy.indexOf(source) == -1){
			//define linked and linkedToSourceCopy
                        links.push(Link);
                        graphNodes[source].linked = true;
                        graphNodes[target].linkedToSourceCopy.push(source);
                        graphNodes[target].linked = true;
                    }
                } 
            }
        }
    });

    var g = new dagreD3.graphlib.Graph().setGraph({rankdir: 'LR'});
    
    //only draw nodes that are linked to other nodes
    for (var gg in graphNodes){
	if (graphNodes[gg].linked){
            g.setNode(gg, graphNodes[gg]);
	}
    }	
    
    links.forEach(function(element){
	g.setEdge(element.source, 
		  element.target, 
		  { label: "", lineInterpolate: "basis" });
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

    appendLanguageTagTextAndTooltip(inner, g);
        
    appendTooltip(inner, g, nodes);
    
    // Center the graph       
    var initialScale = 0.75;
    zoom.translate([(width - g.graph().width * initialScale) / 2, 20])
	.scale(initialScale)
	.event(svg);
    
    //svg.attr("height", g.graph().height * initialScale + 40);}}
}

