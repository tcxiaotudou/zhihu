package cn.fciasth.zhihu.controller;

import cn.fciasth.zhihu.bean.HostHolder;
import cn.fciasth.zhihu.bean.Message;
import cn.fciasth.zhihu.bean.User;
import cn.fciasth.zhihu.service.MessageService;
import cn.fciasth.zhihu.service.UserService;
import cn.fciasth.zhihu.util.CommonUtils;
import cn.fciasth.zhihu.vo.ViewObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model) {
        try {
            if(hostHolder.getUser() == null){
                return "redirect:/reglogin";
            }
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<ViewObject>();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user", user);
                vo.set("unread", messageService.getConvesationUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(value = "/msg/detail",method = RequestMethod.GET)
    public String addMessage(@RequestParam("conversationId") String conversationId,
                             Model model){
        try {
            if(hostHolder.getUser() == null){
                return "redirect:/reglogin";
            }
            List<Message> messageList = messageService.getConversationDetail(conversationId,0,10);
            List<ViewObject> messages = new ArrayList<ViewObject>();
            for (Message message:messageList
                 ) {
                ViewObject vo = new ViewObject();
                vo.set("message",message);
                vo.set("user",userService.getUser(message.getFromId()));
                messages.add(vo);
            }
            model.addAttribute("messages",messages);
        }catch (Exception e){
            logger.error("获取详情消息失败"+e.getMessage());
        }
        return "letterDetail";
    }


    @RequestMapping(value = "/msg/addMessage",method = RequestMethod.POST)
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {
        try {
            if(hostHolder.getUser() == null){
                return CommonUtils.getJSONString(999,"用户未登录");
            }

            User user = userService.getUserByName(toName);
            if (user == null){
                return CommonUtils.getJSONString(1,"用户不存在");
            }

            Message message = new Message();
            message.setContent(content);
            message.setFromId(hostHolder.getUser().getId());
            message.setCreatedDate(new Date());
            message.setToId(user.getId());
            messageService.addMessage(message);
            return CommonUtils.getJSONString(0);

        }catch (Exception e){
            logger.error("发送消息失败"+e.getMessage());
            return CommonUtils.getJSONString(1,"发送消息失败");
        }
    }
}
