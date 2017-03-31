define(['text!module/trade_records/tpl.html'], function (tpl) {
	var D = {};
    var View1 = Backbone.View.extend({
        el: '.container',
        initialize: function () {
			this.model.on('change:list', this.listShow, this);
        },
		events: {
			//'click .order-records-box .box li': 'detailShow'
		},
        render: function () {
            this.$el.html(tpl);
        },
        listData : [],
        pageIscroll : null,
        listShow: function () {
			var o= this,
				$list = $(".order-records-box .box ul",o.$el),
				listHtml = '';
			
			o.listData = o.listData.concat(o.model.get("list").rows);
			
			$.each(o.model.get("list").rows,function(v,k){
				var c = parseFloat(k.sell_profit_loss.toFixed(2));
				var type_text = '';
				if(k.trading_rule === '01' || k.trading_rule === '02' || k.trading_rule === '03'){
					type_text = '购赊';
				}
				if(k.trading_rule === '04'){
					type_text = '众筹';
				}
				if(k.status === '3'){
					type_text = '购物';
				}
				
				listHtml += '<li hash="order_sh/'+ (k.status === '3'?1:0) +'/'+ (k.shopping_order_id ? k.shopping_order_id:k.order_id) + '"><span>'+ type_text +'<i>'+ (new Date(k.sell_time)).Format("MM-dd hh:mm:ss") +'</i></span><span class="'+ (c>0?'up':(c===0?'':'down')) +'">￥'+ (c>0?'+':'') +''+ c +'</span></li>';
			});	
			
			$list.append(listHtml);
			
			if(o.model.get("list").total===0){
				$list.parent().append('<div class="end none">记录数为0</div>');
			}else{
				if(o.model.get("list").totalPage === o.model.get("list").pageNo){
					$list.parent().append('<div class="end">---- 到底了 ----</div>');
				}
			}
			
			$(".loadnext").remove();
			
			if(o.model.get("list").pageNo > 1){
				setTimeout(function(){
					o.pageIscroll.refresh();//刷新滚动区域
				}, 100);
			}else{
				MSG.X();
				//首次载入需要初始化
				initHeight($('.iscroll'));
				o.pageIscroll = new IScroll('.iscroll', { probeType: 3, mouseWheel: true });
				//当页数大于1页
				if(o.pageIscroll){
					setTimeout(function(){				
						o.pageIscroll.on('scroll', function () {
							if(this.y > 30 && this.y < 101){
								if($(".slideDown").length===0){
									$('.scroller').css("position","relative").prepend("<div class='slideDown tc' style='position: absolute;top:-30px;left:0;right:0;line-height:30px;transition:all 1s ease 0s;opacity:0.5;'>继续下拉刷新</div>");
								}else
								{
									$(".slideDown").css({"top":"-30px"}).text("继续下拉刷新");	
								}
							}
							if(this.y > 100){
								$(".slideDown").css({"top":"-100px"}).text("放开刷新");
							}
						});
						
						o.pageIscroll.on("slideDown",function(){
							//当下拉，使得边界超出时，如果手指从屏幕移开，则会触发该事件
							if(this.y > 100){
								MSG.C("加载中");
								setTimeout(function(){
									o.pageIscroll.destroy();
									o.listData = [];
									$list.html('');
									$(".end").remove();
									o.model.fetch(1);
								},1000);
							}
							$(".slideDown").remove();
						});
						
						//如果总页数大于1，加载下一页
						if(o.model.get("list").totalPage > 1){
							o.pageIscroll.on('scrollEnd', function () {
								//判断是否是最后一页
								if(o.model.get("list").totalPage > o.model.get("list").pageNo){
									if ( this.y < this.maxScrollY + 1 ) {
										if($(".loadnext").length===0){
											$list.parent().append('<div class="loadnext end">加载中...</div>');
										}
										if(o.loadnext){
											clearTimeout(o.loadnext);
										}
										o.loadnext = setTimeout(function(){
											o.model.fetch(o.model.get("list").pageNo+1,o.day);
										}, 1000);
										o.pageIscroll.refresh();//刷新滚动区域
									}
								}
							});
						}
						o.pageIscroll.on('scrollStart', function () {
							$(":focus").blur();
						});
					}, 100);
				}
			}
        },
		detailShow: function(e){
			var $this = $(e.target).closest("li"),
				index = $this.index(),
				k = this.listData[index],
				sell_type = '';
			
				if(k.status === '3'){
		        	Rt.navigate("order_sh/"+ k.shopping_order_id, {
		        	    trigger: true
		        	});
					return false;
				}
			
				switch(k.sell_type){
					case 'manuallySell':
						sell_type = '手动退款';
						break;
					default :
						sell_type = '自动退款';
						break;
				}

				var c = parseFloat(k.sell_profit_loss.toFixed(2));
				html  = '<div class="pop-trade-records">\
							<img src="' + itemImage[k.buy_number] +'" />\
							<ul class="tl">\
								<li><s>数量:</s><i>'+ k.buy_amount +'</i></li>\
								<li><s>'+ (k.buy_type==='buy' ? '订货价':'赊货价') +':</s><i>￥'+ k.buy_point +'</i></li>\
								'+ (k.step==="2" ? '':'<li><s>退款方式:</s><i>'+ sell_type +'</i></li>') +'\
								<li><s>退款价格:</s><i>￥'+ k.sell_point +'</i></li>\
								<li><s>'+ (k.buy_type==="buy" ? '订货金':'赊货金') +':</s><i>￥'+ (k.buy_price * k.buy_amount) +'</i></li>\
								<li><s>仓储费:</s><i>￥'+ k.buy_brokerage +'</i></li>\
							</ul>\
						</div>';
				MSG.C(html,(k.step==="2" ? '【众筹】':(k.buy_type==='buy' ? '【购物】':'【赊货】')) + k.buy_itemname_alias || '订单详情',{t:'确定',f:function(){MSG.X();}});
		}
		
    });
    return View1;
});