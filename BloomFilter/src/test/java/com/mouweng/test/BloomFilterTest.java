package com.mouweng.test;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.mouweng.dao.UserDao;
import com.mouweng.domain.User;
import com.mouweng.utils.SerializeUtil;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import java.util.List;
import static java.lang.Thread.*;

public class BloomFilterTest {

    /**
     * 数据库调用接口
     */
    UserDao userDao;
    /**
     * jedis连接
     */
    Jedis jedis;


    /**
     * 初始化工具
     */
    @Before
    public void before() {
        userDao = new UserDao();
        jedis = new Jedis("localhost");
    }

    /**
     *redis序列化
     */
    @Test
    public void RedisSerialize() {
        User user = userDao.findByUid(1);
        jedis.set("MyUser".getBytes(), SerializeUtil.serialize(user));
    }

    /**
     * redis反序列化
     */
    @Test
    public void RedisReSerialize() {
        byte[] userbyte = jedis.get(("MyUser").getBytes());
        User user = (User) SerializeUtil.unserialize(userbyte);
        System.out.println(user);
    }

    /**
     *  所有对象进行UserRedis序列化
     */
    @Test
    public void RedisInit() {
        List<User> users = userDao.findAll();
        for(User user : users) {
            jedis.lpush("user-list".getBytes(), SerializeUtil.serialize(user));
            jedis.sadd("user-add",user.getUid().toString());
        }

    }

    /**
     *  所有对象进行UserRedis反序列化
     */
    @Test
    public void RedisGet() {
        List<byte[]> list = jedis.lrange(("user-list").getBytes(),0,jedis.llen("user-list") - 1);
        for(int i=0; i<list.size(); i++) {
            byte[] userbyte = list.get(i);
            User user = (User) SerializeUtil.unserialize(userbyte);
            System.out.println(user);
        }
    }



    @Test
    public void TimeCal() throws InterruptedException {
        // 开始时间
        long stime = System.currentTimeMillis();
        // 执行时间（1s）
        sleep(1000);
        // 结束时间
        long etime = System.currentTimeMillis();
        // 计算执行时间
        System.out.printf("执行时长：%d 毫秒.", (etime - stime));
    }


    @Test
    public void BloomFilter() {
        int num = 0;
        // 初始化布隆过滤器
        int exceptedInsertion = 10000;
        List<User> users = userDao.findAll();
        BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(),exceptedInsertion);
        for(User user : users) {
            bloomFilter.put(user.getUid());
        }

        long stime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i ++) {
            if (bloomFilter.mightContain(i)) {
                num ++;
                if(!jedis.sismember("user-add", String.valueOf(i))) {
                    User user = userDao.findByUid(i);
                    System.out.println(user);
                }
            }
        }
        long etime = System.currentTimeMillis();

        System.out.printf("执行时长：%d 毫秒.\n", (etime - stime));
        System.out.println("布隆过滤器过滤 : "  + (100000 - num));
        System.out.println("命中redis : "  + num);
    }

    @Test
    public void NonBloomFilter() {
        int num = 0;
        long stime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i ++) {
            if(!jedis.sismember("user-add", String.valueOf(i))) {
                num ++;
                User user = userDao.findByUid(i);
            }
        }
        long etime = System.currentTimeMillis();
        System.out.printf("执行时长：%d 毫秒.\n", (etime - stime));
        System.out.println("命中redis : "  + (100000 - num));
        System.out.println("查询数据库 : "  + (num));
    }
}
