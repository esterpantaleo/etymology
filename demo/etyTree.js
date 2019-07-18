var ambiguous,
    // panning variables        
    panBoundary = 20,// Within 20px from edges will pan when dragging. 
    panSpeed = 200,
    // misc
    duration = 750,
    root,
    searchedWord,
    searchedWordIndex,
    // size of the diagram                                               
    margin = [0, 0, 0, 0],
    viewerWidth = window.innerWidth - margin[0],
    viewerHeight = window.innerHeight - margin[0] - margin[2],
    // calculate totalNodes and maxLabelLength
    totalNodes = 0,
    maxLabelLength = 0,
    mynamefile,
    langData,
    selectedNode,
    draggingNode,
    i,
    baseSvg,
    svgGroup,
    svgGroup2,
    div,
    div2,
    div3,
    tree,
    zoomListener,
    nodesRight,
    checked = false,
    helpString,
    minYear,
    maxYear,
    axisScale,
    xAxis,
    xAxisGroup;

function drawAxis(myTreeData){    //draw time axis
    //set min and max year                                                                    
    minYear = 5000,
    maxYear = -7000;
    visit(myTreeData,
	  function(d) {
              //console.log(d);
              //console.log(!d.hidden);
              if (!d.hidden){
                  maxYear = Math.max(maxYear, + d.year);
                  minYear = Math.min(minYear, + d.year);
              }
              //console.log(minYear,maxYear,d.year);
	  },
	  function(d) {
	      return d.children && d.children.length > 0 ? d.children : null;
	  });
    maxYear = Math.min(2020, maxYear);
    //draw axis
    axisScale = d3.scale.linear()
        .domain([minYear, maxYear])
        .range([ maxLabelLength * [ + minYear / 50 ] ,  maxLabelLength * [ + maxYear / 50 ] ]);
    xAxis = d3.svg.axis()
        .ticks(Math.max(Math.floor((maxYear - minYear) / 500), 5))
        .tickFormat(function(d){
		return d<0? Math.abs(d) + " BC" : d + " AD";
        })
        .scale(axisScale);
    xAxisGroup = svgGroup.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(0, -40)")
        .call(xAxis);
    d3.selectAll(".tick > text")
	.style("font-size", "10px");
}

//get data from languages.json  
d3.json("tmp/_languages_tree.json", function(error1, json){
        if (error1){
            return console.warn(error1);
        }
        langData=json;
    }
)


// define a d3 diagonal projection for use by the node paths later on.          
var diagonal = d3.svg.diagonal()
        .projection(function(d) {
            return [d.y, d.x];
        });

// a recursive helper function for performing some setup by walking through all nodes    
function visit(parent, visitFn, childrenFn) {
    if (!parent) return;

    visitFn(parent);

    var children = childrenFn(parent);
    if (children) {
        var count = children.length;
        for (var i = 0; i < count; i++) {
	    visit(children[i], visitFn, childrenFn);
        }
    }
}

function printString(s){
    if (isArray(s)){
        toreturn = '<ul style="padding-left: 15px;padding-right: 5px;">';
        for (i=0;i<s.length;i++){
            toreturn = toreturn + "<li>" + s[i] + "</li>";
        }
        toreturn += "</ul>";
    } else {
        toreturn = s;
    }
    return toreturn;
} 

function setSearchedWord(source){
    if (typeof source.index != 'undefined'){
        if (source.index === searchedWordIndex){
	    searchedWord = source;
        }
    }
    return source.children && source.children.length > 0 ? source.children : null;
}

function setLength(source){
    totalNodes++;
    var myLen = 0;
    for (hh=0; hh<source.name.length; hh++){
        myLen += source.name[hh].length;
    }	
    maxLabelLength = Math.max(myLen, maxLabelLength);
}

/**
 * Check whether an object is Array or not
 * @type Boolean
 * @param {object} subject is the variable that is
 * tested for Array identity check
 */
function isArray(d){
    return (d instanceof Array && d.length != 0)? true : false;
} 

// Helper functions for collapsing and expanding nodes.     
function collapse(d) {
    //if (d === searchedWord){                      
    if (d.children) {
	d._children = d.children;
	d._children.forEach(collapse);
	d.children = null;
    }
    //}                          
}

function expand(d) {
    //if (d === searchedWord){                   
    if (d._children) {
	d.children = d._children;
	d.children.forEach(expand);
	d._children = null;
    }
    //}                                          
}

// Toggle children function               
function toggleChildren(source) {
    //console.log(root.children);
    if (source.children) {
        //console.log("t");
	source._children = source.children;
        source._children.forEach(collapse); 
	source.children = null;
    } else if (source._children) {
        //console.log("t");
	source.children = source._children;
        source.children.forEach(expand);
	source._children = null;
    } //else {
    //console.log("nodesRight");
    //  console.log(nodesRight);
    //}
    return source;
}

function sortTree(tree) {
    tree.sort(function(a, b) {          
            if (typeof(a.order) != "undefined" && typeof(b.order) != "undefined"){//if tree has left side, i.e. word is composed by multiple words
               	return +a.order > +b.order ? 1 : -1;
            } else {//first compare languages
                if (a.iso != b.iso){
		    return a.iso < b.iso ? -1 : 1;
                } else {//then compare names
	       	    var aName, bName;
                    if (isArray(a.name)){
	                aName = a.name[0];
                    } else {
	                aName = a.name;
                    }
                    if (isArray(b.name)){
	                bName = b.name[0];
                    } else {
	                bName = b.name;
                    }
                    return bName.toLowerCase() < aName.toLowerCase() ? 1 : -1;
                }
            }
    });
}

var toArray = function(item, arr, d){
    arr = arr || [];
    var dr = d || 1;
    var i = 0, l = item.children ? item.children.length : 0;
    arr.push(item);
    if (item.position && item.position === 'left'){
    	dr = -1;
    }
    item.y = dr * item.y;
    for (; i < l; i++){
	toArray(item.children[i], arr, dr);
    }
    return arr;
};

function loadTreeFromFile(mynamefile){
    hopscotch.endTour();    
    var namefile = "tmp/" + mynamefile.split(' ').join('_') + ".json";
    treeJSON = d3.json(namefile, function(error, treeData) {    
            d3.select("#show-time")
                .on("mouseover", function(d) {
                    div3.transition()
		        .duration(200)
		        .style("opacity", 1);
                    div3.html("Check box to visualize words on a time scale.")
                        .style("left", d3.event.pageX + "px")                          
                        .style("top", d3.event.pageY + "px");                        
                })
                .on("mouseout", function(d) {
                    div3.transition()
		    .duration(500)
		    .style("opacity", 0);
                });
	    
	    searchedWordIndex = 0;
            var K = 0,
            data = [],
            tmpTreeData = [],
            myTreeData = null;
	    clean();
            if (!getCookie("toured")){
                hopscotch.startTour(tour2);
            }
            //if treeData is scalar (i.e., json file does not have multiple etymology trees)
            //make it into an array of size one
            if (!isArray(treeData)){
		var array = [];
                array[0] = jQuery.extend(true, {}, treeData);
                treeData = array;
            }
            //for each tree in treeData, for each node, set iso and year (except if year of node is already specified or node link == borrowing or abbreviation)
            //also vectorize both language name and node name
            for (kk=0; kk<treeData.length; kk++){
	        visit(treeData[kk], 
		      function(d){
			  visit(langData,
				function(language){
                                    var name = [];
				    if (isArray(language.name)){
				        name = language.name;
				    } else {
				        name[0] = language.name;
				    }
				    for (hh=0; hh<name.length; hh++){
					if (name[hh] === d.language){
					    if (!d.year) {
						if (!(d.link === "borrowing" || d.link === "abbreviation")){
						    d.year = language.year;
                                                    d.year_string = "_inferred from year associated to language";
					        }
					    }
					    d.iso = language.iso;
					    break;
					}
				    }
				},
				function(language) {
				    if (language.children && language.children.length > 0){
					return language.children;
				    }
				});
			  if (!isArray(d.name)){
			      var dname = [];
			      dname[0] = d.name;
			      d.name = dname;
			  }
		      },
                      function(d) {
                        return d.children && d.children.length > 0 ? d.children : null;
		      }
		      );
                //for each tree in treeData, for each node, set year for derived words in the same language and for derived from the same language/borrowed/abbreviated words
                visit(treeData[kk], function(){}, function(d) {	
			if (d.children && d.children.length > 0){
			    for (hh=0; hh<d.children.length; hh++){
				if ((!d.children[hh].year && (d.children[hh].link === "borrowing" || d.children[hh].link === "abbreviation"))|| (d.children[hh].language === d.language && d.children[hh].year_string && d.children[hh].year_string[0] == "_")) {//if a node has a special link || a node is derived from a word in the same language
				    d.children[hh].year = + d.year + 300; //300 is arbitrary                                               
				    d.children[hh].year_string = "_inferred by parent node because of borrowed/abbreviated/derived from parent word " + d.name;
			        }
                            }
			}
                        for (hh=0; hh<d.name.length; hh++){
                            if (d.name[hh] == mynamefile && !d.duplicate){
                                tmpTreeData[K] = jQuery.extend({}, d);
                                tmpTreeData[K].kindex = kk;
                                tmpTreeData[K].index = K;
                                d.index = K;
                                K++;
                                break;
                            }
			}
                        return d.children && d.children.length > 0 ? d.children : null;
		    });	     
	    }
            //define myTreeData
	    if (tmpTreeData.length === 0){//if the word is not in the database
                d3.select("#tree-overlay").remove(); 
                d3.select("#tree-container")
                    .append("p")
                    .attr("id","message")
                    .attr("align", "center")
                    .html("sorry, word <i>" + mynamefile + "</i> is not in the database yet");
            } else {
                if (tmpTreeData.length === 1){//if there is only one word in the database matching the searched word
	            ambiguous = false;
                    helpString = "This is the etymological tree of word <br><br><b>" + mynamefile + "</b>.<br><br><i>Mouseover</i> words to visualize their definition and their language. <br><br>If a node is <i>filled with color</i>, you can click on it.";
                    myTreeData = treeData[0];
                } else { //if there are multiple words in the database matching the searched word
                    ambiguous = true; 
                    helpString = "This is a disambiguation page: word <b>" + mynamefile + "</b> has multiple entries in the etymology dictionary. <br><br><i>Mouseover</i> words to visualize their definition and their language. <br><br><i>Click</i> on the word that you are interested in.";    
                    myTreeData = {
			name: "_invisible",
			language: "_invisible",
			type: "",
			definition_english: "",
			year: "-5400",
			hidden: true
		    };
                    
                    for (hh=0; hh<tmpTreeData.length; hh++){
		        tmpTreeData[hh].children = null;
                        tmpTreeData[hh].link = null;
                    }
                    myTreeData.children = tmpTreeData;
                }
                d3.select("#p-helpPopup").html(helpString);
         
                tree = d3.layout.cluster()
                    .size([viewerHeight, viewerWidth]);

                // Sort the tree initially in case the JSON isn't in a sorted order.            
                sortTree(tree);

                zoomListener = d3.behavior.zoom().scaleExtent([0.1, 3]).on("zoom", zoom);
                
                //////////////////////////////DEFINE SOME FUNCTIONS

                // TODO: Pan function, can be better implemented.
                function pan(domNode, direction) {
                    var speed = panSpeed;
                    if (panTimer) {
                        clearTimeout(panTimer);
                        translateCoords = d3.transform(svgGroup.attr("transform"));
                        if (direction == "left" || direction == "right") {
                            translateX = direction == "left" ? translateCoords.translate[0] + speed : translateCoords.translate[0] - speed;
                            translateY = translateCoords.translate[1];
                        } else if (direction == "up" || direction == "down") {
                            translateX = translateCoords.translate[0];
                            translateY = direction == "up" ? translateCoords.translate[1] + speed : translateCoords.translate[1] - speed;    
                        }
                        scaleX = translateCoords.scale[0];
                        scaleY = translateCoords.scale[1];
                        scale = zoomListener.scale();
                        svgGroup.transition().attr("transform", "translate(" + translateX + "," + translateY + ")scale(" + scale + ")");
                        d3.select(domNode).select('g.node').attr("transform", "translate(" + translateX + "," + translateY + ")");
                        zoomListener.scale(zoomListener.scale());
                        zoomListener.translate([translateX, translateY])   
                        panTimer = setTimeout(function() {
                                pan(domNode, speed, direction);
                            }, 50);
                    }
                }

                function initiateDrag(d, domNode) {
                    draggingNode = d;
                    d3.select(domNode).select(".ghostCircle").attr("pointer-events", "none");
                    d3.selectAll(".ghostCircle").attr("class", "ghostCircle show");
                    d3.select(domNode).attr("class", "node activeDrag");

                    svgGroup.selectAll("g.node").sort(function(a, b) { // select the parent and sort the path's
                        if (a.id != draggingNode.id) return 1; // a is not the hovered element, send "a" to the back
                        else return -1; // a is the hovered element, bring "a" to the front
     	            }); 
                    // if nodes has children, remove the links and nodes
                    if (nodes.length > 1) {
                        // remove link paths
                        links = tree.links(nodes);
                        nodePaths = svgGroup.selectAll("path.link")
                            .data(links, function(d) {
                                return d.target.id;
                            }).remove();
                        // remove child nodes
                        nodesExit = svgGroup.selectAll("g.node")
                            .data(nodes, function(d) {
                                return d.id;
                            }).filter(function(d, i) {
                                if (d.id == draggingNode.id) {
                                return false;
                            }
                            return true;
                        }).remove();               
                    }

                    // remove parent link
                    parentLink = tree.links(tree.nodes(draggingNode.parent));
                    svgGroup.selectAll("path.link").filter(function(d, i) {
                            if (d.target.id == draggingNode.id) {
                                return true;
                            }
                            return false;
                        }).remove();

                    dragStarted = null;
                }
                           
                function mydragstart(d){
                    if (d == root) {
	                return;
                    }
                    dragStarted = true;
     	            nodes = tree.nodes(d);
                    d3.event.sourceEvent.stopPropagation();
                    // it's important that we suppress the mouseover event on the node being dragged. Otherwise it will absorb the mouseover event and the underlying node will not detect it d3.select(this).attr('pointer-events', 'none');  
                }

                function mydrag(d){
                    if (d == root) {
                        return;
                    }
                    if (dragStarted) {
                        domNode = this;
                        initiateDrag(d, domNode);
                    }
	            // get coords of mouseEvent relative to svg container to allow for panning 
                    relCoords = d3.mouse($('svg').get(0));
                    if (relCoords[0] < panBoundary) {
                        panTimer = true;
                        pan(this, 'left');
	            } else if (relCoords[0] > ($('svg').width() - panBoundary)) {
                        panTimer = true;
                        pan(this, 'right');
                    } else if (relCoords[1] < panBoundary) {
                        panTimer = true;
                        pan(this, 'up'); 
                    } else if (relCoords[1] > ($('svg').height() - panBoundary)) {
                        panTimer = true;
                        pan(this, 'down');
                    } else {
                        try {
                            clearTimeout(panTimer);
                        } catch (e) {
                        }
	            }

	            d.x0 += d3.event.dy;
                    d.y0 += d3.event.dx;
                    var node = d3.select(this);
	            node.attr("transform", "translate(" + d.y0 + "," + d.x0 + ")");
                    updateTempConnector();
                }

                function mydragend(d){
	            if (d == root) {
                        return;
                    }
                    domNode = this;
                    if (selectedNode) {
                        // now remove the element from the parent, and insert it into the new elements children                         
		        var index = draggingNode.parent.children.indexOf(draggingNode);
                        if (index > -1) {
		            draggingNode.parent.children.splice(index, 1);
                        }
                        if (typeof selectedNode.children !== "undefined" || typeof selectedNode._children !== "undefined") {
		            if (typeof selectedNode.children !== "undefined") {
		                selectedNode.children.push(draggingNode);
                            } else {
		                selectedNode._children.push(draggingNode);
                            }
	                } else {
		            selectedNode.children = [];
		            selectedNode.children.push(draggingNode);
	                }
	                // Make sure that the node being added to is expanded so user can see added node is correctly moved       
                        expand(selectedNode);
                        sortTree(tree);
                        endDrag();
                    } else {
                        endDrag();
                    }
                }

                function endDrag() {
                    selectedNode = null;
                    d3.selectAll(".ghostCircle").attr("class", "ghostCircle");
                    d3.select(domNode).attr("class", "node");
                    // now restore the mouseover event or we won't be able to drag a 2nd time
                    d3.select(domNode).select(".ghostCircle").attr("pointer-events", "");
                    updateTempConnector();
                    if (draggingNode !== null) {
                        update(root);
                        centerNode(draggingNode);                 
                        draggingNode = null;
                    }
                }

                                    

                // Function to update the temporary connector indicating dragging affiliation
                var updateTempConnector = function() {
                    var data = [];
                    if (draggingNode !== null && selectedNode !== null) {
                        // have to flip the source coordinates since we did this for the existing connectors on the original tree
                        data = [{
                            source: {
                                x: selectedNode.y0,
                                y: selectedNode.x0
                            },
                            target: {
                                x: draggingNode.y0,
                                y: draggingNode.x0
                            } 
                        }];
                    }
                    var link = svgGroup.selectAll(".templink").data(data);
                    link.enter().append("path")
                        .attr("class", "templink")
	                .attr("d", d3.svg.diagonal());

                    link.attr("d", d3.svg.diagonal());

                    link.exit().remove();
                };

                // Function to center node when clicked/dropped so node doesn't get lost when collapsing/moving with large amount of children.
                function centerNode(source) {
                    scale = zoomListener.scale();
                    x = - source.y0;
                    y = - source.x0;
                    x = x * scale + viewerWidth / 2;
                    y = y * scale + viewerHeight / 2;
                    d3.select('g').transition()
                        .duration(duration)
                        .attr("transform", "translate(" + x + "," + y + ")scale(" + scale + ")");
                    zoomListener.scale(scale);
                    zoomListener.translate([x, y]);
                }
                
                // Toggle children on click.
                function click(d) {
     	            if (ambiguous){
                        ambiguous = false;
                        if (!(d.hidden)){
                            clean();
                            if (isArray(treeData)){
		                myTreeData = treeData[d.kindex];
                                //set id of elements 
                                var idindex = 1;
                                visit(myTreeData,
		                    function(d) {
                                        d.id = idindex; 
                                        idindex++;
                                    },
		                    function(d) {
			                return d.children && d.children.length > 0 ? d.children : null;
		                    }
	                        );
	                    } else {
		                myTreeData = treeData;
                            }
                            tree = d3.layout.cluster()
	                        .size([viewerHeight, viewerWidth]);
              
                            // Call visit function to establish maxLabelLength and to identify index of searchedWord  
                            searchedWordIndex = d.index;
                            visit(myTreeData, function(){}, setSearchedWord);
                            helpString = "This is the etymological tree of the " + d.language + " word <b>" + d.name + "</b>. <i>Drag/zoom</i> to visualize details. <br><br><i>Mouseover</i> any of the words to visualize their definition and their language. <br><br><i>Click over nodes</i> to collapse/expand the tree. <br><br>If a node is <i>filled with color</i>, you can click on it.";
                            d3.select("#p-helpPopup").html(helpString);
	                    // Sort the tree initially in case the JSON isn't in a sorted order
                            sortTree(tree);
                            
                            dragListener = d3.behavior.drag()
                                .on("dragstart", mydragstart)
                                .on("drag", mydrag)
                                .on("dragend", mydragend);

                            root = myTreeData;
                            root.x0 = viewerWidth / 4;
                            root.y0 = 0;

                            // Layout the tree initially 
                            //collapse all children of searchedWord   
                            //collapse(searchedWord);
                            update(root);
                            
                            //center around searchedWord
			    //console.log(searchedWord);
                            //centerNode(searchedWord);
                            centerNode(root);
                        }
                    } else {
                        if (d3.event.defaultPrevented) return; // click suppressed
                        if ((typeof(d.position) != "undefined" && d.position == "left") || d.link == "composite"){
                            h++;
                            myHistory[h] = d.name[0];
			    $("#tags").val(myHistory[h]);
			    loadTreeFromFile(d.name[0]);
                        } else {
			    d = toggleChildren(d);
                            if (!isArray(root.left)){
                                update(d);
                            }
                            centerNode(d);
                        }
                    }
                }

                function update(source) {
                    // Compute the new height, function counts total children of root node and sets tree height accordingly.
                    // This prevents the layout looking squashed when new nodes are made visible or looking sparse when nodes are removed
                    // This makes the layout more consistent.
                    var levelWidth = [1];
                    var childCount = function(level, n) {
                        if (n.children && n.children.length > 0) {
                            if (levelWidth.length <= level + 1) 
                                levelWidth.push(0);
                            levelWidth[level + 1] += n.children.length;
                            n.children.forEach(function(d) {
                                childCount(level + 1, d);
                            });  
                        }
                    };
                    childCount(0, root);
                    var newHeight = d3.max(levelWidth) * 35; // 25 pixels per line
                    var nodes;
                    if (isArray(root.left)){
                        helpString="The " + source.language + " word <b>" + mynamefile + "</b> is a compound word</b>. <br><br> To visualize the etymological tree of the words composing word <i>" + mynamefile + "</i> click on them.";
			d3.select("#p-helpPopup").html(helpString);
                        var nodesLeft = tree.size([newHeight, viewerWidth / 2])
			    .children(function(d){
				      return (d.depth === 0) ? d.left : d.children;
				      })
			    .nodes(root)
			    .reverse();
		        nodesRight = tree.size([newHeight, viewerWidth / 2])
			    .children(function(d){
				    return (d.depth === 0) ? d.right : d.children; 
			      })
			    .nodes(root)
			    .reverse();
		        root.children = root.left.concat(root.right);
		        root._children = null;
		        nodes = toArray(root);
                    } else {
                        tree = tree.size([newHeight, viewerWidth]);
                        nodes = tree.nodes(root).reverse();
                    }
                    visit(root, setLength, function(d) {
				return d.children && d.children.length > 0 ? d.children : null;
			});
                    
                    links = tree.links(nodes);
                    $("#time-checkbox").click(
                    /////////////////timeout = setTimeout(function() { input.property("checked", true).each(changed); }, 2000);
			 function () {
			    
                            checked = this.checked;
                            console.log("checked=" + checked);
                            if (checked){
				drawAxis(root);
			    } else {
				d3.select(".axis").remove();
			    }
      	                    //clearTimeout(timeout);
                            nodes.forEach(function (d) { 
                                var dr = 1;
                                if (d.position && d.position === "left"){
                                    dr = -1;
                                } 
                                d.y = checked ? maxLabelLength * [ + d.year / 50 ] : dr * d.depth * (maxLabelLength * 10);
                                console.log("should be " + d.y + "=" + (maxLabelLength * [ + d.year / 50 ]))
                            });

                            node.transition()
	                        .duration(duration)
	                        .attr("transform", function(d) {
	                                return "translate(" + d.y + "," + d.x + ")";
                                    });			     
      
                            link.transition()
                                .duration(duration)
                                .attr("d", diagonal);
	                }
		    );

                    // Set widths between levels based on maxLabelLength.
                    nodes.forEach(function(d) {
                        var parent_year, dr = 1;
                        if (this.parentNode == null){ 
                            parent_year = 0; 
                        } else {
                            parent_year = d3.select(this.parentNode).datum.y ;
                        }
                        if (d.position && d.position == "left"){
                            dr = -1;
                        }
                        d.y = checked ? maxLabelLength * [ +d.year / 50 ] : dr * d.depth * (maxLabelLength * 10); 
                        // alternatively to keep a fixed scale one can set a fixed depth per level
                        // Normalize for fixed-depth by commenting out below line
                        // d.y = (d.depth * 500); //500px per level.
                    });

                    // Update nodes…
                    node = svgGroup.selectAll("g.node")
                        .data(nodes, function(d) {
                                return d.id || (d.id = ++i); 
                            });

                    // Enter any new nodes at the parent's previous position.
                    var nodeEnter = node.enter().append("g")
   		        .call(dragListener)
                        .attr("class", "node")
		        .attr("transform", function(d) {
                                return "translate(" + source.y0 + "," + source.x0 + ")";
                            })
                        .on('click', click);
                
                    //add node symbol
                    nodeEnter.append("rect")
		        .filter(function(d) { return !d.hidden; })
		        .attr("class", "nodeCircle")
		        .attr("width", 0)
		        .attr("height", 12)
		        .attr("y", -6)
		        .attr("x", -16)
		        .attr("rx", 5)
		        .attr("ry", 5)
		        .style("fill", function(d) {
                            if (d._children){
                                return "lightsteelblue";
			    } else if (d.link === "composite" || (typeof(d.position) != "undefined" && d.position == "left")){
				return "khaki";
                            } else {
			        return "#fff";
                            }
		        })
		        .style("stroke", function(d){ 
                                return (ambiguous || d == searchedWord) ? "red" : "steelblue"; 
                            })
		        .on("mouseover", function(d) {
			    div.transition()
			        .duration(200)
			        .style("opacity", 1);
			    div.html(d.language)
			        .style("left", d3.event.pageX + "px")
			        .style("top", (d3.event.pageY - 18) + "px");
			    })
		        .on("mouseout", function(d) {
			    div.transition()
				.duration(500)
				.style("opacity", 0);
		            });

                    // phantom node to give us mouseover in a radius around it
                    nodeEnter.append("circle")
                        .attr('class', 'ghostCircle')
		        .attr("r", 30)
                        .attr("opacity", 0.2) // change this to zero to hide the target area
                        .style("fill", "red")
                        .attr('pointer-events', 'mouseover')
                        .on("mouseover", function(node) {
                               overCircle(node);
                            })
                        .on("mouseout", function(node) {
                               outCircle(node);
                            });

                    // Update the text to reflect whether node has children or not.
                    node.append("text")
		        .attr("class","nodeDescriptive")
                        .attr("x", function(d) {
                                if (isArray(d.left)){
                                    return 19; 
                                }
			        return d.children || d._children ? -19 : 19;
                            })
		        .attr("y", 3)
		        .attr("font-size","13px")
                        .attr("text-anchor", function(d) {
                                if (isArray(d.left)) { 
                                    return "start"; 
                                }
			        return d.children || d._children ? "end" : "start";
                            })
                        .text(function(d) {
			        if (!d.hidden){
                                    return d.name[0];
                                } else {
                                    return;
                                }
                            })
                        .on("mouseover", function(d) {
                                 div.transition()
                                     .duration(200)
                                     .style("opacity", 1);
                                 div.html(function(){
                                         var a, b, y = "";
                                         if (d.long_name){
                                             a = d.long_name;
                                         } else {
                                             a = d.name;
                                         }
                                         if (d.long_definition_english) {
                                             if (d.long_definition_english != ""){ 
                                                 b = printString(d.long_definition_english);
     			                     }
                                         } else { 
                                             b = printString(d.definition_english);
                                         } 
                                         if (d.year){
                                             if (d.year_string){
                                                 if (d.year_string[0] != "_"){
                                                     var yy = + d.year > 0 ? d.year + " AD" : Math.abs(+d.year) + " BC";
                                                     y = " (" + yy + ") ";
                                                 }
                                             }
                                         }
                                         return "<span style='font-weight:bold'>" + a + "</span>, " + d.type  + y + " <br><br>" + b;
					 //'<a onclick="this.firstChild.play()"><audio src="https://upload.wikimedia.org/wikipedia/commons/c/ce/En-uk-door.ogg"></audio>&#128266;</a><br><br>' + b;
                                     })
	                             .style("left", (d3.event.pageX) + "px")
	                             .style("top", (d3.event.pageY - 18) + "px");
			    })
		        .on("mouseout", function(d) {
			      div.transition()
			          .duration(500)
			          .style("opacity", 0);
		            });

                    //add iso tag on node                                                     
                    node.append("text")
		        .filter(function(d) { return !d.hidden; })
		        .attr("x", 0)
		        .attr("y", 2)
                        .attr("text-anchor","middle")
		        .text(function(d){ return d.iso; })
                        .attr("font-size","8px")
		        .on("mouseover", function(d) {
                            div.transition()
                                .duration(200)
                                .style("opacity", 1);
                            div.html(d.language)
                                .style("left", d3.event.pageX + "px")
                                .style("top", (d3.event.pageY - 18) + "px");
			})
		        .on("mouseout", function(d) {
                            div.transition()
                                .duration(500)
                                .style("opacity", 0);
			});

                    // Change the circle fill depending on whether it has children and is collapsed
                    node.select("rect.nodeCircle")
		        .attr("width", 33)
                        .style("fill", function(d) {
			    if (d._children){
      	      	      	      	return "lightsteelblue";
      	      	      	    } else if (d.link === "composite" || (typeof(d.position) != "undefined" && d.position == "left" || ambiguous)){
                                return	"khaki";
      	      	      	    } else {
                                return "#fff";
      	      	      	    }
                        })
		        .style("stroke", function(d){ return (ambiguous || d === searchedWord) ? "red" : "steelblue"; });

                    // Transition nodes to their new position.
                    var nodeUpdate = node.transition()
                        .duration(duration)
                        .attr("transform", function(d) {
                            var dr = 1;
                            if (d.position && d.position === "left"){ 
				dr = -1;
			    }
	                    d.y = checked ? maxLabelLength * [ + d.year / 50] : dr * d.depth * (maxLabelLength * 10);
                            return "translate(" + d.y + "," + d.x + ")";
                        });

                    // Fade the text in
                    nodeUpdate.select("text")
                        .style("fill-opacity", 1);

                    // Transition exiting nodes to the parent's new position.
                    var nodeExit = node.exit().transition()
                        .duration(duration)
                        .attr("transform", function(d) {
                            return "translate(" + source.y + "," + source.x + ")";
                        })
                        .remove();
 
                    nodeExit.select("rect")
	    	        .attr("height",0);
		
                    nodeExit.select("text")
                        .style("fill-opacity", 0);

                    // Update the links…
                    var link = svgGroup.selectAll("path.link")
                        .data(links, function(d) {
                            return d.target.id;
                        });

                    // Enter any new links at the parent's previous position.
                    link.enter().insert("path", "g")
                        .attr("class", "link")
                        .attr("d", function(d) {
                            var o = {
                                x: source.x0,
                                y: source.y0
                            };
                            return diagonal({
                                source: o,
                                target: o
                            });
                        })            
		        .attr("stroke", function(d) {
		            if (d.target.link == "borrowing" || d.target.link == "abbreviation" || d.target.link == "composite"){
			        return "steelblue";
                            } else if (d.source.hidden == true){
                                return "transparent";
			    } else { 
                                return "#ccc";
                            }})
		        .attr("stroke-dasharray", 
                                function(d){ 
                                    if (d.target.link == "composite") {
                                        return "2,4";
                                    }
                                }
                            )
		        .attr("fill", "none")
                        .on("mouseover", function(d) {
			    div2.transition()
			        .duration(200)
			        .style("opacity", 1);
			    div2.html(d.target.link_note_english ? d.target.link_note_english : d.target.link)
			        .style("left", (d3.event.pageX - 30) + "px")
			        .style("top", (d3.event.pageY - 38) + "px");
			    })
		   	.on("mouseout", function(d) {
			    div2.transition()
		                .duration(500)
		                .style("opacity", 0);
		           });

                    // Transition links to their new position.
                    link.transition()
                        .duration(duration)
                        .attr("d", diagonal);

                    // Transition exiting nodes to the parent's new position.
                    link.exit().transition()
                        .duration(duration)
                        .attr("d", function(d) {
                            var o = {
                                x: source.x,
                                y: source.y
                            };
                            return diagonal({
                                source: o,
                                target: o
                            });
                            })
                        .remove();

                    // Stash the old positions for transition.
                    nodes.forEach(function(d) {
                        d.x0 = d.x;
                        var dr = 1;
                        if (d.position && d.position === "left") { 
			    d.r = -1;
	                }   
                        d.y = checked ? maxLabelLength * [(+ d.year) / 50] : dr * d.depth * (maxLabelLength * 10);
                        d.y0 = d.y;
                    });
                
                    if (checked){
                        drawAxis(root);
                    } else {
                        d3.select(".axis").remove();
                    }
		}
                /////////////////////END OF DEFINE SOME FUNCTIONS

                // Define the drag listeners for drag/drop behaviour of nodes.   
               dragListener = d3.behavior.drag()
		    .on("dragstart", mydragstart)
		    .on("drag", mydrag)
		    .on("dragend", mydragend);

                var overCircle = function(d) {
                    selectedNode = d;
                    updateTempConnector();
                };
                var outCircle = function(d) {
                    selectedNode = null;
                    updateTempConnector();
                };

                //// Define the root                                                                       
                ////if there is left and right then define the root as the element in between them        
                root = myTreeData;
                root.x0 = viewerWidth / 4;
                root.y0 = 0;
                root.left = [];
                root.right = [];
                for (i=0; i<root.children.length; i++){
                    if (root.children[i].position === "left"){
                        root.left.push(root.children[i]);
                    } else {
                        root.right.push(root.children[i]);
                    }
                }
                // Layout the tree initially and center on the searchWord node.                 
                visit(root, function(d) {}, setSearchedWord);
		//collapse(searchedWord);                                                 
                update(root);
                if (ambiguous){
                   centerNode(searchedWord);
                } else {
		    centerNode(root);
                }
                //console.log(searchedWord);
	    }
         
            // Define the zoom function for the zoomable tree
            function zoom() {
                div.transition().duration(0).style("opacity", 0);
                div2.transition().duration(0).style("opacity", 0);
                div3.transition().duration(0).style("opacity", 0);
                div.attr("style", "zoom:" + (+ d3.event.scale * 100) + "%" );
                div2.attr("style", "zoom:" + (+ d3.event.scale * 100) + "%");
                svgGroup.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
            }

            function clean() {
		    //remove any existing tree or tooltip                               
		    d3.selectAll("svg").remove();
		    d3.selectAll("g.node").remove();
		    d3.select("#message").remove();
		    d3.select("#logo").remove();
		    if (!checked){
			d3.select(".axis").remove();
		    }
		    d3.selectAll(".tooltip").remove();
		    d3.selectAll(".tooltip2").remove();
		    d3.selectAll(".tooltip3").remove();

		    totalNodes = 0;
		    maxLabelLength = 0;
		    node = null;
		    nodes = null;
		    // variables for drag/drop                        
		    selectedNode = null;
		    draggingNode = null;
		    // Misc. variables                                            
		    i = 0;
		    // define the zoomListener which calls the zoom function on the "zoom" event constrained within the scaleExtents                    
                    zoomListener = d3.behavior.zoom().scaleExtent([0.1, 3]).on("zoom", zoom);
		    baseSvg = d3.select("#tree-container").append("svg")
                        .attr("id", "tree-overlay")
                        .attr("width", viewerWidth)
                        .attr("height", viewerHeight)
                        .attr("class", "overlay")
                        .attr("transform", "translate(" + margin[3] + "," + (margin[0]+20) + ")")
                        .call(zoomListener);
		    svgGroup = baseSvg.append("g");

		    // Append a group which holds the legend                                     
		    var myNodes = [["red","#fff"],
				   ["steelblue","#fff"],
				   ["steelblue","#fff"],
				   ["steelblue","lightsteelblue"],
				   ["steelblue","khaki"]],
		    myLines = [["#ccc",null],
			       ["#ccc","2,4"],
			       ["steelblue",null]],
		    myTexts = ["the searched word","a word","an English (en) word","click to expand the tree","click to jump to the etymological tree of the word","derived word","compound word","borrowed word / abbreviation"],
		    aX = viewerWidth * 6 / 9;
		    svgGroup2 = baseSvg.append("g");
		    svgGroup2.selectAll("rect")
                        .data(myNodes).enter()
                        .append("rect")
                        .attr("stroke", function(d){ return d[0]; })
                        .attr("stroke-width", "1.5px")
                        .attr("class", "nodeCircle")
                        .attr("width",  33)
                        .attr("height", 12)
                        .attr("y", function(d, i){ return (i + 1.3) * viewerHeight / 50; })
                        .attr("x", aX)
                        .attr("rx", 5)
                        .attr("ry", 5)
                        .style("fill", function(d){ return d[1]; })
		        .style("z-index", 1)
		        .attr("id", function(d, i){ return "rect" + i; });
		    svgGroup2.selectAll("text").data(myTexts).enter()
                        .append("text")
                        .text(function(d){ return d; })
                        .attr("x", aX + 43)
                        .attr("y", function(d, i){ return i < 5 ? (i + 2) * viewerHeight / 50 : (i + 2.1) * viewerHeight / 50; })
                        .attr("font-size", "10px");
		    svgGroup2.append("text")
		        .attr("text-align","center")
                        .attr("x", aX + 12)
                        .attr("y", 3.3 * viewerHeight / 50 + 8)
		    //.attr("width", 35)
		    //  .attr("height", 12)
		    //  .append("xhtml:body")
		    //  .html(function(d) {
		    //    return '<div id="iso-tag" style="z-index:1000;width:35px;font-size:8px;text-align:center;">en</div>';
		    //});
		        .text("en")
                        .style("font-size","8px");
		    svgGroup2.append("text")
			.text("LEGEND")
		        .attr("font-size", "10px")
			.attr("x", viewerWidth * 6 / 9)
		        .attr("y", viewerHeight / 50);
		    svgGroup2.selectAll("line").data(myLines).enter()
                        .append("line")
                        .attr("x1", aX)
                        .attr("x2", aX + 33)
                        .attr("y1", function(d, i){ return (i + 6.8) * viewerHeight / 50; })
                        .attr("y2", function(d, i){ return (i + 6.8) * viewerHeight / 50; })
                        .attr("stroke-width", 1.5)
                        .attr("stroke", function(d){ return d[0]; })
                        .attr("stroke-dasharray", function(d){ return  d[1]; });
		    //tooltips and help                                                                
		    div = d3.select("#tree-container").append("div")
                        .attr("class", "tooltip")
                        .style("opacity", 0),
		    div2 = d3.select("#tree-container").append("div")
                        .attr("class", "tooltip2")
                        .style("opacity", 0);
		    div3 = d3.select("#tree-container").append("div")
                        .attr("class", "tooltip3")
                        .style("opacity", 0);
                    helpString = "Search the etymology of a word: enter a word in the search tab, then press Enter or click on one of the words in the drop-down menu. <br><br>In this demo only a small number of words is available (try words 'wiki', 'etymology', 'door', 'butter', 'latte', 'milk', 'coffee', 'web'; see what happens if you try word '_languages_tree') <br> We are currently working on a version that can use the etymology of any words in wiktionary.";
		    d3.select("#p-helpPopup").html(helpString);
            }
	});
}

var myHistory = [],
h = -1;
$(function() {
    var availableTags = [
        "wiki",
        "WikiWikiWeb",
        "World Wide Web",
        "wikiwiki",
        "etymology",
        "door",
        "butter",
        "latte",
        "milk",
        "coffee",
        "caffè",
        "caffè latte",
        "_languages_tree",
        "web"
    ];
    var accentMap = {
	    'ẚ':'a',
	    'Á':'a',
	    'á':'a',
	    'À':'a',
	    'à':'a',
	    'Ă':'a',
	    'ă':'a',
	    'Ắ':'a',
	    'ắ':'a',
	    'Ằ':'a',
	    'ằ':'a',
	    'Ẵ':'a',
	    'ẵ':'a',
	    'Ẳ':'a',
	    'ẳ':'a',
	    'Â':'a',
	    'â':'a',
	    'Ấ':'a',
	    'ấ':'a',
	    'Ầ':'a',
	    'ầ':'a',
	    'Ẫ':'a',
	    'ẫ':'a',
	    'Ẩ':'a',
	    'ẩ':'a',
	    'Ǎ':'a',
	    'ǎ':'a',
	    'Å':'a',
	    'å':'a',
	    'Ǻ':'a',
	    'ǻ':'a',
	    'Ä':'a',
	    'ä':'a',
	    'Ǟ':'a',
	    'ǟ':'a',
	    'Ã':'a',
	    'ã':'a',
	    'Ȧ':'a',
	    'ȧ':'a',
	    'Ǡ':'a',
	    'ǡ':'a',
	    'Ą':'a',
	    'ą':'a',
	    'Ā':'a',
	    'ā':'a',
	    'Ả':'a',
	    'ả':'a',
	    'Ȁ':'a',
	    'ȁ':'a',
	    'Ȃ':'a',
	    'ȃ':'a',
	    'Ạ':'a',
	    'ạ':'a',
	    'Ặ':'a',
	    'ặ':'a',
	    'Ậ':'a',
	    'ậ':'a',
	    'Ḁ':'a',
	    'ḁ':'a',
	    'Ⱥ':'a',
	    'ⱥ':'a',
	    'Ǽ':'a',
	    'ǽ':'a',
	    'Ǣ':'a',
	    'ǣ':'a',
	    'Ḃ':'b',
	    'ḃ':'b',
	    'Ḅ':'b',
	    'ḅ':'b',
	    'Ḇ':'b',
	    'ḇ':'b',
	    'Ƀ':'b',
	    'ƀ':'b',
	    'ᵬ':'b',
	    'Ɓ':'b',
	    'ɓ':'b',
	    'Ƃ':'b',
	    'ƃ':'b',
	    'Ć':'c',
	    'ć':'c',
	    'Ĉ':'c',
	    'ĉ':'c',
	    'Č':'c',
	    'č':'c',
	    'Ċ':'c',
	    'ċ':'c',
	    'Ç':'c',
	    'ç':'c',
	    'Ḉ':'c',
	    'ḉ':'c',
	    'Ȼ':'c',
	    'ȼ':'c',
	    'Ƈ':'c',
	    'ƈ':'c',
	    'ɕ':'c',
	    'Ď':'d',
	    'ď':'d',
	    'Ḋ':'d',
	    'ḋ':'d',
	    'Ḑ':'d',
	    'ḑ':'d',
	    'Ḍ':'d',
	    'ḍ':'d',
	    'Ḓ':'d',
	    'ḓ':'d',
	    'Ḏ':'d',
	    'ḏ':'d',
	    'Đ':'d',
	    'đ':'d',
	    'ᵭ':'d',
	    'Ɖ':'d',
	    'ɖ':'d',
	    'Ɗ':'d',
	    'ɗ':'d',
	    'Ƌ':'d',
	    'ƌ':'d',
	    'ȡ':'d',
	    'ð':'d',
	    'É':'e',
	    'Ə':'e',
	    'Ǝ':'e',
	    'ǝ':'e',
	    'é':'e',
	    'È':'e',
	    'è':'e',
	    'Ĕ':'e',
	    'ĕ':'e',
	    'Ê':'e',
	    'ê':'e',
	    'Ế':'e',
	    'ế':'e',
	    'Ề':'e',
	    'ề':'e',
	    'Ễ':'e',
	    'ễ':'e',
	    'Ể':'e',
	    'ể':'e',
	    'Ě':'e',
	    'ě':'e',
	    'Ë':'e',
	    'ë':'e',
	    'Ẽ':'e',
	    'ẽ':'e',
	    'Ė':'e',
	    'ė':'e',
	    'Ȩ':'e',
	    'ȩ':'e',
	    'Ḝ':'e',
	    'ḝ':'e',
	    'Ę':'e',
	    'ę':'e',
	    'Ē':'e',
	    'ē':'e',
	    'Ḗ':'e',
	    'ḗ':'e',
	    'Ḕ':'e',
	    'ḕ':'e',
	    'Ẻ':'e',
	    'ẻ':'e',
	    'Ȅ':'e',
	    'ȅ':'e',
	    'Ȇ':'e',
	    'ȇ':'e',
	    'Ẹ':'e',
	    'ẹ':'e',
	    'Ệ':'e',
	    'ệ':'e',
	    'Ḙ':'e',
	    'ḙ':'e',
	    'Ḛ':'e',
	    'ḛ':'e',
	    'Ɇ':'e',
	    'ɇ':'e',
	    'ɚ':'e',
	    'ɝ':'e',
	    'Ḟ':'f',
	    'ḟ':'f',
	    'ᵮ':'f',
	    'Ƒ':'f',
	    'ƒ':'f',
	    'Ǵ':'g',
	    'ǵ':'g',
	    'Ğ':'g',
	    'ğ':'g',
	    'Ĝ':'g',
	    'ĝ':'g',
	    'Ǧ':'g',
	    'ǧ':'g',
	    'Ġ':'g',
	    'ġ':'g',
	    'Ģ':'g',
	    'ģ':'g',
	    'Ḡ':'g',
	    'ḡ':'g',
	    'Ǥ':'g',
	    'ǥ':'g',
	    'Ɠ':'g',
	    'ɠ':'g',
	    'Ĥ':'h',
	    'ĥ':'h',
	    'Ȟ':'h',
	    'ȟ':'h',
	    'Ḧ':'h',
	    'ḧ':'h',
	    'Ḣ':'h',
	    'ḣ':'h',
	    'Ḩ':'h',
	    'ḩ':'h',
	    'Ḥ':'h',
	    'ḥ':'h',
	    'Ḫ':'h',
	    'ḫ':'h',
	    'H':'h',
	    '̱':'h',
	    'ẖ':'h',
	    'Ħ':'h',
	    'ħ':'h',
	    'Ⱨ':'h',
	    'ⱨ':'h',
	    'Í':'i',
	    'í':'i',
	    'Ì':'i',
	    'ì':'i',
	    'Ĭ':'i',
	    'ĭ':'i',
	    'Î':'i',
	    'î':'i',
	    'Ǐ':'i',
	    'ǐ':'i',
	    'Ï':'i',
	    'ï':'i',
	    'Ḯ':'i',
	    'ḯ':'i',
	    'Ĩ':'i',
	    'ĩ':'i',
	    'İ':'i',
	    'i':'i',
	    'Į':'i',
	    'į':'i',
	    'Ī':'i',
	    'ī':'i',
	    'Ỉ':'i',
	    'ỉ':'i',
	    'Ȉ':'i',
	    'ȉ':'i',
	    'Ȋ':'i',
	    'ȋ':'i',
	    'Ị':'i',
	    'ị':'i',
	    'Ḭ':'i',
	    'ḭ':'i',
	    'I':'i',
	    'ı':'i',
	    'Ɨ':'i',
	    'ɨ':'i',
	    'Ĵ':'j',
	    'ĵ':'j',
	    'J':'j',
	    'ǰ':'j',
	    'ȷ':'j',
	    'Ɉ':'j',
	    'ɉ':'j',
	    'ʝ':'j',
	    'ɟ':'j',
	    'ʄ':'j',
	    'Ḱ':'k',
	    'ḱ':'k',
	    'Ǩ':'k',
	    'ǩ':'k',
	    'Ķ':'k',
	    'ķ':'k',
	    'Ḳ':'k',
	    'ḳ':'k',
	    'Ḵ':'k',
	    'ḵ':'k',
	    'Ƙ':'k',
	    'ƙ':'k',
	    'Ⱪ':'k',
	    'ⱪ':'k',
	    'Ĺ':'a',
	    'ĺ':'l',
	    'Ľ':'l',
	    'ľ':'l',
	    'Ļ':'l',
	    'ļ':'l',
	    'Ḷ':'l',
	    'ḷ':'l',
	    'Ḹ':'l',
	    'ḹ':'l',
	    'Ḽ':'l',
	    'ḽ':'l',
	    'Ḻ':'l',
	    'ḻ':'l',
	    'Ł':'l',
	    'ł':'l',
	    'Ł':'l',
	    'ł':'l',
	    'ƚ':'l',
	    'Ⱡ':'l',
	    'ⱡ':'l',
	    'Ɫ':'l',
	    'ɫ':'l',
	    'ɬ':'l',
	    'ɭ':'l',
	    'ȴ':'l',
	    'Ḿ':'m',
	    'ḿ':'m',
	    'Ṁ':'m',
	    'ṁ':'m',
	    'Ṃ':'m',
	    'ṃ':'m',
	    'ɱ':'m',
	    'Ń':'n',
	    'ń':'n',
	    'Ǹ':'n',
	    'ǹ':'n',
	    'Ň':'n',
	    'ň':'n',
	    'Ñ':'n',
	    'ñ':'n',
	    'Ṅ':'n',
	    'ṅ':'n',
	    'Ņ':'n',
	    'ņ':'n',
	    'Ṇ':'n',
	    'ṇ':'n',
	    'Ṋ':'n',
	    'ṋ':'n',
	    'Ṉ':'n',
	    'ṉ':'n',
	    'Ɲ':'n',
	    'ɲ':'n',
	    'Ƞ':'n',
	    'ƞ':'n',
	    'ɳ':'n',
	    'ȵ':'n',
	    'N':'n',
	    'n':'n',
	    'Ó':'o',
	    'ó':'o',
	    'Ò':'o',
	    'ò':'o',
	    'Ŏ':'o',
	    'ŏ':'o',
	    'Ô':'o',
	    'ô':'o',
	    'Ố':'o',
	    'ố':'o',
	    'Ồ':'o',
	    'ồ':'o',
	    'Ỗ':'o',
	    'ỗ':'o',
	    'Ổ':'o',
	    'ổ':'o',
	    'Ǒ':'o',
	    'ǒ':'o',
	    'Ö':'o',
	    'ö':'o',
	    'Ȫ':'o',
	    'ȫ':'o',
	    'Ő':'o',
	    'ő':'o',
	    'Õ':'o',
	    'õ':'o',
	    'Ṍ':'o',
	    'ṍ':'o',
	    'Ṏ':'o',
	    'ṏ':'o',
	    'Ȭ':'o',
	    'ȭ':'o',
	    'Ȯ':'o',
	    'ȯ':'o',
	    'Ȱ':'o',
	    'ȱ':'o',
	    'Ø':'o',
	    'ø':'o',
	    'Ǿ':'o',
	    'ǿ':'o',
	    'Ǫ':'o',
	    'ǫ':'o',
	    'Ǭ':'o',
	    'ǭ':'o',
	    'Ō':'o',
	    'ō':'o',
	    'Ṓ':'o',
	    'ṓ':'o',
	    'Ṑ':'o',
	    'ṑ':'o',
	    'Ỏ':'o',
	    'ỏ':'o',
	    'Ȍ':'o',
	    'ȍ':'o',
	    'Ȏ':'o',
	    'ȏ':'o',
	    'Ơ':'o',
	    'ơ':'o',
	    'Ớ':'o',
	    'ớ':'o',
	    'Ờ':'o',
	    'ờ':'o',
	    'Ỡ':'o',
	    'ỡ':'o',
	    'Ở':'o',
	    'ở':'o',
	    'Ợ':'o',
	    'ợ':'o',
	    'Ọ':'o',
	    'ọ':'o',
	    'Ộ':'o',
	    'ộ':'o',
	    'Ɵ':'o',
	    'ɵ':'o',
	    'Ṕ':'p',
	    'ṕ':'p',
	    'Ṗ':'p',
	    'ṗ':'p',
	    'Ᵽ':'p',
	    'Ƥ':'p',
	    'ƥ':'p',
	    'P':'p',
	    'p':'p',
	    'ẗ':'t',
	    'Ṫ':'t',
	    'ṫ':'t',
      	    'W':'w',
	    'ẘ':'w',
	    'Ẅ':'w',
	    'Ẅ':'w',
	    'ẅ':'w',
	    'Ẇ':'w',
	    'ẇ':'w',
	    'Ẉ':'w',
	    'ẉ':'w',
	    'Ẍ':'x',
	    'ẍ':'x',
	    'Ẋ':'x',
	    'ẋ':'x',
	    'Ý':'y',
	    'ý':'y',
	    'Ỳ':'y',
	    'ỳ':'y',
	    'Ŷ':'y',
	    'ŷ':'y',
	    'Y':'y',
	    'ẙ':'y',
	    'Ÿ':'y',
	    'ÿ':'y',
	    'Ỹ':'y',
	    'ỹ':'y',
	    'Ẏ':'y',
	    'ẏ':'y',
	    'Ȳ':'y',
	    'ȳ':'y',
	    'Ỷ':'y',
	    'ỷ':'y',
	    'Ỵ':'y',
	    'ỵ':'y',
	    'ʏ':'y',
	    'Ɏ':'y',
	    'ɏ':'y',
	    'Ƴ':'y',
	    'ƴ':'y',
	    'Ź':'z',
	    'ź':'z',
	    'Ẑ':'z',
	    'ẑ':'z',
	    'Ž':'z',
	    'ž':'z',
	    'Ż':'z',
	    'ż':'z',
	    'Ẓ':'z',
	    'ẓ':'z',
	    'Ẕ':'z',
	    'ẕ':'z',
	    'Ƶ':'z',
	    'ƶ':'z',
	    'Ȥ':'z',
	    'ȥ':'z',
	    'ʐ':'z',
	    'ʑ':'z',
	    'Ⱬ':'z',
	    'ⱬ':'z',
	    'Ǯ':'z',
	    'ǯ':'z',
            'ƺ':'z'
    };
	var normalize = function( term ) {
            var ret = "";
            for ( var i = 0; i < term.length; i++ ) {
                ret += accentMap[ term.charAt(i) ] || term.charAt(i);
            }
            return ret;
        };
	function split(val) {
	    return val.split(/,\s*/);
	}

	function extractLast(term) {
	    return split(term).pop();
	}
    $( "#tags" ).autocomplete({
        //select first option in dropdown menu when pushing return    
        autoFocus: true,
	source: function(request, response) {
	        var matcher = new RegExp( $.ui.autocomplete.escapeRegex( extractLast(request.term) ), "i" );
		response( $.grep( availableTags, function( value ) {
		        value = value.truc || value.value || value;
			return matcher.test( value ) || matcher.test( normalize( value ) );
		    }) 
                    );
	    },
	    //source: availableTags,
        select: function(event, ui) {
	    h++;
            $(this).val(ui.item.label); 
            loadTreeFromFile(ui.item.value);
            myHistory[h] = ui.item.value;        
            myHistory.splice(h + 1, myHistory.length - 1);
            return false;
        }
    });

    $("#go-forward").click(function(){
        if (h === myHistory.length - 1){
        } else {
	    h++;
            $("#tags").val(myHistory[h]);
            loadTreeFromFile(myHistory[h]);
        }      
    })

    $("#go-back").click(function(){
        if (h == 0){
        } else {
	    h --;
            $("#tags").val(myHistory[h]);
            loadTreeFromFile(myHistory[h]);  
        }
    })
}) 