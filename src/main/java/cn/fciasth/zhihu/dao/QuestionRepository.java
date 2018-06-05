package cn.fciasth.zhihu.dao;

import cn.fciasth.zhihu.bean.Question;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends ElasticsearchRepository<Question,Integer>{

    public List<Question> findQuestionsByContentLikeOrTitleLike(String title,String content);
}
