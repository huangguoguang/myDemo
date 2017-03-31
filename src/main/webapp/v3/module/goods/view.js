
define(['text!module/goods/tpl.html','highstock'], function (tpl) {
	Highcharts.setOptions({
		global: {                                                               
				useUTC: false                                                    
		}, 
		lang : {
		  rangeSelectorZoom : '',
		  resetZoom :'重置缩放'
		}
	});
	
    var View2 = Backbone.View.extend({
        el: '.container',
        events: {
        	
        },

        initialize: function () {
			this.model.on('change:listLoad', this.showList, this);
        },
        carousel:null,
        prevPoint:{},
        timeIscroll:null,
        boxIscroll:null,
        render: function () {
        	var o = this;
        	this.$el.html(_.template(tpl, {}));
        	var w = $('.container').width();
        	$("#scroller").width(w*3);
        	$("#wrapper").height(w);
        	$("#wrapper .slide").css({width:w,height:w});
        	$('.chart').height(w*0.618);
        	$('.goods_detail').height($(window).height()-$(".goods_detail_buy").height());
        	o.timeIscroll = new IScroll('.chartbox .time', { scrollX: true, scrollY: false,bounceLockX:true,bounceLockY:false});
        	o.boxIscroll = new IScroll('.goods_detail', {probeType:3,mouseWheel: true,preventDefault:false,bounce: true});
        	
        	o.boxIscroll.on('scroll', function(){
        		var h_1 = $("#wrapper").height() + $(".view").height();
        		if((o.boxIscroll.y*-1 - h_1) > 0){
        			$(".goods_detail_tab_mid").css("visibility","hidden");
        			$(".goods_detail_tab_fixed").css({"opacity":1,"z-index":''});
        		}else{
        			$(".goods_detail_tab_mid").css("visibility","visible");
        			$(".goods_detail_tab_fixed").css({"opacity":0,"z-index":'-1'});
        		}
        	});
        	
        	o.boxIscroll.on('scrollEnd', function(){
        		var h_1 = $("#wrapper").height() + $(".view").height();
        		if((o.boxIscroll.y*-1 - h_1) > 0){
        			$(".goods_detail_tab_mid").css("visibility","hidden");
        			$(".goods_detail_tab_fixed").css({"opacity":1,"z-index":''});
        		}else{
        			$(".goods_detail_tab_mid").css("visibility","visible");
        			$(".goods_detail_tab_fixed").css({"opacity":0,"z-index":'-1'});
        		}
        	});
        	
			setTimeout(function(){
				o.carousel = new IScroll('#wrapper', {
					scrollX: true,
					scrollY: false,
					momentum: false,
					bounce: false,
					snap: true,
					snapSpeed: 400,
					keyBindings: true,
					preventDefault:false,
					indicators: {
						el: document.getElementById('indicator'),
						resize: false
					}
				});
				
			},200);
        },

        chartType:0,
        
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
					$.each(data.total,function(x,z){
						if(z.buy_type === o.model.get("fx")){
							rule = parseInt(z.trading_rule);
							$('[lots="lots_'+ rule +'_'+ z.buy_number +'"]').text(z.total_lot);
						}
					});
				}
			});
        },
        
        showList:function(){
			var o = this,
				mode_index = o.model.get("id_1"),
				dl_index = o.model.get("id_2"),
				is_zc = o.model.get("id_3"),
				ul_index = 0,
				mode_type = appData.itemInfo[mode_index].type,
				list_ = appData.itemInfo[mode_index].list[dl_index],
				list = list_.list[ul_index],
				goods_code = list_.type.code,
				detail = list_.type.detail,
				img_2 = list_.type.img_2.split(","),
				img_3 = list_.type.img_3.split(",");
			
			o.chartType = parseInt(goods_code);
				
			var $img = o.$el.find("#scroller .slide");
			
			$img.eq(0).html('<img src="'+ img_2[0] +'" />');
			$img.eq(1).html('<img src="'+ img_2[1] +'" />');
			$img.eq(2).html('<img src="'+ img_2[2] +'" />');
			
        	o.$el.find(".view").html('\
				<ul>\
        			<li class="goods_title">'+( is_zc==='0' ? '爆款推荐':'众筹' )+'</li>\
					<li class="goods_name">'+ (list.gzp_alias || list.gdsy_alias) +'</li>\
					<li class="goods_price"><i>￥<s>'+ parseFloat((list.gzp_money || list.gdsy_money)) +'</s>.00</i>'+ (o.model.get("fx") === 'buy' ? '订货金':'赊货金') +'</li>\
					<li class="goods_num"><span>仓储费 ￥'+ parseFloat((list.gzp_poundage || list.gdsy_time[0].gdsy_poundage)) +'</span><span>'+( is_zc==='0' ? (o.model.get("fx") === 'buy' ? '月订':'月赊'):'众筹量' )+'  <i lots="lots_'+ mode_type +'_'+ goods_code +'">0</i> 笔</span><span>全款 ￥<u price='+ (list.gzp_number || list.gdsy_number) +'></u></span></li>\
				</ul>\
				<ul><li class="goods_tip"><span>正品保证</span><span>赠运费险</span><span>极速退款</span><span>七天退换</span></li>\</ul>\
        	');
        	
        	$(".goods_detail_buy").text(( is_zc==='0' ? (o.model.get("fx") === 'buy' ? '立即购物':'立即赊货'):'立即众筹' ));

        	o.$el.find(".attr").html(function(){
        		var _c = '';
        		if(detail.length > 0){
        			detail = detail.split("#");
            		for(var c=0;c<detail.length;c++){
            			if(detail[c].length > 0){
            				_c += '<li><s>'+ detail[c].split("$")[0] +'</s><i>'+ detail[c].split("$")[1] +'</i></li>';
            			}
            		}
        		}
        		if(_c!==''){
        			_c = '<ul>'+ _c +'</ul>';
        		}
        		return _c;
        	});
        	
        	o.$el.find(".info").html(function(){
        		var _c = '';
        		for(var c=0;c<img_3.length;c++){
        			_c += '<img src="'+ img_3[c] + '" />';
        		}
        		return _c;
        	});
        	
        	o.$el.find(".info img").load(function(){
        		o.boxIscroll.refresh();
        	});
        	
        	o.showLot();
        	
        	o.$el.find('.tab li').click(function(e){
				o.clickViewTab(e);
			}).eq(0).click();
        	
        	$('.chartbox .time li').click(function(e){
				o.viewTimeSelect(e);
			});
        	
        	$(".goods_detail_buy").click(function(){
        		o.viewClickBuy(this);
        	});
        	
        	$("#back").click(function(){
        		history.back();
        	});
        	
			var updatePoint = this.updatePoint;
			
			//小数点位数
			appData.xsd = 0;
			if(o.chartType === 13){
				appData.xsd = 3;
			}
			
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
			        		$this.text((e.ws_new_point*1).toFixed(e.ws_type==="13"?3:0));
			        		o.prevPoint[e.ws_type] = e;
		        		}
		        		
		        		
						_this = $('.chart').highcharts();

						if(_this){
							var series = _this.series[0];
	
							if(parseInt(e.ws_type)===o.chartType){
								var _min= appData.lineList[appData.lineIndex()];

								//分时数据更新
								if(_min<0){
									log("分时数据更新");
									log([e.ws_time.replace(/-/g, "/"),new Date(e.ws_time.replace(/-/g, "/")).getTime(),Number(e.ws_new_point)]);
									series.addPoint([new Date(e.ws_time.replace(/-/g, "/")).getTime(), Number(e.ws_new_point)],true,(series.points.length >200 ? true : false));
								}
								//1，5，15分钟k线更新
								if(_min > 0 && _min < 30){
									var lastdata = dataLine[dataLine.length-1];
									if(new Date(e.ws_time.replace(/-/g, "/")).getTime() - lastdata[0] - _min * 60 * 1000 > 0){
										dataLine.splice(0,1);
										dataLine.push([
											lastdata[0] + _min * 60 * 1000,
											e.ws_new_point,
											e.ws_new_point,
											e.ws_new_point,
											e.ws_new_point
										]);
										log("k线数据(增加时间点):"+_min);
									}else{
										lastdata[2] = lastdata[2]<e.ws_new_point ? e.ws_new_point : lastdata[2];
										lastdata[3] = lastdata[3]>e.ws_new_point ?  e.ws_new_point : lastdata[3];
										lastdata[4] = e.ws_new_point;
										log("k线数据(更新时间点):"+_min + lastdata);
									}
									log("k线总数:" + dataLine.length);
									
									series.update({
								            data: dataLine
								    });
								}
							}
						}
		        		
		        		
		        		
		        		
		        	};
		        	
		        	if(o.chartType === 13){
		        		ws = new ReconnectingWebSocket(appData.wsUrl_13 +'/websocket/13');
		        	}else{
						ws = new ReconnectingWebSocket(appData.wsUrl +'/websocket/61012');
		        	}
		        	
					ws.onmessage = function(e){
						onMessage(e);
					};
				}
			});
        },
        
        initDestroy : function(){
			//销毁图表
			var _this = $('.chart').highcharts();
			if(_this){_this.destroy();}
        },
        
		initAll : function(){
			
			var o = this,
				initK = o.initK,
				initLine = o.initLine;
			
			o.initDestroy();
			
			//获取当前点位类型
			var type = o.chartType;
			
			
			//获取图表类型
			var min = appData.lineList[appData.lineIndex()];
			
			log('点位类型:'+ type + '-图表类型:' + min);
			
			//生成新的图表
			if(appData.lineIndex()){
	            $.ajax({
					url : serverUrl+'trade/kline',
					type : "post",
					data : {
						type:type,
						min:min
					},
					dataType:"json",
					success:function(data) {
						data = JSON.parse(data);
						log(data.length);
						initK(data);
						dataLine = data;
					}
				});
			}else{
				
	            $.ajax({
					url : serverUrl+'trade/kline',
					type : "post",
					data :{
						type:type,
						min:-1
					},
					dataType:"json",
					success:function(data) {
						data = JSON.parse(data);
						initLine(data);
						dataLine = data;
					}
				});
				
			}
		},
		initK: function(data){
			var color = function(){
				if(appData.cssId()===1){
					return {
						backgroundColor : '#1f2633',
						xyColor:'#4f5c73',
						xLineColor : '#41506b',
						xSpliteColor : '#2c3648',
						ySpliteColor : '#2c3648',
						tooltipsbackgroundColor : '#3e4b66'
					};
				}
				if(appData.cssId()===2){
					return {
						backgroundColor : '#ffffff',
						xyColor:'#808080',
						xLineColor : '#cecece',
						xSpliteColor : '#e8e8e8',
						ySpliteColor : '#2c3648',
						tooltipsbackgroundColor : '#2b84e7'
					};
				}
				if(appData.cssId()===3){
					return {
						backgroundColor : '#ffffff',
						xyColor:'#808080',
						xLineColor : '#cecece',
						xSpliteColor : '#e8e8e8',
						ySpliteColor : '#2c3648',
						tooltipsbackgroundColor : '#a0a0a0'
					};
				}
				if(appData.cssId()===4){
					return {
						backgroundColor : '#ffffff',
						xyColor:'#808080',
						xLineColor : '#cecece',
						xSpliteColor : '#e8e8e8',
						ySpliteColor : '#2c3648',
						tooltipsbackgroundColor : '#2bba90'
					};
				}
				if(appData.cssId()===5){
					return {
						backgroundColor : '#ffffff',
						xyColor:'#808080',
						xLineColor : '#cecece',
						xSpliteColor : '#e8e8e8',
						ySpliteColor : '#2c3648',
						tooltipsbackgroundColor : '#bbbbbb'
					};
				}
				if(appData.cssId()===6){
					return {
						backgroundColor : '#ffffff',
						xyColor:'#808080',
						xLineColor : '#cecece',
						xSpliteColor : '#e8e8e8',
						ySpliteColor : '#2c3648',
						tooltipsbackgroundColor : '#bbbbbb'
					};
				}
			};
			$('.chart').highcharts('StockChart', {
				chart : {
					events : {
						load : function () {
						}
					},
					animation:false,
					backgroundColor :color().backgroundColor,
					margin:1,
					marginBottom:15+dpr*10,
					panning: false, //禁用放大
	                pinchType:'', //禁用手势操作
				},
				rangeSelector : {
					enabled: false,
				},
				xAxis: {
					tickWidth:1,//刻度宽度
					tickLength:5,//刻度高度
					//tickPixelInterval:120,
					//tickAmount:10,
					tickInterval:1000 * 60 * 15 * appData.lineList[appData.lineIndex()] , //间隔时间区间
					tickColor:color().xSpliteColor,
					//ordinal:false, //间隔相等
					//startOnTick:true,
					//endOntick:true,
					labels: {
						style:{
							"color":color().xyColor,
							fontSize:10*dpr
						},
						formatter: function () {
							return Highcharts.dateFormat('%H:%M',this.value);
						},
					},
					gridLineDashStyle : 'DashDot',
					/*
					'Solid','ShortDash','ShortDot','ShortDashDot','ShortDashDotDot','Dot',
					'Dash','LongDash','DashDot','LongDashDot','LongDashDotDot'
					*/
					gridLineColor : color().xSpliteColor,
					gridLineWidth : 0,
					lineWidth:1,
					lineColor:color().xLineColor,
					x: 0,
					y: 10*dpr
				},
				yAxis : [{
					gridLineDashStyle : 'Dashed',
					gridLineColor : color().xSpliteColor,
					gridLineWidth : 0.5,
					lineWidth:0,
					labels: {
						align: 'left',
						x: 5,
						y: -5,
						formatter: function () {
						  return this.value.toFixed(appData.xsd);
						},
						style:{
							"color":color().xyColor,
							fontSize:10*dpr
						}
					},
					opposite: false
				}],
				series : [
					{
						  type: 'candlestick',
						  data: data
					}
				],
				credits : {
					enabled : false
				},
				scrollbar : {
					enabled : false
				},
				navigator : {
					enabled : false
				},
				tooltip: {
					useHTML: true,
					shadow: false,
					borderColor: "rgba(255, 255, 255, 0)",
					backgroundColor: color().tooltipsbackgroundColor,
					valueDecimals: 0,
					formatter : function() {
						return Highcharts.dateFormat('%m/%d %H:%M',this.x) +'\
								<br />开盘:' +this.points[0].point.open.toFixed(appData.xsd) +'\
								<br />最高:' + this.points[0].point.high.toFixed(appData.xsd) +'\
								<br />最低:' + this.points[0].point.low.toFixed(appData.xsd) +'\
								<br />收盘:' + this.points[0].point.close.toFixed(appData.xsd) +'\
							';
					},
					style: {
						color: '#ffffff',
						fontSize:10*dpr + 'px',
						padding:5*dpr,
						fontWeight:'normal'
					}
				},
				plotOptions : {
					//修改蜡烛颜色  
					candlestick: {  
						color: '#33AA11',  
						upColor: '#DD2200',  
						lineColor: '#33AA11',                 
						upLineColor: '#DD2200', 
						pointPadding:0.1,
						pointWidth:3*dpr,
						//pointInterval: 50,
						maker:{  
							states:{  
								hover:{  
									enabled:false,  
								}  
							}  
						},
						states : {
						  hover : {
							enabled : false
						  }
						} 
					},
					line : {
						marker : {
						  states : {
							hover : {
							  enabled : true
							},
							select : {
							  enabled : true
							}
						  }
						},
						states : {
						  hover : {
							enabled : false
						  }
						}
					}
				}	
			});
		},
		initLine :function(data){	
			log(data.length);
			
			var color = function(){
				if(appData.cssId()===3){
					return {
						backgroundColor : '#ffffff',
						xyColor:'#808080',
						xLineColor : '#cecece',
						xSpliteColor : '#e8e8e8',
						ySpliteColor : '#2c3648',
						mainLine : '#666666',
						crosshairBg : '#2c3648',
						crosshairColor: '#ffffff',
						area : '#b7b7b7'
					};
				}
			};
			
			$('.chart').highcharts('StockChart', {
				chart : {
					animation:false,
					backgroundColor :color().backgroundColor,
					margin:1,
					marginBottom:15+dpr*10,
					panning: false, //禁用放大
	                pinchType:'', //禁用手势操作
				},
				rangeSelector : {
					enabled: false
				},
	            series : [{
	                data : data,
	                type : 'areaspline',
	                lineWidth:1,
	                lineColor: color().mainLine,
	                threshold : null,
	                fillColor : {
	                    linearGradient : {
	                        x1: 0,
	                        y1: 0,
	                        x2: 0,
	                        y2: 1
	                    },
	                    stops : [
	                        [0, Highcharts.Color(color().area).setOpacity(0.5).get('rgba')],
	                        [1, Highcharts.Color(color().area).setOpacity(0.2).get('rgba')]
	                    ]
	                },
					states : {
						hover : {
						  enabled : false
						},
						select : {
						  enabled : false
						}
					}
	            }],
				credits : {
					enabled : false
				},
				scrollbar : {
					enabled : false
				},
				navigator : {
					enabled : false
				},
				tooltip: {
					positioner: function (labelWidth) {
						return { x: ($(".chart").width()-labelWidth)/2, y: 10*dpr };
					},
					//shared: true,
					useHTML: false,
					shadow: false,
					borderColor: "rgba(255, 255, 255, 0)",
					backgroundColor: "rgba(255, 255, 255,0)",
					valueDecimals: 2,
					formatter: function () {
						var e, t, o = this;
						$.each(this.points, function () {
							e = Highcharts.dateFormat("%H:%M:%S", this.x);
							t = "点位:" + o.y.toFixed(appData.xsd);
						});
						return "时间:"+ e + " " + t;
					},
					style: {
						color: color().mainLine,
						fontSize:10*dpr,
						padding: '0'
					}
				},
				yAxis : [ {
					crosshair:{
						label:{
							enabled: false
						}
					},
					gridLineDashStyle : 'Solid',
					gridLineColor : color().xSpliteColor,
					gridLineWidth : 0.5,
					lineWidth:0,
					lineColor : color().xLineColor,
					labels: {
						align: 'left',
						x: 5,
						y: -10,
						formatter: function () {
						  return this.value.toFixed(appData.xsd);
						},
						style:{
							"color":color().xyColor,
							fontSize:10*dpr
						}
					},
					opposite: false
				}],
				xAxis: {
					crosshair:{
						label:{
							enabled: false
						}
					},
					tickWidth:1,
					//tickPixelInterval:100,
					tickInterval:1000 * 60 * 5 , //间隔时间区间
					tickColor:color().xSpliteColor,
					//ordinal:false, //间隔相等
					//startOnTick:true,
					//endOntick:true,
					//min:minTime,
					//max:maxTime,
					labels: {
						formatter: function () {
							return  Highcharts.dateFormat("%H:%M", this.value);
						},
						x: 0,
						y: 10*dpr,
						style:{
							"color":color().xyColor,
							fontSize:10*dpr
						}
					},
					//minorGridLineColor :'#41506b',
					//minorGridLineWidth :0,
					gridLineDashStyle : 'Dot',
					gridLineColor : color().xSpliteColor,
					gridLineWidth : 0,
					borderWidth:0,
					lineWidth:1,
					lineColor : color().xLineColor
				}
			});
		},
		
		getLotHtml:function(lot){
			//手数分页计算
			var D={};
			D.maxLot = lot;
			D.maxLotCur = D.maxLot > 50 ? 15 : 10;//每页显示手数
			D.maxLotPage = Math.ceil(D.maxLot / D.maxLotCur);//页数
			var html_1='',html_2='',html_3='';
			for(var i=0;i<D.maxLotPage;i++){
				var start = i*D.maxLotCur + 1;
				var end = (i+1)*D.maxLotCur;
				if(end > D.maxLot){
					end = D.maxLot;
				}
				html_1 += '<li>'+ start +'~'+ end +'</li>';
				for(var j=start;j<=end;j++){			
					html_2 += '<li><i>'+ j +'</i></li>';
				}
				html_3 += '<ul>'+ html_2 +'</ul>';
				html_2 = '';
			}
			html_1 = '<ul class="tab">'+ html_1 +'</ul><div class="list">'+ html_3 +'</div>';
			return html_1;
		},
		
		clickViewTab: function(e){
			var $this = $(e.target),index = $this.index();
			if($this.hasClass("on")){
				return false;
			}
			
			$(".goods_detail_tab_fixed li").eq(index).addClass("on").siblings().removeClass("on");
			$(".goods_detail_tab_mid li").eq(index).addClass("on").siblings().removeClass("on");
			
			var o = this;
			
			if(index===2){
				o.$el.find('.chartbox').show().siblings().hide();
				$('.chartbox .time li').eq(0).click();
			}else{
				o.$el.find('.tabs').children().eq(index).show().siblings().hide();
				o.initDestroy();
			}
			o.boxIscroll.refresh();
		},
		
		viewTimeSelect: function(e){
			var $this = $(e.target).closest("li"),index = $this.index();
			$this.addClass("on").siblings().removeClass("on");
			
			localStorage.setItem("lineIndex",index);
			
			this.initAll();
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
				mode_index = o.model.get("id_1"),
				dl_index = o.model.get("id_2"),
				is_zc = o.model.get("id_3"),
				ul_index = 0,
				mode_type = appData.itemInfo[mode_index].type,
				list_ = appData.itemInfo[mode_index].list[dl_index],
				list = list_.list,
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
						<div class="close"></div>\
					</div>\
				';
				
			
			MSG.C(html,
				'购物',
				{
					t:'<span class="total">合计<i>￥0</i></span>',
					f:function(){
						
					}
				},
				{
					t:(is_zc===1 ? '请等待…':'确定'),
					f:function(){
						submitBuy();													   
					}
				}
			);
			
			if(is_zc==='1'){
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
							//倒计时
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
			
			
			$(".maskbg").click(function(e){
				if($(e.target).closest(".msgbox").length===0){
					MSG.X();
				}
			});
        	
        	$(".pop-buy .close").click(function(){
        		MSG.X();
        	});
			
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
						
						type = o.model.get("fx");
						
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
							$('.pop-buy .info li:eq(2) s').text('￥' + poundage);
						}
						
						$('.msgbox .total i').text('￥' + parseFloat((money + poundage) * lotNum));
						
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
							<li class="img"><img src="'+ list_.type.img_1 +'" /></li>\
							<li>'+ (o.model.get("fx") === 'buy' ? '订货金':'赊货金') +'<s>￥'+ parseFloat(data.gzp_money) +'</s></li>\
							<li>仓储费<s>￥'+ parseFloat(data.gzp_poundage) +'</s></li>\
							<li>商品现价<s price="'+ data.gzp_number +'">￥'+ point +'</s></li>\
						';
					}
					if(mode_type==='2' || mode_type==='3'){
						if(!isNullObj(o.prevPoint)){
							point = o.prevPoint[data.gdsy_number].ws_new_point;
						}
							
						lot = data.gdsy_lot;
						info = '\
							<li class="img"><img src="'+ list_.type.img_1 +'" /></li>\
							<li>'+ (o.model.get("fx") === 'buy' ? '订货金':'赊货金') +'<s>￥'+ parseFloat(data.gdsy_money) +'</s></li>\
							<li>仓储费<s></s></li>\
							<li>商品现价<s price="'+ data.gdsy_number +'">￥'+ point +'</s></li>\
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
					if(mode_type==='1'){
						submitUrl = serverUrl+'hangingDelisted/buy';
					}
					if(mode_type==='2'){
						submitUrl = serverUrl+'binaryOptions/buy';
					}
					if(mode_type==='3'){
						submitUrl = serverUrl+'pointOptions/buy';
					}
					if(is_zc==='1'){
						submitUrl = serverUrl+'crowdfunding/buy';
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
								MSG.C("恭喜您，购买成功",'系统提示',{t:'确定',f:function(){
									MSG.X();
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
        }
    });

    return View2;
});