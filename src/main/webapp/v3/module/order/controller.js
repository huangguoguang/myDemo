define(['module/order/model', 'module/order/view','text!module/common/footer.html'], function (Model, View, Footer) {
    var controller = function() {
        var model = new Model();
      
        var view = new View({model:model});
        view.render();
        model.fetch();

        controller.onRouteChange = function () {
        	if(typeof ws !== 'undefined'){
				ws.close();
				delete window.ws;
			}
        	if(typeof ws13 !== 'undefined'){
				ws13.close();
				delete window.ws13;
			}
        	clearInterval(window.countdown);
        	log("change");
            view.undelegateEvents();
        };
    };
	controller.footer = Footer;
	controller.className = 'order';
    return controller;
});