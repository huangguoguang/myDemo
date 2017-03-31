define([], function () {
    var Model2 = Backbone.Model.extend({
        defaults: function () {
            return {};
        },
        fetch: function () {
			var o = this;		
            $.ajax({
				url : serverUrl+'trade/buyOrder',
				type : "post",
				data :{},
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

    });
    return Model2;
});