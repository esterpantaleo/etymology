/*globals
    Rx, XMLHttpRequest, console, d3
*/
var DB = (function(module) {

    module.bindModule = function(base, moduleName) {
        var etyBase = base;

        var getXMLHttpRequest = function(url) {
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
        };

        var disambiguationQuery = function(lemma) {
            var encodedLemma = lemma
		.replace(/'/g, "\\\\'")
		.replace("·", "%C2%B7")
		.replace("*", "_"); //parse reconstructed words 
	    //.replace("/", "!slash!");  
            var query =
                "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org/dbnaryetymology#> " +
                "PREFIX dbnary: <http://kaiko.getalp.org/dbnary#> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "SELECT DISTINCT ?iri (group_concat(distinct ?ee ; separator=\",\") as ?et) ?lemma " +
                "WHERE { " +
                "    ?iri rdfs:label ?label . ?label bif:contains \"\'" + encodedLemma + "\'\" . " +
                // exclude entries that contain the searched word but include other words
                // (e.g.: search="door" label="doorbell", exclude "doorbell")
                "    FILTER REGEX(?label, \"^" + encodedLemma + "$\", 'i') . " +
                "    ?iri rdf:type dbetym:EtymologyEntry . " +
                "    OPTIONAL { " +
                "        ?iri dbnary:describes  ?ee . " +
                "        ?ee rdf:type dbetym:EtymologyEntry . " +
                "    } " +
                "    BIND (STR(?label) AS ?lemma) " +
                "} ";

            return query;
	};

        //DEFINE QUERY TO GET LINKS, POS AND GLOSS           
        var lemmaQuery = function(iri) {
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

	    var query = function(iteration, describes, resource) {
		var toreturn = "";
		if (undefined === resource){
		    resource = "?ancestor" + iteration;
		}
		if (describes) {
		    toreturn += resource + " dbnary:describes ?var" + iteration + " . ";
		    resource = "?var" + iteration;
		}
		toreturn += resource + " dbetym:etymologicallyRelatedTo ?ancestor" + (iteration+1) + " . " +
                " BIND(EXISTS {" + resource + " dbetym:etymologicallyDerivesFrom ?ancestor" + (iteration+1) + " } AS ?der" + (iteration+1) + ") " +
                " BIND(EXISTS {" + resource + " dbetym:etymologicallyEquivalentTo ?ancestor" + (iteration+1) + " } AS ?eq" + (iteration+1) + ") ";
		return toreturn;
	    }
	    //Array(N).fill().map((e,i)=>i+1);
	    //for (var i = 1; i <= N; i++) {

	    var intertwinedQuery = function(){
		var toreturn = 
		"OPTIONAL { " +
                query(1, false) +
                "    OPTIONAL { " +
                query(2, false) +
                "        OPTIONAL { " +
                query(3, false) +
                "        } " +
                "        OPTIONAL { " +
                query(3, true) +
                "        } " +
                "    } " + 
                "    OPTIONAL { " + 
                query(2, true) +
		"        OPTIONAL { " +
                query(3, false) +
                "        } " +
		"        OPTIONAL { " +
                query(3, true) +
                "        } " +
                "    } " + 
                "} " + 
                "OPTIONAL { " +
                query(1, true) +
                "    OPTIONAL { " +
                query(2, false) +
		"        OPTIONAL { " +
                query(3, false) +
                "        } " +
		"        OPTIONAL { " +
                query(3, true) +
                "        } " +
                "    } " +
	        "    OPTIONAL { " +
                query(2, true) +
		"        OPTIONAL { " +
                query(3, false) +
                "        } " +
		"        OPTIONAL { " +
                query(3, true) +
                "        } " +
                "    } " +
                "} " ;
		return toreturn;
	    }
	    
	    var detailedAncestorQuery = function(iri) {
		var toreturn = 
		"PREFIX dbnary: <http://kaiko.getalp.org/dbnary#> " +
		"PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org/dbnaryetymology#> " + 
		"SELECT ?var0 ?ancestor1 ?var1 ?der1 ?eq1 ?ancestor2 ?var2 ?der2 ?eq2 ?ancestor3 {" +//?h ?der3 ?eq3 ?ancestor4 { " +//?k ?der4 ?eq4 ?ancestor5 {" + //?l ?der5 ?eq5 ?ancestor6 { " +
		"    { " + //open {
		"	SELECT DISTINCT * { " + //open select 
                query(0, false, "<" + iri+ ">") +
                intertwinedQuery() +
                "    }}" + //close select
	        " UNION " +
		"    {{ " +
	        "       SELECT DISTINCT * { " + //open select
	        query(0, true, "<" + iri+ ">") +
		intertwinedQuery() +
		"    }" + //close select 
                "}} " +//close {
                "} ";
		
		return toreturn;
            };

        //DEFINE QUERIES TO PLOT GRAPH          
        var ancestorQuery = function(iri, queryDepth) {
            var query = "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org/dbnaryetymology#> ";
            if (queryDepth === 1) {
                query +=
                    "SELECT DISTINCT ?ancestor1 ?der ?ancestor2 ?der1 ?der1_1 ?der2 ?der2_1" +
                    "{ " +
                    "   <" + iri + "> dbetym:etymologicallyRelatedTo{0,5} ?ancestor1 . " +
		    "   OPTIONAL { <" + iri + "> dbetym:etymologicallyDerivesFrom ?der . } " +
                    "   OPTIONAL { ?der1 dbetym:etymologicallyDerivesFrom/(dbetym:etymologicallyRelatedTo*) ?ancestor1 .  " +
                    "              ?der1 dbetym:etymologicallyDerivesFrom ?der1_1 . }" +
                    "   OPTIONAL { " +
                    "       ?eq dbetym:etymologicallyEquivalentTo ?ancestor1 . " +
                    "       ?eq dbetym:etymologicallyRelatedTo* ?ancestor2 . " +
                    "       OPTIONAL { ?der2 dbetym:etymologicallyDerivesFrom/(dbetym:etymologicallyRelatedTo*) ?ancestor2 . " +
		    "                  ?der2  dbetym:etymologicallyDerivesFrom ?der2_1 . }" +
                    "   } " +     //filter not exists { <" + iri + "> (dbetym:etymologicallyRelatedTo*)/dbetym:etymologicallyDerivesFrom ?ancestor1 . }
                    "} ";
            } else if (queryDepth === 2) {
                query +=
                    "SELECT DISTINCT ?ancestor1 ?der ?der1 ?der1_1" +
                    "{ " +
                    "   <" + iri + "> dbetym:etymologicallyRelatedTo{0,5} ?ancestor1 . " +
		    "   OPTIONAL { <" + iri + "> dbetym:etymologicallyDerivesFrom ?der . } " +
                    "   OPTIONAL { ?der1 dbetym:etymologicallyDerivesFrom/(dbetym:etymologicallyRelatedTo*) ?ancestor1 . " +
                    "              ?der1 dbetym:etymologicallyDerivesFrom ?der1_1 . } " +
                    "} ";
            }
	    console.log(query);
            return query;
        };

        var descendantQuery = function(iri) {
            var query =
                "SELECT DISTINCT ?descendant1 " + // ?descendant2",
                "{ " +
                "   ?descendant1 dbetym:etymologicallyRelatedTo{0,2} <" + iri + "> . " +
                //   "   OPTIONAL {?eq dbetym:etymologicallyEquivalentTo ?descendant1 . " +
                //  "   ?descendant2 dbetym:etymologicallyRelatedTo* ?eq .} " +
                "} ";
            return query;
        };

        var propertyQuery = function(iri) {
            var query =
                "SELECT DISTINCT ?s ?rel ?eq ?der ?sLabel ?relLabel ?eqLabel ?derLabel " +
                "{           " +
                "   VALUES ?rel " +
                "   {           " +
                "       <" + iri + "> " +
                "   } " +
                "   ?rel rdfs:label ?relTmp" +
                "   BIND (STR(?relTmp) AS ?relLabel) " +
                "   ?s dbetym:etymologicallyRelatedTo ?rel . " +
		"   ?s rdfs:label ?sTmp " +
                "   BIND (STR(?sTmp) AS ?sLabel) " +
                "   OPTIONAL { " +
                "       ?eq dbetym:etymologicallyEquivalentTo{0,6} ?rel . " +
		"       ?eq rdfs:label ?eqTmp " +
                "       BIND (STR(?eqTmp) AS ?eqLabel) " +
                "   } " +
                "   OPTIONAL { " +
                "       ?s dbetym:etymologicallyDerivesFrom ?der . " +
		"       ?der rdfs:label ?derTmp " +
                "       BIND (STR(?derTmp) AS ?derLabel) " +
                "   } " +
                //  "   FILTER NOT EXISTS { ?rel dbetym:etymologicallyDerivesFrom ?der2 . } "+
                "}";
            return query;
        };

        var unionQuery = function(iriArray, queryFunction) {
            var query =
                "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org/dbnaryetymology#> " +
                "SELECT * WHERE {{ " +
                iriArray.map(function(iri) { return queryFunction(iri); }).join("} UNION {") +
                "}}";
            return query;
        };

        this.getXMLHttpRequest = getXMLHttpRequest;
        this.disambiguationQuery = disambiguationQuery;
        this.lemmaQuery = lemmaQuery;
        this.ancestorQuery = ancestorQuery;
        this.descendantQuery = descendantQuery;
        this.propertyQuery = propertyQuery;
        this.unionQuery = unionQuery;
	    this.query = query;
	    this.intertwinedQuery = intertwinedQuery;
	    this.detailedAncestorQuery = detailedAncestorQuery;
        etyBase[moduleName] = this;
	};

    return module;
})(DB || {});
