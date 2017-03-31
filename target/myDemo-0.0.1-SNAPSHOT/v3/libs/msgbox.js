define([], function() {
	var app ={
		C: function(b,t,c,o){
			this.X();
			var arg = arguments.length,
				f = '',
				html = '';
			if(arg==4){
				f = '<div>'+ c.t +'</div><div>'+ o.t +'</div>'
			}else if(arg==3){
				f = '<div class="only">'+ c.t +'</div>';
			}
			if(!(/<div/i.test(b))){
				b = '<div class="msg">'+ b +'</div>';	
			}
			
			if(f){
				html = '\
				<div class="maskbg">\
					<div class="msgbox">\
						<header>'+ t +'</header>\
						<article>'+ b +'</article>\
						<footer>'+ f +'</footer>\
					</div>\
				</div>';
			}else{
				html = '\
				<div class="maskbg">\
					<article class="load7"><div class="loader"></div>'+ b +'</article>\
				</div>';	
			}
			
			$(".body").append(html);
			
			if(c && c.f){
				$(".maskbg footer div").eq(0).click(function(){
					c.f();
				});
			}
			if(o && o.f){
				$(".maskbg footer div").eq(1).click(function(){
					o.f();
				});
			}
			
		},
		X:function(){
			$(".maskbg").remove();
		}
	};
	window.MSG = app;
});