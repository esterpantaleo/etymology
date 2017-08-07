/*globals
    jQuery, d3, debug, console, window, document, searchSparql, displayDisambiguation, ENDPOINT, getXMLHttpRequest
*/
jQuery('document').ready(function($) {
    displayMainMessage("welcome");
    d3.select("helpPopup").html(helpMessage["welcome"]);

    $('#tags').on("keypress click", function(e) {
        if (e.which === 13 || e.type === 'click') {
            var word = $(this).val(); //.replace("/", "!slash!");

            if (word) {
                var url = ENDPOINT + "?query=" + encodeURIComponent(searchSparql(word));
                if (debug) {
		    console.log("searching word in database");
                    console.log(url);
                }

                const source = getXMLHttpRequest(url);
                source.subscribe(response => displayDisambiguation(response),
                    error => console.error(error),
		    function() { 
		        if (debug) {
			    console.log('done disambiguation')
			}
		    });
            }
        }
    });
});
