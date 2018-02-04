/*globals
  jQuery, $, d3, console, window
*/

/**
 * @namespace Etytree 
 */
var EtyTree = {
    /**
     * @function create
     * @memberof Etytree   
     */
    create: function() {
	/**
         * Binds modules.   
         * @function bindModules    
         * @memberof Etytree                           
         */
        var bindModules = function(base, modules) {
            for (var i = 0; i < modules.length; i++) {
                if (!window[modules[i]]) {
                    console.error('Module ' + modules[i] + ' is not loaded.');
                    return false;
                }
                window[modules[i]].bindModule(base, modules[i]);
            }
        };


        var etyBase = Object.create(this);
	        
        /** 
	 * @namespace etyBase
         * @memberof Etytree 
         * @property {Object} helpers
	 * @function helpers.onlyUnique
         * @return {Boolean}
	 * @function helpers.debugLog
         * @property {Object} config
         * @property {Array.<String>} config.modules
         * @property {Boolean} config.debug
         * @property {Object} config.urls
         * @property {String} config.urls.ENDPOINT
	 * @property {String} config.urls.DBNARY_ENG
         * @property {String} config.urls.WIKT
         * @property {String} config.urls.WIKT_RECONSTRUCTION
         * @property {Object} config.notForeign - a regular expression
         * @property {Number} config.depthAncestors - depth of etyBase.DB.ancestorQuery
         */

	etyBase.helpers = {
            onlyUnique: function(value, index, self) {
                return self.indexOf(value) === index;
            },
            debugLog: function(logText) {
                if (etyBase.config.debug) {
                    console.log(logText);
                }
            }
        };

        etyBase.config = {
            modules: ['DB', 'GRAPH', 'DATA', 'DATAMODEL', 'APP'],
            debug: false,
            urls: {
                ENDPOINT: "https://etytree-virtuoso.wmflabs.org/sparql",
                DBNARY_ENG: "http://etytree-virtuoso.wmflabs.org/dbnary/eng/",
                WIKT: "https://en.wiktionary.org/wiki/",
                WIKT_RECONSTRUCTION: "https://en.wiktionary.org/wiki/Reconstruction:"
            },
	    notForeign: /^[a-z\u00C0-\u00F6\u00F8-\u017E]+$/i,
            depthAncestors: 5
        };
	
        bindModules(etyBase, etyBase.config.modules);
        return etyBase;
    },
    /**
     * @function init
     * @memberof Etytree 
     */
    init: function() {
        var etyBase = this;
	
        etyBase.tree = {}; // This will get populated with data, LangMap, inner, g, and some other key items
	
        /* Load init function for every module */
        etyBase.config.modules.forEach((moduleName) => {
            if (etyBase[moduleName] && (typeof etyBase[moduleName].init === 'function')) {
                etyBase[moduleName].init();
            }
        });
    }
};

var ety;

jQuery(window).load(function($) {
    ety = EtyTree.create();
    ety.init();
});
//window.location.href
//var url = new URL(url_string
//var c 0 url.searchParams.get("c"
