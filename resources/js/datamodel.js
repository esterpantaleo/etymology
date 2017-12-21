/*globals
  $, d3, console, dagreD3, Rx, window, document, URLSearchParams
*/
/*jshint loopfunc: true, shadow: true, latedef: false */ // Consider removing this and fixing these
var DATAMODEL = (function(module) {
    
    module.bindModule = function(base, moduleName) {
        var etyBase = base;

	var urlFromQuery = function(query) {
            return etyBase.config.urls.ENDPOINT + "?query=" + encodeURIComponent(query);
        };

	var wiktionaryLabelOf = function(iri) {
            if (iri.startsWith(etyBase.config.urls.WIKT_RECONSTRUCTION)) {
                return iri.replace(etyBase.config.urls.WIKT_RECONSTRUCTION, "")
                    .split("/")
                    .join(" ");
            } else {
                return iri.split("/")
                    .pop()
                    .split("#")
                    .reverse()
                    .join(" ")
                    .replace(/_/g, " ");
            }
        };

        var parseLabel = function(label) {
            return label.replace(/^_/, '*')
                .replace("__", "'")
                .replace("%C2%B7", "·")
                .replace(/_/g, " ");
        };

	var encodeLabel = function(label) {
	    return label.replace(/'/g, "\\\\'")   //"
	        .replace("·", "%C2%B7") 
		.replace("*", "_") 
		.replace("'", "__");
		//.replace("/", "!slash!");  
	};
        
        var dbnaryLabelOf = function(iri) {
            var tmp1 = iri.replace(etyBase.config.urls.DBNARY_ENG, "").split("/");
            var tmp2 = (tmp1.length > 1) ? tmp1[1] : tmp1[0];
            return parseLabel(tmp2.replace(/__ee_[0-9]+_/g, "")
			      .replace("__ee_", ""));
        };

        var dbnaryIsoOf = function(iri) {
            var tmp1 = iri.replace(etyBase.config.urls.DBNARY_ENG, "").split("/");
            return (tmp1.length > 1) ? tmp1[0] : "eng";
        };
	
        var dbnaryEtyOf = function(iri) {
            var tmp1 = iri.replace(etyBase.config.urls.DBNARY_ENG, "").split("/");
            var tmp2 = (tmp1.length > 1) ? tmp1[1] : tmp1[0];
            var tmp2 = tmp2.match(/__ee_[0-9]+_/g);
            return (null === tmp2) ? 0 : tmp2[0].match(/__ee_(.*?)_/)[1];
        };

        class EtymologyEntry {
            constructor(iri, label) {
                this.id = iri;
		this.label = (undefined === label) ? dbnaryLabelOf(iri) : parseLabel(label);
		this.iso = dbnaryIsoOf(iri);

                //set this.ety
                //this.ety is an integer representing the etymology number encoded in the iri;
                //if ety === 0 the iri is __ee_word         
                //if ety === 1 the iri is __ee_1_word            
                //etc       
	        this.ety = dbnaryEtyOf(iri);
                
                //set this.lang
                this.lang = etyBase.tree.langMap.get(this.iso);
		if (undefined === this.lang) {
                    console.log("Warning: no language corresponding to iso " + this.iso);
		}

                //initialize this.graphNode specifying the graphNode corresponding to the node
                this.node = undefined;
                
                //initialize this.iri is an array of iri-s of EtymologyEntries-s that are equivalent to the EtymologyEntry 
                this.iri = [];
                this.iri.push(iri);
                
                //initialize this.isAncestor
                this.isAncestor = false;

		//initialize posAndGloss and urlAndLabel
		this.posAndGloss = [];
		this.urlAndLabel = [];
            }
	}
	
	var assignNodes = function(values) {            
            //MERGING EtymologyEntries
            //a node merges EtymologyEntries that are etymologically equivalent 
            //or that refer to the same word 
            //MERGE EtymologyEntries THAT HAVE THE SAME iso AND label BUT A DIFFERENT ety 
            //(e.g.: if only ee_word and ee_n_word with n an integer belong to 
            //the set of ancestors and descendants 
            //then merge them into one node)
            //the final graph will use these nodes 
	    
            var id = 0; //counts how many nodes have been created so far 
            for (var n in values) {
                if (values[n].ety === 0) {
                    //count how many nodes share the same iso and label
                    var iso = values[n].iso;
                    var label = values[n].label;
                    var tmp = [];
                    for (var m in values) {
                        if (undefined !== values[m]) {
                            if (values[m].iso === iso && 
				values[m].label === label && 
				values[m].ety > 0) {
                                tmp.push(m);
                            }
                        }
                    }
                    tmp = tmp.filter(etyBase.helpers.onlyUnique);
                    //if only values[ee_word] and values[ee_ety_word] exist (with ety an integer > 0)
                    //then merge them in one node 
                    if (tmp.length === 1) {
                        //define node 
                        var iri = values[n].iri
                            .concat(values[tmp[0]].iri)
                            .filter(etyBase.helpers.onlyUnique)
                            .reduce(function(eq, element) {
                                eq = eq.concat(values[element].iri)
				    .filter(etyBase.helpers.onlyUnique);
                                return eq;
                            }, []);
			
                        var node = iri.reduce(function(gn, element) {
                            if (undefined === values[element].node) {
                                return gn;
                            } else {
                                gn.push(values[element].node);
                                return gn;
                            }
                        }, [])
			    .filter(etyBase.helpers.onlyUnique)
			    .sort();
                        if (node.length === 0) {
                            node = id;
                            id ++;
                        } else {
                            node = node[0];
                        }
			
                        iri.forEach(function(element) {
                            values[element].iri = iri;
                            values[element].node = node;
                            });
			
                        id ++;
                    }
                }
            }
	    
            //MERGE NODES IN iri
            for (var n in values) {
                if (undefined === values[n].node) {
                    var iri = values[n].iri;
                    iri = iri.reduce(function(eq, element) {
			eq = eq.concat(values[element].iri)
			    .filter(etyBase.helpers.onlyUnique);
			return eq;
		    }, []);
                    var node = iri.reduce(function(gn, element) {
			if (undefined === values[element].node) {
			    return gn;
			} else {
				gn.push(values[element].node);
			    return gn;
			}
		    }, [])
			.filter(etyBase.helpers.onlyUnique).sort();


                    if (node.length === 0) {
                        node = id;
                        id ++;
                    } else {
                        node = node[0];
                    }
			
                    iri.forEach(function(element) {
			values[element].iri = iri;
			values[element].node = node;
		    });
		    
                    id ++;
                }
            }

	    return values;
	};

	var disambiguationQuery = function(lemma) {
	    var encodedLemma = encodeLabel(lemma);
	    var url = urlFromQuery(etyBase.DB.disambiguationQuery(encodedLemma));

	    return etyBase.DB.getXMLHttpRequest(url)
		.map(parseDisambiguation);
	};

	var glossQuery = function(iri) {
	    var url = urlFromQuery(etyBase.DB.glossQuery(iri));

	    return etyBase.DB.getXMLHttpRequest(url)
		.map(parseData);
	};

	var propertyQueryPart = function(iri) {
	    var url = urlFromQuery(etyBase.DB.propertyQuery(iri));
	    
	    return etyBase.DB.getXMLHttpRequest(url)
		.map(parseProperties);
	};

	var propertyQuery = function(ancestors) {
	    return Rx.Observable.zip
		.apply(this, ancestors.map(propertyQueryPart));
	};

	var dataQuery = function(iris, graph) {
	    return Rx.Observable.zip
                .apply(this, iris.map(glossQuery))
	        .map(d => {
			d.map((a, i) => {
				var iri = iris[i];
				graph.values[iri].posAndGloss = a.posAndGloss;
			        graph.values[iri].urlAndLabel = a.urlAndLabel;
			    });
			return graph;
		    });
	};

	var parseData = function(response) {
	    var posAndGloss = JSON.parse(response).results.bindings
                .map(element => {
			return {
			    pos: (undefined === element.pos)? "" : element.pos.value, 
                            gloss: (undefined === element.gloss)? "" : element.gloss.value.split(";;;;") 
                        }});
	    var urlAndLabel = JSON.parse(response).results.bindings[0]
	        .links.value
	        .split(",")
	        .map(url => { 
		        var label = wiktionaryLabelOf(url);
			return {
		     	    url: url, 
			    label: label
			}; 
		    });
	    return {
                posAndGloss: posAndGloss,
                urlAndLabel: urlAndLabel
            };
        };
	
	var parseDisambiguation = function(response) {
	    return JSON.parse(response)
                .results
                .bindings
                .filter(n => {
                    return (n.et.value === "" ||
                            n.et.value.split(",").length > 1) ?
                        true : false;
                })
                .map(n => {
                    return {
                        id: n.iri.value,
                        label: n.lemma.value
                    };
                })
		.reduce((a, n) => {
		    a[n.id] = new EtymologyEntry(n.id, n.label);
		    return a;
		}, {});
	};

	var parseProperties = function(response) {
	    return JSON.parse(response).results.bindings;
	};

	var disambiguationGraphQuery = function(response) {
	    //sort iris (and therefore nodes) in alphabetical order by language
	    var iris = Object.keys(response)
	        .map((n) => response[n].id)
	        .sort(function(a, b){
		    if (a < b) return -1;
		    if (a > b) return 1;
		    return 0;
		    });
	    
	    var graph = {}
	    graph.values = {};
	    iris.map((iri, i) => graph.values[iri] = new EtymologyEntry(iri, response[iri].label));
	    
	    return dataQuery(iris, graph);
	};

	var findMoreAncestors = function(response) {
	    return etyBase.DB.moreAncestorsQuery(response)
		.map((moreResponse) => {
		    var moreAncestors = [];
		    if (0 !== moreResponse) {
			moreAncestors = moreResponse.reduce(function(val, r) {
			    var moreVal = parseAncestors(r).all;
			    return val.concat(moreVal);
			}, []);
		    }
		    return moreAncestors;
		});
	};
    
	var findAncestors = function(iri) {
	    return etyBase.DB.postXMLHttpRequest(etyBase.DB.iterativeAncestorQuery(0, iri))
		.map((response) => {
		    var ancestors = parseAncestors(response);
		    ancestors.all = ancestors.all.concat(iri);
		    return ancestors;
	    });
	};

	var mergeAncestors = function(ancestors, moreAncestors) {
	    if (moreAncestors !== 0) {
		return ancestors.all
		    .concat(moreAncestors)
		    .filter(etyBase.helpers.onlyUnique);
	    }
	    return ancestors.all;
	};

	
	var ancestorsQuery = function(iri, showAncestors) {
	    
	    var ancestors$ = findAncestors(iri);
	    
	    return ancestors$.subscribe((ancestors) => {
		var moreAncestors$ = (ancestors.last.length === 0) ? Rx.Observable.timer(1) : findMoreAncestors(ancestors.last)
                    .catch((err) => {
                        d3.select("#message").html(etyBase.MESSAGE.serverError);
			
                        /* Return an empty Observable which gets collapsed in the output */
                        return Rx.Observable.empty();
		    });
		return moreAncestors$.subscribe((moreAncestors) => {
		    
		    ancestors = mergeAncestors(ancestors, moreAncestors);
		    
		    var properties$ = propertyQuery(ancestors);
		    return properties$.subscribe((properties) => {
			var properties = [].concat.apply([], properties);
			var ancestorsGraph = setEtymologyEntries(properties, ancestors);

			//query data (gloss, pos, link url, link label)   
			var iris = [];
			for (var e in ancestorsGraph.values) {
			    iris.push(ancestorsGraph.values[e].id);
			}
			return dataQuery(iris, ancestorsGraph)
			    .subscribe(showAncestors);
		    });
		});
	    });
	};
	
	var parseAncestors = function(response) {
            if (etyBase.config.depthAncestors > 5) {
		console.log("Warning: etytree is only parsing ancestors up to a depth of 5");
	    }
            return JSON.parse(response).results.bindings
		.reduce((val, a) => {
                    val.all.push(a.ancestor1.value);
                    if (a.der1.value === "0" && undefined !== a.ancestor2) {
			var label1 = dbnaryLabelOf(a.ancestor1.value);
			if (!(label1.startsWith("-") || 
			      (label1.endsWith("-") && 
			       !label1.startsWith("*")))) {
                            val.all.push(a.ancestor2.value);
                            if (a.der2.value === "0" && 
				undefined !== a.ancestor3) {
				var label2 = dbnaryLabelOf(a.ancestor2.value);
				if (!(label2.startsWith("-") || 
				      (label2.endsWith("-") && 
				       !label2.startsWith("*")))) {
				    val.all.push(a.ancestor3.value);
				    
				    if (a.der3.value === "0" && undefined !== a.ancestor4) {
					var label3 = dbnaryLabelOf(a.ancestor3.value);
					if (!(label3.startsWith("-") || 
					      (label3.endsWith("-") && 
					       !label3.startsWith("*")))) {
					    val.all.push(a.ancestor4.value);
					    if (a.der4.value === "0" && 
						undefined !== a.ancestor5) { 
						var label4 = dbnaryLabelOf(a.ancestor4.value);
						if (!(label4.startsWith("-") 
						      || (label4.endsWith("-") 
							  && !label4.startsWith("*")))) {
						    val.all.push(a.ancestor5.value);
						    val.last.push(a.ancestor5.value);
						}
					    }
					}
				    }
				}
                            }
			}
                    }
                    return val;
		}, { all: [], last: [] });
        }
	
        var setEtymologyEntries = function(properties, ancestors) {
	    var ee = {};

	    //CONSTRUCTING NODES
	    properties.forEach(function(element) {
		    //save all nodes 
		    //define isAncestor
		    //push to iri 
		    if (undefined !== element.s && undefined === ee[element.s.value]) {
			var label = (undefined === element.sLabel) ? undefined : element.sLabel.value;
			ee[element.s.value] = new EtymologyEntry(element.s.value, label);
			//temporarily add nodes that are not ancestors
			if (ancestors.indexOf(element.s.value) === -1) {
			    ee[element.s.value].temporary = true;
			}
		    }
		    if (undefined !== element.rel) {
			if (undefined === ee[element.rel.value]) {
			    var label = (undefined === element.relLabel) ? undefined : element.relLabel.value;
			    ee[element.rel.value] = new EtymologyEntry(element.rel.value, label);
			}
			if (ancestors.indexOf(element.rel.value) > -1) {
			    ee[element.rel.value].isAncestor = true;
			}
		    }
		    if (undefined !== element.rel && undefined !== element.eq) {
			if (undefined === ee[element.eq.value]) {
			    var label = (undefined === element.eqLabel) ? undefined : element.eqLabel.value;
			    ee[element.eq.value] = new EtymologyEntry(element.eq.value, label);
			}
			if (element.rel.value !== element.eq.value) {
			    if (ee[element.rel.value].iri.indexOf(element.eq.value) === -1) {
				ee[element.rel.value].iri.push(element.eq.value);
			    }
			    if (ee[element.eq.value].iri.indexOf(element.rel.value) === -1) {
				ee[element.eq.value].iri.push(element.rel.value);
			    }
			}
		    }
		});
            
	    ee = cleanEtymologyEntries(ee);
	    ee = assignNodes(ee);
	    
	    var edges = properties
	        .filter((p) => {
                    return (
			    undefined !== p.rel &&
                            undefined !== p.s &&
                            undefined !== ee[p.s.value] &&
                            undefined !== ee[p.rel.value]
			    ) ? true : false;
		})
	        .reduce((a, p) => {
                    var source = ee[p.rel.value].node,
                    target = ee[p.s.value].node;
                    if (source !== target) {
			a.push({
				source: source,
				    target: target,
				    style: {
				    label: "",
					lineInterpolate: "basis",
					arrowheadStyle: "fill: steelblue"
					}
			    });
                    }
		    return a;
		}, [])//remove duplicate edges, maybe remove this
	        .filter((thing, index, self) =>
		    index === self.findIndex((t) => (
						     t.source === thing.source && t.target === thing.target
						     ))
			);
	    return { values: ee, edges: edges };
	};
   
	
	var cleanEtymologyEntries = function(values) { //remove temporary nodes
            for (var n in values) {
                if (values[n].temporary) {
                    for (var m in values) {
                        if (values[m].iso === values[n].iso && 
			    values[m].label === values[n].iso) {
                            if (!values[m].temporary) {
                                values[n].temporary = false;
                            }
                            if (values[m].isAncestor) {
                                values[n].isAncestor = true;
                            }
                        }
                    }
                }
            }
		
            for (var n in values) {
                if (values[n].temporary) {
                    values[n].iri.forEach(function(e) {
                        if (!values[e].temporary) {
                            values[n].temporary = false;
                        }
                        if (values[e].isAncestor) {
                            values[n].isAncestor = true;
                        }
                    });
                }
            }
	    
            for (var n in values) {
                if (!values[n].temporary) {
                    values[n].iri.forEach(function(e) {
                        values[e].temporary = false;
                    });
                }
            }
	    
            for (var n in values) {
                    if (values[n].temporary) {
                        delete values[n];
                    }
            }
	    return values;
	};

        var descendantsQuery = function(iris) {
	    return Rx.Observable.zip
                .apply(this, iris.map(descendantsQueryPart))
	        .map((response) => { 
		    var values = response.reduce((ee, e) => {
			    for (var i in e) {
				ee[i] = e[i];
			    }
			    return ee;
			}, {});
		    return assignNodes(values);
		});	 
	};

	var descendantsQueryPart = function(iri) {
	    var url = etyBase.config.urls.ENDPOINT + "?query=" + encodeURIComponent(etyBase.DB.descendantQuery(iri));
	    
	    return etyBase.DB.getXMLHttpRequest(url)
		.map(parseDescendants);
	};

	var parseDescendants = function(response) {
	    var values = {};
	    JSON.parse(response).results
		.bindings
		.forEach(function(j) {
		    if (undefined !== j.descendant1) {
			values[j.descendant1.value] = new EtymologyEntry(j.descendant1.value, j.label1.value);
			if (undefined != j.ee) {
			    values[j.ee.value] = new EtymologyEntry(j.ee.value, j.labele.value);
			    values[j.descendant1.value]
				.iri
				.push(j.ee.value);
			}
		    }
		});	    
            return values;
	};
	
	this.EtymologyEntry = EtymologyEntry;
	this.glossQuery = glossQuery;
	this.disambiguationQuery = disambiguationQuery;
	this.disambiguationGraphQuery = disambiguationGraphQuery;
	this.ancestorsQuery = ancestorsQuery;
	this.descendantsQuery = descendantsQuery;
	this.assignNodes = assignNodes;
	this.dataQuery = dataQuery;

	etyBase[moduleName] = this;
    };
    
    return module;

})(DATAMODEL || {});