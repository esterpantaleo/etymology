/*globals
    Rx, XMLHttpRequest, console, d3, URLSearchParams, FormData, Blob
*/
var DB = (function(module) {

    module.bindModule = function(base, moduleName) {
        var etyBase = base;

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

        /* function to slice up a big sparql query (that cannot be processed by virtuoso) */
        /* into a bunch of smaller queries in chunks of "chunk" */
        var slicedQuery = function(myArray, queryFunction, chunk) {
            var i, j, tmpArray, url, sources = [];
            for (i = 0, j = myArray.length; i < j; i += chunk) {
                tmpArray = myArray.slice(i, i + chunk);

                url = etyBase.config.urls.ENDPOINT + "?query=" + encodeURIComponent(etyBase.DB.unionQuery(tmpArray, queryFunction));
                if (etyBase.config.debug) {
                    console.log(url);
                }
                sources.push(etyBase.DB.getXMLHttpRequest(url));
            }
            const queryObservable = Rx.Observable.zip.apply(this, sources)
                .catch((err) => {
                    d3.select("#message").html(etyBase.LOAD.MESSAGE.serverError);

                    /* Return an empty Observable which gets collapsed in the output */
                    return Rx.Observable.empty();
                });
            return queryObservable;
        };

        //this function takes as input a string 
        //and outputs a query to the etytree SPARQL endpoint;
        //the query returns a table with 3 headers
        //"iri": the iri of a resources with rdfs label the input string (e.g. http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_link)
        //"et": a list of iris of resources that are described by the resource in "iri" (e.g. http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_1_link,http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_2_link,http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_3_link)
        //"lemma": a string containing the rdfs label of the resource "iri"
        var disambiguationQuery = function(lemma) {
            var encodedLemma = lemma
                .replace(/'/g, "\\\\'")
                .replace("·", "%C2%B7")
                .replace("*", "_")
                .replace("'", "__"); //parse reconstructed words 
            //.replace("/", "!slash!");  
            var query =
                "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org/dbnaryetymology#> " +
                "PREFIX dbnary: <http://kaiko.getalp.org/dbnary#> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "SELECT DISTINCT ?iri (group_concat(distinct ?ee ; separator=\",\") as ?et) ?lemma " +
                "WHERE { " +
                "    ?iri rdfs:label ?label . " +
                "    ?label bif:contains \"\'" + encodedLemma + "\'\" . " +
                // exclude entries that contain the searched word but include other words
                // (e.g.: search="door" label="doorbell", exclude "doorbell")
                "    FILTER REGEX(?label, \"^" + encodedLemma + "$\", 'i') . " +
                "    ?iri rdf:type dbetym:EtymologyEntry . " +
                "    OPTIONAL { " +
                "        ?iri dbnary:describes ?ee . " +
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

        //(related|equivalent){0,5}
        //DEFINE QUERIES TO PLOT GRAPH
        var ancestorSubquery = function(iteration, describes, resource) {
            var query = "";
            if (undefined === resource) {
                resource = "?ancestor" + iteration;
            }
            if (describes) {
                query += resource + " dbnary:describes ?var" + iteration + " . ";
                resource = "?var" + iteration;
            }
            query += resource + " dbetym:etymologicallyRelatedTo ?ancestor" + (iteration + 1) + " . " +
                " BIND(EXISTS {" + resource + " dbetym:etymologicallyDerivesFrom ?ancestor" + (iteration + 1) + " } AS ?der" + (iteration + 1) + ") " +
                " BIND(EXISTS {" + resource + " dbetym:etymologicallyEquivalentTo ?ancestor" + (iteration + 1) + " } AS ?eq" + (iteration + 1) + ") ";

            return query;
        };

        var iterativeQuery = function(i, depth) {
            var query = "";
            if (i < depth) {
                var tmp = iterativeQuery(i + 1, depth);
                query += "OPTIONAL {" + ancestorSubquery(i, true) + tmp + "} ";
                query += "OPTIONAL {" + ancestorSubquery(i, false) + tmp + "} ";
            }
            return query;
        };

        var ancestorQuery = function(iri, depth) {
            var sources = [];
            var queryPart1 =
                "PREFIX dbnary: <http://kaiko.getalp.org/dbnary#> " +
                "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org/dbnaryetymology#> " +
                "SELECT DISTINCT * {";
            var queryPart3 = iterativeQuery(1, depth) + "}";
            var query = queryPart1 + ancestorSubquery(0, true, "<" + iri + ">") + queryPart3;
            sources.push(etyBase.DB.postXMLHttpRequest(query));
            query = queryPart1 + ancestorSubquery(0, false, "<" + iri + ">") + queryPart3;
            sources.push(etyBase.DB.postXMLHttpRequest(query));

            const queryObservable = Rx.Observable.zip.apply(this, sources)
                .catch((err) => {
                    d3.select("#message").html(etyBase.LOAD.MESSAGE.serverError);

                    /* Return an empty Observable which gets collapsed in the output */
                    return Rx.Observable.empty();
                });
            return queryObservable;
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
                "SELECT DISTINCT ?s ?rel ?eq ?sLabel ?relLabel ?eqLabel" +
                "{           " +
                "   VALUES ?rel " +
                "   {           " +
                "       <" + iri + "> " +
                "   } " +
                "   OPTIONAL { " +
                "       ?s dbetym:etymologicallyRelatedTo ?rel . " +
                "       OPTIONAL { " +
                "           ?m dbnary:describes ?s . " +
                "           ?m rdfs:label ?sTmp . " +
                "           BIND (STR(?sTmp) AS ?sLabel) " +
                "       } " +
                "       OPTIONAL { " +
                "           ?s rdfs:label ?sTmp " +
                "           BIND (STR(?sTmp) AS ?sLabel) " +
                "       } " +
                "       OPTIONAL { " +
                "           ?eq dbetym:etymologicallyEquivalentTo{0,6} ?rel . " +
                "           ?eq rdfs:label ?eqTmp " +
                "           BIND (STR(?eqTmp) AS ?eqLabel) " +
                "       } " +
                "    } " +
                "    OPTIONAL { " +
                "        ?rel rdfs:label ?relTmp" +
                "        BIND (STR(?relTmp) AS ?relLabel) " +
                "    } " +
                //  "   FILTER NOT EXISTS { ?rel dbetym:etymologicallyDerivesFrom ?der2 . } "+
                "} LIMIT 200";
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
        this.postXMLHttpRequest = postXMLHttpRequest;
        this.slicedQuery = slicedQuery;
        this.disambiguationQuery = disambiguationQuery;
        this.lemmaQuery = lemmaQuery;
        this.ancestorQuery = ancestorQuery;
        this.descendantQuery = descendantQuery;
        this.propertyQuery = propertyQuery;
        this.unionQuery = unionQuery;
        etyBase[moduleName] = this;
    };

    return module;
})(DB || {});