function remark(el,type){
	var cssSample = '{"background-attachment":"scroll","background-clip":"border-box","background-color":"rgba(0, 0, 0, 0)","background-image":"none","background-origin":"padding-box","background-position":"0% 0%","background-repeat":"repeat","background-size":"auto","border-bottom-color":"rgb(0, 0, 0)","border-bottom-left-radius":"0px","border-bottom-right-radius":"0px","border-bottom-style":"none","border-bottom-width":"0px","border-collapse":"separate","border-image-outset":"0px","border-image-repeat":"stretch","border-image-slice":"100%","border-image-source":"none","border-image-width":"1","border-left-color":"rgb(0, 0, 0)","border-left-style":"none","border-left-width":"0px","border-right-color":"rgb(0, 0, 0)","border-right-style":"none","border-right-width":"0px","border-top-color":"rgb(0, 0, 0)","border-top-left-radius":"0px","border-top-right-radius":"0px","border-top-style":"none","border-top-width":"0px","bottom":"auto","box-shadow":"none","box-sizing":"content-box","caption-side":"top","clear":"none","clip":"auto","color":"rgb(0, 0, 0)","cursor":"auto","direction":"ltr","display":"block !important","empty-cells":"show","float":"none","font-family":"STHeiti","font-size":"16px","font-style":"normal","font-variant":"normal","font-weight":"normal","height":"0px","image-rendering":"auto","left":"auto","letter-spacing":"normal","line-height":"normal","list-style-image":"none","list-style-position":"outside","list-style-type":"disc","margin-bottom":"0px","margin-left":"0px","margin-right":"0px","margin-top":"0px","max-height":"none","max-width":"none","min-height":"0px","min-width":"0px","opacity":"1","orphans":"auto","outline-color":"rgb(0, 0, 0)","outline-style":"none","outline-width":"0px","overflow-wrap":"normal","overflow-x":"visible","overflow-y":"visible","padding-bottom":"0px !important","padding-left":"0px !important","padding-right":"0px !important","padding-top":"0px !important","page-break-after":"auto","page-break-before":"auto","page-break-inside":"auto","pointer-events":"auto","position":"static","resize":"none","right":"auto","speak":"normal","table-layout":"auto","tab-size":"8","text-align":"start","text-decoration":"none !important","text-indent":"0px","text-rendering":"auto","text-shadow":"none","text-overflow":"clip","text-transform":"none","top":"auto","unicode-bidi":"normal","vertical-align":"baseline","visibility":"visible","white-space":"normal","widows":"auto","width":"1264px","word-break":"normal","word-spacing":"0px","word-wrap":"normal","z-index":"auto","zoom":"1","-webkit-animation-delay":"0s","-webkit-animation-direction":"normal","-webkit-animation-duration":"0s","-webkit-animation-fill-mode":"none","-webkit-animation-iteration-count":"1","-webkit-animation-name":"none","-webkit-animation-play-state":"running","-webkit-animation-timing-function":"cubic-bezier(0.25, 0.1, 0.25, 1)","-webkit-appearance":"none","-webkit-backface-visibility":"visible","-webkit-background-clip":"border-box","-webkit-background-composite":"source-over","-webkit-background-origin":"padding-box","-webkit-background-size":"auto","-webkit-border-fit":"border","-webkit-border-horizontal-spacing":"0px","-webkit-border-image":"none","-webkit-border-vertical-spacing":"0px","-webkit-box-align":"stretch","-webkit-box-decoration-break":"slice","-webkit-box-direction":"normal","-webkit-box-flex":"0","-webkit-box-flex-group":"1","-webkit-box-lines":"single","-webkit-box-ordinal-group":"1","-webkit-box-orient":"horizontal","-webkit-box-pack":"start","-webkit-box-reflect":"none","-webkit-box-shadow":"none","-webkit-clip-path":"none","-webkit-color-correction":"default","-webkit-column-break-after":"auto","-webkit-column-break-before":"auto","-webkit-column-break-inside":"auto","-webkit-column-axis":"auto","-webkit-column-count":"auto","-webkit-column-gap":"normal","-webkit-column-progression":"normal","-webkit-column-rule-color":"rgb(0, 0, 0)","-webkit-column-rule-style":"none","-webkit-column-rule-width":"0px","-webkit-column-span":"none","-webkit-column-width":"auto","-webkit-filter":"none","-webkit-align-content":"stretch","-webkit-align-items":"stretch","-webkit-align-self":"stretch","-webkit-flex-basis":"auto","-webkit-flex-grow":"0","-webkit-flex-shrink":"1","-webkit-flex-direction":"row","-webkit-flex-wrap":"nowrap","-webkit-justify-content":"flex-start","-webkit-font-kerning":"auto","-webkit-font-smoothing":"auto","-webkit-font-variant-ligatures":"normal","-webkit-grid-columns":"none","-webkit-grid-rows":"none","-webkit-grid-column":"auto","-webkit-grid-row":"auto","-webkit-highlight":"none","-webkit-hyphenate-character":"auto","-webkit-hyphenate-limit-after":"auto","-webkit-hyphenate-limit-before":"auto","-webkit-hyphenate-limit-lines":"no-limit","-webkit-hyphens":"manual","-webkit-line-align":"none","-webkit-line-box-contain":"block inline replaced","-webkit-line-break":"auto","-webkit-line-clamp":"none","-webkit-line-grid":"none","-webkit-line-snap":"none","-webkit-locale":"auto","-webkit-margin-before-collapse":"collapse","-webkit-margin-after-collapse":"collapse","-webkit-marquee-direction":"auto","-webkit-marquee-increment":"6px","-webkit-marquee-repetition":"infinite","-webkit-marquee-style":"scroll","-webkit-mask-box-image":"none","-webkit-mask-box-image-outset":"0px","-webkit-mask-box-image-repeat":"stretch","-webkit-mask-box-image-slice":"0 fill","-webkit-mask-box-image-source":"none","-webkit-mask-box-image-width":"auto","-webkit-mask-clip":"border-box","-webkit-mask-composite":"source-over","-webkit-mask-image":"none","-webkit-mask-origin":"border-box","-webkit-mask-position":"0% 0%","-webkit-mask-repeat":"repeat","-webkit-mask-size":"auto","-webkit-nbsp-mode":"normal","-webkit-order":"0","-webkit-perspective":"none","-webkit-perspective-origin":"632px 0px","-webkit-print-color-adjust":"economy","-webkit-rtl-ordering":"logical","-webkit-shape-inside":"auto","-webkit-shape-outside":"auto","-webkit-tap-highlight-color":"rgba(0, 0, 0, 0.4)","-webkit-text-combine":"none","-webkit-text-decorations-in-effect":"none !important","-webkit-text-emphasis-color":"rgb(0, 0, 0)","-webkit-text-emphasis-position":"over","-webkit-text-emphasis-style":"none","-webkit-text-fill-color":"rgb(0, 0, 0)","-webkit-text-orientation":"vertical-right","-webkit-text-security":"none","-webkit-text-stroke-color":"rgb(0, 0, 0)","-webkit-text-stroke-width":"0px","-webkit-transform":"none","-webkit-transform-origin":"632px 0px","-webkit-transform-style":"flat","-webkit-transition-delay":"0s","-webkit-transition-duration":"0s","-webkit-transition-property":"all","-webkit-transition-timing-function":"cubic-bezier(0.25, 0.1, 0.25, 1)","-webkit-user-drag":"auto","-webkit-user-modify":"read-only","-webkit-user-select":"text","-webkit-writing-mode":"horizontal-tb","-webkit-flow-into":"none","-webkit-flow-from":"none","-webkit-region-overflow":"auto","-webkit-region-break-after":"auto","-webkit-region-break-before":"auto","-webkit-region-break-inside":"auto","-webkit-app-region":"no-drag","-webkit-wrap-flow":"auto","-webkit-shape-margin":"0px","-webkit-shape-padding":"0px","-webkit-wrap-through":"wrap","clip-path":"none","clip-rule":"nonzero","mask":"none","filter":"none","flood-color":"rgb(0, 0, 0)","flood-opacity":"1","lighting-color":"rgb(255, 255, 255)","stop-color":"rgb(0, 0, 0)","stop-opacity":"1","color-interpolation":"srgb","color-interpolation-filters":"linearrgb","color-rendering":"auto","fill":"#000000","fill-opacity":"1","fill-rule":"nonzero","marker-end":"none","marker-mid":"none","marker-start":"none","mask-type":"luminance","shape-rendering":"auto","stroke":"none","stroke-dasharray":"none","stroke-dashoffset":"0","stroke-linecap":"butt","stroke-linejoin":"miter","stroke-miterlimit":"4","stroke-opacity":"1","stroke-width":"1","alignment-baseline":"auto","baseline-shift":"baseline","dominant-baseline":"auto","kerning":"0","text-anchor":"start","writing-mode":"lr-tb","glyph-orientation-horizontal":"0deg","glyph-orientation-vertical":"auto","-webkit-svg-shadow":"none","vector-effect":"none;"}';
	cssSample = eval("("+cssSample+")");
	if (type == 1) {
		var $dom = $("."+el);
		var dom = $dom.get(0);
	} else if (type == 0) {
		var $dom = $("#"+el);
		var dom = $dom.get(0);
	}
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
		}
	}
	
	var $dom2 = addattr(dom);
	var iframe = "<iframe frameborder='1' id='bad' style='background-color: white; position: absolute; width: 672px; height: 400px; top: 0; left: -370px; margin-top: 50px; margin-left: 50%;'></iframe>";
	document.getElementsByTagName("body")[0].innerHTML += iframe;
	document.getElementById("bad").contentDocument.getElementsByTagName("body")[0].appendChild($dom2.get(0).cloneNode(true));
}

var template = [{"site":"www.36kr.com","element":"mainContent","type":"1"},{"site":"www.cnbeta.com","element":"news_content","type":"0"}];

for (var i=0; i<template.length; ++i){
	if (template[i].site == window.location.host) {
		remark(template[i].element,template[i].type);
		break;
	} else if (i == template.length-1)
		alert("Template says NO!");
}