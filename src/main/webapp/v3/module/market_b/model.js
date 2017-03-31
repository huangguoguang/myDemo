define([], function () {
    var Model2 = Backbone.Model.extend({
        defaults: function () {
            return {
            	bannerImg : "skin/images/banner2.jpg",
            	userInfo : {},
            	itemInfo : [],
            	listLoad : 0
            };
        },
        fetch: function () {
            var o = this;
            if(appData.itemInfo.length === 0){
                $.ajax({
    				url : serverUrl+'trade/itemInfo',
    				type : "post",
    				timeout : 10000,
    				data :{},
    				dataType:"json",
    				success:function(data){
    					appData.itemInfo = data;
    	                o.set({
    	                	listLoad : new Date()
    	                });
    				}
    			});
            }else{  
                o.set({
                	listLoad : new Date()
                });
            }
            
            if(isNullObj(appData.userInfo)){
                $.ajax({
    				url : serverUrl+'user/main',
    				type : "post",
    				timeout : 10000,
    				data :{},
    				dataType:"json",
    				success:function(data){
    					appData.userInfo = data;
    	                o.set({
    	                	userInfo : appData.userInfo
    	                });
    	                
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
    	                if(data.impartant_msg_list.length>0){
							var msg_list = data.impartant_msg_list,
								msg_context ='',
								msg_con = '<div class="abstract-title">' +msg_list[0].center_title + '</div><div class="abstract-box tl">' + msg_list[0].center_abstract + '</div><div class="abstract-more">[详细]</div>';
							
							//重要消息提醒
		    				if(msg_list.length===1){
		    					MSG.C(msg_con,"重要消息",{t:'关闭',f:function(){
		    						MSG.X();
		    					}});
							}else{
		    					MSG.C(msg_con,"重要消息",{
		    						t:'关闭',
		    						f:function(){
			    						MSG.X();
			    						//不传id表示所有未读信息置为已读
			    						readMsg();
		    						}
		    					},{
		    						t:'下一条('+ (msg_list.length-1) +')',
		    						f:function(){
			        					if(msg_list.length==1){
			        						$(".msgbox footer div:eq(0)").addClass('only').siblings().remove();
			        					}else{
			        						$(".msgbox footer div:eq(1)").html('下一条('+(msg_list.length-1)+')');
			        					}
			        					
			        					if(msg_list.length>0){
			    	    					$(".msgbox article").html('<div class="abstract-title">' +msg_list[0].center_title + '</div><div class="abstract-box tl">' + msg_list[0].center_abstract + '</div><div class="abstract-more">[详细]</div>');
			    	    					msg_context = msg_list[0].text;
			    	    					readMsg(msg_list[0].center_id);
			    	    					msg_list.splice(0,1);
			        					}else{
			        						MSG.X();
			        					}
		    						}
		    					});
							}
		    				
		    				readMsg(msg_list[0].center_id);
		    				msg_list.splice(0,1);
		    				
		    				$(".msgbox article").click(function(){
		    					$(".msgbox article .abstract-box").html('<div>' +msg_context+ '</div>').next().remove();
								setTimeout(function(){
									new IScroll(".abstract-box");
								}, 100);
		    				});
						
    	                }
    				}
    			});
            }else{
                o.set({
                	userInfo : appData.userInfo
                });
            }
            
        }

    });
    return Model2;
});