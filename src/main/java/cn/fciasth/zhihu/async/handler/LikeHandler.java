package cn.fciasth.zhihu.async.handler;


import cn.fciasth.zhihu.async.EventHandler;
import cn.fciasth.zhihu.async.EventModel;
import cn.fciasth.zhihu.async.EventType;
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

/**
 * Created by nowcoder on 2016/7/30.
 */
@Component
public class LikeHandler implements EventHandler {
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
        message.setContent("用户" + user.getName()
                + "赞了你的评论,http://127.0.0.1:8080/question/" + model.getExt("questionId"));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
