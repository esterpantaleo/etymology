/*globals
    document, console, screen, hopscotch
*/

/**
 * A {@link http://linkedin.github.io/hopscotch/ Hopscotch} tour.
 * @namespace tour
 * @property {string}  id               - Id of the tutorial.
 * @property {Array.<Object>} steps     - Array of steps in the tutorial
 * @property {string}  steps.target     - Target of the step
 * @property {string}  steps.placement  - Placement of the step
 * @property {string}  steps.title      - Title of the step 
 * @property {content} steps.content    - Description of the step
 */
var tour = {
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
     * Set cookie on end (tour.onEnd).
     * @function onEnd
     * @memberof tour
     */
    onEnd: function() {
        setCookie("toured", "toured");
    },
    /**
     * Set cookie on close (tour.onClose).
     * @function onClose
     * @memberof tour
     */
    onClose: function() {
        setCookie("toured", "toured");
    }
};

/**
 * Set cookie for the {@link http://linkedin.github.io/hopscotch/ Hopscotch} tour.
 * @function setCookie
 * @param {string} key 
 * @param {string} value
 */
function setCookie(key, value) {
    var expires = new Date();
    expires.setTime(expires.getTime() + (1 * 24 * 60 * 60 * 1000));
    document.cookie = key + '=' + value + ';path=/' + ';expires=' + expires.toUTCString();
}

/**
 * Get cookie for the {@link http://linkedin.github.io/hopscotch/ Hopscotch} tour
 * @function getCookies 
 * @param {string} key
*/
function getCookie(key) {
    var keyValue = document.cookie.match('(^|;) ?' + key + '=([^;]*)(;|$)');
    return keyValue ? keyValue[2] : null;
}

//Run tour only if screen is large enough
if (screen.width > 800) {
    // Initialize tour if it's the user's first time
    if (!getCookie("toured")) {
        hopscotch.startTour(tour);
    }
}