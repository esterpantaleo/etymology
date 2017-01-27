var tour = {
    id: "etytree_tutorial",
    steps: 
        [{
            target: "tags",
            placement: "right",
            title: "Search bar",
            content: "Enter a word here to visualize its etymological tree."
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
            target: "lan",
            placement: "left", 
            title: "Language",
            content: "Choose language version."
        }]
};
hopscotch.startTour(tour);
var playTour = true;
