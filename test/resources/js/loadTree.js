//DEFINE QUERY TO PLOT GRAPH
function treeSparql(id){
    var query = [
        "DEFINE input:inference \"etymology_ontology\"",
        "PREFIX dbetym: <http://kaiko.getalp.org/dbnaryetymology#>", 
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",  
        "SELECT DISTINCT ?target1 ?target2 ?target3 ?target4 ?source (group_concat(distinct ?p ; separator=\",\") as ?ety) {",     
        "    ?source ?p ?o .",    
        "    FILTER (?p in (dbetym:etymologicallyDerivesFrom,dbetym:descendsFrom,dbetym:derivesFrom,dbetym:etymologicallyEquivalentTo))",
        "    {",  
        "        SELECT ?source", 
        "            {",   
        "                ?source dbetym:etymologicallyRelatedTo{1,} <" + id + "> .", 
        "            } LIMIT 100",    
        "    }",
        "    UNION", 
        "    {",
        "        SELECT ?source",
        "            {<" + id + "> dbetym:etymologicallyRelatedTo{1,} ?source .",
        "            } LIMIT 100",
        "    }",  
        "    UNION", 
        "    {",
        "        SELECT ?source",     
        "            {<" + id + "> dbetym:etymologicallyRelatedTo{1,} ?ancestor .",   
        "                ?source dbetym:etymologicallyRelatedTo{1,} ?ancestor .",
        "            } LIMIT 100", 
        "    }",      
        "    OPTIONAL",   
        "    {",   
        "        ?source dbetym:etymologicallyDerivesFrom ?target1",
        "    }",
        "    OPTIONAL",
        "    {",
        "        ?source dbetym:derivesFrom ?target2",          
        "    }",
        "    OPTIONAL", 
        "    {", 
        "        ?source dbetym:descendsFrom ?target3",
            "    }", 
        "    OPTIONAL",  
        "    {",   
        "        ?source dbetym:etymologicallyEquivalentTo ?target4", 
        "    }", 
        "}"   
    ];    
    return query.join(" ");   
}

function loadTree(iri) {
    var url = ENDPOINT + "?query=" + encodeURIComponent(treeSparql(iri));
    if (debug) { 
	console.log(url);
    }
    
    //TODO: use wheel
    //DEFINE SIZE            
    var width = window.innerWidth,
    height = $('#tree-container').height() - $(header).height();
    d3.select("#tree-overlay").remove();
    d3.select("#message")
	.html("Loading, please wait...");
    
    //TODO: MANAGE ERROR and RELOAD FROM CALLBACK
    d3.xhr(url, MIME, function(request) {
	//clean screen
	d3.select("#tree-overlay").remove();
	d3.select("#myPopup")
	    .style("display", "none");
	d3.select("#message").html("");
	if (request == null){
	    //print error message
            d3.select("#message")
                .html("Sorry, the server cannot extract etymological relationships correctly for this word. <br>We are working to fix this!")
	} else {
	    //change help       
	    d3.select("#p-helpPopup").remove(); 
	    d3.select("#helpPopup")
		.append("p")
		.attr("id", "p-helpPopup")
                .attr("class", "help")
		.html("Arrows go from ancestor to descendant.<ul>" +
                      "<li>Click on a circle to display the language</li>" + 
                      "<li>Click on a word to display lexical information.</li>" +
                      "</ul>");
	    
	    var json = JSON.parse(request.responseText);

	    var graph = json.results.bindings;
	    if (debug) {
		console.log(graph);
	    }

	    var links = [];
	    var nodes = {};
	    
	    //set nodes
	    graph.forEach(function(element){
		nodes[element.source.value] = new Node(element.source.value);
	
		["target1", "target2", "target3", "target4"].map(function(target){
		    if (element[target] != undefined) {
			if (nodes[element[target].value] == undefined) {
			    nodes[element[target].value] = new Node(element[target].value);
			}
		    }			    
		});
	    });

	    //set links
	    graph.forEach(function(element){
		["target1", "target2", "target3", "target4"].forEach(function(target){
		    var type = "inherited";
                    if (target == "target4"){
                        type = "equivalent";
                    }

		    if (element[target] != undefined) {
			if (element[target].value != element.source.value){
                            var Link = {"source": nodes[element[target].value], "target": nodes[ element.source.value], "type": type};
			    if (links.indexOf(Link) == -1) {
                                links.push(Link);
                            }
			}
		    }
		})
	    })
            //merge nodes that are linked by a Link of type equivalent  
            if (mergeEquivalentNodes) {
		links.forEach(function(element){
                    if (element.type == "equivalent"){
			if (element.target.eqIri == undefined){
			    if (element.source.eqIri == undefined){
				element.target.eqIri = [];
				element.target.eqWord = [];
			    } else {
				element.target.eqIri = element.source.eqIri;
				element.target.eqWord = element.source.eqWord;
				element.source.eqIri = undefined;
				element.source.eqWord = undefined;
			    }
			}
			if (!element.target.eqIri.includes(element.source.iri)) {//if it is not in array
			    element.target.eqIri.push(element.source.iri);
			    element.target.eqWord.push(element.source.word);
			    
			    if (element.source.eqIri != undefined){
				element.source.eqIri.forEach(function(q){
				    if (!element.target.eqIri.includes(q)) {
					element.target.eqIri.push(q);
				    }
				});
				element.source.eqWord.forEach(function(q){
				    if (!element.target.eqWord.includes(q)) {
					element.target.eqWord.push(q);
				    }
				});
			    }
			} 
			//merge node element.source into node element.target, and delete node element.source
			links.forEach(function(f){
                            if (f != element){
				if (f.source.iri == element.source.iri) {
				    f.source = element.target;
				} else if (f.target.iri == element.source.iri) {
				    f.target = element.target;
				}
                            }
			})
		    }
                });

                for (var i = links.length-1; i >= 0; i--){
		    if (links[i].source.iri == links[i].target.iri){
			links.splice(i, 1);
		    } else if (links[i].type == "equivalent"){
			if (links[i].source.eqIri == undefined){
			    delete nodes[links[i].source.iri];
			}
			if (links[i].target.eqIri == undefined){
			    delete nodes[links[i].target.iri];
                        }
                        links.splice(i, 1);
                    }
                }
		for (var n in nodes){		    
		    if (nodes[n].eqWord != undefined) {
			nodes[n].word += "," + nodes[n].eqWord.join(",");
		    }
		}
	    }

	    if (excludeStarLikeStructures){
		//find links between words in the same language, but exclude links that have as target the searched word
                var toDeleteLinks = links.filter(function(element) {
		    //if (element.target.word.find(function(w){ return w == myWord; }) == undefined){
			return element.source.iso == element.target.iso;
		    //} else {
	//		return false;
	//	    }
                }).filter(function(element) {//don't delete a node if a link starts from it
                    for (var i=0; i<links.length; i++) {
                        if (element.target.iri == links[i].source.iri)
                            return false;
                    }
                    return true;
                });
		
		//remove links  
                links = links.filter(function(d) {
                    for (var i=0; i<toDeleteLinks.length; i++) {
                        if (toDeleteLinks[i] == d) return false;
                    }
                    return true;
                });
		
		//remove nodes that are not connected by a link
                for (var aNode in nodes) {
                    var isLinked = false;
                        for (var i=0; i<links.length; i++) {
                            if (links[i].source.iri == aNode || links[i].target.iri == aNode) {
                                isLinked = true;
                                break;
                            }
                        }
                    if (isLinked == false) {
                        delete nodes[aNode];
                    }
                }
            }
	    
	    if (links.length == 0){
                d3.select("#tree-overlay").remove();
                d3.select("#message")
                    .html("Sorry, no etymology available for this word");
            }
	    
	    // Create a dagre graph 
	    var g = new dagreD3.graphlib.Graph().setGraph({rankdir: 'LR'});
	    var counter = 0;
	    for (var n in nodes){		
		g.setNode(n, 
			  { label: nodes[n].word, 
                            language: nodes[n].lang,
                            iso: nodes[n].iso,
			    shape: "rect", 
			    id: n,
			    number: counter,
			    style: "fill: #ffb380; stroke: lightBlue"
			  });
		counter ++;
	    }

	     //style nodes
	    g.nodes().forEach(function(v) {
		var node = g.node(v);
		node.rx = node.ry = 7; 
	    });

	    links.forEach(function(element){
		g.setEdge(element.source.iri, 
			  element.target.iri, 
			  { label: "", lineInterpolate: "basis" })
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
                .on("mousedown", function() { d3.event.stopPropagation(); })

	    //showtooltip on click on nodes
            inner.selectAll("g.node")
		.on("click", function(d) {
                    //clear html text
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
                    console.log(g.node(d).id)
                    console.log(nodes[g.node(d).id])
                    nodes[g.node(d).id].showTooltip(true);
		    d3.event.stopPropagation();
		})
                .on("mousedown", function() { d3.event.stopPropagation(); })

	    // Center the graph       
	    var initialScale = 0.75;
	    zoom.translate([(width - g.graph().width * initialScale) / 2, 20])
		.scale(initialScale)
		.event(svg);
	    
//	    svg.attr("height", g.graph().height * initialScale + 40);
	    
	}
    });
}
