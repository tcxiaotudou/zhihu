package cn.fciasth.zhihu.controller;

import cn.fciasth.zhihu.bean.Comment;
import cn.fciasth.zhihu.bean.EntityType;
import cn.fciasth.zhihu.bean.HostHolder;
import cn.fciasth.zhihu.service.CommentService;
import cn.fciasth.zhihu.service.QuestionService;
import cn.fciasth.zhihu.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Controller
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private QuestionService questionService;

    @RequestMapping(value = "/addComment",method = RequestMethod.POST)
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setCreatedDate(new Date());
            if(hostHolder.getUser() == null){
                return "redirect:/reglogin";
            }else{
                comment.setUserId(hostHolder.getUser().getId());
            }
            commentService.addComment(comment);
            int count = commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(),count);
        }catch (Exception e){
            logger.error("增加评论失败"+e.getMessage());
        }
        return "redirect:/question/"+ questionId;
    }
}
