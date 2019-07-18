var tour = {
    id: "etytree_tutorial",
    steps: 
        [{
            target: "tags",
            placement: "right",
            title: "Search bar",
            content: "Enter a word here to visualize its etymological tree. In this demo, only a few words are available.<br><br> Try the following words: 'door','wiki','etymology','butter', 'latte','milk'. <br><br>Try word '_languages_tree' to visualize a tree of languages."
        }]
}

var tour2 = {
    id: "etytree_tutorial2",
    steps:
        [{
            target: "aHelpPopup",
            placement: "right",
            title: "Help",
            content: "Click on this icon to visualize an help page"
        },{
            target: "show-time",
            placement: "left",
            title: "Visualize timeline",
            content: "Click here to visualize a time line: the lenght of the branches will become proportional to an estimate of how old the word is."
        },{
            target: "lan",
            placement: "left", 
            title: "Language",
            content: "Choose language version."
        },{
            target: "rect0",
            placement: "left",
            title: "Legend: Node filling/contour",
            width: 400,
            content: "Etymological trees are made of nodes and links.<br> <ul><li>A <b>red contour</b> highlights the searched word, <li>A node with <b>non-white filling</b> is clickable.</li></ul>"
	},{
            target: "rect0",
            placement: "left",
            width: 400,
            title: "Legend: Link color/type",
            content: "Etymological trees are made of nodes and links.<br>Links are <ul><li><b>grey</b> by default,</li> <li><b>blue</b> if the word is borrowed or is an abbreviation,</li> <li><b>dotted</b> for composite words.</li></ul>"
        },{
            target: "tree-overlay",
	    placement: "top",
            width: 400,
            title: "Drag and Drop the tree",
            content: "<b>Dragging</b> can be performed on any node other than root. <b>Dropping</b> can be done on any node.",
            yOffset: 200,
            xOffset: 200
        },{
            target: "tree-overlay",
            placement: "top",
            width: 400,
            title: "Zooming or Panning the tree",
            content: "<b>Zooming</b> is performed by either double clicking on an empty part of the SVG or by scrolling the mouse-wheel. To Zoom out hold shift when double-clicking.<br><b>Panning</b> can either be done by dragging an empty part of the SVG around or dragging a node towards an edge.",
            yOffset: 200,
            xOffset: 200            
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
};

function getCookie(key) {
    var keyValue = document.cookie.match('(^|;) ?' + key + '=([^;]*)(;|$)');
    return keyValue ? keyValue[2] : null;
};

if (!getCookie("toured")) {
    hopscotch.startTour(tour);
}