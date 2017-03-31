define(['module/login/view'],function(View){
	var controller = function(agent_code,count){
		var view = new View({
			model:{
				agent_code : agent_code,
				count : count || 0
			}
		});
		view.render();
		controller.onRouteChange = function(){
			view.undelegateEvents();
		};
	};
	return controller;
});