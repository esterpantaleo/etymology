/*globals
    jQuery, $, d3, console, window
*/
var EtyTree = {
    create: function() {
        var etyBase = Object.create(this);

        //HELPER FUNCTIONS
        etyBase.helpers = {
            onlyUnique: function(value, index, self) {
                return self.indexOf(value) === index;
            },
            transform: function(d) {
                return "translate(" + d.x + "," + d.y + ")";
            }
        };

        /* Binding Modules */
        var bindModules = function(base, modules) {
            for (var i = 0; i < modules.length; i++) {
                if (!window[modules[i]]) {
                    console.error('Module ' + modules[i] + ' is not loaded.');
                    return false;
                }
                window[modules[i]].bindModule(base, modules[i]);
            }
        };

        /* Setup basic settings */
        etyBase.config = {
            modules: ['DB', 'GRAPH', 'LOAD'],
            debug: false,
            urls: {
                ENDPOINT: "https://etytree-virtuoso.wmflabs.org/sparql"
            },
            showDerivedNodes: true
        };

        bindModules(etyBase, etyBase.config.modules);
        return etyBase;
    },
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

jQuery('document').ready(function($) {
    ety = EtyTree.create();
    ety.init();
});