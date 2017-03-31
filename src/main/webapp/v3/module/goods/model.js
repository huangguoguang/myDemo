define([], function () {
    var Model2 = Backbone.Model.extend({
        defaults: function () {
            return {
            	itemInfo : [],
            	listLoad : 0
            };
        },
        fetch: function () {
            var o = this;
            if(appData.itemInfo.length === 0){
                $.ajax({
    				url : serverUrl+'trade/itemInfo',
    				type : "post",
    				timeout : 10000,
    				data :{},
    				dataType:"json",
    				success:function(data){
    					appData.itemInfo = data;
    	                o.set({
    	                	listLoad : new Date()
    	                });
    				}
    			});
            }else{  
                o.set({
                	listLoad : new Date()
                });
            }
            
        }

    });
    return Model2;
});