define(['text!module/market/tpl.html'],function(tpl){
	var View2 = Backbone.view.extend({
		el : '.container',
		events : {
		},
		initialize : function(){
			this.model.on('change:listLoad',this.showList,this);
		},
		reuder : function(){
			var o = this;
			var bannerImg = '<img src="'+ baseUrl + o.model.get("bannerImg") + '"/>';
			this.$el.html(_.template(tpl,{bannerImg:bannerImg}));
		}
	});
	return View2;
});