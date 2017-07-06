function refreshScreen5(){
    d3.select("#tree-overlay").remove();
    d3.select("#myPopup")
        .style("display", "none");
    d3.select("#message").html("");
}
    
function refreshScreen6(){
    d3.select("#message")
        .html("Sorry, the server cannot extract etymological relationships correctly for this word. <br>We are working to fix this!");
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
  

function drawDAGRE(response, width, height) {
    refreshScreen5();
    if (response == null){
        refreshScreen6();
    } else {
        refreshScreen7();
	var ancestorsArray = [];
	JSON.parse(response).results.bindings.forEach(function(element){
	    ancestorsArray.push(element.ancestor1.value);
	});
	console.log("ancestors array");
        console.log(ancestorsArray);
        var i, j, tmpArray, url, chunk = 5, sources = [];
	for (i=0, j=ancestorsArray.length; i<j; i+=chunk) {
	    tmpArray = ancestorsArray.slice(i, i+chunk);
	
	    url = ENDPOINT + "?query=" + encodeURIComponent(unionSparql(tmpArray, descendantSparql));
	    sources.push(get(url));
	}
	const query = Rx.Observable.zip.apply(this, sources);
        const subscribe = query.subscribe(function(val){
	    var dataArray = []; 
	    val.forEach(function(e) {
		for (var b in JSON.parse(e).results.bindings){
                    dataArray.push(JSON.parse(e).results.bindings[b].descendant1.value);
		}
	    });
	    console.log("data array");
	    console.log(dataArray);
	    var i2, j2, tmpArray2, url2, chunk2 = 5, sources2 = [];
            for (i2=0, j2=dataArray.length; i2<j2; i2+=chunk2) {
		tmpArray2 = dataArray.slice(i2, i2+chunk2);

		url2 = ENDPOINT + "?query=" + encodeURIComponent(unionSparql(tmpArray2, dataSparql));console.log(url2);
		sources2.push(get(url2));
            }
	    const query2 = Rx.Observable.zip.apply(this, sources2);
	    const subscribe2 = query2.subscribe(function(val2){
		var graphArray = [];
		val2.forEach(function(e) {
                    for (var b in JSON.parse(e).results.bindings){
			graphArray.push(JSON.parse(e).results.bindings[b]);
                    }
		})
		console.log("graph array"); 
		console.log(graphArray);
		drawData(graphArray, width, height);
            });
          	    
	},
					  error => console.log("Error DAGREZIP"),
					  () => console.log('done DAGREZIP'));	
    }
}

function drawData(response, width, height){
    var nodes = {};
    var links = [];
    var classes = [];
    
    response.forEach(function(element){
        //save all nodes                                                      
        if (element.s != undefined && nodes[element.s.value] == null){
            nodes[element.s.value] = new Node(element.s.value);
        }
	if (element.rel != undefined && nodes[element.rel.value] == null) {
            nodes[element.rel.value] = new Node(element.rel.value);
        }
        if (element.eq != undefined){
            classes.push([element.s.value, element.eq.value]);
            //add eqIri                             
//            nodes[element.s.value].eqIri.push(element.eq.value);
            if (nodes[element.eq.value] == null) {
		nodes[element.eq.value] = new Node(element.eq.value);
	    }
	} 
	if (element.der != undefined && nodes[element.der.value] == null) {
	    nodes[element.der.value] = new Node(element.der.value);
            //add property der
	    nodes[element.s.value].der = true;
	}
    })
    
    //create equivalence classes
    //if both ee_door and ee_1_door are nodes,           
    //and there is no other node (no ee_2_door or ee_3_door),     
    //then merge them in the future  
    for (var n in nodes){
	var iso = nodes[n].iso;
	var label = nodes[n].label;
	var ety = nodes[n].ety;
        var etys = [ety];
	var equivalent = [n];
	for (var m = n+1; m < nodes.length; m++){
	    if (nodes[m].iso.equals(iso) && nodes[m].label.equals(label)){
		equivalent.push(m);  
		etys.push(nodes[m].ety);
	    }
        }
	if (etys.sort().pop()>1){
	    //there are multiple etymologies
	} else {
	    equivalent = sort_unique(equivalent);
	    if (equivalent.length>1){
		classes.push(equivalent);
		console.log(equivalent[0] + " " + equivalent[1]);
	    }
	}
    }
	
    response.forEach(function(element){
	if (element.rel != undefined){
	    if (element.eq == undefined){
		var Link = {"source": element.rel.value, "target": element.s.value};
		links.push(Link);
	    } 
	}
    });

    var g = new dagreD3.graphlib.Graph().setGraph({rankdir: 'LR'});
    
    for (var n in nodes){
        g.setNode(n, nodes[n]);
    }	
    
    links.forEach(function(element){
	g.setEdge(element.source, 
		  element.target, 
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
	    refreshScreen4(this);
	    d3.select("#myPopup").html(function() { 
		return g.node(d).lang; 
	    }) 
		.style("left", (d3.event.pageX) + "px")     
		.style("top", (d3.event.pageY - 28) + "px");
	    d3.event.stopPropagation(); 
	})
	.on("mousedown", function() { d3.event.stopPropagation(); })
    
    //show tooltip on click on nodes
    inner.selectAll("g.node")
	.on("click", function(d) {
	    refreshScreen4(this);
	    console.log(g.node(d).iri)
	    console.log(nodes[g.node(d).iri])
	    nodes[g.node(d).iri].showTooltip(true);
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

