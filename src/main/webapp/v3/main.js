(function(win){
	win.appData = {
		//开户调试打印模式
		isDebug : isTest,
		//数据调试接口
		wsUrl : wsUrl,
		wsUrl_13 : wsUrl_13,
		//拉取默认值或者localStorage值
		GETVAL: function(n,m){
			if(localStorage.getItem(n)!==null){
				return parseInt(localStorage.getItem(n));
			}else{
				return m;
			}
		},
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
		//行情图类型
		lineIndex : function(){
			return this.GETVAL("lineIndex",0);
		},
		//图表类型列表
		lineList : [-1,1,5,15,30,60,0],
		//商品信息
		itemInfo : [],
		//用户信息
		userInfo :[]
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
	
	win.Zdie = function(a,b) {
		if(a>b){return 'up';}
		if(a<b){return 'down';}
		if(a==b){return '';}
	}
	
	win.xDec = function(a,b){
		if(b===2){
			return (Math.floor(a * 100) / 100);
		}
		if(b===1){
			return (Math.floor(a * 10) / 10);
		}
	}
	
	win.initHeight = function(obj) {
		var getHeight = $(".container").outerHeight();
		obj.siblings("div").each(function(){
			getHeight -= $(this).outerHeight(true);
		});
		obj.height(getHeight);
	}
	
	// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
	// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
	Date.prototype.Format = function (fmt) {
	    var o = {
	        "M+": this.getMonth() + 1, //月份 
	        "d+": this.getDate(), //日 
	        "h+": this.getHours(), //小时 
	        "m+": this.getMinutes(), //分 
	        "s+": this.getSeconds(), //秒 
	        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
	        "S": this.getMilliseconds() //毫秒 
	    };
	    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	    for (var k in o)
	    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	    return fmt;
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
			underscore : 'libs/underscore',
			websocket: 'libs/reconnecting-websocket'
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
	require(['iscroll','websocket','backbone','underscore','libs/msgbox','router'],function(IScroll,WS){
		$("head [name = 'css']").attr("href",baseUrl + 'skin/' + appData.cssId()+'/css/css.css');
		
		function isPassive() {
            var supportsPassiveOption = false;
            try {
                addEventListener("test", null, Object.defineProperty({}, 'passive', {
                    get: function () {
                        supportsPassiveOption = true;
                    }
                }));
            } catch(e) {}
            return supportsPassiveOption;
        }
		
		//屏蔽触摸下拉
    	document.addEventListener('touchmove', function(e){e.preventDefault()}, isPassive() ? {
    		capture: false,
    		passive: false
    	} : false);
    	
    	new IScroll('.body',{preventDefault:false,bounce:false,bclick:false});
    	
		
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
		
		//交易中订单总数量
		appData.userLot = -1;
		appData.userLotGet = function(){
			$.ajax({
				url : serverUrl + 'trade/totalOrder',
				type : 'post',
				data : {},
				dataType :'json',
				success:function(data){
					if(data >0){
						appData.userLot = data;
						if($(".footer .order s").length > 0){
							$(".footer .order s").text(data);
						} else {
							$(".footer .order").append('<s>'+data+'</s>');
						}
					} else {
						appData.userLot = 0;
						$(".footer .order s").remove();
					}
				}
			});
		};
		appData.userLotGetNum = function(data){
			if(data>0){
				appData.userLot = data;
				if($(".footer .order s").length>0){
					$(".footer .order s").text(data);
				}else{
					$(".footer .order").append('<s>'+ data +'</s>');
				}
				
			}else{
				appData.userLot = 0;
				$(".footer .order s").remove();
			}
		};
		//userInfo
		appData.userInfoGet = function(){
            $.ajax({
				url : serverUrl+'user/main',
				type : "post",
				timeout : 10000,
				data :{},
				dataType:"json",
				success:function(data){
					data.user_money = parseFloat((data.user_money*1).toFixed(2));
					appData.userInfo = data;
					$(".user-money",this.$el).html(appData.userInfo.user_money +'元');
				}
            });
		};
		//滑动控件
		win.IScroll = IScroll;
		
		//推送重连
		win.ReconnectingWebSocket = WS;
		
		Backbone.history.start({pubState:true});//开始监控url变化
	});

	
})(window);