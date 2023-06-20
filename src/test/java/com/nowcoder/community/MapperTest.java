package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser(){
//        User user = userMapper.selectById(101);
        User user1 = userMapper.selectByName("zheng");
//        User user2 = userMapper.selectByEmail("nowcoder101@sina.com");
//        System.out.println(user);
        System.out.println(user1);
//        System.out.println(user2);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setHeaderUrl("http://www.nowcoder.com/101/png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser(){
        int rows = userMapper.updateStatus(153, 1);
        System.out.println(rows);
//
//        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102/png");
//        System.out.println(rows);
//
//        rows = userMapper.updatePassword(150, "654321");
//        System.out.println(rows);
    }

    @Test
    public void testSelectPost(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for(DiscussPost dp : list){
            System.out.println(dp);
        }
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println("rows:"+rows);
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testAboutMessage(){
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for(Message m : messages){
            System.out.println(m);
        }
        System.out.println("-----------------------------------------------------------------------");
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);
        System.out.println("-----------------------------------------------------------------------");
        messages = messageMapper.selectLetters("111_112", 0, 10);
        for(Message m : messages){
            System.out.println(m);
        }
        System.out.println("-----------------------------------------------------------------------");
        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);
        System.out.println("-----------------------------------------------------------------------");
        count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count);

    }

}
