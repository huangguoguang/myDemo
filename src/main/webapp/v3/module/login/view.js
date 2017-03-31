define(['text!module/login/tpl.html'],function(tpl){
	var View1 = Backbone.View.extend({
		el : '.container',
		events: {
			'click .submit' : 'loginSubmit'
		},
		initialize : function(){

		},
		render : function(){
			this.$el.html(_.template(tpl,{agent_code:this.model.agent_code,count:this.model.count,userId:appData.userId(),img:'<img src = "'+ baseUrl + 'skin/3/images/login-top.jpg">'}));
		},
		loginSubmit : function (){
			log(appData.userId());
			var agent_code = this.model.agent_code;
			var username = this.$("input[name = 'login_name']").val();
			var password = this.$("input[name = 'user_password']").val();
			MSG.C("登录中");
			$.ajax({
				url : serverUrl + 'user/login',
				type : 'post',
				data :{
					agent_code : agent_code,
					username : username,
					password : password
				},
				dataType : 'json',
				success : function(data) {
					setTimeout(function(){
						if(data.success){
							localStorage.setItem("userId",username);
							MSG.C(data.msg,"系统提示",{t:'确定',f:function(){
								Rt.navigate('market',{
									trigger:true
								});
							}});
						} else {
							MSG.C(data.msg,"系统提示",{t:'确定',f:function(){
								Rt.navigate('login/'+agent_code,{
									trigger:true
								});
								MSG.X();
							}});
						}
					},(/Android/i.test(navigator.userAgent)) ? 2500 : 1500);
				}
			});
		}
	});
	return View1;
});