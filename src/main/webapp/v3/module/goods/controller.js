define(['module/goods/model', 'module/goods/view'], function (Model, View) {
    var controller = function (id_1,id_2,id_3,fx) {
        var model = new Model();
			model.set({
				id_1:id_1,
				id_2:id_2,
				id_3:id_3,
				fx:fx
			});
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
            log('change');
            view.undelegateEvents();
        };
    };
	
	controller.footer = '';
	controller.className = 'market';

    return controller;
});