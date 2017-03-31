define([], function () {
    var Model2 = Backbone.Model.extend({
        defaults: function () {
            return {
            };
        },
        fetch: function () {
            var o = this;
            if(isNullObj(appData.userInfo)){
                $.ajax({
    				url : serverUrl+'user/main',
    				type : "post",
    				timeout : 10000,
    				data :{},
    				dataType:"json",
    				success:function(data){
    					appData.userInfo = data;
    	                o.set({
    	                	userInfo : appData.userInfo
    	                });
    				}
    			});
            }else{
                o.set({
                	userInfo : appData.userInfo
                });
            }
        }

    });
    return Model2;
});