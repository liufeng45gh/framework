function addattr(node,nodejq){
	if (typeof(node) != "undefined"){ //判断当前获取到的节点是否为未定义，即判断是否为空节点
		if (typeof(node.nodeType) != "undefined"){ //判断当前获取到的节点类型是否为未定义，即判断是否为正常节点（不是纯文字节点）
			if (node.nodeType == "1"){ //判断当前节点类型是否为元素
				if (typeof(node.hasChildNodes()) != "undefined"){ //判断当前节点元素是否包含子节点
					var nodelist = node.childNodes;
					for (var i=0; i<=nodelist.length; ++i){
						addattr(nodelist[i]);
					}
				}
				//if (nodejq != "undefined"){
				//	var $node1 = $(nodejq);
				//} else {
					var $node1 = $(node);
				//}
				//var node2 = $node1.get(0).cloneNode(true);
				var style = window.getComputedStyle($node1.get(0));
				//alert($node1.css(style[0]));
				//alert(window.getComputedStyle($node1.get(0)));
				//$node1.get(0).style.cssText = window.getComputedStyle($node1.get(0)).cssText;
				var cssCur = '{"' + style.cssText + '"}';
				var reg1 = new RegExp("\:\\s","g");
				var reg2 = new RegExp(";\\s","g")
				cssCur = cssCur.replace(reg1, '":"');
				cssCur = cssCur.replace(reg2, '","');
				cssCur = eval("("+cssCur+")");
				//alert(style.length);
				for (var j=0; j<style.length; ++j){
					//$node1.css(attrs[j],$node1.css(attrs[j]));
					if (cssCur[style[j]] != cssSample[style[j]]){
						$node1.css(style[j],cssCur[style[j]]);
					}
					//$node1.get(0).style.cssText = $node1.get(0).currentStyle;
				}
				//alert(j);
				return $node1;
			}
		}
	}
}

function addattr1(node){
	if (typeof(node) != "undefined"){
		if (node.children != "undefined"){
			var nodelist = node.children;
			for (var i=0; i<=nodelist.length; ++i){
				addattr1(nodelist[i]);
			}
		}
		var $node1 = $(node);
		
		if(node.currentStyle) {
			//IE、Opera
			//alert("我支持currentStyle");
			var style = $node1.get(0).currentStyle;
		} else {
			//FF、chrome、safari
			//alert("我不支持currentStyle");
			var style = window.getComputedStyle($node1.get(0));
		}
		
		
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
	//var divSample = document.createElement("div");
	//divSample.id = "divSamplePento";
	//document.getElementsByTagName("body")[0].appendChild(divSample);
	//var divSample = document.getElementById("divSamplePento");
	//var cssSample = '{"' + window.getComputedStyle(divSample).cssText + '"}';
	//var reg1 = new RegExp("\:\\s","g");
	//var reg2 = new RegExp(";\\s","g")
	//cssSample = cssSample.replace(reg1, '":"');
	//cssSample = cssSample.replace(reg2, '","');
	//alert(cssSample["background-attachment"]);
	//alert(typeof(cssSample));
	//var dom = document.getElementById(el);
	//alert(window.getComputedStyle(dom).cssText);
	//var dom1 = $("#aaa");
	//var attrs = ["color","background-color","font-size","font-family","width","height","max-width","position","top","left","right","bottom","margin-top","margin-left","margin-right","margin-bottom","padding-top","padding-left","padding-right","padding-bottom","list-style","border-radius","box-shadow","display","float"];
	//var dom = getDom(window.location.host);
	if (typeof(dom) == "undefined")
		alert("Template says NO!");
	else {
		var $dom2 = addattr1(dom);
		var screenWidth = document.body.offsetWidth;
		var screenHeight = document.body.offsetHeight;
		var iframe = "<div id='mask' style='width: "+screenWidth+"px; height: "+screenHeight+"px; background-color: rgba(0,0,0,0.5); position: fixed; z-index: 99999; top: 0; display: none;'><iframe frameborder='1' id='bad' style='background-color: white; position: fixed; z-index: 100000; width: 672px; height: 400px; top: 0; left: -370px; margin-top: 50px; margin-left: 50%;'></iframe></div>";
		document.getElementsByTagName("body")[0].innerHTML += iframe;
		document.getElementById("bad").contentDocument.getElementsByTagName("body")[0].appendChild($dom2.get(0).cloneNode(true));
		$("#mask").fadeIn(1000);
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
		case "www.dgtle.com":
			var $dom = $("#v-article");
			var dom = $dom.get(0);
			remark(dom);
			break;
		default:
			alert("Template says NO!");
	}
})(window.location.host);