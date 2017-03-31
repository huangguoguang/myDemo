define(['text!module/forget/tpl.html'], function (tpl) {
    var View1 = Backbone.View.extend({
        el: '.container',
        initialize: function () {
        	
        },
		events: {
			'click .submit': 'loginSubmit',
			'click .code s:not(".off")': 'getCode',
			'click .change_get_code': 'getCodeVoice',
			'click .input': 'removeError'
		},
		
        render: function () {
        	this.$el.html(_.template(tpl, {agent_code:this.model.code,count:this.model.count}));
        },

        loginSubmit: function(){  
        	var agent_code = this.model.code;
			this.$('.error').remove();
			var status = 0
				,$name = this.$('[name="login_name"]')
	         	,$password = this.$('[name="user_password"]')
	         	,$password_2 = this.$('[name="user_password_2"]')
	         	,$code = this.$('[name="validate_code"]')
	         	,name = $.trim($name.val())
	         	,password = $.trim($password.val())
	         	,password_2 = $.trim($password_2.val())
	         	,code = $.trim($code.val());
			if(!/^[^\s]{6,16}$/.test(name)){
				$name.prev().append("<u class='error'>帐号为手机号或6-16位字符</u>");
				status = 1;
			}
			
			if(!/^[^\s]{4,4}$/.test(code) && $('.code', this.$el).is(":visible")){
				$code.prev().append("<u class='error'>验证码应为4位字符</u>");
				status = 1;
			}
			
			if(!/^[^\s]{6,16}$/.test(password)){
				$password.prev().append("<u class='error'>密码应为6-16位字符</u>");
				status = 1;
			}
			
			if(!/^[^\s]{6,16}$/.test(password_2) || password_2!= password){
				$password_2.prev().append("<u class='error'>重复输入的密码不同</u>");
				status = 1;
			}

			if(status){
				return false;
			}
			
			MSG.C('提交中');
            $.ajax({
				url : serverUrl+'user/forgetPassword',
				type : "post",
				data :{
					agent_code : agent_code,
					user_password:password,
					login_name:name,
					validate_code:code
				},
				dataType:"json",
				success:function(data) {
					setTimeout(function(){
						if(data.success){
							MSG.C(data.msg,'系统提示',{t:'确定',f:function(){
					        	Rt.navigate('login/'+ agent_code, {
					        	    trigger: true
					        	});
							}});
						}else{
							MSG.C(data.msg,'系统提示',{t:'确定',f:function(){
								MSG.X();
							}});
						}
					},2500);
				}
			});
		},
		
		msgType:'msg',
		
		getCodeVoice: function(e){
			this.msgType = 'voice';
			this.$('.code s:not(".off")').trigger("click");
		},
		
        getCode: function(e){
			this.$('.error').remove();
			var status = 0
				,sec = 60
				,$name = this.$('[name="login_name"]')
	         	,name = $.trim($name.val())
	         	,$this = $(e.target);
			
			if(!/^[^\s]{6,16}$/.test(name)){
				$name.prev().append("<u class='error'>账号应为6-16位数字</u>");
				status = 1;
			}

			if(status){
				return false;
			}
			
			$this.addClass("off").text(sec+'秒');
			var delay = setInterval(function(){
				sec--;
				$this.text(sec+'秒');
				if(sec<=0){
					clearInterval(delay);
					$this.removeClass("off").text("获取验证码");
				}
			},1000);
			
			MSG.C("发送中");
            $.ajax({
				url : serverUrl+'authMum/getLoginMsgCode',
				type : "post",
				data :{
					validate_type:this.msgType,
					login_name:name
				},
				dataType:"json",
				success:function(data) {
					MSG.C(data.msg,'系统提示',{t:'确定',f:function(){
						MSG.X();
					}});
					if(!data.success){
						clearInterval(delay);
						$this.removeClass("off").text("获取验证码");
					}
				}
			});

			this.msgType = 'msg';
		},
		removeError: function(e){
			var $this = $(e.target).closest(".input:not(.code)").find("input").focus();
			this.$('.error').remove();
		}
    });
    return View1;
});