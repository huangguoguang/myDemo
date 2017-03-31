define(['text!module/order_sh/tpl.html'], function (tpl) {
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
        	o.listData = this.model.get("orderData").data;
        	
        	if(this.model.get("lx") === '1'){
				$('.order-sh-box .address').html('\
						<ul>\
							<li>收货人：'+ o.listData.receiver_name +' </li>\
							<li>收货地址：'+ o.listData.receiver_addr +' </li>\
							<li>联系电话：'+ o.listData.receiver_mobile +' </li>\
						</ul>');
				$('.order-sh-box .detail').html('\
						<dl>\
							<dt>\
								<img src="' + itemImage[o.listData.buy_number] +'" />\
								<ul>\
									<li><i>'+ (o.listData.buy_type==='buy' ? '<span class="t_yd">购物</span>':'<span class="t_sh">赊货</span>') +''+ o.listData.item_name_alias +'</i>' + o.listData.buy_unit.replace(/kg|桶|m³/g,"件")+ '</li>\
									<li><i>￥'+ o.listData.buy_price +'</i>* '+ o.listData.buy_amount +'</li>\
								</ul>\
							</dt>\
							<dd>\
							<ul>\
									<li><i>商品总价</i><s>￥' + (o.listData.amount - o.listData.buy_brokerage) +'</s></li>\
									<li><i>订金</i><s>￥' + (o.listData.buy_price * o.listData.buy_amount) +'</s></li>\
									<li><i>仓储费</i><s>￥' + o.listData.buy_brokerage +'</s></li>\
									<li><i>订金支付时间</i><s>'+ (new Date(o.listData.create_date)).Format("yyyy-MM-dd hh:mm:ss")  +'</s></li>\
									<li><i>尾款支付时间</i><s>'+ (new Date(o.listData.buy_time)).Format("yyyy-MM-dd hh:mm:ss")  +'</s></li>\
							</ul>\
							</dd>\
						</dl>\
				');
				
				$('.order-sh-box .status').html(function(){
					if(o.listData.status === '1'){
						return '买家已付款，待发货';
					}
					if(o.listData.status === '2'){
						return '已发货，待收货';
					}
					if(o.listData.status === '3'){
						return '已收货';
					}
				});
				
				$('.order-sh-box .wl').html(function(){
					if(o.listData.status === '1'){
						return '<b>物流信息</b><br>配货中';
					}
					if(o.listData.status === '2'){
						$(".order-sh-btn").text("确认收货").addClass("order-sh-btn-ok");
						return '<b>物流信息</b><br>承运物流：中通快递<br>运单编号：6655200000212';
					}
					if(o.listData.status === '3'){
						$(".order-sh-btn").text("交易已完成");
						return '<b>物流信息</b><br>承运物流：中通快递<br>运单编号：6655200000212';
					}
				});
				
        	}else{
				$('.order-sh-box .address').html('\
						<ul>\
							<li>收货人：- </li>\
							<li>收货地址：- </li>\
							<li>联系电话：- </li>\
						</ul>');
				$('.order-sh-box .detail').html('\
						<dl>\
							<dt>\
								<img src="' + itemImage[o.listData.buy_number] +'" />\
								<ul>\
									<li><i>'+ (o.listData.step==='2' ? '<span class="t_zc">众筹</span>':'') +''+ (o.listData.buy_type==='buy' ? '<span class="t_yd">购物</span>':'<span class="t_sh">赊货</span>') +''+ o.listData.buy_itemname_alias +'</i>' + o.listData.buy_unit.replace(/kg|桶|m³/g,"件")+ '</li>\
									<li><i>￥'+ o.listData.buy_price +'</i>* '+ o.listData.buy_amount +'</li>\
								</ul>\
							</dt>\
							<dd>\
							<ul>\
									<li><i>'+ (o.listData.buy_type==='buy' ? '购物':'赊货') +'价格</i><s>￥' + o.listData.buy_point +'</s></li>\
									<li><i>'+ (o.listData.buy_type==='buy' ? '购物':'赊货') +'时间</i><s>'+ (new Date(o.listData.buy_time)).Format("yyyy-MM-dd hh:mm:ss")  +'</s></li>\
									<li><i>退款价格</i><s>￥' + o.listData.sell_point +'</s></li>\
									<li><i>退款时间</i><s>'+ (new Date(o.listData.sell_time)).Format("yyyy-MM-dd hh:mm:ss")  +'</s></li>\
									<li><i>仓储费</i><s>￥' + o.listData.buy_brokerage +'</s></li>\
									<li><i>差价</i><s>￥' + parseFloat(o.listData.sell_profit_loss.toFixed(2)) +'</s></li>\
							</ul>\
							</dd>\
						</dl>\
				');
				$('.order-sh-box .status').html(function(){
					return "已退款";
				});
				$('.order-sh-box .wl').remove();
				$(".order-sh-btn").text("交易已完成");
        	}

            $(document).off("click.d");
            $(document).on("click.d",".order-sh-btn-ok",function(){

            	MSG.C('确认已收到货吗？','系统提示',{t:'取消',f:function(){MSG.X();}},{t:'确定',f:function(){
                    $.ajax({
        				url : serverUrl+'trade/confirm',
        				type : "post",
        				data :{
        					order_id:o.listData.shopping_order_id
        				},
        				dataType:"json",
        				timeout :10000,
        				success:function(data) {
        					if(data.success === true){
        						MSG.C(data.msg,'系统提示',{t:'确定',f:function(){
						        	Rt.navigate("order_b", {
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