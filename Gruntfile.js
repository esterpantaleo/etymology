module.exports = function(grunt) {

  grunt.initConfig({
    jshint: {
      all: ['Gruntfile.js']
    }
  });

  grunt.loadNpmTasks('grunt-contrib-jshint');

  // A very basic default task.
  grunt.registerTask('default', 'Grunt is running', function() {
    grunt.log.write('Grunt is running...').ok();
  });

  grunt.registerTask('js', ['jshint']);

};