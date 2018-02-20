const hopscotch = require('hopscotch');

/**
 * A {@link http://linkedin.github.io/hopscotch/ Hopscotch} tour.
 * @namespace Tour
 * @property {String}  id               - Id of the tutorial.
 * @property {Array.<Object>} steps     - Array of steps in the tutorial.
 * @property {String}  steps.target     - Target of the step.
 * @property {String}  steps.placement  - Placement of the step.
 * @property {String}  steps.title      - Title of the step.
 * @property {String} steps.content     - Description of the step.
 */

var Tour = {
	id: "etytree_tutorial",
	steps: [{
		target: "tags",
		placement: "right",
		title: "Search bar",
		content: "<ul><li>Type a word then press enter: Some words will appear.</li><li><b>Mouse over</b> them to choose language and meaning.</li><li><b>Click</b> on the word you are interested in."
	}, {
		target: "aHelpPopup",
		placement: "right",
		title: "Help",
		content: "Click on this icon to visualize a help page."
		/*    }, {
			target: "lan",
			placement: "left",
			title: "Language",
			content: "Choose language version (not available yet)."*/
	}],
	/**
	 * Sets cookie on end (Tour.onEnd).
	 * @function onEnd
	 * @memberof Tour
	 */
	onEnd: function () {
		var expires = new Date();
		expires.setTime(expires.getTime() + (1 * 24 * 60 * 60 * 1000));
		document.cookie = 'toured=toured;path=/' + ';expires=' + expires.toUTCString();
	},
	/**
	 * Sets cookie on close (Tour.onClose).
	 * @function onClose
	 * @memberof Tour
	 */
	onClose: function () {
		var expires = new Date();
		expires.setTime(expires.getTime() + (1 * 24 * 60 * 60 * 1000));
		document.cookie = key + '=' + value + ';path=/' + ';expires=' + expires.toUTCString();
	},
	/**
	 * Gets cookie.
	 * @function getCookie
	 * @memberof Tour
	 */
	getCookie: function (key) {
		var keyValue = document.cookie.match('(^|;) ?' + key + '=([^;]*)(;|$)');
		return keyValue ? keyValue[2] : null;
	}
};


//Run tour only if screen is large enough
if (screen.width > 800) {
	// Initialize tour if it's the user's first time
	if (!Tour.getCookie("toured")) {
		hopscotch.startTour(Tour);
	}
}
