package cn.fciasth.zhihu.service;

import cn.fciasth.zhihu.bean.LoginTicket;
import cn.fciasth.zhihu.bean.User;
import cn.fciasth.zhihu.dao.LoginTicketDAO;
import cn.fciasth.zhihu.dao.UserDAO;
import cn.fciasth.zhihu.util.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id){
        return userDAO.selectById(id);
    }

    public User getUserByName(String name){
        return userDAO.selectByName(name);
    }

    public Map<String,String> register(String username,String password){
        Map<String,String> map = new HashMap<String, String>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user != null){
            map.put("msg","该用户已经被注册");
            return map;
        }

        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setPassword(CommonUtils.MD5(password+user.getSalt()));
        userDAO.addUser(user);
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public Map<String,String> login(String username, String password) {
        Map<String,String> map = new HashMap<String, String>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user == null){
            map.put("msg","该用户不存在");
            return map;
        }

        if(!CommonUtils.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","用户名或密码错误");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);

        return map;
    }

    public String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        loginTicket.setStatus(0);
        Date date = new Date();
        date.setTime(date.getTime()+3600*24*1000*10);
        loginTicket.setExpired(date);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }
}
