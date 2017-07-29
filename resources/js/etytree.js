/*globals
    jQuery, d3, debug, console, window, document, searchSparql, drawDisambiguation, ENDPOINT, getXMLHttpRequest
*/
jQuery('document').ready(function($) {
    var div = d3.select("body").append("div")
        .attr("data-role", "popup")
        .attr("data-dismissible", "true")
        .attr("id", "myPopup")
        .attr("class", "ui-content")
        .style("position", "absolute")
        .style("background", "lightBlue")
        .style("text-align", "left")
        .style("padding", "2px")
        .style("font", "12px sans-serif")
        .style("border", "0px")
        .style("border-radius", "8px")
        .attr("width", 0)
        .attr("height", 0);
    d3.select("#tree-container")
        .append("p")
        .attr("id", "message")
        .attr("align", "center");
    $('#tags').on("keypress click", function(e) {
        if (e.which === 13 || e.type === 'click') {
            var word = $(this).val(); //.replace("/", "!slash!");

            if (word) {
                if (debug) console.log("searching word in database");
                var width = window.innerWidth,
                    height = $(document).height() - $('#header').height();
                var url = ENDPOINT + "?query=" + encodeURIComponent(searchSparql(word));
                if (debug) {
                    console.log(url);
                }

                const source = getXMLHttpRequest(url);
                source.subscribe(response => drawDisambiguation(response, width, height),
                    error => console.error(error),
                    () => console.log('done disambiguation'));
            }
        }
    });
});
