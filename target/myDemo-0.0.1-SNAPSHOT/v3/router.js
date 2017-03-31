define([ 'backbone' ], function() {
	var moduleBase = baseUrl + "module/";
	var routesList = {
		'login/:agent_code/:count' : moduleBase + 'login/controller.js',
		'reg/:name' : moduleBase + 'reg/controller.js',
		'list' : moduleBase + 'list/controller.js'
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
		log(route);
		log(params);
		if (route.substr(route.length - 3, 3) == '.js') {
			MSG.X();//转换模块时先关闭弹窗
			route = route.replace('.js','').replace(moduleBase,'module/');
			log(route);
			require([ route ], function(controller) {
				var $footer = $("footer.footer");
				var $container = $(".container");
				var $body = $(".body");
				if(controller.footer){
					$footer.show().html(controller.footer).find('.'+controller.className).closest("li").addClass('on').siblings().removeClass('on');
					$container.height($(window).height() - $footer.height());
					$body.height($(window).height());
				}else{
					$footer.hide().html('');
					$container.height($(window).height());
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