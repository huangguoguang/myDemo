define(['text!module/reg/tpl.html'],function(tpl){
	var View = Backbone.View.extend({
		el : '#container',
		events : {
			'click .submit' : 'regSubmit'
		},
		initialize : function(){
			
		},
		render : function(){
			this.$el.html(_.template(tpl,{name : this.model.name}));
		},
		regSubmit : function(){
			var name = this.model.name
			var username = this.$("input[name = 'name']").val();
			var password = this.$("input[name = 'password']").val();
			$.ajax({
				url : serverUrl + "user/regist",
				type : 'post',
				data : {
					username : username,
					password : password
				},
				dataType : "json",
				success : function(data){
					if(data.success) {
						Rt.navigate('login/'+name,{
							trigger : true
						});
					}else {
						Rt.navigate('reg/'+name,{
							trigger : true
						})
					}
				}
			});
		}
	});
	return View;
});