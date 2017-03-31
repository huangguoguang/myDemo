define(['text!module/me_info/tpl.html'], function (tpl) {
	var D = {};
    var View1 = Backbone.View.extend({
        el: '.container',
        initialize: function () {
        	this.model.on('change:userInfo', this.userInfo, this);
        },
		events: {
		},
        render: function () {
            this.$el.html(tpl);
        },
        userInfo : function(){
        	var $this = $(".me-info-box li",this.$el);
        	if(appData.userInfo.headimgurl){
        		$this.eq(0).find("i").html('<img src="'+ appData.userInfo.headimgurl +'">');
        	}
        	$this.eq(1).find("i").html(appData.userInfo.user_name);
        	$this.eq(2).find("i").html(appData.userInfo.user_mobile);
        	$this.eq(3).find("i").html(appData.userInfo.user_login_id);
        }
    });
    return View1;
});