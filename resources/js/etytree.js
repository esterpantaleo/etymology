/*globals
    jQuery, d3, LOAD, console, window, document, SPARQL, drawDisambiguation, buildDisambiguationDAGRE, renderGraph, drawDAGRE, appendDefinitionTooltipOrDrawDAGRE, appendLanguageTagTextAndTooltip
*/
jQuery('document').ready(function($) {
    d3.select("#helpPopup").html(LOAD.HELP.intro);

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

    $('#tooltipPopup').click(function(event) {
        event.stopPropagation();
    });

    $('#tags').on("keypress click", function(e) {
        if (e.which === 13 || e.type === 'click') {
            var lemma = $(this).val(); //.replace("/", "!slash!");

            if (lemma) {
                if (LOAD.settings.debug) {
                    console.log("searching lemma in database");
                }
                var width = window.innerWidth,
                    height = $(document).height() - $('#header').height();
                var url = SPARQL.ENDPOINT + "?query=" + encodeURIComponent(SPARQL.disambiguationQuery(lemma));
                if (LOAD.settings.debug) {
                    console.log(url);
                }

                const source = SPARQL.getXMLHttpRequest(url);
                source.subscribe(
				            function(response) {
				                if (response !== undefined && response !== null) { 
					                  d3.select("#tree-overlay").remove();
					                  d3.select("#tooltipPopup").style("display", "none");
					 
					                  var g = buildDisambiguationDAGRE(response);
					                  if (null === g) { 
					                      d3.select("#message").style("display", "inline").html(MESSAGE.notAvailable);
					                  } else {
					                      if (Object.keys(g.nodess).length > 1) {
						                        d3.select("#helpPopup").html(LOAD.HELP.disambiguation);  
						                        d3.select("#message").style("display", "inline").html("There are multiple words in the database. <br>Which word are you interested in?");
						                        var inner = renderGraph(g, width, height);
						                        appendLanguageTagTextAndTooltip(inner, g);
						                        appendDefinitionTooltipOrDrawDAGRE(inner, g, width, height);
						 
						                        d3.selectAll(".edgePath").remove();
					                      } else {    
						                        var iri = Object.keys(g.nodess)[0];
						                        drawDAGRE(iri, 2, width, height);   
					                      }
					                  }
				                }
				            },
				            function(error){ 
				                console.error(error);
				                d3.select("#message").style("display", "inline").html(MESSAGE.notAvailable);
				            },
				            () => console.log('done disambiguation'));
            }
        }
    });
});
