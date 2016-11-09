function exec(){
    var debug = false,
    width = 900,
    height = 4500,
    margin =  350,
    radius = 5

//set query parameters
    var endpoint = "http://togostanza.org/sparql" //http://kaiko.getalp.org/sparql
    
var sparql = [
    "PREFIX up: <http://purl.uniprot.org/core/>",
    "PREFIX tax: <http://purl.uniprot.org/taxonomy/>",
    "SELECT ?root_name ?parent_name ?child_name",
    "FROM <http://togogenome.org/graph/uniprot>",
    "WHERE",
    "{",
    "VALUES ?root_name { \"Tardigrada\" }",
    "?root up:scientificName ?root_name .",
    "?child rdfs:subClassOf+ ?root .",
    "?child rdfs:subClassOf ?parent .",
    "?child up:scientificName ?child_name .",
    "?parent up:scientificName ?parent_name .",
    "}",
].join(" ")
//query
var url = endpoint + "?query=" + encodeURIComponent(sparql)
if (debug) { console.log(endpoint) }
if (debug) { console.log(url) }
var mime = "application/sparql-results+json"
d3.xhr(url, mime, function(request) {
    var json = request.responseText
    if (debug) { console.log(json) }
    json = JSON.parse(json)
    var data = json.results.bindings

    var pair = d3.map()
    var size = d3.map()
    var root = data[0]["root_name"].value
    var parent = child = children = true
    for (var i = 0; i < data.length; i++) {
	parent = data[i]["parent_name"].value
	child = data[i]["child_name"].value
	if (parent != child) {
	    if (pair.has(parent)) {
		children = pair.get(parent)
		children.push(child)
	    } else {
		children = [child]
	    }
	    pair.set(parent, children)
	    if (data[i]["value"]) {
		size.set(child, data[i]["value"].value)
	    }
	}
    }
    function traverse(node) {
	var list = pair.get(node)
	if (list) {
	    var children = list.map(function(d) { return traverse(d) })
	    // sum of values of children
	    var subtotal = d3.sum(children, function(d) { return d.value })
	    // add a value of parent if exists
	    var total = d3.sum([subtotal, size.get(node)])
	    return {"name": node, "children": children, "value": total}
	} else {
	    return {"name": node, "value": size.get(node) || 1}
	}
    }
    var tree = traverse(root)

    if (debug) { console.log(JSON.stringify(tree)) }

    var cluster = d3.layout.cluster()
        .size([height, width - margin])
    var diagonal = d3.svg.diagonal()
        .projection(function(d) { return [d.y, d.x] })
    
    var svg = d3.select("#result")
	.html("")
	.append("div")
//	.attr("class", "d3sparql " + "dendrogram")
	.append("svg")
        .attr("width", width)
        .attr("height", height)
        .append("g")
        .attr("transform", "translate(40,0)")
    var nodes = cluster.nodes(tree)
    var links = cluster.links(nodes)
    var link = svg.selectAll(".link")
        .data(links)
        .enter().append("path")
        .attr("class", "link")
        .attr("d", diagonal)
	.attr({
	    "fill": "none",
	    "stroke": "#cccccc",
	    "stroke-width": "1.5px",
	})
    var node = svg.selectAll(".node")
        .data(nodes)
        .enter().append("g")
        .attr("class", "node")
        .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")" })
    var circle = node.append("circle")
        .attr("r", radius)
	.attr({
	    "fill": "#ffffff",
	    "stroke": "steelblue",
	    "stroke-width": "1.5px",
	    "opacity": 1,
	})
    var text = node.append("text")
        .attr("dx", function(d) { return (d.parent && d.children) ? -8 : 8 })
        .attr("dy", 5)
        .style("text-anchor", function(d) { return (d.parent && d.children) ? "end" : "start" })
        .text(function(d) { return d.name })
    	.attr({
	    "font-size": "10px",
	    "font-family": "sans-serif"
        })
}


      )
}






