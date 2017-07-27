function searchSparql(word) {
    var encodedWord = word.replace(/'/g, "\\\\'").replace("·", "%C2%B7");
    var query = [
        "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org/dbnaryetymology#>",
        "PREFIX dbnary: <http://kaiko.getalp.org/dbnary#>",
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
        "SELECT DISTINCT ?iri (group_concat(distinct ?ee ; separator=\",\") as ?et) ",
        "WHERE {",
        "    ?iri rdfs:label ?label . ?label bif:contains \"\'" + encodedWord + "\'\" .",
        //exclude entries that contain the searched word but include other words
        //(e.g.: search="door" label="doorbell", exclude "doorbell")
        "    FILTER REGEX(?label, \"^" + encodedWord + "$\", 'i') .",
        "    ?iri rdf:type dbetym:EtymologyEntry .",
        "    OPTIONAL {",
        "        ?iri dbnary:describes  ?ee .",
        "        ?ee rdf:type dbetym:EtymologyEntry .",
        "    }",
        "}"
    ];
    return query.join(" ");
}

//DEFINE QUERY TO GET LINKS, POS AND GLOSS           
function sparql(iri) {
    var query = [
        "PREFIX dbnary: <http://kaiko.getalp.org/dbnary#>",
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
        "PREFIX lexinfo: <http://www.lexinfo.net/ontology/2.0/lexinfo#>",
        "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>",
        "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>",
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
        "SELECT DISTINCT ?ee ?pos (group_concat(distinct ?def ; separator=\";;;;\") as ?gloss) (group_concat(distinct ?also ; separator=\",\") as ?links)",
        "WHERE {",
        "    <" + iri.replace(/__ee_[0-9]+_/g, "__ee_") + "> rdfs:seeAlso ?also .",
        "    OPTIONAL {",
        "        <" + iri + "> dbnary:describes ?ee .",
        "        OPTIONAL {",
        "            ?ee rdf:type ontolex:LexicalEntry .",
        "            ?ee dbnary:partOfSpeech ?pos .",
        "        }",
        "        OPTIONAL {",
        "            ?ee dbnary:describes ?nee .",
        "            ?nee rdf:type ontolex:LexicalEntry .",
        "            ?nee dbnary:partOfSpeech ?pos .",
        "        }",
        "        OPTIONAL {",
        "            ?ee dbnary:describes ?cee .",
        "            ?cee dbnary:describes ?nee .",
        "            ?nee rdf:type ontolex:LexicalEntry .",
        "            ?nee dbnary:partOfSpeech ?pos .",
        "        }",
        "        OPTIONAL {",
        "            ?ee ontolex:sense ?sense .",
        "            ?sense skos:definition ?val .",
        "            ?val rdf:value ?def .",
        "        }",
        "        OPTIONAL {",
        "            ?ee dbnary:describes ?nee .",
        "            ?nee rdf:type ontolex:LexicalEntry .",
        "            ?nee ontolex:sense ?sense .",
        "            ?sense skos:definition ?val .",
        "            ?val rdf:value ?def .",
        "        }",
        "        OPTIONAL {",
        "            ?ee dbnary:describes ?cee .",
        "            ?cee dbnary:describes ?nee .",
        "            ?nee rdf:type ontolex:LexicalEntry .",
        "            ?nee skos:sense ?sense .",
        "            ?sense skos:definition ?val .",
        "            ?val rdf:value ?def .",
        "        }",
        "    }",
        "}"
    ];
    return query.join(" ");
}

//DEFINE QUERIES TO PLOT GRAPH          
function ancestorSparql(id, parameter){
    if (parameter === 1){
	var query = [
            "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org/dbnaryetymology#> ",
            "SELECT DISTINCT ?ancestor1 ?ancestor2",
            "{ ",
            "   <" + id + "> dbetym:etymologicallyRelatedTo{0,5} ?ancestor1 .",
            "   OPTIONAL {?eq dbetym:etymologicallyEquivalentTo ?ancestor1 .",
            "   ?eq dbetym:etymologicallyRelatedTo* ?ancestor2 .}",
            "} "
	];
	return query.join(" ");
    } else if (parameter === 2){
	var query = [
            "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org/dbnaryetymology#> ",
            "SELECT DISTINCT ?ancestor1",
            "{ ",
            "   <" + id + "> dbetym:etymologicallyRelatedTo{0,5} ?ancestor1 .",
            "} "
	];
	return query.join(" ");
    }
}

function descendantSparql(id) {
    var query = [
        "SELECT DISTINCT ?descendant1", // ?descendant2",
        "{ ",
        "   ?descendant1 dbetym:etymologicallyRelatedTo{0,1} <" + id + "> .",
        //   "   OPTIONAL {?eq dbetym:etymologicallyEquivalentTo ?descendant1 .",
        //  "   ?descendant2 dbetym:etymologicallyRelatedTo* ?eq .}",
        "} "
    ];
    return query.join(" ");
}

function dataSparql(id) {
    var query = [
        "SELECT DISTINCT ?s ?rel ?eq ?der ",
        "{           ",
        "   VALUES ?rel",
        "   {           ",
        "       <" + id + ">",
        "   }",
        "   ?s dbetym:etymologicallyRelatedTo ?rel .",
        "   OPTIONAL { ?rel dbetym:etymologicallyEquivalentTo{0,6} ?eq . }",
        "   OPTIONAL { ?s dbetym:etymologicallyDerivesFrom ?der . }",
        //  "   FILTER NOT EXISTS { ?rel dbetym:etymologicallyDerivesFrom ?der2 . }",
        "}"
    ];
    return query.join(" ");
}

function unionSparql(id_arr, sparql) {
    var query = [
        "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org/dbnaryetymology#>",
        "SELECT * WHERE {{"
    ];
    for (var i in id_arr) {
        query.push(sparql(id_arr[i]));
        if (i < id_arr.length - 1) {
            query.push("} UNION {");
        }
    }
    query.push("}}");
    return query.join(" ");
}
