/*globals
    Rx, XMLHttpRequest, console, d3, URLSearchParams, FormData, Blob
*/
var DB = (function(module) {

    module.bindModule = function(base, moduleName) {
        var etyBase = base;

        /**
         * Gets an XMLHttpRequest using RxJs
         *
         * @param {url} 
         * @return {Observable} 
         */
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

        /**
         * Posts an XMLHttpRequest using RxJs
         *
         * @param {string} 
         * @return {Observable} 
         */
        var postXMLHttpRequest = function(content) {        
            return Rx.Observable.create(observer => {
                const req = new XMLHttpRequest();
                const params = new URLSearchParams();
                params.set("format", "application/sparql-results+json");
                var formData = new FormData();
                var blob = new Blob([content], { type: "text/xml" });
                formData.append("query", blob);
                req.open('POST', etyBase.config.urls.ENDPOINT + "?" + params);
                req.onload = function(oEvent) {
                    if (req.status === 200) {
                        observer.next(req.responseText);
                        observer.complete();
                    } else {
                        observer.error(new Error('An error occured'));
                    }
                };
        
                req.send(formData);
            });
        };

        /**
         * Posts an array of XMLHttpRequest to the server using RxJs
         * each requesting ancestors of an iri
         *
         * @param {array} of iri-s
         * @return {Observable}
         */   
        var postMoreAncestorsQuery = function(iris) {
            return Rx.Observable.zip 
                .apply(this, iris
                    .map((iri) => {
                        var content = iterativeAncestorQuery(0, iri);
                        return postXMLHttpRequest(content);
                    }));
        };
	
        /**
         * Prints the disambiguation query into a string.
         * The generated response will consists of a table with three headers:
         * "iri": the iri of a resources with rdfs label the input string (e.g. http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_link)
         * "et": a list of iris of resources that are described by the resource in "iri" (e.g. http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_1_link,http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_2_link,http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_3_link)
         * "lemma": a string containing the rdfs label of the resource "iri"
         *
         * @param {string} a word e.g. "door"
         * @return {string} a query string
         */
        var disambiguationQuery = function(lemma) {
            return "SELECT DISTINCT ?iri (group_concat(distinct ?ee ; separator=\",\") as ?et) ?lemma " +
                "WHERE { " +
                "    ?iri rdfs:label ?lemma . " +
                "    ?lemma bif:contains \"\'" + lemma + "\'\" . " +
                // exclude entries that contain the searched word but include other words
                // (e.g.: search="door" label="doorbell", exclude "doorbell")
                "    FILTER REGEX(?lemma, \"^" + lemma + "$\", 'i') . " +
                "    ?iri rdf:type dbetym:EtymologyEntry . " +
                "    OPTIONAL { " +
                "        ?iri dbnary:describes ?ee . " +
                "        ?ee rdf:type dbetym:EtymologyEntry . " +
                "    } " +
                "} ";
        };
	
        /**
         * Prints the query to get links, pos and gloss of an entry.
         *
         * The generated response will consists of a table with three headers:
         * "iri"
         * "ee"
         * "pos": a string containing the rdfs label of the resource "iri"
         * "gloss": a string containing glossesseparated by ";;;;""
         * "links": a string containing links separated by ","
         *
         * @param {string} iri
         * @return {string} a query string
         */           
        var glossQuery = function(iri) {
            var query =
                "SELECT DISTINCT ?iri ?ee ?pos (group_concat(distinct ?def ; separator=\";;;;\") as ?gloss) (group_concat(distinct ?also ; separator=\",\") as ?links) " +
                "WHERE { " +
                "    VALUES ?iri " +
                "    {           " +
                "       <" + iri + "> " +
                "    } " +
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

        /**
         * Prints the query to get ancestors 
         *
         * @param {integer}
         * @param {string} iri
         * @return {string} a query string
         */
        var iterativeAncestorQuery = function(iteration, iri) {
            if (iteration === etyBase.config.depthAncestors) return [];

            var query, resource, tmp = iterativeAncestorQuery(iteration + 1).join("");
            if (iteration === 0) {
                query = "SELECT DISTINCT * ";
                resource = "<" + iri + ">";    
            } else {
                query = "OPTIONAL ";
                resource = "?ancestor" + iteration;
            }
            //it was [true, false]
            return [false].map(function(describes) {
                var _query = query + "{";
                var _resource = resource; 
                if (describes) {
                    _query += _resource + " dbnary:describes ?var" + iteration + " . ";
                    _resource = "?var" + iteration; 
                }
                _query += _resource + " dbetym:etymologicallyRelatedTo ?ancestor" + (iteration + 1) + " . " +
                    " BIND(EXISTS {" + _resource + " dbetym:etymologicallyDerivesFrom ?ancestor" + (iteration + 1) + " } AS ?der" + (iteration + 1) + ") ";
                //" BIND(EXISTS {" + _resource + " dbetym:etymologicallyEquivalentTo ?ancestor" + (iteration + 1) + " } AS ?eq" + (iteration + 1) + ") "; 
                _query += tmp + "}";
                return _query;
            });
        };
	
        /**
         * Prints the query to get descendants 
         *
         * @param {string} iri
         * @return {string} a query string
         */
        var descendantQuery = function(iri) {
            return "SELECT DISTINCT ?descendant1 ?label1 ?ee ?labele " +
                "{ " +
                "   ?descendant1 dbetym:etymologicallyRelatedTo* <" + iri + "> . " +
                "   ?descendant1 rdfs:label ?label1 . " +
                "   OPTIONAL { " +
                "       ?ee rdf:type dbetym:EtymologyEntry . " +
                "       ?descendant1 dbnary:describes ?ee . " +
                "       ?ee rdfs:label ?labele . " +
                "   } " +
                "}";
        };

        /**
         * Prints the query to get properties about nodes 
         *
         * @param {string} iri
         * @return {string} a query string
         */
        var propertyQuery = function(iri) {
            return "SELECT DISTINCT ?s ?rel ?eq ?sLabel ?relLabel ?eqLabel" +
                "{           " +
                "   VALUES ?rel " +
                "   {           " +
                "       <" + iri + "> " +
                "   } " +
                "   OPTIONAL { " +
                "       ?s dbetym:etymologicallyRelatedTo ?rel . " +
                "       ?s rdfs:label ?sLabel ." +
                "       OPTIONAL { " +
                "           ?eq dbetym:etymologicallyEquivalentTo{0,6} ?rel . " +
                "           ?eq rdfs:label ?eqLabel . " +
                "       } " +
                "    } " +
                "    ?rel rdfs:label ?relLabel . " +
                "} LIMIT 500";
        };

        /**
         * Prints the union of an array of queries 
         * one for each of the elements in input array iris 
         *
         * @param {array} of strings iris
         * @return {function} a function that takes as argument a string iri
         */
        var unionQuery = function(iris, queryFunction) {
            return "SELECT * WHERE {{ " +
                iris.map(function(iri) { return queryFunction(iri); })
                    .join("} UNION {") +
                "}}";
        };

        this.getXMLHttpRequest = getXMLHttpRequest;
        this.postXMLHttpRequest = postXMLHttpRequest;
        this.postMoreAncestorsQuery = postMoreAncestorsQuery;

        this.disambiguationQuery = disambiguationQuery;
        this.glossQuery = glossQuery;
        this.descendantQuery = descendantQuery;
        this.propertyQuery = propertyQuery;
        this.unionQuery = unionQuery;
	    this.iterativeAncestorQuery = iterativeAncestorQuery;
      
        etyBase[moduleName] = this;
    };

    return module;
})(DB || {});
