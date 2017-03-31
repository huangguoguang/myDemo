(function(win){
	win.appData = {
		//开户调试打印模式
		isDebug : isTest,
		//风格
		cssId : function(){
			return 3;
		},
		//记住登录成功的用户名
		userId : function(){
			if(localStorage.getItem("userId") !== null) {
				return localStorage.getItem("userId");
			} else {
				return '';
			}
		},
		//商品信息
		itemInfo : []
	}
	
	win.log = function(m){
		if (!appData.isDebug) {
			return false;
		}
		if(window.console && window.console.log){
			window.console.log(m);
		}
	}
	
	win.isNullObj = function(obj){
		for(var key in obj) {
			return false;
		}
		return true;
	}
	
	win.initHeight = function(obj) {
		var getHeight = $(".container").outerHeight();
		obj.sibling("div").each(function(){
			getHeight -= $(this).outerHeight(true);
		});
		obj.height(getHeight);
	}
	//文件依赖
	var config = {
		baseUrl : baseUrl,//依赖相对路径
		paths : {//如果某个前缀的依赖不是按照baseUrl拼接这么简单，就需要在这里指出
			iscroll : 'libs/iscroll',
			highstock : 'libs/highstock',
			backbone : 'libs/backbone',
			jquery : 'libs/jquery-2.1.4.min',
			text : 'libs/text',//用于requirejs导入html类型的依赖
			underscore : 'libs/underscore'
		},
		shim : {//引入没有使用requirejs模板写法的类库，backbone依赖underscore
			'underscore' : {
				exports : '_'
			},
			'jquery' : {
				exports : '$'
			},
			'highstock' : {
				deps : ['jquery'],
				exports : 'Highcharts'
			},
			'backbone' : {
				deps : ['underscore','jquery'],
				exports : 'Backbone'
			}
		}
	};

	require.config(config);

	//Backbone会把自己加到全局变量中
	require(['iscroll','backbone','underscore','libs/msgbox','router'],function(){
		$("head [name = 'css']").attr("href",baseUrl + 'skin/' + appData.cssId()+'/css/css.css');
		
		//全局监控
		$(document).on('ajaxComplete',function(e,xhr,options){
			if(xhr.status === 401) {
				alert(401);
			}
			if(xhr.status === 400 || xhr.status === 403 || xhr.status === 404 || xhr.status ===500){
				alert("系统错误");
			}
		})
		.on("click","[hash]",function(){
			var $this = $(this);
			var hash = $this.attr("hash");
			if(hash && (hash.indexOf("http://") > -1 || hash.indexOf("https://") > -1)){
				location.href = hash;
			} else {
				Rt.navigate(hash, {
					trigger : true,
					replace : false
				});
			}
		});
		Backbone.history.start();//开始监控url变化
	});

	
})(window);