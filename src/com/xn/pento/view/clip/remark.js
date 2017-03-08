(function() {
	var jsfiles = new Array(
            "jquery-1.5.1.min.js",
            "csstemplate.js",
            "myclip.js"
        ); 

	var agent = navigator.userAgent;
	var docWrite = (agent.match("MSIE") || agent.match("Safari"));
	if(docWrite) {
	    var allScriptTags = new Array(jsfiles.length);
	}
	var host = "http://192.168.1.17/clippage/";    //文件的所在目录
	for (var i=0, len=jsfiles.length; i<len; i++) {
        var s = document.createElement("script");
        s.src = host + jsfiles[i];
        var h = document.getElementsByTagName("head").length ? 
                   document.getElementsByTagName("head")[0] : 
                   document.body;
        h.appendChild(s);
	}

})();