/* global module */
module.exports = function(grunt) {

  grunt.initConfig({
    jshint: {
      options: {
        'esversion': 6,
        'eqeqeq': true,
        'latedef': 'nofunc', // 'nofunc' will ignore function calls, true will include function calls
        // 'nocomma': true,
        // 'nonew': true, // worth trying?
        // 'trailingcomma': true,
        'undef': true,
        // 'unused': 'vars'
      },
      all: [
        'Gruntfile.js',
        './resources/js/dagre.js',
        './resources/js/etytree.js',
        './resources/js/liveTour.js',
        './resources/js/load.js',
        './resources/js/sparql.js'
      ]
    }
  });

  grunt.loadNpmTasks('grunt-contrib-jshint');

  // A very basic default task.
  grunt.registerTask('default', 'Grunt is running', function() {
    grunt.log.write('Grunt is running...').ok();
  });

  grunt.registerTask('js', ['jshint']);

};