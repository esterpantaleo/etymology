/* global module */
module.exports = function(grunt) {

  grunt.initConfig({
    jshint: {
      options: {
        'esversion': 6,
        'eqeqeq': true,
        'latedef': true, // 'nofunc' will ignore function calls
        // 'nocomma': true,
        // 'nonew': true, // worth trying?
        // 'trailingcomma': true,
        'undef': true,
        // 'unused': 'vars'
      },
      all: ['Gruntfile.js', './resources/js/load.js']
    }
  });

  grunt.loadNpmTasks('grunt-contrib-jshint');

  // A very basic default task.
  grunt.registerTask('default', 'Grunt is running', function() {
    grunt.log.write('Grunt is running...').ok();
  });

  grunt.registerTask('js', ['jshint']);

};