define(['module/order_buy/model', 'module/order_buy/view','text!module/common/footer.html'], function (Model, View, Footer) {
    var controller = function(id) {
        var model = new Model();
		var view = new View({model:model});
        view.render();
        model.fetch(id);

        controller.onRouteChange = function () {
        	if(typeof ws !== 'undefined'){
				ws.close();
				delete window.ws;
			}
        	if(typeof ws13 !== 'undefined'){
				ws13.close();
				delete window.ws13;
			}
        	log("change");
            view.undelegateEvents();
        };
    };
	controller.footer = '<div class="order-buy-btn">立即支付</div>';
	controller.className = 'order';
    return controller;
});