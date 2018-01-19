/*globals 
    d3, Rx, console, XMLHttpRequest
*/

/** 
 * @module DATA
 */ 
var DATA = (function(module) {

    module.bindModule = function(base, moduleName) {
        var etyBase = base;

        /**
         * Loads etymology-only_languages.csv, list_of_languages.csv, iso-639-3.tab
         * located in the resources/data/ folder
         * into etyBase.tree.langMap .
         * @function init
	 */
        var init = function() {
            //LOAD LANGUAGES
            //used to print on screen the language name when the user clicks on a node (e.g.: eng -> "English")      
            etyBase.helpers.debugLog("loading languages");

            etyBase.tree.langMap = new Map();
            var ssv = d3.dsv(";", "text/plain");
            ssv("resources/data/etymology-only_languages.csv", data => {
		data.forEach(entry => {
		    entry.code.split(",")
                        .map((code) => etyBase.tree.langMap.set(code, entry["canonical name"]));
                });
            });
            ssv("resources/data/list_of_languages.csv", data => {
                data.forEach(entry => {
                    etyBase.tree.langMap.set(entry.code, entry["canonical name"]);
                });
            });
            d3.text("resources/data/iso-639-3.tab", (error, textString) => {
                var headers = ["Id", "Part2B", "Part2T", "Part1", "Scope", "Language_Type", "Ref_Name", "Comment"].join("\t");
                var data = d3.tsv.parse(headers + textString);
                data.forEach(entry => {
                    etyBase.tree.langMap.set(entry.Id, entry.Ref_Name);
                });
            });
        };

        this.init = init;

        etyBase[moduleName] = this;
    };

    return module;

})(DATA || {});
