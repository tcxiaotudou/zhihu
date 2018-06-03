package cn.fciasth.zhihu.util;

import cn.fciasth.zhihu.controller.CommentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;

@Service
public class JedisAdapter implements InitializingBean{

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool jedisPool;

    @Override
    public void afterPropertiesSet() throws Exception {
        jedisPool = new JedisPool("redis://123.207.13.53:6379/2");
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("push发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("pop发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long sadd(String key,String value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.sadd(key,value);
        }catch (Exception e){
            logger.error("redis添加失败"+e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key,String value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.srem(key,value);
        }catch (Exception e){
            logger.error("redis删除失败"+e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("redis计数异常"+e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key,String value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.sismember(key,value);
        }catch (Exception e){
            logger.error("redis判断成员异常"+e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return false;
    }

    public static void print(int index,Object obj){
        System.out.println(String.format("%d, %s",index,obj.toString()));
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("redis://123.207.13.53:6379/1");
        jedis.flushDB();

        jedis.set("hello","world");
        print(1,jedis.get("hello"));
        jedis.rename("hello","newhello");
        print(2,jedis.get("newhello"));
        jedis.setex("hello2",15,"world");

        jedis.set("pv","100");
        jedis.incr("pv");
        jedis.incrBy("pv",5);
        jedis.decrBy("pv",3);
        print(3,jedis.get("pv"));
        print(4,jedis.keys("*"));

        String listNmae = "list";
        jedis.del(listNmae);
        for (int i = 0; i < 10; ++i){
            jedis.lpush(listNmae,"a"+String.valueOf(i));
        }
        print(4,jedis.lrange(listNmae,0,12));
        print(5,jedis.lrange(listNmae,0,3));

        print(6,jedis.llen(listNmae));
        print(7,jedis.lpop(listNmae));
        print(8,jedis.llen(listNmae));
        print(9,jedis.lindex(listNmae,3));
        print(10,jedis.linsert(listNmae, BinaryClient.LIST_POSITION.AFTER,"a4","bbb"));
        print(11,jedis.linsert(listNmae, BinaryClient.LIST_POSITION.BEFORE,"a4","xxx"));
        print(12,jedis.lrange(listNmae,0,12));

        String userKey = "userxx";
        jedis.hset(userKey,"name","jim");
        jedis.hset(userKey,"age","12");
        jedis.hset(userKey,"phone","12312323123");
        print(13,jedis.hget(userKey,"name"));
        print(14,jedis.hgetAll(userKey));
        jedis.hdel(userKey,"phone");
        print(15,jedis.hgetAll(userKey));
        print(16,jedis.hexists(userKey,"email"));
        print(17,jedis.hexists(userKey,"name"));
        print(18,jedis.hkeys(userKey));
        print(19,jedis.hvals(userKey));
        jedis.hsetnx(userKey,"school","zju");
        jedis.hsetnx(userKey,"name","yxy");
        print(20,jedis.hgetAll(userKey));

        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for (int i = 0; i < 10; i++){
            jedis.sadd(likeKey1,String.valueOf(i));
            jedis.sadd(likeKey2,String.valueOf(i*i));
        }
        print(21,jedis.smembers(likeKey1));
        print(22,jedis.smembers(likeKey2));
        print(23,jedis.sunion(likeKey1,likeKey2));
        print(24,jedis.sdiff(likeKey1,likeKey2));
        print(25,jedis.sinter(likeKey1,likeKey2));
        print(26,jedis.sismember(likeKey1,"12"));
        print(27,jedis.sismember(likeKey2,"16"));
        jedis.srem(likeKey1,"5");
        print(28,jedis.smembers(likeKey1));
        jedis.smove(likeKey2,likeKey1,"25");
        print(29,jedis.smembers(likeKey1));
        print(30,jedis.smembers(likeKey2));
        print(31,jedis.scard(likeKey1));

        String rankKey = "rankKey";
        jedis.zadd(rankKey,15,"jim");
        jedis.zadd(rankKey,60,"Ben");
        jedis.zadd(rankKey,90,"Lee");
        jedis.zadd(rankKey,75,"Lucy");
        jedis.zadd(rankKey,80,"Mei");
        print(32,jedis.zcard(rankKey));
        print(33,jedis.zcount(rankKey,61,100));
        print(34,jedis.zscore(rankKey,"Lucy"));
        jedis.zincrby(rankKey,2,"Lucy");
        print(35,jedis.zscore(rankKey,"Lucy"));
        print(36,jedis.zrange(rankKey,0,100));
        print(37,jedis.zrange(rankKey,1,3));
        print(38,jedis.zrevrange(rankKey,1,3));
        for (Tuple tuple: jedis.zrangeByScoreWithScores(rankKey,"60","100")
             ) {
            print(39,tuple.getElement()+":"+String.valueOf(tuple.getScore()));
        }
        print(40,jedis.zrank(rankKey,"Ben"));
        print(41,jedis.zrevrank(rankKey,"Ben"));

        String setKey = "zset";
        jedis.zadd(setKey,1,"a");
        jedis.zadd(setKey,1,"b");
        jedis.zadd(setKey,1,"c");
        jedis.zadd(setKey,1,"d");
        jedis.zadd(setKey,1,"e");
        print(42,jedis.zlexcount(setKey,"-","+"));
        print(42,jedis.zlexcount(setKey,"[b","[d"));
        print(42,jedis.zlexcount(setKey,"(b","[d"));
        jedis.zremrangeByLex(setKey,"(c","+");
        print(43,jedis.zrange(setKey,0,2));

        JedisPool pool = new JedisPool("redis://123.207.13.53:6379/1");
        for (int i = 0; i < 100; ++i){
            Jedis j = pool.getResource();
            print(44,j.get("pv"));
            j.close();
        }

    }


}
