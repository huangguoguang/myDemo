define([], function () {
    var Model2 = Backbone.Model.extend({
        defaults: function () {
            return {};
        },
        fetch: function (lx,id) {
			var o = this;
			if(lx==='1'){
	            $.ajax({
					url : serverUrl+'trade/getGoodDetail',
					type : "post",
					data :{
						shopping_order_id:id
					},
					dataType:"json",
					timeout :10000,
					success:function(data) {
						var _tmpData = data;
						data_ = {data:_tmpData,time:new Date()};
						log(data_);
		                o.set({
		                	orderData : data_
		                });
					}
				});
			}else{
	            $.ajax({
					url : serverUrl+'trade/getbuyOrderById',
					type : "post",
					data :{
						order_id:id
					},
					dataType:"json",
					timeout :10000,
					success:function(data) {
						var _tmpData = data;
						data_ = {data:_tmpData,time:new Date()};
						log(data_);
		                o.set({
		                	orderData : data_
		                });
					}
				});
			}
			
        }

    });
    return Model2;
});