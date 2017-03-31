define(['text!module/order/tpl.html'], function (tpl) {
    var View1 = Backbone.View.extend({
        el: '.container',
        initialize: function () {
        	this.model.on('change:orderData', this.showList, this);
        },
		events: {
            'click .order-box .btn li': 'viewBtnClick'
		},
		prevPoint : {},
		listData : [],
        render: function () {
        	var o = this;
            o.$el.html(tpl);
            if(appData.ajaxTopdata){appData.ajaxTopdata.abort();}
            appData.ajaxTopdata = $.ajax({
				url : serverUrl+'trade/topdata',
				type : "post",
				data :{},
				dataType:"json",
				success:function(data) {
					var onMessage = function(e){
						e = JSON.parse(e.data);
		        		if(e.ws_new_point !== o.prevPoint[e.ws_type].ws_new_point){

			        		o.prevPoint[e.ws_type] = e;
			        		//更新持仓点位计算盈亏
	
							o.delindex = [];
							
							log([o.listData.length,"推送执行",$('.order-box dl').length]);
							
							$('[price="'+ e.ws_type +'"]').text('￥'+ e.ws_new_point.toFixed(e.ws_type==='13'?3:0));
							
							$.each(o.listData,function(i,k){
								
								if((e.ws_type - k.buy_number)===0){
									var fdyk = '…',
									ykb = '…',
									updown = '',
									fangx = (k.buy_type==='buy')? "1" : "-1",
									$li = $('.order-box dl#'+ k.order_id +' dd li');
									
									if(k.zyzs_confirm!=="3" && k.zyzs_confirm!=="4"){
										//平仓中的订单不写入实时点位
										$li.eq(1).find("span").text('￥'+ e.ws_new_point.toFixed(e.ws_type==='13'?3:0));
									}
									
									if(k.trading_rule==="01"){
										//排除建仓中订单
										if(k.confirm!=="2" && k.zyzs_confirm!=="3" && k.zyzs_confirm!=="4"){
											fdyk = (e.ws_new_point - k.buy_point) * k.gzp_profit * k.buy_amount * fangx;
											if(fdyk>0){updown='up';}
											if(fdyk<0){updown='down';}
											ykb = fdyk/(k.buy_price * k.buy_amount);
											fdyk = '￥' + xDec(fdyk,(e.ws_type==='13'?2:1));
											ykb = parseFloat((ykb*100).toFixed(1)) + '%';
											$li.eq(5).find("span").text(fdyk);
											
											//限盈限损检测
											if((e.ws_new_point - k.gzp_sell_zs_point) * fangx * -1 > -1 || (k.gzp_sell_zy_point > 0 && (e.ws_new_point - k.gzp_sell_zy_point) * fangx > -1)){
												o.delindex.push(k.order_id);
											}
											
										}	
									}
									
									if(k.trading_rule==="02"){
										//排除过期未平仓订单  、建仓中订单
										if(k.time>=0 && k.confirm!=="2"){
											fdyk = ((e.ws_new_point - k.buy_point)>0 ? 1 : ((e.ws_new_point - k.buy_point)===0 ? 0 : -1)) * k.gdsy_buy_ratio/100 * k.buy_price * k.buy_amount * fangx;
											if(fdyk>0) {updown='up';}
											if(fdyk<0) {updown='down';}
											fdyk = '￥' + xDec(fdyk,(e.ws_type==='13'?2:1));
											ykb = ((e.ws_new_point - k.buy_point)>0 ? 1 : ((e.ws_new_point - k.buy_point)===0 ? 0 : -1)) * fangx;
											ykb = ykb * k.gdsy_buy_ratio + '%';
											$li.eq(5).find("span").text(fdyk);
										}
									}
									
									if(k.trading_rule==="03"){
										//排除过期未平仓订单  、建仓中订单
										if(k.time>=0 && k.confirm!=="2"){
											fdyk = ((e.ws_new_point - k.buy_point)>0 ? 1 : ((e.ws_new_point - k.buy_point)===0 ? 0 : -1)) * k.gdsy_buy_ratio/100 * k.buy_price * k.buy_amount * fangx;
											if(fdyk>0) {updown='up';}
											if(fdyk<0) {updown='down';}
											fdyk = '￥' + xDec(fdyk,(e.ws_type==='13'?2:1));
											ykb = ((e.ws_new_point - k.buy_point)>0 ? 1 : ((e.ws_new_point - k.buy_point)===0 ? 0 : -1)) * fangx;
											ykb = ykb * k.gdsy_buy_ratio + '%';
											$li.eq(6).find("span").text(fdyk).attr("class",updown);
											$li.eq(7).find("span").text(ykb).attr("class",updown);
											
											//限盈限损检测
											if((e.ws_new_point - k.gzp_sell_zs_point) <= 0 || (e.ws_new_point - k.gzp_sell_zy_point) >= 0){
												o.delindex.push(k.order_id);
											}
											
										}
									}
								}
							});
							
							if(o.delindex.length>0){
								log("限盈限损清除订单");
								o.nSplice(o.delindex);
							}						
							
		        		}
						//发现需计算盈亏的产品
						if(o.listData.length===0){
				        	if(typeof ws !== 'undefined'){
								ws.close();
								delete window.ws;
							}
				        	if(typeof ws13 !== 'undefined'){
								ws13.close();
								delete window.ws13;
							}
						}
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
            
            appData.userLotGet();
            //更新用户余额等信息
			appData.userInfoGet();
        },
        nSplice: function(id){
        	log('清除订单:'+ id);
        	var o = this;
			var buy_amount = 0;
			var del = [];
			$.each(id, function(ii,kk){
				$.each(o.listData, function(i,k){
					if(k.order_id===kk){
						del.push(i);
						return false;
					}
				});
			});
			
			var tmplistData = [];
			
			$.each(o.listData, function(i,k){
				if($.inArray(i,del) === -1){
					tmplistData.push(k);
				}else{
					$('.order-box dl#'+ k.order_id).remove();
				}
			});
			
			o.listData = tmplistData;
			
			$.each(o.listData, function(i,k){
				buy_amount += k.buy_amount;
			});

			appData.userLotGetNum(o.listData.length);
			//更新用户余额等信息
			appData.userInfoGet();
			
			setTimeout(function(){
    			o.pageIscroll.refresh();
			}, 200);
			
        },
        showList: function(){
        	MSG.X();
        	var o = this,
        		buy_amount = 0,
        		html = '',
        		html__ = '',
        		trading_rule_02 = 0;
        	
        	o.listData = this.model.get("orderData").data;
			$.each(o.listData, function(i,k){
				var point = '…',
					fdyk = '…',
					ykb = '…',
					updown = '',
					fangx = (k.buy_type==='buy')? "1" : "-1";
				
				if(!isNullObj(o.prevPoint)){
					point = o.prevPoint[k.buy_number].ws_new_point;
				}
				
				buy_amount += k.buy_amount;
				
				var point_text = "当前点位";
				var point_ = point;
				
				if(k.trading_rule==="01"){
					if(point!=='…' && k.confirm!=="2"){
						if(k.zyzs_confirm==="3" || k.zyzs_confirm==="4"){
							//平仓中订单 用平仓点位来计算盈亏
							point_text = "平仓点位";
							point_ = k.sell_point;
						}
						fdyk = (point_ - k.buy_point) * k.gzp_profit * k.buy_amount * fangx;
						if(fdyk>0) {updown='up';}
						if(fdyk<0) {updown='down';}
						ykb = fdyk/(k.buy_all_price - k.buy_brokerage);
						fdyk = '￥' +parseFloat(fdyk.toFixed(1));
						ykb = parseFloat((ykb*100).toFixed(1)) + '%';
					}
					html__ ='\
						<dd class="info">\
						<img src="' + itemImage[k.buy_number] +'" />\
						<ul>\
							<li><i>'+ (k.buy_type==='buy' ? '订货价':'赊货价') +'</i><span>￥'+ k.buy_point.toFixed(k.buy_number==='13'?3:0) +'</span></li>\
							<li><i>现价</i><span>￥'+ (point_==='…'?point_:Number(point_).toFixed(k.buy_number==='13'?3:0)) +'</span></li>\
							<li><i>数量</i><span>'+ k.buy_amount +'</span></li>\
							<li><i>'+ (k.buy_type==='buy' ? '订货金':'赊货金') +'</i><span>￥'+ (k.buy_price * k.buy_amount) +'</span></li>\
							<li><i>仓储费</i><span>￥'+ k.buy_brokerage +'</span></li>\
							<li><i>差价</i><span>' + fdyk + '</span></li>\
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
						if(k.zyzs_confirm==="3" || k.zyzs_confirm==="4"){
							html__ += '\
								<dd class="unknown">\
									<ul>\
										<li>系统退款处理中</li>\
									</ul>\
								</dd>\
							';
						}else{
							html__ += '\
								<dd class="btn">\
									<div>'+ (new Date(k.buy_time)).Format("MM-dd hh:mm:ss")  +'</div>\
									<ul>\
										<li class="c_zyzs">商品设置</li><li hash="order_buy/'+ k.order_id +'">支付尾款</li><li class="c_tk">申请退款</li>\
									</ul>\
								</dd>\
							';
						}
					}
				}
					
				if(k.trading_rule==="02"){
					k.time = parseInt(k.sell_time-k.now_date);
					if(point!=='…' && k.time>=0 && k.confirm!=="2"){
						fdyk = ((point - k.buy_point)>0 ? 1 : ((point - k.buy_point)===0 ? 0 : -1)) * k.gdsy_buy_ratio/100 * k.buy_price * k.buy_amount * fangx;
						if(fdyk>0) {updown='up';}
						if(fdyk<0) {updown='down';}
						ykb = ((point - k.buy_point)>0 ? 1 : ((point - k.buy_point)===0 ? 0 : -1)) * fangx;
						ykb = ykb * k.gdsy_buy_ratio + '%';
						fdyk = '￥' +parseFloat(fdyk.toFixed(1));
					}
					trading_rule_02++;
					html__ ='\
						<dd class="info">\
						<img src="' + itemImage[k.buy_number] +'" />\
						<ul>\
							<li><i>下单价格</i><span>￥'+ k.buy_point.toFixed(k.buy_number==='13'?3:0) +'</span></li>\
							<li><i>现价</i><span>￥'+ (point==='…'?point:Number(point).toFixed(k.buy_number==='13'?3:0)) +'</span></li>\
							<li><i>数量</i><span>'+ k.buy_amount +'</span></li>\
							<li><i>'+ (k.buy_type==='buy' ? '订货金':'赊货金') +'</i><span>￥'+ (k.buy_price * k.buy_amount) +'</span></li>\
							<li><i>仓储费</i><span>￥'+ k.buy_brokerage +'</span></li>\
							<li><i>差价</i><span>￥' + fdyk + '</span></li>\
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
						k.unknown = "no";
					}else{
						//已到期未平仓的单子
						if(k.time<0){
							html__ += '\
								<dd class="unknown">\
									<ul>\
										<li>系统退款处理中</li>\
									</ul>\
								</dd>\
							';
							k.unknown = "no";
						}else{
							html__ += '\
								<dd class="btn">\
									<div>'+ (new Date(k.buy_time)).Format("MM-dd hh:mm:ss")  +'</div>\
									<ul>\
										<li hash="order_buy/'+ k.order_id +'">支付尾款</li>\
									</ul>\
								</dd>\
							';
						}
					}
				}
				
				if(k.trading_rule==="03"){
					if(point!=='…' && k.confirm!=="2"){
						if(k.zyzs_confirm==="3" || k.zyzs_confirm==="4"){
							//平仓中订单 用平仓点位来计算盈亏
							point_text = "平仓点位";
							point_ = k.sell_point;
						}
						fdyk = (point_ - k.buy_point) * k.gzp_profit * k.buy_amount * fangx;
						if(fdyk>0) {updown='up';}
						if(fdyk<0) {updown='down';}
						ykb = fdyk/(k.buy_all_price - k.buy_brokerage);
						fdyk = '￥' +parseFloat(fdyk.toFixed(1));
						ykb = parseFloat((ykb*100).toFixed(1)) + '%';
					}
					html__ ='\
						<dd class="info">\
						<img src="' + itemImage[k.buy_number] +'" />\
						<ul>\
							<li><i>'+ (k.buy_type==='buy' ? '订货价':'赊货价') +'</i><span>￥'+ k.buy_point.toFixed(k.buy_number==='13'?3:0) +'</span></li>\
							<li><i>现价</i><span>￥'+ (point==='…'?point:Number(point).toFixed(k.buy_number==='13'?3:0)) +'</span></li>\
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
						if(k.zyzs_confirm==="3" || k.zyzs_confirm==="4"){
							html__ += '\
								<dd class="unknown">\
									<ul>\
										<li>系统退款处理中</li>\
									</ul>\
								</dd>\
							';
						}else{
							html__ += '\
								<dd class="btn">\
									<div>'+ (new Date(k.buy_time)).Format("MM-dd hh:mm:ss")  +'</div>\
									<ul>\
										<li hash="order_buy/'+ k.order_id +'">支付尾款</li>\
									</ul>\
								</dd>\
							';
						}
					}
				}
				
				
				html +='\
					<dl id="'+ k.order_id +'">\
						<dt>\
							<ul>\
								<li>'+ (k.step==='2' ? '<span class="t_zc">众筹</span>':'') +''+ (k.buy_type==='buy' ? '<span class="t_yd">购物</span>':'<span class="t_sh">赊货</span>') +''+ k.buy_itemname_alias +''+ ((k.trading_rule==="02")?'('+ ((k.gdsy_buy_xz_time < 60 ? k.gdsy_buy_xz_time+ 'S': (k.gdsy_buy_xz_time / 60)+ 'M'))+')':'') +''+ ((k.trading_rule==="03")?'('+k.point+'点)':'') +'</li>\
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
	            $('.top-title i',this.$el).text(' '+ o.listData.length +'笔/'+ buy_amount +'手');
			}
			
			if(o.pageIscroll){
				setTimeout(function(){
					o.pageIscroll.refresh();//刷新滚动区域
				}, 300);
			}


        	if(trading_rule_02 > 0){
	        	//格式化时间
	        	o.formatTime = function(s){
	        		s = s/1000;
	        		var m = parseInt(s/60);
	        		s = parseInt(s%60);
	        		if(m>0){m = m+'\'';}else{m = '';}
	        		return m+s;
	        	};
     
	        	//倒计时
	        	clearInterval(window.countdown);
	        	window.countdown = setInterval(function(){
	        		log([o.listData.length,"倒计时执行",$('.order-box dl').length]);
					if(o.listData.length===0){
						clearInterval(window.countdown);
						$('.order-box .end').html('持仓数为0').addClass("none");
					}else{
						o.timeindex = [];
						o.timenum = 0;
						$.each(o.listData,function(i,k){
							if(k.trading_rule=='02' && k.unknown!=='no'){
								k.time -= 1000;
								//到期时间
								var dtime1 = k.gdsy_buy_xz_time * 1000;
								
								//当前剩余时间
								var dtime2 = k.time;
								
								if(dtime2<=0){
									o.timeindex.push(k.order_id);
								}else{
									o.timenum++;
									//$('.order-box dl#'+ k.order_id +' .totime li:eq(0)').text(o.formatTime(dtime2));
									//$('.order-box dl#'+ k.order_id +' .totime li:eq(2) i').width(((dtime1 - dtime2) / dtime1 * 100).toFixed(2) +'%');
								}
							}
						});
						
						if(o.timeindex.length>0){
							o.nSplice(o.timeindex);
						}
						
						if(o.listData.length===0 || o.timenum===0){
							clearInterval(window.countdown);
						}
						
					}
				},1000);
        	}
        	
        },
		clickNumSelect: function(){
			var $this = $(this);
			if($this.hasClass("on")){
				return false;	
			}
			$this.addClass("on").siblings().removeClass("on");
		},
        viewBtnClick: function (e) {
			var o = this,
				$this = $(e.target).closest("li"),
				index = $this.index(),
				index_dl = $this.closest("dl").index(),
				clickTab = o.clickTab,
				clickNumSelect = o.clickNumSelect,
				html = '',
				dataThis = o.listData[index_dl],
				point = '…';
			if(!isNullObj(o.prevPoint)){
				point = o.prevPoint[dataThis.buy_number].ws_new_point;
			}
			
			log(dataThis);
			
			if($this.hasClass("c_zyzs")){
				var html____ = '';
				for(var i=0;i<10;i++){
					html____ += '<li><i>'+(i===0?'不设':(i*10)+ '%')+'</i></li>';
				}
				
				html = '\
					<div class="pop-order">\
						<div class="box">\
							<div class="list">\
								<b>设置限盈</b>\
								<ul>'+ html____ +'</ul>\
							</div>\
							<div class="list">\
								<b>设置限损</b>\
								<ul>'+ html____ +'</ul>\
							</div>\
						</div>\
					</div>\
				';
				MSG.C(html,'商品设置 ',
					{
						t:'返回',
						f:function(){
							MSG.X();
						}
					},
					{
						t:'确定',
						f:function(){
							var stop_loss = $('.pop-order .list:eq(1) li.on').index()*10,
								stop_profit = $('.pop-order .list:eq(0) li.on').index()*10;
								if(stop_loss===0){
									stop_loss = 100;
								}

							MSG.C('提交中');
							$.ajax({
								url : serverUrl+'hangingDelisted/setStopProfitStopLoss',
								type : "post",
								data :{
									order_id:dataThis.order_id,
									stop_loss:stop_loss,
									stop_profit:stop_profit
								},
								timeout :10000,
								dataType:"json",
								success:function(data) {
									if(data.success){
										MSG.C(data.msg,'系统提示',
												{
													t:'确定',
													f:function(){
														MSG.X();
														
														if(stop_loss===0){
															dataThis.gzp_sell_zs_point = -100/100/dataThis.gzp_profit * dataThis.buy_price * (dataThis.buy_type==='buy'?1:-1) + dataThis.buy_point;
														}else{
															dataThis.gzp_sell_zs_point = -stop_loss/100/dataThis.gzp_profit * dataThis.buy_price * (dataThis.buy_type==='buy'?1:-1) + dataThis.buy_point;
														}
														
														if(stop_profit===0){
															dataThis.gzp_sell_zy_point = 0;
														}else{
															dataThis.gzp_sell_zy_point = stop_profit/100/dataThis.gzp_profit * dataThis.buy_price * (dataThis.buy_type==='buy'?1:-1) + dataThis.buy_point;
														}
													}
												}
										);
									}else{
										MSG.C(data.msg,'系统提示',
												{
													t:'确定',
													f:function(){
														MSG.X();   
													}
												}
										);
									}
								}
							});												   
						}
					}
				);
				var gzp_sell_zs_point = parseInt(Math.abs(dataThis.gzp_sell_zs_point - dataThis.buy_point) / dataThis.buy_price * dataThis.gzp_profit * 10);
				if(gzp_sell_zs_point===10){
					gzp_sell_zs_point = 0;
				}
				
				var gzp_sell_zy_point = 0;
				if(dataThis.gzp_sell_zy_point!==0){
					gzp_sell_zy_point = parseInt(Math.abs(dataThis.gzp_sell_zy_point - dataThis.buy_point) / dataThis.buy_price * dataThis.gzp_profit * 10);
				}
				
				$('.pop-order .list li').click(clickNumSelect);
				$('.pop-order .list:eq(1) li:eq('+ gzp_sell_zs_point +')').click();
				$('.pop-order .list:eq(0) li:eq('+ gzp_sell_zy_point +')').click();
			}
			
			if($this.hasClass("c_tk")){
				html = '\
					<div class="pop-order">\
						<div class="info">\
							<ul>\
								<li style="width:100%;white-space:normal;line-height:1.5;padding-bottom:0.05rem;">商品:<i>'+ dataThis.buy_itemname_alias +'/' + dataThis.buy_unit.replace(/kg|桶|m³/g,"件")+ '</i></li>\
								<li>类型:<i>'+ (dataThis.buy_type==='buy'?'购物':'赊货') +'</i></li>\
								<li>订货金:<i>￥'+ (dataThis.buy_price * dataThis.buy_amount) +'</i></li>\
								<li>数量:<i>'+ dataThis.buy_amount +'</i></li>\
								<li>'+ (dataThis.buy_type==='buy' ? '订货价':'赊货价') +':<i>￥'+ dataThis.buy_point.toFixed(dataThis.buy_number==='13'?3:0) +'</i></li>\
								<li>现价:<i price="'+ dataThis.buy_number +'">￥'+ (point==='…'?point:Number(point).toFixed(dataThis.buy_number==='13'?3:0)) +'</i></li>\
							</ul>\
							<div class="tc">您是否确定要退款?</div>\
						</div>\
					</div>\
				';
				MSG.C(html,'退款确认',
					{
						t:'取消',
						f:function(){
							MSG.X();
						}
					},
					{
						t:'确定',
						f:function(){
							MSG.C('提交中');
							//平仓提交
							$.ajax({
								url : serverUrl+'hangingDelisted/manuallyClosePosition',
								type : "post",
								data :{order_id:dataThis.order_id},
								timeout :10000,
								dataType:"json",
								success:function(data) {
									if(data.success){
										MSG.C(data.msg,'系统提示',
												{
													t:'确定',
													f:function(){
														MSG.X();
														o.nSplice([dataThis.order_id]);
													}
												}
										);
									}else{
										MSG.C(data.msg,'系统提示',
												{
													t:'确定',
													f:function(){
														MSG.X();   
													}
												}
										);
									}
								}
							});
						}
					}
				);	
			}
        }
    });
    return View1;
});