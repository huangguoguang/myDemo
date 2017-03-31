defint(['module/market/model','module/market/view','text!module/common/footer.html'],function(Model,View,Footer){
	var model = new Model();
	var view = new View({
		model : model
	});
	view.render();
	model.fetch();
	
	controller.onRouteChange = funtion(){
		view.undelegateEvents();
	};
	
	controller.footer = Footer;
	controller.className = 'market';
	
	return controller;
});