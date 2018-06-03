package cn.fciasth.zhihu.async.handler;


import cn.fciasth.zhihu.async.EventHandler;
import cn.fciasth.zhihu.async.EventModel;
import cn.fciasth.zhihu.async.EventType;
import cn.fciasth.zhihu.bean.EntityType;
import cn.fciasth.zhihu.bean.Message;
import cn.fciasth.zhihu.bean.User;
import cn.fciasth.zhihu.service.MessageService;
import cn.fciasth.zhihu.service.UserService;
import cn.fciasth.zhihu.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class FollowHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(CommonUtils.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(model.getActorId());

        if (model.getEntityType() == EntityType.ENTITY_QUESTION) {
            message.setContent("用户" + user.getName()
                    + "关注了你的问题,http://127.0.0.1:8080/question/" + model.getEntityId());
        } else if (model.getEntityType() == EntityType.ENTITY_USER) {
            message.setContent("用户" + user.getName()
                    + "关注了你,http://127.0.0.1:8080/user/" + model.getActorId());
        }

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
