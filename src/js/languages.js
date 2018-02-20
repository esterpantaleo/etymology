/**
 * @module LANGUAGES
 */

/**
 * Loads etymology-only_languages.csv, list_of_languages.csv, iso-639-3.tab
 * located in the resources/data/ folder
 * these data is used to print on screen the language name 
 * when user hovers on a node (e.g.: eng -> "English")
 * @function load
 * @return {Map} a map of languages
 */
var d3 = require('d3');
const etymology_only_languages = require('../../data/etymology-only_languages.csv');
const list_of_languages = require('../../data/list_of_languages.csv');
const iso_639_3 = require('../../data/iso-639-3.tab');

var load = () => {
    console.log("loading lang");
    var langMap = new Map();
    
    d3.dsvFormat(";").parse(etymology_only_languages)
	.forEach((entry) => {
	    entry.code.split(",")
		.map((code) => langMap.set(code, entry['canonical name']));
	});
    d3.dsvFormat(";").parse(list_of_languages)
	.forEach((entry) => {
	    langMap.set(entry.code, entry['canonical name']);
	});

    var headers = ['Id', 'Part2B', 'Part2T', 'Part1', 'Scope', 'Language_Type', 'Ref_Name', 'Comment']
	.join('\t');
    d3.tsvParse(headers + "\n" + iso_639_3)
	.forEach((entry) => {
	    langMap.set(entry.Id, entry.Ref_Name);    
	});
    return langMap;
};

module.exports = {
    langMap: load()
}
