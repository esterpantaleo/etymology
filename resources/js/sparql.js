var SPARQL = (function(module) {

    module.ENDPOINT = "https://etytree-virtuoso.wmflabs.org/sparql";

    module.getXMLHttpRequest = function(url) {
	return Rx.Observable.create(observer => {
            const req = new XMLHttpRequest();
            req.open('GET', url);
            req.overrideMimeType('application/sparql-results+json');
            req.onload = () => {
		if (req.status === 200) {
                    observer.next(req.responseText);
                    observer.complete();
		} else {
                    observer.error(new Error(req.statusText));
		}
            };
            req.onerror = () => {
		observer.error(new Error('An error occured'));
            };
            req.setRequestHeader('Accept', 'application/json, text/javascript');
            req.send();
	});
    }

    //function to slice up a big sparql query (that cannot be processed by virtuoso)                     
    // into a bunch of smaller queries in chunks of "chunk"                                             
    module.slicedQuery = function(myArray, queryFunction, chunk) {
	var i, j, tmpArray, url, sources = [];
	for (i = 0, j = myArray.length; i < j; i += chunk) {
            tmpArray = myArray.slice(i, i + chunk);
	    //console.log(SPARQL.unionQuery(tmpArray, query));                   
            url = this.ENDPOINT + "?query=" + encodeURIComponent(this.unionQuery(tmpArray, queryFunction));
            if (debug) {
		console.log(url);
            }
	    sources.push(this.getXMLHttpRequest(url));
	}
	const queryObservable = Rx.Observable.zip.apply(this, sources)
            .catch((err) => {
		d3.select("#message").html(MESSAGE.serverError);
		
		//Return an empty Observable which gets collapsed in the output             
		return Rx.Observable.empty();
            });
	return queryObservable;
    }


    module.disambiguationQuery = function(lemma) {
        var encodedWord = lemma.replace(/'/g, "\\\\'").replace("·", "%C2%B7");
        var query = 
            "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org/dbnaryetymology#> " +
            "PREFIX dbnary: <http://kaiko.getalp.org/dbnary#> " +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "SELECT DISTINCT ?iri (group_concat(distinct ?ee ; separator=\",\") as ?et) " +
            "WHERE { " +
            "    ?iri rdfs:label ?label . ?label bif:contains \"\'" + encodedWord + "\'\" . " +
            //exclude entries that contain the searched word but include other words
            //(e.g.: search="door" label="doorbell", exclude "doorbell")
            "    FILTER REGEX(?label, \"^" + encodedWord + "$\", 'i') . " +
            "    ?iri rdf:type dbetym:EtymologyEntry . " +
            "    OPTIONAL { " +
            "        ?iri dbnary:describes  ?ee . " +
            "        ?ee rdf:type dbetym:EtymologyEntry . " +
            "    } " +
	    "} ";
        
        return query;
    };

    //DEFINE QUERY TO GET LINKS, POS AND GLOSS           
    module.lemmaQuery = function(iri) { 
        var query = 
            "PREFIX dbnary: <http://kaiko.getalp.org/dbnary#> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX lexinfo: <http://www.lexinfo.net/ontology/2.0/lexinfo#> " +
            "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
            "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#> " +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
            "SELECT DISTINCT ?ee ?pos (group_concat(distinct ?def ; separator=\";;;;\") as ?gloss) (group_concat(distinct ?also ; separator=\",\") as ?links) " +
            "WHERE { " +
            "    <" + iri.replace(/__ee_[0-9]+_/g, "__ee_") + "> rdfs:seeAlso ?also . " +
            "    OPTIONAL { " +
            "        <" + iri + "> dbnary:describes ?ee . " +
            "        OPTIONAL { " +
            "            ?ee rdf:type ontolex:LexicalEntry . " +
            "            ?ee dbnary:partOfSpeech ?pos . " +
            "        } " +
            "        OPTIONAL { " +
            "            ?ee dbnary:describes ?nee . " +
            "            ?nee rdf:type ontolex:LexicalEntry . " +
            "            ?nee dbnary:partOfSpeech ?pos . " +
            "        } " +
            "        OPTIONAL { " +
            "            ?ee dbnary:describes ?cee . " +
            "            ?cee dbnary:describes ?nee . " +
            "            ?nee rdf:type ontolex:LexicalEntry . " +
            "            ?nee dbnary:partOfSpeech ?pos . " +
            "        } " +
            "        OPTIONAL { " +
            "            ?ee ontolex:sense ?sense . " +
            "            ?sense skos:definition ?val . " +
            "            ?val rdf:value ?def . " +
            "        } " +
            "        OPTIONAL { " +
            "            ?ee dbnary:describes ?nee . " +
            "            ?nee rdf:type ontolex:LexicalEntry . " +
            "            ?nee ontolex:sense ?sense . " +
            "            ?sense skos:definition ?val . " +
            "            ?val rdf:value ?def . " +
            "        } " +
            "        OPTIONAL { " +
            "            ?ee dbnary:describes ?cee . " +
            "            ?cee dbnary:describes ?nee . " +
            "            ?nee rdf:type ontolex:LexicalEntry . " +
            "            ?nee skos:sense ?sense . " +
            "            ?sense skos:definition ?val . " +
            "            ?val rdf:value ?def . " +
            "        } " +
            "    } " +
	    "} ";
        return query;
    };

    //DEFINE QUERIES TO PLOT GRAPH          
    module.ancestorQuery = function(iri, queryDepth) {     
        var query = "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org/dbnaryetymology#> ";
        if (queryDepth === 1) {
            query +=
                "SELECT DISTINCT ?ancestor1 ?ancestor2 " +
                "{ " +
                "   <" + iri + "> dbetym:etymologicallyRelatedTo{0,5} ?ancestor1 . " +
                "   OPTIONAL {?eq dbetym:etymologicallyEquivalentTo ?ancestor1 . " +
                "   ?eq dbetym:etymologicallyRelatedTo* ?ancestor2 .} " +
                "} ";
        } else if (queryDepth === 2) {
            query += 
                "SELECT DISTINCT ?ancestor1 " +
                "{ " +
                "   <" + iri + "> dbetym:etymologicallyRelatedTo{0,5} ?ancestor1 . " +
	        "} ";
	}
	return query;
    };

    module.descendantQuery = function(iri) { 
        var query = 
            "SELECT DISTINCT ?descendant1 " + // ?descendant2",
            "{ " +
            "   ?descendant1 dbetym:etymologicallyRelatedTo{0,1} <" + iri + "> . " +
            //   "   OPTIONAL {?eq dbetym:etymologicallyEquivalentTo ?descendant1 . " +
            //  "   ?descendant2 dbetym:etymologicallyRelatedTo* ?eq .} " +
	    "} ";
        return query;
    };

    module.propertyQuery = function(iri) { 
        var query = 
            "SELECT DISTINCT ?s ?rel ?eq ?der " +
            "{           " +
            "   VALUES ?rel " +
            "   {           " +
            "       <" + iri + "> " +
            "   } " +
            "   ?s dbetym:etymologicallyRelatedTo ?rel . " +
            "   OPTIONAL { ?rel dbetym:etymologicallyEquivalentTo{0,6} ?eq . } " +
            "   OPTIONAL { ?s dbetym:etymologicallyDerivesFrom ?der . } " +
            //  "   FILTER NOT EXISTS { ?rel dbetym:etymologicallyDerivesFrom ?der2 . } "+
	    "}";
        return query;
    };

    module.unionQuery = function(iriArray, query) {
        var query = 
            "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org/dbnaryetymology#> " +
	    "SELECT * WHERE {{ " +
             iriArray.map(function(iri) { return query(iri); }).join("} UNION {") +
            "}}";
        return query;
    };

    return module;
})(SPARQL || {});
