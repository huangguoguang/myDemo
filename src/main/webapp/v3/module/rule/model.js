define([], function () {
    var Model2 = Backbone.Model.extend({
        defaults: function () {
            return {
				
            };
        },
        fetch: function (page) {
			var o = this;
            $.ajax({
				url : serverUrl+'user/msgCenter',
				type : "post",
				data :{
					center_type:2,
					page:page
				},
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