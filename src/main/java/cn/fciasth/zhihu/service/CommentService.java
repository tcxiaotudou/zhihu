package cn.fciasth.zhihu.service;

import cn.fciasth.zhihu.bean.Comment;
import cn.fciasth.zhihu.bean.Question;
import cn.fciasth.zhihu.dao.CommentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private SensitiveService sensitiveService;

    public int addComment(Comment comment){
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDAO.addComment(comment) > 0 ? comment.getId():0;
    }

    public int getCommentCount(int entityId,int entityType){
        return commentDAO.getCommentCount(entityId,entityType);
    }

    public List<Comment> selectCommentsByEntity(int entityId,int entityType){
        return commentDAO.selectCommentByEntity(entityId,entityType);
    }

    public boolean deleteComment(int commentId){
        return commentDAO.updateStatus(commentId,1) > 0;
    }

    public Comment getCommentById(int id) {
        return commentDAO.getCommentById(id);
    }
}
