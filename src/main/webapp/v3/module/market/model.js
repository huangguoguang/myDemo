define([],function(){
	var Model2 = Backbone.Model.extend({
		defaults : function (){
			return {
				bannerImg : "skin/images/banner.jpg",
				userInfo : {},
				itemInfo : [],
				listLoad : 0
			};
		},
		fetch : function (){
			var o = this;
			if(appData.itemInfo.length === 0){
				$.ajax({
					url : serverUrl + 'trade/itemInfo',
					type : 'post',
					data : {},
					dataType : 'json',
					success : function (data){
						appData.itemInfo = data;
						o.set({
							listLoad : new Date()
						});
					}
				});
			} else {
				o.set({
					listLoad : new Date()
				});
			}
			
			if(isNullObj(appData.userInfo)){
				$.ajax({
					url : serverUrl + "user/main",
					type : "post",
					data : {},
					dataType : 'json',
					success : function(data){
						log("userInfo:"+data);
						appData.userInfo = data;
						o.set({
							userInfo : appData.userInfo
						});
					}
				});
			} else {
				o.set({
					userInfo : appData.userInfo
				});
			}
		}
	});
	return Model2;
});