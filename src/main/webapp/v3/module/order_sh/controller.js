define(['module/order_sh/model', 'module/order_sh/view','text!module/common/footer.html'], function (Model, View, Footer) {
    var controller = function(lx,id) {
        var model = new Model();
		model.set({
			id:id,
			lx:lx
		});
		var view = new View({model:model});
        view.render();
        model.fetch(lx,id);

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
	controller.footer = '<div class="order-sh-btn">待发货</div>';
	controller.className = 'order';
    return controller;
});