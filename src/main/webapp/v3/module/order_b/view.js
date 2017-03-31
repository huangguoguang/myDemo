define(['text!module/order_b/tpl.html'], function (tpl) {
	var D = {};
    var View1 = Backbone.View.extend({
        el: '.container',
        initialize: function () {
			this.model.on('change:list', this.listShow, this);
        },
		events: {
		},
        render: function () {
            this.$el.html(tpl);
        },
        listData : [],
        pageIscroll : null,
        listShow: function () {
			var o= this,
				$list = $(".order-box .box",o.$el),
				listHtml = '';
			
			o.listData = o.listData.concat(o.model.get("list").rows);
			
			$.each(o.model.get("list").rows,function(v,k){
				listHtml += '\
					<dl id="'+ k.order_id +'">\
						<dt>\
							<ul>\
								<li>'+ k.item_name_alias +'</li>\
								<li><span>' + k.buy_unit.replace(/kg|桶|m³/g,"件")+ '</span></li>\
							</ul>\
						</dt>\
						<dd class="info">\
						<img src="' + itemImage[k.buy_number] +'" />\
						<ul>\
							<li><i>'+ (k.buy_type==='buy' ? '订货价':'赊货价') +'</i><span>￥'+ Number(k.buy_point).toFixed(k.buy_number==='13'?3:0) +'</span></li>\
							<li><i>数量</i><span>'+ k.buy_amount +'</span></li>\
							<li><i>订货金</i><span>￥'+ (k.buy_price * k.buy_amount) +'</span></li>\
							<li><i>尾款</i><span>￥'+ k.balance_payment +'</span></li>\
							<li><i>仓储费</i><span>￥'+ k.buy_brokerage +'</span></li>\
						</ul>\
					</dd>\
					<dd class="btn">\
					<div>'+ (new Date(k.buy_time)).Format("MM-dd hh:mm:ss")  +'</div>\
					<ul>\
						<li hash="order_sh/1/'+ k.shopping_order_id +'">查看订单</li>\
					</ul>\
					</dd>\
					</dl>';
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
											o.model.fetch(o.model.get("list").pageNo+1);
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
        }
		
    });
    return View1;
});