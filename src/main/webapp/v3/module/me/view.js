define(['text!module/me/tpl.html'],function(tpl){
	var View1 = Backbone.View.extend({
		el : '.container',
		initialize : function(){
			this.model.on('change:userInfo',this.userInfo,this);
		},
		events : {
			'click .i_logout': 'logout',
		},
		render : function(){
			this.$el.html(_.template(tpl));
			initHeight($('.iscroll'));
            this.pageIscroll = new IScroll(".iscroll");
		},
		userInfo : function(){
			if(appData.userInfo.headimgurl){
				$('.user-img .img',this.$el).html('<img src = "'+appData.userInfo.headimgurl+'">');
			}
			$('.user-name',this.$el).html(appData.userInfo.user_name);
			$('.user-money i',this.$el).html(parseFloat(appData.userInfo.user_money)+'元，收支明细&raquo;');
			$(".i_pwd",this.$el).attr("hash","forget/"+appData.userInfo.agent_code);
			$(".i_pay",this.$el).attr("hash",appData.userInfo.pay_url);
			if(appData.userInfo.important_msg_list.length>0){
        		$(".i_message span",this.$el).append("<s>"+ appData.userInfo.important_msg_list.length +"</s>");
        	}
			var o = this;
			setTimeout(function(){
    			o.pageIscroll.refresh();
			}, 100);
		},
		logout:function(){
			MSG.C('确定要退出登录吗？','系统提示',{t:'取消',f:function(){
				MSG.X();
			}},{t:'确定',f:function(){
				MSG.C('退出中');
	            $.ajax({
					url : serverUrl+'user/loginOut',
					type : "post",
					timeout : 10000,
					data :{},
					dataType:"json",
					success:function(data){
						if(data.success){
							MSG.C(data.msg,'系统提示',{t:'确认',f:function(){
					        	Rt.navigate('login/'+appData.userInfo.agent_code, {
					        	    trigger: true
					        	});
								appData.userInfo ={};
							}});
						}
					}
				});
			}});
		}
	});
	return View1;
});