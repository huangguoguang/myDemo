define([], function () {
    var Model2 = Backbone.Model.extend({
        defaults: function () {
            return {
				
            };
        },
        fetch: function (page,day) {
			var o = this;
            $.ajax({
				url : serverUrl+'trade/sellOrder',
				type : "post",
				data :{page:page,sell_time:day},
				dataType:"json",
				timeout :10000,
				success:function(data) {
					log(data);
					data.time = new Date();
	                o.set({
	                	list : data
	                });
				}
			});
			
        }

    });
    return Model2;
});