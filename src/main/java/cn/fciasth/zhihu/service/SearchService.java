package cn.fciasth.zhihu.service;

import cn.fciasth.zhihu.bean.Question;
import cn.fciasth.zhihu.dao.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> searchQuestion(String keywords){
        return questionRepository.findQuestionsByContentLikeOrTitleLike(keywords,keywords);
    }
}
