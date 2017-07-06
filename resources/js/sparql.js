function searchSparql(word){
    var encodedWord = word; 
    var query = [ 
	"PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org//dbnaryetymology#>",
	"PREFIX dbnary: <http://etytree-virtuoso.wmflabs.org/dbnary#>",
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>", 
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>", 
	"SELECT DISTINCT ?iri (group_concat(distinct ?ee ; separator=\",\") as ?et) ",
	"WHERE {",  
	"    ?iri rdfs:label ?label . ?label bif:contains \"\'" + encodedWord + "\'\" .",
	//exclude entries that contain the searched word but include other words
	//(e.g.: search="door" label="doorbell", exclude "doorbell")
	"    FILTER REGEX(?label, \"^" + encodedWord + "$\", 'i') .",  
	"    ?iri rdf:type <http://etytree-virtuoso.wmflabs.org//dbnaryetymology#EtymologyEntry> .",
	"    OPTIONAL {",
	"        ?iri <http://etytree-virtuoso.wmflabs.org/dbnary#refersTo>  ?ee .",
	"        ?ee rdf:type <http://etytree-virtuoso.wmflabs.org//dbnaryetymology#EtymologyEntry> .",
	"    }",   
	"}" 
    ];
    return query.join(" "); 
}

//DEFINE QUERY TO GET LINKS, POS AND GLOSS                             
function sparql(iri, printLinks){
    var select = "", option = "";
    if (printLinks){
        select = "(group_concat(distinct ?also ; separator=\",\") as ?links)";
        option = "OPTIONAL {<" + iri.replace(/__ee_[0-9]+_/g,"__ee_") + "> rdfs:seeAlso ?also .}";
    }
    var query = [
        "PREFIX dbnary: <http://etytree-virtuoso.wmflabs.org/dbnary#>",
        "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org//dbnaryetymology#>",
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
        "PREFIX lemon: <http://lemon-model.net/lemon#>",
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
        "SELECT DISTINCT ?ee ?pos (group_concat(distinct ?def ; separator=\";;;;\") as ?gloss)" + select,
        "WHERE {",
        "    <" + iri + "> dbnary:refersTo ?ee ." + option,
        "    OPTIONAL {",
        "        ?ee rdf:type lemon:LexicalEntry .",
        "        ?ee dbnary:partOfSpeech ?pos .",
        "    }",
        "    OPTIONAL {",
        "        ?ee dbnary:refersTo ?nee .",
        "        ?nee rdf:type lemon:LexicalEntry .",
        "        ?nee dbnary:partOfSpeech ?pos .",
        "    }",
        "    OPTIONAL {",
        "        ?ee dbnary:refersTo ?cee .",
        "        ?cee dbnary:refersTo ?nee .",
        "        ?nee rdf:type lemon:LexicalEntry .",
        "        ?nee dbnary:partOfSpeech ?pos .",
        "    }",
        "    OPTIONAL {",
        "        ?ee lemon:sense ?sense .",
        "        ?sense lemon:definition ?val .",
        "        ?val lemon:value ?def .",
        "    }",
        "    OPTIONAL {",
        "        ?ee dbnary:refersTo ?nee .",
        "        ?nee rdf:type lemon:LexicalEntry .",
        "        ?nee lemon:sense ?sense .",
        "        ?sense lemon:definition ?val .",
        "        ?val lemon:value ?def .",
        "    }",
        "    OPTIONAL {",
        "        ?ee dbnary:refersTo ?cee .",
        "        ?cee dbnary:refersTo ?nee .",
        "        ?nee rdf:type lemon:LexicalEntry .",
        "        ?nee lemon:sense ?sense .",
        "        ?sense lemon:definition ?val .",
        "        ?val lemon:value ?def .",
        "    }",
        "}"
    ];
    return query.join(" ");
}

//DEFINE QUERY TO PLOT GRAPH          
function ancestorsSparql(id){
    var query = [
        "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org//dbnaryetymology#> ",
        "SELECT DISTINCT ?ancestor1",// ?ancestor2",
        "{ ",
        "   <" + id + "> dbetym:etymologicallyRelatedTo* ?ancestor1 .",
     //   "   OPTIONAL {?eq dbetym:etymologicallyEquivalentTo ?ancestor1 .",
       // "   ?eq dbetym:etymologicallyRelatedTo* ?ancestor2 .}",
        "}"
    ];
    return query.join(" ");
}

function descendantSparql(id){
    var query = [
        "SELECT DISTINCT ?descendant1",// ?descendant2",
        "{ ",
        "   ?descendant1 dbetym:etymologicallyRelatedTo* <" + id + "> .",
     //   "   OPTIONAL {?eq dbetym:etymologicallyEquivalentTo ?descendant1 .",
    //     "   ?descendant2 dbetym:etymologicallyRelatedTo* ?eq .}",
        "} LIMIT 100 "
    ];
    return query.join(" ");
}

function dataSparql(id){
    var query = [
        "SELECT DISTINCT ?s ?rel ?eq",// ?der ",
        "{           ",
        "   VALUES ?rel",
        "   {           ",
        "       <" + id + ">",
        "   }",
        "   ?s dbetym:etymologicallyRelatedTo ?rel .",
	"   OPTIONAL { ?rel dbetym:etymologicallyEquivalentTo ?eq . }",
      //  "   FILTER NOT EXISTS { ?s dbetym:etymologicallyDerivesFrom ?der . }",
//	"   FILTER NOT EXISTS { ?rel dbetym:etymologicallyDerivesFrom ?der2 . }",
        "}"
    ];
    return query.join(" ");
}

function unionSparql(id_arr, sparql){
    var query = [
        "PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org//dbnaryetymology#>",
        "SELECT * WHERE {{"
    ];
    for (var i in id_arr) {
        query.push(sparql(id_arr[i]));
        if (i < id_arr.length - 1){
           query.push("} UNION {");
        }
    }
    query.push("}}");
    return query.join(" ");
}
