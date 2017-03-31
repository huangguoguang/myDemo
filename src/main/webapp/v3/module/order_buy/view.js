define(['text!module/order_buy/tpl.html'], function (tpl) {
    var View1 = Backbone.View.extend({
        el: '.container',
        initialize: function () {
        	this.model.on('change:orderData', this.showList, this);
        },
		events: {
			'click .back': function(){history.back();}
		},
		prevPoint : {},
		listData : [],
        iscrollBox: null,
		thisData : null,
        render: function () {
        	var o = this;
            o.$el.html(tpl);
			initHeight($('.iscroll'));
			setTimeout(function(){
				o.iscrollBox = new IScroll('.iscroll', {mouseWheel: true });
			},200);
        },
        address:null,
        showList: function(){
        	MSG.X();
        	var o = this;
        	
        	o.thisData = this.model.get("orderData").data;
		
            $.ajax({
				url : serverUrl+'addr/getDefaultAddr',
				type : "post",
				data :{},
				dataType:"json",
				timeout :10000,
				success:function(data) {
					if(data.success === true){
						o.address = data.data;
						$('.order-buy-box .address').html('\
							<ul>\
								<li>收货人：'+ data.data.receiver_name +' <span>'+ data.data.receiver_mobile +'</span></li>\
								<li>收货地址：'+ data.data.receiver_addr +' </li>\
							</ul>');
					}else{
						$('.order-buy-box .address').html('\
							<ul>\
								<li class="null">设置收货地址</li>\
							</ul>');
					}
				}
			});
            
			$('.order-buy-box .detail').html('\
					<img src="' + itemImage[o.thisData.buy_number] +'" />\
					<ul>\
					<li>'+ o.thisData.buy_itemname_alias +'<span>' + o.thisData.buy_unit.replace(/kg|桶|m³/g,"件")+ '</span></li>\
					<li>商品数量<span>* ' + o.thisData.buy_amount +'</span></li>\
			</ul>');
           
			$('.order-buy-box .status').html('\
					<ul>\
					<li>阶段1：已完成</li>\
					<li>商品订金<span>￥'+ (o.thisData.buy_price * o.thisData.buy_amount) +'</span></li>\
				</ul>\
				<ul>\
					<li>阶段2：尾款支付</li>\
					<li>支付尾款<span>￥'+ xDec((o.thisData.buy_point*1000 - o.thisData.buy_price*1000) * o.thisData.buy_amount/1000,2) +'</span></li>\
			</ul>');
			
			$(".order-buy-btn .total i").html(o.thisData.buy_point - o.thisData.buy_price * o.thisData.buy_amount);
			

            $(document).off("click.d");
            $(document).on("click.d",".order-buy-btn",function(){
            	if(o.address===null){
            		MSG.C('请先设置收货人信息？','系统提示',{t:'确定',f:function(){MSG.X();}});
            		return false;
            	}
            	MSG.C('确认要支付吗？','系统提示',{t:'取消',f:function(){MSG.X();}},{t:'确定',f:function(){
                    $.ajax({
        				url : serverUrl+'trade/paymentTail',
        				type : "post",
        				data :{
        					order_id:o.thisData.order_id,
        					address:o.address.receiver_addr,
        					mobile:o.address.receiver_mobile,
        					name:o.address.receiver_name
        				},
        				dataType:"json",
        				timeout :10000,
        				success:function(data) {
        					if(data.success === true){
        						MSG.C(data.msg,'系统提示',{t:'查看订单',f:function(){
						        	Rt.navigate("order_b", {
						        	    trigger: true
						        	});
								}},{t:'确定',f:function(){
						        	Rt.navigate("order", {
						        	    trigger: true
						        	});
        						}});
        					}else{
        						MSG.C(data.msg,'系统提示',{t:'确定',f:function(){MSG.X();}});
        					}
        				}
        			});
            	}});
            });
        }
    });
    return View1;
});