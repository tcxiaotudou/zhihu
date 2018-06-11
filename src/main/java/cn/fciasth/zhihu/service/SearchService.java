package cn.fciasth.zhihu.service;

import cn.fciasth.zhihu.bean.Question;
import cn.fciasth.zhihu.dao.QuestionRepository;
import cn.fciasth.zhihu.vo.ViewObject;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class SearchService {

    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> searchQuestion(String keywords){
        return questionRepository.findQuestionsByContentLikeOrTitleLike(keywords,keywords);
    }

    public List<Question> testSearch(String keywords,int offset,int limit){
        QueryStringQueryBuilder builder = new QueryStringQueryBuilder(keywords);
        Pageable pageable =  PageRequest.of(offset,limit,Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Question> page = questionRepository.search(builder,pageable);
        System.out.println("总条数："+page.getTotalElements());
        System.out.println("总页数："+page.getTotalPages());
        return  page.getContent();
    }

}
