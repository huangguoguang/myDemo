define([ 'backbone' ], function() {
	var moduleBase = baseUrl + "module/";
	var routesList = {
		'login/:agent_code(/:count)' : moduleBase + 'login/controller.js',
		'market' : moduleBase + "market/controller.js",
		'market_b':moduleBase + 'market_b/controller.js',
		'order' : moduleBase + 'order/controller.js',
		'order_b' : moduleBase + 'order_b/controller.js',
		'order_c' : moduleBase + 'order_c/controller.js',
		'order_buy/:id' : moduleBase + 'order_buy/controller.js',//支付尾款
		'goods/:id_1/:id_2/:id_3/:fx' :  moduleBase + 'goods/controller.js',//商品详细信息
		'order_sh/:lx/:id': moduleBase + 'order_sh/controller.js',//订单确认收货
		
		'trade_records' : moduleBase + 'trade_records/controller.js',
		'me' : moduleBase + 'me/controller.js',
		'me_info':moduleBase +'me_info/controller.js',
		'message(/:id)':moduleBase +'message/controller.js',
		'rule' : moduleBase + 'rule/controller.js',
		'forget/:agent_code' : moduleBase + 'forget/controller.js'
	};

	var Router = Backbone.Router.extend({
		routes : routesList,
		defaultAction : function(a) {
			console.log('404');
		}
	});

	var router = new Router();
	window.Rt = router;

	router.on('route', function(route, params) {
		// route mybatisDemo/v3/module/login/controller.js
		log("router.on"+route);
		log(params);
		if (route.substr(route.length - 3, 3) == '.js') {
			MSG.X();//转换模块时先关闭弹窗
			route = route.replace('.js','').replace(moduleBase,'module/');
			log("弹窗关闭跳转"+route);
			require([ route ], function(controller) {
				var $footer = $("footer.footer");
				var $container = $(".container");
				var $body = $(".body");
				if(controller.footer){
					log("footer.show");
					$footer.show().html(controller.footer).find('.'+controller.className).closest("li").addClass('on').siblings().removeClass('on');
					$container.height($(window).height() - $footer.height());
					$body.height($(window).height());
				}else{
					log("footer.hide");
					$footer.hide().html('');
					$container.height($(window).height());
				}
				//首次进入加载footer上订单数量
				if(controller.footer){
					if(appData.userLot === -1){
						appData.userLotGet();
					}else {
						if(appData.userLot>0){
							if($(".footer .order s").length >0){
								$(".footer .order s").text(appData.userLot);
							} else {
								$(".footer .order").append('<s>'+appData.userLot+'</s>');
							}
						}
					}
				}
				router.currentController && router.currentController.onRouteChange && router.currentController.onRouteChange();

				router.currentController = controller;
				controller.apply(null, params); // 每个模块约定都返回controller
			});
		}

		log('hash change', arguments);// 这里route是路由对应的方法名
	});

	return router; // 这里必须的，让路由表执行
});