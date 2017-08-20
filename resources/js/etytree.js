/*globals
    jQuery, $, d3, console, window
*/
var EtyTree = {
    create: function() {
        var etyBase = Object.create(this);
        var bindModules = function(base, modules) {
            for (var i = modules.length - 1; i >= 0; i--) {
                window[modules[i]].bindModule(base, modules[i]);
            }
        };
        var modules = ['DB', 'GRAPH', 'LOAD'];
        bindModules(etyBase, modules);
        return etyBase;
    },
    init: function() {
        var etyBase = this;
        
        /* Run LOAD's init function -- Should this be called differently? */
        etyBase.LOAD.init();
        etyBase.GRAPH.init();
    }
};

var ety;

jQuery('document').ready(function($) {
    ety = EtyTree.create();
    ety.init();
});