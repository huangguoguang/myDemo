
define(['text!module/market_b/tpl.html'], function (tpl) {
    var View2 = Backbone.View.extend({
        el: '.container',
        events: {
            'click .goods_buy': 'viewClickBuy'
        },

        initialize: function () {
			this.model.on('change:listLoad', this.showList, this);
        },
        iscrollBox: null,
        prevPoint:{},
        render: function () {
        	var o = this,
        	bannerImg = '<img src="'+ baseUrl + o.model.get("bannerImg") +'" />';
        	
        	this.$el.html(_.template(tpl, {bannerImg:bannerImg}));
			initHeight($('.iscroll'));
			setTimeout(function(){
				o.iscrollBox = new IScroll('.iscroll', {mouseWheel: true });
			},200);
        },
        //订单数量明细
        showLot :function(){
        	var o = this,
        	rule = 0;
            $.ajax({
				url : serverUrl+'trade/userLot',
				type : "post",
				data :{},
				dataType:"json",
				success:function(data) {
					$.each(data.holder,function(x,z){
						if(z.buy_type === 'sell'){
							rule = parseInt(z.trading_rule);
							$('[lot="lot_'+ rule +'_'+ z.buy_number +'"]').html((z.holder_lot > 0?'<i>'+ z.holder_lot +'</i>':''));
						}
					});
					$.each(data.total,function(x,z){
						if(z.buy_type === 'sell'){
							rule = parseInt(z.trading_rule);
							$('[lots="lots_'+ rule +'_'+ z.buy_number +'"]').text(z.total_lot);
						}
					});
				}
			});
        },
        showList:function(){
        	if(appData.itemInfo.length===0){
        		return false;
        	}
        	var o = this,
        	listHtml = '',
        	listHtml_1 = '',
        	listHtml_2 = '',
        	listHtml_3 = '',
        	listHtml_4 = '';
        	var html_mode = '';
        	$.each(appData.itemInfo,function(q,w){
            	var html='';
            	$.each(w.list,function(v,k){
            		if(k.list && k.list.length>0){
            			if(w.type*1===1 && k.type.is_zc === '1'){
            				listHtml_4 += '\
            					<div class="goods" hash="goods/'+q+'/'+v+'/1/sell">\
	            					<div class="goods_img"><img src="' + k.type.img_1 +'" /></div>\
	    							<ul>\
	    								<li class="goods_name">'+ k.type.alias +'</li>\
	    								<li class="goods_num"><span>众筹量<i lots="lots_4_'+ k.type.code +'">0</i>笔</span><span><u>7天退换</u></span><span><u>包邮</u></span></li>\
	    								<li class="goods_price"><i>￥<s>'+ parseFloat(k.list[0].gzp_money) +'</s>.00</i> 赊货金 <span>全款￥<u price='+ k.type.code +'></u></span></li>\
	    								<li class="goods_buy goods_sell" data="['+q+','+v+',1]" lot="lot_4_'+ k.type.code +'"></li>\
	    	        				</ul>\
    	        				</div>\
    	        			';
            			}
            			if(w.type*1===1){
            				listHtml_1 += '\
            					<div class="goods" hash="goods/'+q+'/'+v+'/0/sell">\
	            					<div class="goods_img"><img src="' + k.type.img_1 +'" /></div>\
	    							<ul>\
	    								<li class="goods_name">'+ k.type.alias +'</li>\
	    								<li class="goods_num"><span>月赊<i lots="lots_1_'+ k.type.code +'">0</i>笔</span><span><u>7天退换</u></span><span><u>包邮</u></span></li>\
	    								<li class="goods_price"><i>￥<s>'+ parseFloat(k.list[0].gzp_money) +'</s>.00</i> 赊货金 <span>全款￥<u price='+ k.type.code +'></u></span></li>\
	    								<li class="goods_buy goods_sell" data="['+q+','+v+',0]" lot="lot_1_'+ k.type.code +'"></li>\
	    	        				</ul>\
    	        				</div>\
    	        			';
            			}
            			if(w.type*1===2){
            				listHtml_2 += '\
            					<div class="goods" hash="goods/'+q+'/'+v+'/0/sell">\
	            					<div class="goods_img"><img src="' + k.type.img_1 +'" /></div>\
	    							<ul>\
	    								<li class="goods_title">限时抢购</li>\
	    								<li class="goods_name">'+ k.type.alias +'</li>\
	    								<li class="goods_price">赊货金￥'+ parseFloat(k.list[0].gdsy_money) +'</li>\
	    								<li class="goods_num"><span>全款￥<u price='+ k.list[0].gdsy_number +'></u></span></li>\
	    								<li class="goods_buy goods_sell" data="['+q+','+v+',0]" lot="lot_2_'+ k.type.code +'"></li>\
	    	        				</ul>\
    	        				</div>\
    	        			';
            			}
            			if(w.type*1===3){
            				listHtml_3 += '\
            					<div class="goods" hash="goods/'+q+'/'+v+'/0/sell">\
	            					<div class="goods_img"><img src="' + k.type.img_1 +'" /></div>\
	    							<ul>\
	    								<li class="goods_title">价格竞猜</li>\
	    								<li class="goods_name">'+ k.type.alias +'</li>\
	    								<li class="goods_price">赊货金￥'+ parseFloat(k.list[0].gdsy_money) +'</li>\
	    								<li class="goods_num"><span>全款￥<u price='+ k.list[0].gdsy_number +'></u></span></li>\
	    								<li class="goods_buy goods_sell" data="['+q+','+v+',0]" lot="lot_3_'+ k.type.code +'"></li>\
	    	        				</ul>\
    	        				</div>\
    	        			';
            			}
            		}
            	});
        	});
        	
        	listHtml_1 = '<div class="title"><div>爆品推荐</div></div><div class="goods_rule goods_rule_1">'+listHtml_1+'</div>';
        	listHtml_2 = '<div class="goods_rule_2">'+listHtml_2+'</div>';
        	listHtml_3 = '<div class="goods_rule_3">'+listHtml_3+'</div>';
        	
        	if(listHtml_4 !== ''){
        		listHtml_4 = '<div class="title title2"><div>限时众筹</div></div><div class="goods_rule goods_rule_4">'+listHtml_4+'</div>';
        	}
        	
        	listHtml = listHtml_1 + '<div class="goods_box">'+ listHtml_2 +''+ listHtml_3 +'</div>' + listHtml_4;
        	
        	this.$el.find(".list").html(listHtml);
        	
			setTimeout(function(){
				o.iscrollBox.refresh();
			},200);
			
			o.showLot();
			
			var updatePoint = this.updatePoint;
            $.ajax({
				url : serverUrl+'trade/topdata',
				type : "post",
				data :{},
				dataType:"json",
				success:function(data) {
					o.prevPoint = data;
		        	$.each(data,function(v,k){
		        		var $this = $('[price="'+ v +'"]');
		        		$this.text(k.ws_new_point);
		        	});
		        	
		        	var onMessage = function(e){
						e = JSON.parse(e.data);
		        		var $this = $('[price="'+ e.ws_type +'"]');
		        		
		        		log(o.prevPoint[e.ws_type]);
		        		
		        		//如果新的点位与上一点位相同不处理点位更新
		        		if(e.ws_new_point !== o.prevPoint[e.ws_type].ws_new_point){
			        		$this.text('￥' + (e.ws_new_point*1).toFixed(e.ws_type==="13"?3:0));
			        		o.prevPoint[e.ws_type] = e;
		        		}	
		        	};
					//切换交易模式时保持ws连接状态
					if(typeof ws === 'undefined'){
						ws = new ReconnectingWebSocket(appData.wsUrl +'/websocket/61012');
						ws.onmessage = function(e){
							onMessage(e);
						};
					}
					if(typeof ws13 === 'undefined'){
						if(!isMimic && isMLM){
							ws13 = new ReconnectingWebSocket(appData.wsUrl_13 +'/websocket/13');
							ws13.onmessage = function(e){
								onMessage(e);
							};
						}
					}
				}
			});
        },
		
		clickTypeSelect: function(e){
			var $this = $(e);
			if($this.hasClass("on")){
				return false;	
			}
			$this.addClass("on").siblings().removeClass("on");
		},
		
		clickTimeSelect: function(e){
			var $this = $(e);
			if($this.hasClass("on")){
				return false;	
			}
			$this.addClass("on").siblings().removeClass("on");
		},
		
        viewClickBuy: function (e) {
			var o = this;
			var $this = $(e.target).closest("li"),
				data = eval($this.attr("data")),
				mode_index = data[0],
				dl_index = data[1],
				ul_index = 0,
				mode_type = appData.itemInfo[mode_index].type,
				is_zc = data[2],
				list = appData.itemInfo[mode_index].list[dl_index].list,
				typeHtml = function(){
					var html__='';
					$.each(list,function(v,k){
						if(mode_type==='1'){
							html__ += '<li>' + k.gzp_unit.replace(/kg|桶|m³/g,"件")+ '</li>';
						}
						if(mode_type==='2' || mode_type==='3'){
							html__ += '<li>' + k.gdsy_unit.replace(/kg|桶|m³/g,"件")+ '</li>';
						}
					});
					return html__;
				},
				html ='\
					<div class="pop-buy">\
						<div class="type">\
							<div>商品规格</div>\
							<ul>'+ typeHtml() +'</ul>\
						</div>\
						<div class="main"></div>\
						<div class="total">\
							合计: <i></i>\
						</div>\
					</div>\
				';
				
			
			MSG.C(html,
				'赊货',
				{
					t:'取消',
					f:function(){
						MSG.X();
					}
				},
				{
					t:(is_zc===1 ? '请等待…':'确定'),
					f:function(){
						submitBuy();													   
					}
				}
			);
			
			if(is_zc===1){
				var $footerbtn = $(".maskbg footer div").eq(1);
				$footerbtn.addClass("disabled");
				
				$.ajax({
					url : serverUrl+'crowdfunding/tradeInfo',
					type : "post",
					data :{},
					timeout :10000,
					dataType:"json",
					success:function(data) {
						if(data.buyType === '0'){
							data.countdown = Math.floor(data.countdown/1000);
				        	clearInterval(window.countdown);
				        	window.countdown = setInterval(function(){
				        		data.countdown = data.countdown - 1;
				        		$footerbtn.html(data.countdown + 'S');
				        		if(data.countdown<=0){
				        			clearInterval(window.countdown);
				        			$footerbtn.removeClass("disabled").html("确定");
				        		}
				        	},1000);
						}
						if(data.buyType === '1'){
							$footerbtn.removeClass("disabled").html("确定");
						}
						if(data.buyType === '2'){
							$footerbtn.html("暂不能购买");
						}
					}
				});
			}
			
			var ct = 0,
				typeIndex = 0,
				lotNum = 0,
				timeIndex = 0,
				id = '',
				info = '',
				point = '-',
				time = '',
				timeArray = [],
				poundage = 0,
				money = 0,
				submitUrl = '',
				type = '',
				gdsyRule = '02',
				//金额计算
				viewTotal = function(){
					if(ct){clearTimeout(ct);}
					ct = setTimeout(function(){
						typeIndex = $('.pop-buy .type li.on').index();
						type = 'sell';//购物
						
						if(mode_type==='1'){
							money = parseFloat(list[typeIndex].gzp_money);
							poundage = parseFloat(list[typeIndex].gzp_poundage);
							id = list[typeIndex].gzp_id;
							log([typeIndex,lotNum,type,id]);
						}
						
						if(mode_type==='2' || mode_type==='3'){
							timeIndex = $('.pop-buy .time li.on').index();
							timeArray = list[typeIndex].gdsy_time[timeIndex];
							money = parseFloat(list[typeIndex].gdsy_money);
							poundage = parseFloat(timeArray.gdsy_poundage);
							id= timeArray.gdsy_id;
							gdsyRule = timeArray.gdsy_rule;
							log([typeIndex,timeIndex,lotNum,id,type]);
							//$('.pop-buy .info li:eq(1) s').text((timeArray.gdsy_ratio * money / 100) +'元');
							$('.pop-buy .info li:eq(2) s').text('￥' + poundage);
						}
						
						$('.pop-buy .total i').text('￥' + parseFloat((money + poundage) * lotNum));
						
					},100);	
				},
				viewInfo = function(){
					typeIndex = $('.pop-buy .type li.on').index();
					var data = list[typeIndex];
					log(data);
					if(mode_type==='1'){
						if(!isNullObj(o.prevPoint)){
							point = o.prevPoint[data.gzp_number].ws_new_point;
						}
						lot = data.gzp_lot;
						info = '\
							<li class="img"><img src="' + itemImage[data.gzp_number] +'" /></li>\
							<li>赊货金<s>￥'+ parseFloat(data.gzp_money) +'</s></li>\
							<li>仓储费<s>￥'+ parseFloat(data.gzp_poundage) +'</s></li>\
							<li>商品现价<s price='+ data.gzp_number +'>￥'+ point +'</s></li>\
						';
					}
					if(mode_type==='2' || mode_type==='3'){
						if(!isNullObj(o.prevPoint)){
							point = o.prevPoint[data.gdsy_number].ws_new_point;
						}
							
						lot = data.gdsy_lot;
						info = '\
							<li class="img"><img src="' + itemImage[data.gdsy_number] +'" /></li>\
							<li>赊货金<s>￥'+ parseFloat(data.gdsy_money) +'</s></li>\
							<li>仓储费<s></s></li>\
							<li>商品现价<s price='+ data.gdsy_number +'>￥'+ point +'</s></li>\
						';
						
						timeArray = list[typeIndex].gdsy_time;
						
						time = '';
						$.each(timeArray,function(v,k){
							if(mode_type==='2'){
								time += '<li>'+(k.gdsy_time < 60 ? k.gdsy_time+ 'S': (k.gdsy_time / 60)+ 'M')+'</li>';
							}
							if(mode_type==='3'){
								time += '<li>'+k.gdsy_time+'点</li>';
							}
						});
						
						time = '<div class="time"><div>购买类型</div><ul>'+ time +'</ul></div>';
					}
					
					html = '\
						<div class="info"><ul>'+ info +'</ul></div>\
						'+ time +'\
						<div class="num">\
							<div>购买数量</div>\
							<ul><li class="minus"></li><li><input type="number" value="1" /></li><li class="add"></li><li class="lot">限购数量:'+ lot +'</li></ul>\
						</div>\
					';
					
					$('.pop-buy .main').html(html);
					
					$('.pop-buy .num .add').click(function(){
						lotNum++;
						if(lotNum > lot){
							lotNum = lot;
						}
						$('.pop-buy .num input').val(lotNum);
						viewTotal();
					}).eq(0).click();
					
					$('.pop-buy .num .minus').click(function(){
						lotNum--;
						if(lotNum < 1){
							lotNum = 1;
						}
						$('.pop-buy .num input').val(lotNum);
						viewTotal();
					}).eq(0).click();
					
					$('.pop-buy .num').on("focus","input",function(){
						$(this).val('');
					});
					
					$('.pop-buy .num').on("blur","input",function(){
						var val = $(this).val();
						if(val==='' || isNaN(val)){
							lotNum = 1;
							$('.pop-buy .num input').val(lotNum);
						}else{
							lotNum = val;
							if(val - lot > 0){
								lotNum = lot;
								$('.pop-buy .num input').val(lotNum);
							}
							if(val - 1 < 0){
								lotNum = 1;
								$('.pop-buy .num input').val(lotNum);
							}
						}
						viewTotal();
					});
					
					$('.pop-buy .time li').click(function(){
						o.clickTimeSelect(this);
						viewTotal();
					}).eq(0).click();
				},
				//建仓提交
				submitBuy = function(){
					if($(".maskbg footer div").eq(1).hasClass("disabled")){
						return false;
					}
					MSG.C("提交中…");
					var toUrl = 'order';
					if(mode_type==='1'){
						submitUrl = serverUrl+'hangingDelisted/buy';
					}
					if(mode_type==='2'){
						submitUrl = serverUrl+'binaryOptions/buy';
					}
					if(mode_type==='3'){
						submitUrl = serverUrl+'pointOptions/buy';
					}
					if(is_zc===1){
						submitUrl = serverUrl+'crowdfunding/buy';
						toUrl = 'order_c';
					}

		            $.ajax({
						url : submitUrl,
						type : "post",
						data :{
							id : id,
							type:type,
							lot:lotNum
						},
						dataType:"json",
						success:function(data) {
							log(data);
							if(data.success){
								appData.userLotGet();
								//更新用户余额等信息
								appData.userInfoGet();
								MSG.C("恭喜您，购买成功",'系统提示',{t:'查看订单',f:function(){
						        	Rt.navigate(toUrl, {
						        	    trigger: true
						        	});
								}},{t:'确定',f:function(){
									MSG.X();
									o.showLot();
								}});
							}else{
								if(data.code==='000001'){
						            $.ajax({
										url : serverUrl+'user/main',
										type : "post",
										timeout : 10000,
										data :{
										},
										dataType:"json",
										success:function(datas) {
											MSG.C(data.msg,'系统提示',{t:'取消订单',f:function(){
												MSG.X();
											}},{t:'立即充值',f:function(){
												location.href = datas.pay_url;
											}});
										}
									});
		            			}else{
									MSG.C(data.msg,'系统提示',{t:'确定',f:function(){
										MSG.X();
									}});
		            			}
							}
						}
					});
				};
				
			$('.pop-buy .type li').click(function(){
				o.clickTypeSelect(this);
				viewInfo();
				viewTotal();
			}).eq(ul_index).click();
			
			return false;
        }
    });

    return View2;
});