$('document').ready(function(){
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
    $('#tags').on("keypress click", function(e){
	if (e.which == 13 || e.type === 'click') {
            var word = $(this).val();//.replace("/", "!slash!");
	    
            if (word){
		if (debug) console.log("searching word in database");
		search(word);
	    }
	};
    });
});

