/*globals
    $, d3, console, dagreD3, Rx, window, document, URLSearchParams
*/
/*jshint loopfunc: true, shadow: true, latedef: false */ // Consider removing this and fixing these
var GRAPH = (function(module) {

    module.bindModule = function(base, moduleName) {
        var etyBase = base;

        class Dot {
            //add style
            //returns this
	    attr(a, b) {
		this.style = "fill: white; stroke: " + (this.isAncestor? "red" : "steelBlue") + "; stroke-width: 0.2em;";
		this.shape = "rect"; 
		this.rx = this.ry = 25;
		return this;
	    }
        }

        class Node extends Dot{
            constructor(iri, label) {
                super();
                //set this.iri
                this.iri = iri;

                var id = iri.replace("http://etytree-virtuoso.wmflabs.org/dbnary/eng/", "")
                    .split("/");
	        var tmp = (id.length > 1) ? id[1] : id[0];

                //set this.iso
                this.iso = (id.length > 1) ? id[0] : "eng";

                //set this.label
                this.label = (undefined === label) ? tmp.replace("__ee_", "") : label;
                this.label = label.replace(/^_/, '*').replace("__", "'").replace("%C2%B7", "·").replace(/_/g, " ");
                
                //set this.ety
                //this.ety is an integer representing the etymology number encoded in the iri;
                //if ety === 0 the iri is __ee_word                                    
                //if ety === 1 the iri is __ee_1_word            
                //etc       
                tmp = tmp.match(/__ee_[0-9]+_/g);
                this.ety = (null === tmp) ? 0 : tmp[0].match(/__ee_(.*?)_/)[1]; 
                
                //set this.lang
                this.lang = etyBase.tree.langMap.get(this.iso);
                
                //initialize this.graphNode specifying the graphNode corresponding to the node
                this.graphNode = undefined;
                
                //initialize this.eqIri is an array of iri-s of Node-s that are equivalent to the Node 
                this.eqIri = [];
                this.eqIri.push(iri);
                
                //initialize this.isAncestor
                this.isAncestor = false;
            }

            queryTooltip() { 
                var that = this;
                d3.selectAll(".tooltip").remove();
                var query = etyBase.DB.lemmaQuery(this.iri);
                var url = etyBase.config.urls.ENDPOINT + "?query=" + encodeURIComponent(query);
                return etyBase.DB.getXMLHttpRequest(url)
		    .flatMap(response => that.parseTooltip(response));
            }

            parseTooltip(response) {
                var tooltip = "<b>" + this.label + "</b><br><br><br>";
                if (null !== response) {
                    //print definition  
                    var dataJson = JSON.parse(response).results.bindings;
                    tooltip += this.parseDefinition(dataJson);
                    //print links 
                    tooltip += this.parseLinks(dataJson);
                } else {
                    tooltip += "-";
                }
		return Promise.resolve(tooltip);
            }
                
            parseDefinition(dataJson) {
                return dataJson.map(d => {
                    if (undefined !== d.pos && undefined !== d.gloss) {
                        return d.gloss.value.split(";;;;").map(e => {
                            return d.pos.value + " - " + e + "<br><br>";
                        }).join("");
                    }
                    return "-";
                 }).join("");
            }

            parseLinks(dataJson) {
                var toreturn = "<br><br>Data is under CC BY-SA and has been extracted from: ";
                toreturn += dataJson[0].links.value.split(",")
                    .map(function(e) {
                        var linkName;
                        if (e.startsWith("https://en.wiktionary.org/wiki/Reconstruction")) {
                            linkName = e.replace(/https:\/\/en.wiktionary.org\/wiki\/Reconstruction:/g, "")
                                .split("/")
                                .join(" ");
                        } else {
                            linkName = e.split("/")
                                .pop()
                                .split("#")
                                .reverse()
                                .join(" ")
                                .replace(/_/g, " ");
                        }
                        return "<a href=\"" + e + "\" target=\"_blank\">" + linkName + "</a>";
                    }).join(", ");
                return toreturn;
            }
        }

        class GraphNode extends Dot{
            constructor(i) {
                super();
                this.counter = i;
                this.iri = [];
                this.isAncestor = false;
		this.toDelete = false;
            }
        }

        class Data {
            constructor() {
                this.ancestors = [];
                this.lastAncestors = [];
                this.ancestorsAndDescendants = [];
            }

            parseAncestors(response) {
                return response.reduce((all, a) => {
                        return all.concat(JSON.parse(a).results.bindings);
                    }, []).reduce((val, a) => {
                        val.all.push(a.ancestor1.value);
                        if (a.der1.value === "0" && undefined !== a.ancestor2 && etyBase.helpers.lemmaNotStartsOrEndsWithDash(a.ancestor1.value)) {
                            val.all.push(a.ancestor2.value);
                            if (a.der2.value === "0" && undefined !== a.ancestor3 && etyBase.helpers.lemmaNotStartsOrEndsWithDash(a.ancestor2.value)) {
                                val.all.push(a.ancestor3.value);
                                if (a.der3.value === "0" && undefined !== a.ancestor4 && etyBase.helpers.lemmaNotStartsOrEndsWithDash(a.ancestor3.value)) {
                                    val.all.push(a.ancestor4.value);
                                    if (a.der4.value === "0" && undefined !== a.ancestor5 && etyBase.helpers.lemmaNotStartsOrEndsWithDash(a.ancestor4.value)) {
                                        val.all.push(a.ancestor5.value);
                                        val.last.push(a.ancestor5.value);
                                    }
                                }
                            }
                        }
                        return val;
                    }, { all: [], last: [] });
            }

            setAncestors(iri, response) {
                var that = this;
                var ancestors = that.parseAncestors(response);
                ancestors.all.push(iri);
                this.ancestors = this.ancestors.concat(ancestors.all).filter(etyBase.helpers.onlyUnique);
                this.lastAncestors = this.lastAncestors.concat(ancestors.last).filter(etyBase.helpers.onlyUnique);
            }

            addAncestors(response) {
                var that = this;
                if (0 !== response) {
                    var ancestors = response.reduce(function(val, r) {
                        return val.concat(that.parseAncestors(r).all); 
                        },
                        []);
                    this.ancestors = this.ancestors.concat(ancestors).filter(etyBase.helpers.onlyUnique);
                }
            }

            setProperties(response) {
	        this.properties = response.reduce((val, a) => {
		    val = val.concat(JSON.parse(a).results.bindings);
		    return val;
	         }, []);
	    }

            addDescendants(response) {
                var descendants = response.reduce((val, d) => {
                    val = val.concat(JSON.parse(d).results.bindings.map(function(t) { return t.descendant1.value; }));
                    return val;
                }, []).filter(etyBase.helpers.onlyUnique);
                this.ancestorsAndDescendants = this.ancestors.concat(descendants).filter(etyBase.helpers.onlyUnique);
            }
        }

        class Graph {
            constructor() {
                this.nodes = {};
                this.graphNodes = {};
                this.graphEdges = [];
                this.dagre = new dagreD3.graphlib.Graph().setGraph({ rankdir: "LR" });
            }

            render() {
                var that = this;
                var svg = d3.select("#tree-container").append("svg")
                    .attr("id", "tree-overlay")
                    .attr("width", window.innerWidth)
                    .attr("height", window.innerHeight - $("#header").height());

                var inner = svg.append("g")
	            .attr("id", "inner");

                // Set up zoom support                      
                var zoom = d3.behavior.zoom().on("zoom", function() {
                    inner.attr("transform", "translate(" + d3.event.translate + ")" +
                        "scale(" + d3.event.scale + ")");
                });
                svg.call(zoom); //.on("dblclick.zoom", null);

                // Create the renderer          
                var render = new dagreD3.render();

                // Run the renderer. This is what draws the final graph.  
                render(inner, that.dagre);

                // Center the graph       
                var initialScale = 0.75;
                zoom.translate([(window.innerWidth - that.dagre.graph().width * initialScale) / 2, 20])
                    .scale(initialScale)
                    .event(svg);
          
	        // Decorate graph
	        inner.selectAll("g.node > rect")
	            .attr("class", "word");
	        //show tooltip on mouseover nodes 
                inner.selectAll(".word")
                    .on("mouseover", function(d) {
                        d3.selectAll(".tooltip").remove();
                        d3.select("#tooltipPopup")
                            .style("display", "inline")
                            .style("left", (d3.event.pageX + 38) + "px")
                            .style("top", (d3.event.pageY - 28) + "px");
                        var iri = that.dagre.node(d).iri;
                        if (typeof iri === "string") {
                            var label = that.dagre.node(d).label;
                            that.nodes[iri]
             		        .queryTooltip()
			        .subscribe(text => {
			            d3.select("#tooltipPopup")
				        .append("p")
				        .attr("class", "tooltip") 
				        .html(text);
			        }, error => {
				    d3.select("#tooltipPopup")
                                        .append("p")
				        .attr("class", "tooltip")
				        .html("<b>" + label + "</b><br><br><br>-");
			        });
                        } else {
                            var tooltips = iri.reduce(function(t, i) {
                                var label = that.nodes[i].label;
                                if (t.labels.indexOf(label) === -1) {
                                    t.labels.push(label);
                                    t.text.push(that.nodes[i].queryTooltip());
                                } 
                                return t;
                            }, { labels: [], text: [] });
                            Rx.Observable.zip
			        .apply(this, tooltips.text)
			        .catch((err) => {
			            console.log(err); 
			            return Rx.Observable.empty();
			        }).subscribe(response => {
			            d3.select("#tooltipPopup")  
			                .append("p") 
				        .attr("class", "tooltip") 
				        .html(response.join("<br><br>"));
			        });
                        }
                        d3.event.stopPropagation();
                    });

                //append language tag to nodes            
                inner.selectAll("g.node")
                    .append("text")
                    .style("display", "inline")
                    .attr("class", "isoText")
                    .attr("x", "1em")
                    .attr("y", "3em")
                    .html(function(d) {
                        return that.dagre.node(d).iso;
                    });

                //show tooltip on mouseover language tag   
                inner.selectAll("g.node")
                    .append("rect")
                    .attr("x", "0.8em")
                    .attr("y", "2.2em")
                    .attr("width", function(d) {
                        return that.dagre.node(d).iso.length / 1.7 + "em";
                    })
                    .attr("height", "1em")
                    .attr("fill", "red")
                    .attr("fill-opacity", 0)
                    .on("mouseover", function(d) {
                        d3.selectAll(".tooltip").remove();
                        d3.select("#tooltipPopup")
                            .style("display", "inline")
                            .style("left", (d3.event.pageX) + "px")
                            .style("top", (d3.event.pageY - 28) + "px")
                            .append("p")
                            .attr("class", "tooltip")
                            .html(that.dagre.node(d).lang);
                        d3.event.stopPropagation();
                    });

                //show tooltip on click on nodes                
                inner.selectAll("g.node")
                    .on("mouseover", function(d) {
                        d3.select("#tooltipPopup")
                            .style("display", "inline")
                            .style("left", (d3.event.pageX + 38) + "px")
                            .style("top", (d3.event.pageY - 28) + "px")
                            .html("");
                        var iri = that.dagre.node(d).iri;
                        if (typeof iri === "string") {
                            that.nodes[iri].queryTooltip();
                        } else {
                            iri.reduce(function(obj, i) {
			        var label = that.nodes[i].label;
			        if (obj.labels.indexOf(label) === -1) {
				    obj.labels.push(label);
				    obj.iris.push(i);
				    return obj;
			        } else {
				    return obj;
			        }
                            }, 
                            { labels: [], iris: [] }).iris.forEach(function(i) { 
                                that.nodes[i].queryTooltip(); 
                            });
                        }
                        d3.event.stopPropagation();
		    });
            }
        }

        class EtymologyGraph extends Graph {
            defineNodes(data) {
	        if (data.properties.length < 2) {
                    return;
                } else {
                    var that = this;
		    //CONSTRUCTING NODES
		    data.properties.forEach(function(element) {
		        //save all nodes
		        //define isAncestor
		        //push to eqIri
                        if (undefined !== element.s && undefined === that.nodes[element.s.value]) {
                            var label = (undefined === element.sLabel) ? undefined : element.sLabel.value;
                            that.nodes[element.s.value] = new Node(element.s.value, label);
			    //temporarily add nodes that are not in allArray (s is not in ancestors or descendants)
			    if (data.ancestorsAndDescendants.length === 0) {
                                if (data.ancestors.indexOf(element.s.value) === -1) {
			            that.nodes[element.s.value].temporary = true;
                                } 
                            } else {
                                if (data.ancestorsAndDescendants.indexOf(element.s.value) === -1) {
                                    that.nodes[element.s.value].temporary = true;
                                }
			    }
	                }
                        if (undefined !== element.rel) {
                            if (undefined === that.nodes[element.rel.value]) {
                                var label = (undefined === element.relLabel) ? undefined : element.relLabel.value;
                                that.nodes[element.rel.value] = new Node(element.rel.value, label);
                            }
                            if (data.ancestors.indexOf(element.rel.value) > -1) {
                                that.nodes[element.rel.value].isAncestor = true;
                            }
                        }
                        if (undefined !== element.rel && undefined !== element.eq) {
                            if (undefined === that.nodes[element.eq.value]) {
                                var label = (undefined === element.eqLabel) ? undefined : element.eqLabel.value;
                                that.nodes[element.eq.value] = new Node(element.eq.value, label);
                            }
                            if (element.rel.value !== element.eq.value) {
                                if (that.nodes[element.rel.value].eqIri.indexOf(element.eq.value) === -1) {
                                    that.nodes[element.rel.value].eqIri.push(element.eq.value);
                                }
                                if (that.nodes[element.eq.value].eqIri.indexOf(element.rel.value) === -1) {
                                    that.nodes[element.eq.value].eqIri.push(element.rel.value);
                                }
                            }
                        }
                    });
                }
            }
           
            defineGraphNodes() {
                var that = this;
	        for (var n in that.nodes) {
		    var gn = that.nodes[n].graphNode;
		    if (undefined === that.graphNodes[gn]) {
		        var gg = new GraphNode(gn);
		        gg.counter = gn;
		        gg.iri = that.nodes[n].eqIri;
		        gg.iso = that.nodes[n].iso;
		        gg.label = gg.iri.map(function(i) {
			        return that.nodes[i].label;
			    })
			    .filter(etyBase.helpers.onlyUnique)
			    .join(",");
		        gg.lang = that.nodes[n].lang;
		        gg.isAncestor = that.nodes[n].isAncestor;
		        that.graphNodes[gn] = gg;
		    }
	        }
	    }
	
	    defineGraphEdges(data) {
                var that = this;
	        data.properties.forEach(function(element) {
		    if (undefined !== element.rel && undefined !== element.s){  
			if (undefined !== that.nodes[element.s.value] && undefined !== that.nodes[element.rel.value]) {
			    var source = that.nodes[element.rel.value].graphNode, target = that.nodes[element.s.value].graphNode;
			    var color = (that.graphNodes[source].isAncestor && that.graphNodes[target].isAncestor) ? "red" : "steelBlue";
			    if (source !== target) {
				that.graphEdges.push({
					source: source,
					target: target,
					style: {label: "",
						lineInterpolate: "basis",
						arrowheadStyle: "fill: " + color,
						style: "stroke: " + color + "; fill: none; stroke-width: 0.2em;"
						}
				    });
			    }
			}
		    }
		});
	    }

            predefine(data) {
                this.clean();
                this.merge();
                this.defineGraphNodes();
                this.defineGraphEdges(data);
            }

            define(data) {
                this.predefine(data);
                for (var n in this.graphNodes) { 
                    this.dagre.setNode(n, this.graphNodes[n].attr("default", true));
                }
		for (var e in this.graphEdges) {
                    var source = this.graphEdges[e].source, target = this.graphEdges[e].target;
		    this.dagre.setEdge(source, target);
                }
            }

            clean() { //remove temporary nodes
                var that = this;
		for (var n in that.nodes) {
		    if (that.nodes[n].temporary) {
			var iso = that.nodes[n].iso;
			var label = that.nodes[n].label;
			for (var m in that.nodes) {
			    if (that.nodes[m].iso === iso && that.nodes[m].label === label) {
				if (!that.nodes[m].temporary) {
				    that.nodes[n].temporary = false;
				}
				if (that.nodes[m].isAncestor) {
				    that.nodes[n].isAncestor = true;
				}
			    }
			}
		    }
		}
                
		for (var n in that.nodes) {
		    if (that.nodes[n].temporary) {
			that.nodes[n].eqIri.forEach(function(e) {
				if (!that.nodes[e].temporary) {
				    that.nodes[n].temporary = false;
				}
				if (that.nodes[e].isAncestor) {
				    that.nodes[n].isAncestor = true;
				}
			    });
		    }
		}

		for (var n in that.nodes) {
		    if (!that.nodes[n].temporary) {
			that.nodes[n].eqIri.forEach(function(e) {
				that.nodes[e].temporary = false;
			    });
		    }
		}
		
		for (var n in that.nodes) {
		    if (that.nodes[n].temporary) {
			delete that.nodes[n];
		    }
		}
            }
 
            merge() {
                 var that = this;
                //CONSTRUCTING GRAPHNODES
                //a graphNode is some kind of super node that merges Nodes that are etymologically equivalent
                //or that refer to the same word - also called here identical Nodes 
                //(e.g.: if only ee_word and ee_n_word with n an integer belong to
                //the set of ancestors and descendants           
                //then merge them into one graphNode) 
                //the final graph will use these super nodes (graphNodes)
                var counter = 0; //counts how many graphNodes have been created so far
                for (var n in that.nodes) {
                    if (that.nodes[n].ety === 0) {
                        var iso = that.nodes[n].iso;
                        var label = that.nodes[n].label;
                        var tmp = [];
                        for (var m in that.nodes) {
                            if (undefined !== that.nodes[m]) {
                                if (that.nodes[m].iso === iso && that.nodes[m].label === label) {
                                    if (that.nodes[m].ety > 0) {
                                        tmp.push(m);
                                    }
                                }
                            }
                        }
                        tmp = tmp.filter(etyBase.helpers.onlyUnique);
                        //if only ee_word and ee_n_word with n an integer belong to
                        //the set of ancestors and descendants
                        //then merge them in one graphNode
                        if (tmp.length === 1) {
                            //define node.graphNode
                            var eqIri = that.nodes[n].eqIri.concat(that.nodes[tmp[0]].eqIri).filter(etyBase.helpers.onlyUnique);

                            eqIri = eqIri.reduce(function(eq, element) {
                                eq = eq.concat(that.nodes[element].eqIri).filter(etyBase.helpers.onlyUnique);
                                return eq;
                            }, []);
                            var graphNode = eqIri.reduce(function(gn, element) {
                                if (undefined === that.nodes[element].graphNode) {
                                    return gn;
                                } else {
                                    gn.push(that.nodes[element].graphNode);
                                    return gn;
                                }
                            }, []).filter(etyBase.helpers.onlyUnique).sort();
                            if (graphNode.length === 0) {
                                graphNode = counter;
                                counter++;
                            } else {
                                graphNode = graphNode[0];
                            }

                            eqIri.forEach(function(element) {
                                that.nodes[element].eqIri = eqIri;
                                that.nodes[element].graphNode = graphNode;
                            });

                            counter++;
                        }
                    }
                }

                for (var n in that.nodes) {
                    if (undefined === that.nodes[n].graphNode) {
                        var eqIri = that.nodes[n].eqIri;
                        eqIri = eqIri.reduce(function(eq, element) {
                            eq = eq.concat(that.nodes[element].eqIri).filter(etyBase.helpers.onlyUnique);
                            return eq;
                        }, []);
                        var graphNode = eqIri.reduce(function(gn, element) {
                            if (undefined === that.nodes[element].graphNode) {
                                return gn;
                            } else {
                                gn.push(that.nodes[element].graphNode);
                                return gn;
                            }
                        }, []).filter(etyBase.helpers.onlyUnique).sort();
                        if (graphNode.length === 0) {
                            graphNode = counter;
                            counter++;
                        } else {
                            graphNode = graphNode[0];
                        }

                        eqIri.forEach(function(element) {
                            that.nodes[element].eqIri = eqIri;
                            that.nodes[element].graphNode = graphNode;
                        });

                        counter++;
                    }
                }
	    }

            filterLeaves(n) {
                 for (var i = 0; i < n; i ++) {
                     this.filterLeaves();
                 }
            }

            filterLeaves() {
                //if a node is a leaf and its parents are in the same language, remove those nodes          
	        for (var s in this.dagre._sucs) {
		    if (!this.graphNodes[s].isAncestor) {		    
		        if (Object.keys(this.dagre._sucs[s]).length === 0) { //if s is not an ancestor and has no children
			    var parents = Object.keys(this.dagre._preds[s]);
			    if (parents.length === 1) { //if s has one parent
			        if (this.graphNodes[s].iso === this.graphNodes[parents[0]].iso) {
				    this.graphNodes[s].toDelete = true;
			        }
			    }
		        } 
		    }
	        }
                for (var n in this.graphNodes) {
		    if (!this.graphNodes[n].toDelete && !this.graphNodes[n].isAncestor) {
	                var iso = this.graphNodes[n].iso;
		        var parents = Object.keys(this.dagre._preds[n]);
		        if (parents.length === 1 && parents[0].iso === iso && Object.keys(this.dagre._sucs[n]).length > 0) {
			    var toDelete = true;
			    for (var s in this.dagre._sucs[n]) {//if all children of s are to be deleted
			        if (!this.graphNodes[s].toDelete) {
				    toDelete = false;
			        }
		            }
			    if (toDelete) {
			        this.graphNodes[n].toDelete = true;
		            }
		        }
		    }
	        }
	    
	        this.dagre = new dagreD3.graphlib.Graph().setGraph({ rankdir: "LR" });
	        for (var n in this.graphNodes) {
		    if (!this.graphNodes[n].toDelete) {
		        this.dagre.setNode(n, this.graphNodes[n].attr("default", true));
		    }
	        }
	        for (var e in this.graphEdges) {
		    var source = this.graphEdges[e].source, target = this.graphEdges[e].target;
		    if (!this.graphNodes[source].toDelete && !this.graphNodes[target].toDelete) {
		        this.dagre.setEdge(source, target, this.graphEdges[e].style);
		    }
	        }
	    }
        }

        class AncestorsGraph extends EtymologyGraph {
            constructor(iri) {
                super();

                $("#message")
                    .css("display", "inline")
                    .html(etyBase.MESSAGE.loadingMore);
                d3.select("#tooltipPopup")
                    .attr("display", "none");
                $("#tree-overlay")
                    .remove();

                const params = new URLSearchParams();
                params.set("format", "application/sparql-results+json");
                var url = etyBase.config.urls.ENDPOINT + "?" + params;
                
                etyBase.DB.ancestorQuery(iri, 5)
                    .subscribe(ancestorResponse => {
                        var data = new Data();
                        data.setAncestors(iri, ancestorResponse);
                        var sources = data.lastAncestors.map(function(element) {
                                return etyBase.DB.ancestorQuery(element, 5);
                            });
                        var obs = (data.lastAncestors.length === 0) ? Rx.Observable.timer(1) : Rx.Observable.zip.apply(this, sources)
                            .catch((err) => {
                                d3.select("#message").html(etyBase.MESSAGE.serverError);

                                /* Return an empty Observable which gets collapsed in the output */
                                return Rx.Observable.empty();
                            });
                        obs.subscribe(moreAncestorsResponse => { 
                                data.addAncestors(moreAncestorsResponse);
                                etyBase.DB.slicedQuery(data.ancestors, etyBase.DB.propertyQuery, 3) 
                                    .subscribe(propertyResponse => {
                                        //constructing etymologyNodes, graphNodes, graphEdges
		                        //etymologyNodes is the set of input etymology entries
		                        //graphNodes is the set of nodes in the full graph; a graphNode can correspond to multiple etymology entries (i.e. multiple elements in etymologyNodes) 
		                        data.setProperties(propertyResponse);
                                        this.defineNodes(data); 

                                        if (Object.keys(this.nodes).length < 2) {
                                            $("#message")
                                                .css("display", "inline")
                                                .html(etyBase.MESSAGE.noEtymology);
                                        } else {
                                            $("#helpPopup")
                                                .html(etyBase.HELP.dagre);
			                    this.define(data);
                                            $("#message")
		                                .css("display", "none");
                                            this.render();

                                            var btnExpand = $("#btnExpand");
                                            Rx.Observable.fromEvent(btnExpand, "click").subscribe(e => {
                                                d3.select("#tooltipPopup")
                                                    .attr("display", "none");
                                                $("#tree-overlay")
                                                    .remove();
                                                $("#message")
                                                    .css("display", "inline")
                                                    .html(etyBase.MESSAGE.loadingMore);

                                                //search the full tree
                                                //filteredAncestorArray is ancestorArray without words that start or end with dash
                                                //we will find descendants of filteredAncestorArray, thus excluding descendants of words that start or end with dash
			                        etyBase.DB.slicedQuery(data.ancestors.filter(etyBase.helpers.lemmaNotStartsOrEndsWithDash), etyBase.DB.descendantQuery, 8)
			                            .subscribe(descendantResponse => {
                                                        data.addDescendants(descendantResponse);
                                                        etyBase.DB.slicedQuery(data.ancestorsAndDescendants, etyBase.DB.propertyQuery, 3) 
                                                            .subscribe(propertyResponseWithDescendants => {
					                        //constructing etymologyNodes, graphNodes, graphEdges
						                //etymologyNodes is the set of input etymology entries
						                //graphNodes is the set of nodes in the full graph; a graphNode can correspond to multiple etymology entries (i.e. multiple elements in etymologyNodes)
						                data.setProperties(propertyResponseWithDescendants);
                                                                var etyGraph = new EtymologyGraph();
						                etyGraph.defineNodes(data);
						                etyGraph.define(data);
	                                                        
                                                                etyGraph.filterLeaves(2);
                                                                $("#message")
		                                                    .css("display", "none");
                                                                
                                                                etyGraph.render();
                                                           
			                                },
                                                        error => etyBase.helpers.serverError(error),
                                                        () => etyBase.helpers.debugLog("done property query"));
                                                },
                                                error => etyBase.helpers.serverError(error),
                                                () => etyBase.helpers.debugLog("done descendant query"));
                                            });
                                        }
                            },
                            error => etyBase.helpers.serverError(error),
                            () => etyBase.helpers.debugLog("done property query"));
                        },
                        error => etyBase.helpers.serverError(error),
                        () => etyBase.helpers.debugLog("done more ancestors query"));
                     },
                     error => etyBase.helpers.serverError(error),
                     () => etyBase.helpers.debugLog("done ancestor query"));
            }
 
            define(data) {
                this.predefine(data);
	        for (var n in this.graphNodes) {
                    if (this.graphNodes[n].isAncestor) {
		        this.dagre.setNode(n, this.graphNodes[n].attr("default", true));
                    }
		}
	        for (var e in this.graphEdges) {
                    var source = this.graphEdges[e].source, target = this.graphEdges[e].target;
                    if (this.graphNodes[source].isAncestor && this.graphNodes[target].isAncestor) {
	                this.dagre.setEdge(source, target, this.graphEdges[e].style);	
                    }
                }
            }
        }

        class DisambiguationGraph extends Graph {
            constructor(lemma) {
                super();

	        if (lemma.length < 2) {
		    return;
	        }

                //clean screen
                $("#tree-overlay")
                    .remove();
                d3.select("#tooltipPopup")
                    .style("display", "none");

                //query database
                var url = etyBase.config.urls.ENDPOINT + "?query=" + encodeURIComponent(etyBase.DB.disambiguationQuery(lemma));
                etyBase.DB.getXMLHttpRequest(url).subscribe(response => {
                    this.defineNodes(response);
                    if (Object.keys(this.nodes).length === 0) {
                        $("#message")
                            .css("display", "inline")
                            .html(etyBase.MESSAGE.notAvailable);
                    } else if (Object.keys(this.nodes).length === 1) {
                        var iri = Object.keys(this.nodes)[0];
                        new AncestorsGraph(iri);
                    } else {
                        $("#helpPopup")
                            .html(etyBase.HELP.disambiguation);
                        $("#message")
                            .css("display", "inline")
                            .html(etyBase.MESSAGE.disambiguation);
                        this.define();
                        this.render();
                        var that = this;
			d3.select("#inner").selectAll("g.node")
                            .on("click", function(d) {
                                var iri = that.dagre.node(d).iri;
                                new AncestorsGraph(iri);
                            });
                    }
                },
                error => $("#message")
                             .css("display", "inline")
                             .html("Server error. " + error),
                () => etyBase.helpers.debugLog("done disambiguation"));
            }

            defineNodes(response) {
                var that = this;
                JSON.parse(response).results.bindings.forEach(function(n) {
                    if (n.et.value === "" || n.et.value.split(",").length > 1) {
                        that.nodes[n.iri.value] = new Node(n.iri.value, n.lemma.value);
                    }
                });
            }

            define() {
	        var target = null;
	        for (var n in this.nodes) {
		    this.dagre.setNode(n, this.nodes[n].attr("default", true));
		    if (null !== target) {
		        this.dagre.setEdge(n, target, { label: "", style: "stroke-width: 0" });
		    }
		    target = n;
	        }
	    }
        }

        var init = function() {

            $("#helpPopup")
                .html(etyBase.HELP.intro);

            var div = d3.select("body").append("div")
                .attr("data-role", "popup")
                .attr("data-dismissible", "true")
                .attr("id", "tooltipPopup")
                .style("display", "none")
                .attr("class", "ui-content tooltipDiv");

            $(window).click(function() {
                d3.select("#tooltipPopup")
                    .style("display", "none");
            });

            $("#tooltipPopup").click(function(e) {
                e.stopPropagation();
            });

            $("#search").on("keypress", function(e) {
                var search = this;
                if (e.which === 13) {
                    var lemma = $(search).val();
		    new DisambiguationGraph(lemma);
		}
	    });

	    $("#btnSearch").click(function(e) {
		var lemma = $("#search").val();
		new DisambiguationGraph(lemma);
            });
        };

        this.init = init;

        etyBase[moduleName] = this;
    };

    return module;

})(GRAPH || {});
