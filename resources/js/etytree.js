/*globals
    jQuery, d3, debug, console, window, document, SPARQL, drawDisambiguation
*/
jQuery('document').ready(function($) {
    d3.select("#helpPopup").html(HELP.intro);
    
    var div = d3.select("body").append("div")
	.attr("data-role", "popup")
	.attr("data-dismissible", "true")
	.attr("id", "tooltipPopup")
	.style("display", "none")
	.attr("class", "ui-content tooltipDiv");
    
    $('#tags').on("keypress click", function(e) {
        if (e.which === 13 || e.type === 'click') {
            var lemma = $(this).val(); //.replace("/", "!slash!");

            if (lemma) {
                if (debug) {
		    console.log("searching lemma in database");
		}
                var width = window.innerWidth,
                    height = $(document).height() - $('#header').height();
                var url = SPARQL.ENDPOINT + "?query=" + encodeURIComponent(SPARQL.disambiguationQuery(lemma));
                if (debug) {
                    console.log(url);
                }

                const source = SPARQL.getXMLHttpRequest(url);
                source.subscribe(
				 function(response) {
				     if (response !== undefined && response !== null) { 
					 var g = buildDisambiguationDAGRE(response);
					 if (null !== g) { 
					     var inner = renderGraph(g, width, height);
					     appendLanguageTagTextAndTooltip(inner, g);
					     appendDefinitionTooltipOrDrawDAGRE(inner, g, width, height);
					     
					     d3.selectAll(".edgePath").remove();
					 }
				     }
				 },
				 function(error){ 
				     console.error(error);
				     d3.select("#message").html(MESSAGE.notAvailable);
				 },
				 () => console.log('done disambiguation'));
            }
        }
    });
});
