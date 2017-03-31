define(['text!module/message/tpl.html'], function (tpl) {
	var D = {};
    var View1 = Backbone.View.extend({
        el: '.container',
        initialize: function () {
			this.model.on('change:list', this.listShow, this);
        },
		events: {
			'click .art-list-box .box li': 'showDetail',
		},
        render: function () {
            this.$el.html(tpl);
            MSG.C('加载中…');
        },
        listData : [],
        pageIscroll : null,
        listShow: function () {
			var o= this,
				$list = $(".art-list-box .box ul",o.$el),
				listHtml = '';
			MSG.X();
			
			o.listData = o.listData.concat(o.model.get("list").rows);
			
			$.each(o.model.get("list").rows,function(v,k){
				listHtml += '<li '+ (k.is_read?'':'class="read"') +'>'+ (k.is_important==="1"?'<i>重要</i>':'') +''+ k.center_title +'</li>';
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
        },
        showDetail: function(e){
        	var $this = $(e.target).closest("li"),
        	index= $this.index(),
        	detail = this.listData[index];
        	MSG.C("加载中");
            var readMsg = function(center_id){
				$.ajax({
					url: serverUrl+'user/read',
					type: "post",
					data: {
						center_id : center_id
					},
					success:function(data){
						log(data);
					}
				});	
            };
            $.ajax({
				url : serverUrl+'user/getMsg',
				type : "post",
				data :{
					center_id:detail.center_id
				},
				dataType:"json",
				timeout :10000,
				success:function(data) {
					log(data);
					$this.removeClass("read");
					readMsg(detail.center_id);
					MSG.C('<div class="abstract-title">' + data.center_title + '</div><div class="abstract-box tl"><div>' + data.center_text + '</div></div>','信息详情',{t:'返回',f:function(){MSG.X();}});
					new IScroll(".abstract-box");
				}
			});
        	
        }
		
    });
    return View1;
});