/*globals
    jQuery, d3, debug, console, window, document, SPARQL, drawDisambiguation
*/
jQuery('document').ready(function($) {
    d3.select("#helpPopup").html(HELP.intro);
  
    var div = d3.select("body").append("div")
	.attr("data-role", "popup")
	.attr("data-dismissible", "true")
	.attr("id", "tooltipPopup")
	.attr("class", "ui-content")
	.style("position", "absolute")
	.style("background", "lightBlue")
	.style("text-align", "left")
	.style("padding", "2px")
	.style("font", "12px sans-serif")
	.style("border", "0px")
	.style("border-radius", "8px")
	.style("width", "auto")
	.style("height","auto");

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
                source.subscribe(response => drawDisambiguation(response, width, height),
                    function(error){ 
			console.error(error);
			d3.select("#message").html(MESSAGE.notAvailable);
		    },
                    () => console.log('done disambiguation'));
            }
        }
    });
});
