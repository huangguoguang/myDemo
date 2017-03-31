define(['text!module/address/tpl.html'], function (tpl) {
	var D = {};
    var View1 = Backbone.View.extend({
        el: '.container',
        initialize: function () {
			this.model.on('change:list', this.listShow, this);
        },
		events: {
			'click .back': function(){history.back();},
			'click .art-list-box .box li': 'showDetail',
			'click .address-list-box .list .opt i': 'setDefault',
			'click .address-list-box .list .opt s._edit': 'editAddress',
			'click .address-list-box .list .opt s._del': 'delAddress'	
		},
        render: function () {
            this.$el.html(tpl);
        },
        listData : [],
        pageIscroll : null,
        listShow: function () {
			var o= this,
				$list = $(".address-list-box .list",o.$el),
				listHtml = '';
			MSG.X();
			
			o.listData = o.listData.concat(o.model.get("list").rows);
			
			$.each(o.model.get("list").rows,function(v,k){
				listHtml += '\
					<ul>\
						<li>收货人：'+ k.receiver_name +' <span>'+ k.receiver_mobile +'</span></li>\
						<li>收货地址：'+ k.receiver_addr +' </li>\
						<li class="opt"><i'+ (k.is_default==='1'?' class="on"':'') +'>设为默认地址</i><span><s class="_edit">编辑</s><s class="_del">删除</s></span></li>\
					</ul>';
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
			
            $(document).off("click.d");
            $(document).on("click.d",".add_address_btn",function(){
            	o.addAddress(o);
            });
            
            if(o.model.get("list").total===0){
            	o.addAddress(o);
            }
			
        },
        
        setDefault: function(e){
        	var o = this;
        	if($(e.target).hasClass("on")){
        		return false;
        	}
        	var $this = $(e.target).closest("ul"),
        	index= $this.index(),
        	detail = this.listData[index];
        	MSG.C("提交中");
            $.ajax({
				url : serverUrl+'addr/setDefaultAddr',
				type : "post",
				data :{
					user_addr_id:detail.user_addr_id
				},
				dataType:"json",
				timeout :10000,
				success:function(data) {
					if(data.success === true){
						MSG.C(data.msg,'系统提示',{t:'确定',f:function(){
							o.pageIscroll.destroy();
							o.listData = [];
							$(".address-list-box .list").html('');
							$(".end").remove();
							o.model.fetch(1);
						}});
					}else{
						MSG.C('系统错误','系统提示',{t:'确定',f:function(){MSG.X();}});
					}
				}
			});
        	
        },
        
        delAddress: function(e){
        	var o = this;
        	var $this = $(e.target).closest("ul"),
        	index= $this.index(),
        	detail = this.listData[index];
        	MSG.C("提交中");
            $.ajax({
				url : serverUrl+'addr/removeUserAddr',
				type : "post",
				data :{
					user_addr_id:detail.user_addr_id
				},
				dataType:"json",
				timeout :10000,
				success:function(data) {
					if(data.success === true){
						MSG.C(data.msg,'系统提示',{t:'确定',f:function(){
							o.pageIscroll.destroy();
							o.listData = [];
							$(".address-list-box .list").html('');
							$(".end").remove();
							o.model.fetch(1);
						}});
					}else{
						MSG.C('系统错误','系统提示',{t:'确定',f:function(){MSG.X();}});
					}
				}
			});
        },
        
        editAddress: function(e){
        	var o = this;
        	var $this = $(e.target).closest("ul"),
        	index= $this.index(),
        	detail = this.listData[index];
        	MSG.C('\
        		<div class="input_address">\
        			<ul>\
	    				<li><span>收货人</span><div><input type="text" name="_name" value="'+ detail.receiver_name +'" placeholder="收货人姓名" maxlength="10" /></div></li>\
	    				<li><span>联系电话</span><div><input type="text" name="_tel" value="'+ detail.receiver_mobile +'" placeholder="收货人联系电话" maxlength="15" /></div></li>\
	    				<li><span>收货地址</span><div><textarea name="_address" placeholder="收货人收货地址">'+ detail.receiver_addr +'</textarea></div></li>\
        			</ul>\
        		</div>\
        	','编辑地址',{t:'取消',f:function(){MSG.X();}},{t:'保存',f:function(){o.saveAddress(o,detail.user_addr_id);}});
        },
        
        addAddress: function(o){
        	MSG.C('\
        		<div class="input_address">\
        			<ul>\
	    				<li><span>收货人</span><div><input type="text" name="_name" value="" placeholder="收货人姓名" maxlength="10" /></div></li>\
	    				<li><span>联系电话</span><div><input type="text" name="_tel" value="" placeholder="收货人联系电话" maxlength="15" /></div></li>\
	    				<li><span>收货地址</span><div><textarea name="_address" placeholder="收货人收货地址"></textarea></div></li>\
        			</ul>\
        		</div>\
        	','添加新地址',{t:'取消',f:function(){MSG.X();}},{t:'保存',f:function(){o.newAddress(o);}});
        },
        
        newAddress: function(o){
        	var receiver_name = $.trim($('[name="_name"]').val());
        	var receiver_mobile = $.trim($('[name="_tel"]').val());
        	var receiver_addr = $.trim($('[name="_address"]').val());
        	
        	if(receiver_name===''  || receiver_mobile === '' || receiver_addr === ''){
        		return false;
        	}
            $.ajax({
				url : serverUrl+'addr/addUserAddr',
				type : "post",
				data :{
					receiver_name: receiver_name,
					receiver_mobile: receiver_mobile,
					receiver_addr: receiver_addr
				},
				dataType:"json",
				timeout :10000,
				success:function(data) {
					if(data.success === true){
						MSG.C(data.msg,'系统提示',{t:'确定',f:function(){
							o.pageIscroll.destroy();
							o.listData = [];
							$(".address-list-box .list").html('');
							$(".end").remove();
							o.model.fetch(1);
						}});
					}else{
						MSG.C('系统错误','系统提示',{t:'确定',f:function(){MSG.X();}});
					}
				}
			});
        },
        
        saveAddress: function(o,user_addr_id){
        	
        	var receiver_name = $.trim($('[name="_name"]').val());
        	var receiver_mobile = $.trim($('[name="_tel"]').val());
        	var receiver_addr = $.trim($('[name="_address"]').val());
        	
        	if(receiver_name===''  || receiver_mobile === '' || receiver_addr === ''){
        		return false;
        	}
        	
            $.ajax({
				url : serverUrl+'addr/editUserAddr',
				type : "post",
				data :{
					user_addr_id:user_addr_id,
					receiver_name: receiver_name,
					receiver_mobile: receiver_mobile,
					receiver_addr: receiver_addr
				},
				dataType:"json",
				timeout :10000,
				success:function(data) {
					if(data.success === true){
						MSG.C(data.msg,'系统提示',{t:'确定',f:function(){
							o.pageIscroll.destroy();
							o.listData = [];
							$(".address-list-box .list").html('');
							$(".end").remove();
							o.model.fetch(1);
						}});
					}else{
						MSG.C('系统错误','系统提示',{t:'确定',f:function(){MSG.X();}});
					}
				}
			});
        }
		
    });
    return View1;
});