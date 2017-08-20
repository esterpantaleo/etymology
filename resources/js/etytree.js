/*globals
    jQuery, $, d3, LOAD, console, window, document, DB, GRAPH
*/
var EtyTree = {
    create: function() {
        var etytree = Object.create(this);
        etytree.DB = DB;
        etytree.GRAPH = GRAPH;
        etytree.LOAD = LOAD;
        return etytree;
    },
    init: function() {
        var that = this;
        d3.select("#helpPopup").html(that.LOAD.HELP.intro);

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
            var tag = this;
            if (e.which === 13 || e.type === 'click') {
                var lemma = $(tag).val(); //.replace("/", "!slash!");

                if (lemma) {
                    if (that.LOAD.settings.debug) {
                        console.log("searching lemma in database");
                    }
                    var width = window.innerWidth,
                        height = $(document).height() - $('#header').height();
                    var url = that.DB.ENDPOINT + "?query=" + encodeURIComponent(that.DB.disambiguationQuery(lemma));
                    if (that.LOAD.settings.debug) {
                        console.log(url);
                    }

                    const source = that.DB.getXMLHttpRequest(url);
                    source.subscribe(
                        function(response) {
                            if (response !== undefined && response !== null) {
                                d3.select("#tree-overlay").remove();
                                d3.select("#tooltipPopup").style("display", "none");

                                var g = that.GRAPH.buildDisambiguationDAGRE(response);
                                if (null === g) {
                                    d3.select("#message").style("display", "inline").html(that.LOAD.MESSAGE.notAvailable);
                                } else {
                                    if (Object.keys(g.nodess).length > 1) {
                                        d3.select("#helpPopup").html(that.LOAD.HELP.disambiguation);
                                        d3.select("#message").style("display", "inline").html("There are multiple words in the database. <br>Which word are you interested in?");
                                        var inner = that.GRAPH.renderGraph(g, width, height);
                                        that.GRAPH.appendLanguageTagTextAndTooltip(inner, g);
                                        that.GRAPH.appendDefinitionTooltipOrDrawDAGRE(inner, g, width, height);

                                        d3.selectAll(".edgePath").remove();
                                    } else {
                                        var iri = Object.keys(g.nodess)[0];
                                        that.GRAPH.drawDAGRE(iri, 2, width, height);
                                    }
                                }
                            }
                        },
                        function(error) {
                            console.error(error);
                            d3.select("#message").style("display", "inline").html(that.LOAD.MESSAGE.notAvailable);
                        },
                        () => console.log('done disambiguation'));
                }
            }
        });

    }
};

var ety;

jQuery('document').ready(function($) {
    ety = EtyTree.create();
    ety.init();
});