define(['text!module/order_c/tpl.html'], function (tpl) {
    var View1 = Backbone.View.extend({
        el: '.container',
        initialize: function () {
        	this.model.on('change:orderData', this.showList, this);
        },
		events: {
		},
		prevPoint : {},
		listData : [],
        render: function () {
        	var o = this;
            o.$el.html(tpl);
            if(appData.ajaxTopdata){appData.ajaxTopdata.abort();}
        },
        showList: function(){
        	MSG.X();
        	var o = this,
        		html = '',
        		html__ = '';
        	o.listData = this.model.get("orderData").data;
        	
            appData.ajaxTopdata = $.ajax({
				url : serverUrl+'trade/topdata',
				type : "post",
				data :{},
				dataType:"json",
				success:function(data) {
					
					$.each(o.listData, function(i,k){
						var point = data[k.buy_number].ws_new_point;
						
						var point_text = "当前点位";
						var point_ = point;
							
						if(k.trading_rule==="04"){
							html__ ='\
								<dd class="info">\
								<img src="' + itemImage[k.buy_number] +'" />\
								<ul>\
									<li><i>'+ (k.buy_type==='buy' ? '订货价':'赊货价') +'</i><span>￥'+ k.buy_point.toFixed(k.buy_number==='13'?3:0) +'</span></li>\
									<li><i>现价</i><span price="'+ k.buy_number +'">￥'+ (point==='…'?point:Number(point).toFixed(k.buy_number==='13'?3:0)) +'</span></li>\
									<li><i>数量</i><span>'+ k.buy_amount +'</span></li>\
									<li><i>'+ (k.buy_type==='buy' ? '订货金':'赊货金') +'</i><span>￥'+ (k.buy_price * k.buy_amount) +'</span></li>\
									<li><i>仓储费</i><span>￥'+ k.buy_brokerage +'</span></li>\
								</ul>\
							</dd>\
							';
							
							//建仓中的单子
							if(k.confirm==="2"){
								html__ += '\
									<dd class="unknown">\
										<ul>\
											<li>系统处理中,请稍后刷新查看</li>\
										</ul>\
									</dd>\
								';
							}else{
								html__ += '\
									<dd class="btn">\
										<div>众筹时间：'+ (new Date(k.buy_time)).Format("MM-dd hh:mm:ss")  +'</div>\
										<ul>\
										</ul>\
									</dd>\
								';
							}
						}
						
						
						html +='\
							<dl id="'+ k.order_id +'">\
								<dt>\
									<ul>\
										<li>'+ (k.buy_type==='buy' ? '<span class="t_yd">购物</span>':'<span class="t_sh">赊货</span>') +''+ k.buy_itemname_alias +'</li>\
										<li><span>' + k.buy_unit.replace(/kg|桶|m³/g,"件")+ '</span></li>\
									</ul>\
								</dt>\
								'+ html__ +'\
							</dl>';
					});
					
					if(html===''){
						html = html + '<div class="end none">订单数为0</div>';
						$(".order-box .box").html(html);
					}else{
						html = html + '<div class="end">---- 到底了 ----</div>';
						$(".order-box .box").html(html);
						initHeight($('.iscroll'));
						if(!o.pageIscroll){
							setTimeout(function(){
								o.pageIscroll = new IScroll('.iscroll', { probeType: 3, mouseWheel: true });
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
											o.model.fetch();
										},1000);
									}
									$(".slideDown").remove();
								});
			
								o.pageIscroll.on("slideUp",function(){
									if(this.maxScrollY - this.y > 40){
										log("slideUp");
									}
								});
							},300);
						}
						
					}
					
					var onMessage = function(e){
						e = JSON.parse(e.data);
		        		o.prevPoint[e.ws_type] = e;
						$('[price="'+ e.ws_type +'"]').text('￥'+Number(e.ws_new_point).toFixed(e.ws_type==='13'?3:0));
					};
					
					o.prevPoint = data;
					
					ws = new ReconnectingWebSocket(appData.wsUrl +'/websocket/61012');
					ws.onmessage = function(e){
						onMessage(e);
					};
					
					if(!isMimic && isMLM){
						ws13 = new ReconnectingWebSocket(appData.wsUrl_13 +'/websocket/13');
						ws13.onmessage = function(e){
							onMessage(e);
						};
					}
				}
			});
			if(o.pageIscroll){
				setTimeout(function(){
					o.pageIscroll.refresh();//刷新滚动区域
				}, 300);
			}
        	
        }
    });
    return View1;
});