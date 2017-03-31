package com.tarena.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.github.miemiedev.mybatis.paginator.domain.Order;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.tarena.common.component.LoginIndex;
import com.tarena.common.component.session.SessionProvider;
import com.tarena.common.component.session.support.Session;
import com.tarena.constans.SessionConstans;
import com.tarena.entity.UserInfo;
import com.tarena.exception.UserLoginException;
import com.tarena.service.IUserService;
import com.tarena.util.CheckLoginCountUtil;
import com.tarena.util.Pagination;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

  @Resource
  private IUserService iUserService;

  @Autowired
  private LoginIndex loginIndex;

  private static Logger logger = Logger.getLogger(UserController.class);

  @RequestMapping("/userLoginPage")
  public Object userLoginPage(@RequestParam Map<String, Object> map,
      @Session(create = true) SessionProvider session, HttpServletRequest req,
      HttpServletResponse res) {
    String agent_code = (String) map.get("agent_code");
    Object loginCount_ = session.getAttribute("login_count");
    int loginCount = loginCount_ != null ? (int) loginCount_ : 0;
    if (session.getAttribute(SessionConstans.KEY_USER_ID) == null) {
      // 如果没有登录
      String result = loginIndex.loginIndex(session, agent_code,
          (String) map.get("code"));
      if (result.equals("ok")) {
        return "redirect:/user/First?#login/" + agent_code + "/" + loginCount;
      } else {
        return result;
      }
      // session不为空时，判断用户session中的代理商是否和url的代理商一致
    } else {
      UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
      if (userInfo.getAgent_code().equals(map.get("agent_code").toString())) {
        return "redirect:/user/First?#market";
      } else {
        String result = loginIndex.loginIndex(session, agent_code,
            (String) map.get("code"));
        if (result.equals("ok")) {
          return "redirect:/user/First?#login/" + agent_code + "/" + loginCount;
        } else {
          return result;
        }
      }
    }
  }

  @RequestMapping("First")
  public String first(@Session(create=false) SessionProvider session,HttpServletRequest request) {
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKeyLogin);
    String agent_code = userInfo.getAgent_code();
    Map<String,Object> itemInfo = iUserService.findItemInfo(agent_code);
    List<Map<String,Object>> itemImage = iUserService.findItemImage();
    JSONObject image_json = new JSONObject();
    for(Map<String,Object> map : itemImage){
      image_json.put((String) map.get("goods_code"), map.get("goods_url"));
    }
    Object jsonObject = JSONObject.toJSON(itemInfo);
    request.setAttribute("item", jsonObject);
    request.setAttribute("item_image", image_json);
    return "first";
  }

  @RequestMapping("/toRegister")
  public String toRegister() {
    return "register";
  }

  @RequestMapping("/regist")
  @ResponseBody
  public Object userRegist(@RequestParam Map<String, Object> map,
      HttpServletRequest request) {
    Map<String, Object> queryMap = new HashMap<String, Object>();
    queryMap.put("username", map.get("username"));
    Map<String, Object> resultMap = new HashMap<String, Object>();

    return resultMap;
  }

  @RequestMapping("/login")
  @ResponseBody
  public Object userLogin(@RequestParam Map<String, Object> map,
      @Session(create = false) SessionProvider session) {
    if (session == null) {
      msg.setMsg("入口网址不正确");
      msg.setSuccess(false);
      return msg;
    }
    UserInfo userInfo = (UserInfo) session
        .getAttribute(UserInfo.sessionKeyLogin);
    // 判断本次登录是否需要验证码，需要为true,不需要为false
    Map<String, Object> checkMap = CheckLoginCountUtil
        .checkNeedMsgCode(session);
    boolean need_code = (boolean) checkMap.get("need_code");
    Map<String, Object> resultMap = new HashMap<String, Object>();

    try {
      @SuppressWarnings("unchecked")
      List<Object> userInfoList = (List<Object>) iUserService.loginCheck(map,userInfo);
      session.setAttribute(UserInfo.sessionKey, (UserInfo) userInfoList.get(0));
      session.setAttribute(SessionConstans.KEY_USER_ID,((UserInfo) userInfoList.get(0)).getUser_id());
      session.removeAttribute("login_count");
      session.removeAttribute("validate_code");
      msg.setSuccess(true);
      msg.setMsg("登录成功");
      return msg;
    } catch (NullPointerException e) {
      msg.setSuccess(false);
      msg.setMsg("登录异常");
      msg.setLogin_count((int) checkMap.get("login_count"));
      return msg;
    } catch (UserLoginException e) {
      msg.setSuccess(false);
      msg.setMsg(e.getMessage());
      msg.setLogin_count((int) checkMap.get("login_count"));
      return msg;
    }
  }

  /**
   * 用户主页信息
   * 
   * @param session
   * @return
   */
  @RequestMapping("/main")
  @ResponseBody
  public Object getUserMain(@Session(create = false) SessionProvider session) {
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    if (userInfo.getUser_id() == null) {
      logger.info("main接口异常:" + userInfo.toString());
    }
    Map<String, Object> userMap;
    try {
      userMap = iUserService.getUserMain(session);
      return userMap;
    } catch (Exception e) {
      msg.setMsg(e.getMessage());
      msg.setSuccess(false);
      return msg;
    }

  }
  
  /**
   * 用户退出
   * @param session
   * @return
   */
  @RequestMapping("/loginOut")
  @ResponseBody
  public Object loginOut(@Session(create = false) SessionProvider session){
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    session.removeAttribute(SessionConstans.KEY_USER_ID);
    session.removeAttribute(SessionConstans.KEY_BROKER_ID);
    Map<String,Object> map = new HashMap<String, Object>();
    map.put("success", "true");
    map.put("msg", "退出登录成功");
    map.put("agent_code", userInfo.getAgent_code().toString());
    return map;
  }
  
  /**
   * 用户页面信息中心
   * @param session
   * @param page
   * @param row
   * @param map
   * @return
   */
  @RequestMapping("/msgCenter")
  @ResponseBody
  public Object msgCenter(@Session(create = false)SessionProvider session,
      @RequestParam(defaultValue ="0",required=false)Integer page,
      @RequestParam(defaultValue = "10",required=false)Integer row,
      @RequestParam Map<String,Object> map){
    if(map.get("center_type")==null){
      msg.setSuccess(false);
      msg.setMsg("参数错误");
      return msg;
    }
    String center_type = map.get("center_type").toString();
    try {
      return iUserService.queryUserMsg(center_type, session, "create_date", page, row, "centerInfo");
    } catch (ParseException e) {
      e.printStackTrace();
      msg.setSuccess(false);
      msg.setMsg("获取信息列表异常");
      return msg;
    }
  }
  
  @RequestMapping("/getMsg")
  @ResponseBody
  public Object getMsg(@Session(create = false) SessionProvider session,@RequestParam Map<String,Object> map){
    if(map.get("center_id")== null){
      msg.setSuccess(false);
      msg.setMsg("参数错误");
      return msg;
    }
    String center_id = map.get("center_id").toString();
    return iUserService.getMsg(session, center_id);
  }
  
  @RequestMapping("/read")
  @ResponseBody
  public Object read(@Session(create = false)SessionProvider session,@RequestParam Map<String,Object> map){
    try {
      if(map.get("center_id")!=null){
        iUserService.readMsg(session, map.get("center_id").toString());
      }else{
        iUserService.readAllMsg(session);
      }
      msg.setSuccess(true);
      msg.setMsg("信息阅读完毕");
      return msg;
    } catch (ParseException e) {
      msg.setSuccess(false);
      msg.setMsg("获取信息列表失败");
      return msg;
    }
  }
  
  @RequestMapping("/queryShoppingOrder")
  @ResponseBody
  public Object getShoppingOrder(@Session(create = false) SessionProvider session,
      @RequestParam(defaultValue = "0",required = false)Integer page,
      @RequestParam(defaultValue = "30",required = false)Integer row){
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    PageBounds pageBounds = new PageBounds(page, row, Order.formString("create_date.desc"));
    Map<String,Object> map = new HashMap<String, Object>();
    map.put("user_id", userInfo.getUser_id());
    PageList<Map<String,Object>> list = iUserService.queryShoppingOrder(map, pageBounds);
    Pagination<Map<String,Object>> addrList = new Pagination<Map<String,Object>>(page, row, list.getPaginator().getTotalCount());
    addrList.setRows(list);
    return addrList;
  }
  
}
