var IsRelease=0,
	defImgArr=[["normal","arrow_folded"],["down","arrow_expanded"]];
$(function(){	
	limitHeight("web_content",12,1.5,10,1);
	$("a#readmord").click(function(){
		if(IsRelease==0){releaseHeight("web_content");IsRelease=1}
		else{limitHeight("web_content",12,1.5,10,1);IsRelease=0;};	
	});
	$("a#fold").click(function(){
		if(IsRelease==0){releaseHeight("web_content");IsRelease=1}
		else{limitHeight("web_content",12,1.5,10,1);IsRelease=0;};	
	});
	$("div#web_content").click(function(){
		if(IsRelease==0){releaseHeight("web_content");IsRelease=1}
		else{limitHeight("web_content",12,1.5,10,1);IsRelease=0;};	
	});
	$("div.web_listoption").click(function(){
		var _this=$(this),
			IsShow=_this.next().is(":visible");
		if(IsShow==true){_this.next().slideUp();_this.find(("div.web_right img")).attr("src","img/"+defImgArr[0][1]+".png")}
		else{_this.next().slideDown();_this.find(("div.web_right img")).attr("src","img/"+defImgArr[1][1]+".png")};
	});
	var _isSlide = false;
	$("div.keywords_list,div.author_list,div.domain_list,div.drug_list,div.obesity_list").click(function(){
		if(_isSlide){
			_isSlide = false;
			return;
		}
		var _this=$(this),
			IsShow=_this.prev().is(":visible");
		if(IsShow==true){_this.slideUp();_this.prev().find(("div.web_right img")).attr("src","img/"+defImgArr[0][1]+".png")}
		else{_this.slideDown();_this.prev().find(("div.web_right img")).attr("src","img/"+defImgArr[1][1]+".png")};
	});
	$("div.keyword_button").click(function(){
		var backStr=$(this).attr("id");
		
        document.location = "gbi::myweb/keyword/"+backStr;
        
        //alert(backStr);
	})

	$("span.clazzurl").click(function(){
		var backStr=$(this).attr("id");
		alert("aaa");
		_isSlide = true;
	})
		
    $("div.drug_button").click(function(){
        var backStr=$(this).attr("id");
                               
        document.location = "gbi::myweb/drug/"+backStr;
                               
        //alert(backStr);
    })
    $("div.clc_button").click(function(){
        var backStr=$(this).attr("id");
                             
        document.location = "gbi::myweb/clc/"+backStr;
                             
                             //alert(backStr);
    })
    $("div.pubmed_category_button").click(function(){
        
        var backStr=$(this).attr("id");
        
        document.location = "gbi::myweb/pubmed_category/"+backStr;
                            
                            //alert(backStr);
    })
    
    $("#subscribe_button").click(function(){
                                        
        var backStr=$(this).attr("journal_info");
        
     //   document.location = "gbi::myweb/journal_subscribe/"+backStr;
	 window.journal_info.journalSubscibe(""+backStr);  
     _isSlide = true;                     
                                        //alert(backStr);
    })
  
})

function setSubscribeButtonStatus(status){
    if(status == "true"){
        $("#subscribe_button").removeClass("unsubscribed").addClass("subscribed");
    }else{
        $("#subscribe_button").removeClass("subscribed").addClass("unsubscribed");
    }
}

function limitHeight(idname,fontSize,lineHeight,line,scale){
	var Heg=fontSize*lineHeight*line*scale,
		obj=document.getElementById(idname),
		objHeg=obj.clientHeight,
		objReadmore=document.getElementById("web_readmore"),
		objFold=document.getElementById("web_fold"),
		finalHeg=0;
		objHeg<=Heg?finalHeg=objHeg:finalHeg=Heg;
		//alert(Heg+"\n"+objHeg);
	$("#"+idname).animate({height:finalHeg+"px"});
	objReadmore.style.display = "block";
	objFold.style.display = "none";
}
function releaseHeight(idname){
	var obj=document.getElementById(idname),
		objReadmore=document.getElementById("web_readmore"),
		objFold=document.getElementById("web_fold"),
		objHeg=obj.clientHeight;
	$("#"+idname).css({"height":"auto"});	
	objReadmore.style.display = "none";
	objFold.style.display = "block";
	
}

function scaleTextSize(idname,fontSize,lineHeight,line,scale){
    if(IsRelease==1){releaseHeight(idname);}
    else{limitHeight(idname,fontSize,lineHeight,line,scale);};
}

function hasClass(element, cls) {
    return (' ' + element.className + ' ').indexOf(' ' + cls + ' ') > -1;
}

function elementFromPointHasClass(x,y,cls){
        
    var element = document.elementFromPoint(x, y);
        
    return hasClass(element,cls);
    
}
