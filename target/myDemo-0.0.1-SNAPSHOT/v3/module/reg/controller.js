define(['module/reg/view'],function(View){
	var controller = function(name){
		var view = new View({
			model : {
				name : name
			}
		});
		view.render();
		controller.onRouteChange = function(){
			view.undeleteEvents();
		};
	};
	return controller;
});