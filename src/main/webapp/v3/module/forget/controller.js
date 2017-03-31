define(['module/forget/view'], function (View) {
    var controller = function(code,count) {
        var view = new View({
	        model:{
	        	code : code,
	        	count : count || 0
	        }
        });
        view.render();

        controller.onRouteChange = function () {
            view.undelegateEvents();
        };
    };
    return controller;
});