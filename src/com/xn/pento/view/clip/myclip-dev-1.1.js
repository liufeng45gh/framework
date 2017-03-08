function addattr(node){
	if (typeof(node) != "undefined"){
		if (node.children != "undefined"){
			var nodelist = node.children;
			for (var i=0; i<=nodelist.length; ++i){
				addattr(nodelist[i]);
			}
		}
		var $node1 = $(node);
		var style = window.getComputedStyle($node1.get(0));
		var cssCur = '{"' + style.cssText + '"}';
		var reg1 = new RegExp("\:\\s","g");
		var reg2 = new RegExp(";\\s","g")
		cssCur = cssCur.replace(reg1, '":"');
		cssCur = cssCur.replace(reg2, '","');
		cssCur = eval("("+cssCur+")");
		for (var j=0; j<style.length; ++j){
			if (cssCur[style[j]] != cssSample[style[j]]){
				$node1.css(style[j],cssCur[style[j]]);
			}
		}
		return $node1;
	}
}

function remark(dom){
	if (typeof(dom) == "undefined")
		alert("Template says NO!");
	else {
		var $dom2 = addattr(dom);
		var iframe = "<iframe frameborder='1' id='bad' style='background-color: white; position: absolute; width: 672px; height: 400px; top: 0; left: -370px; margin-top: 50px; margin-left: 50%;'></iframe>";
		document.getElementsByTagName("body")[0].innerHTML += iframe;
		document.getElementById("bad").contentDocument.getElementsByTagName("body")[0].appendChild($dom2.get(0).cloneNode(true));	
	}
}

(function lifechoice(domain){
	switch (domain){
		case "www.36kr.com":
			var $dom = $(".mainContent");
			var dom = $dom.get(0);
			remark(dom);
			break;
		case "www.cnbeta.com":
			var $dom = $("#news_content");
			var dom = $dom.get(0);
			remark(dom);
			break;
		default:
			alert("Template says NO!");
			return 0;
	}
	return 1;
})(window.location.host);