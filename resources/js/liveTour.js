/*globals
    document, console, screen, hopscotch
*/
var tour = {
    id: "etytree_tutorial",
    steps: [{
        target: "tags",
        placement: "right",
        title: "Search bar",
        content: "<ul><li>Write a word then press enter: Some words will appear.</li><li><b>Click</b> on them to choose language and meaning.</li><li><b>Double click</b> on the word you are interested in."
    }, {
        target: "aHelpPopup",
        placement: "right",
        title: "Help",
        content: "Click on this icon to visualize a help page"
    }, {
        target: "lan",
        placement: "left",
        title: "Language",
        content: "Choose language version (not available yet)."
    }],
    onEnd: function() {
        setCookie("toured", "toured");
    },
    onClose: function() {
        setCookie("toured", "toured");
    }
};

function setCookie(key, value) {
    var expires = new Date();
    expires.setTime(expires.getTime() + (1 * 24 * 60 * 60 * 1000));
    document.cookie = key + '=' + value + ';path=/' + ';expires=' + expires.toUTCString();
}

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
