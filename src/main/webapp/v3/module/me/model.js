define([], function () {
    var Model2 = Backbone.Model.extend({
        defaults: function () {
            return {};
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
    					log(data);
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
//            
//            $.ajax({
//				url : serverUrl+'trade/badOrderAmount',
//				type : "post",
//				timeout : 10000,
//				data :{},
//				dataType:"json",
//				success:function(data){
//	                o.set({
//	                	badOrderCount : data
//	                });
//				}
//			});
            
        }

    });
    return Model2;
});