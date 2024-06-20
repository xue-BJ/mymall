package com.ryw.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ryw.entity.User;
import com.ryw.entity.Userinfo;
import com.ryw.mapper.UserMapper;
import com.ryw.mapper.UserinfoMapper;
import com.ryw.util.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller               //返回值直接跳转
    @CrossOrigin
    @RequestMapping("/tourist")
    public class RegisterController {

        @Value("${mail.fromMail.sender}")
        private String sender;// 发送者

        @Autowired
        private JavaMailSender javaMailSender;

        @Autowired
        private UserMapper userMapper;

        @Autowired
        private UserinfoMapper userinfoMapper;

        List<Map<String, Object>> list = new ArrayList<>();

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        //注册
        @RequestMapping("/register")
        @ResponseBody
        public String register(@RequestParam String username, @RequestParam String password,
                               @RequestParam String email, @RequestParam String identify,
                               User user)
        {
//            if(testCode(identify,email)){
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                wrapper.eq("username",username).or().eq("email",email);   //姓名邮箱都不得重复
                User usertarge = userMapper.selectOne(wrapper); // 查询一个数据，出现多个结果使用List或者Map
                if (usertarge == null) {             //不重复，放入数据库
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);
                    int result = userMapper.insert(user);		//会自动生成id(自动生成唯一id)
                    //System.out.println(result);    插入结果的返回值是受影响的行数
                    if(result!=0){ //插入成功   代码  insertsuccess

                        /*拿到注册的id*/
                        QueryWrapper<User> wrapper2 = new QueryWrapper<>();
                        wrapper2.eq("email",email);
                        User usertarge2 = userMapper.selectOne(wrapper2);
                        Long id = usertarge2.getId();
                        /*自动生成usrinfo信息*/
                        setuserinfo(id);

                        return "insertsuccess";
                    }
                    return   "0";           //插入操作失败    代码0
                }
                return "00" ;                 //用户名或者邮箱重复  代码00
        }

        // 验证修改密码的邮箱
        @RequestMapping("/forgetpasswordtest")
        @ResponseBody
        public String register(@RequestParam String email)
        {
            //      检查一下数据库中的email是否存在该email然后加上sandemail
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("email",email);
            User usertarge = userMapper.selectOne(wrapper);
                return "noemail";      //email未注册，请检查后重试
        }

        //更新密码
        @RequestMapping("/updatepassword")
        @ResponseBody   //   参数： 更新后的密码 ，邮箱
        public int updatepassword(@RequestParam String password,@RequestParam String email)
        {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("email",email);
            User usertarge = userMapper.selectOne(wrapper);
            usertarge.setPassword(password);
            int i = 0;
             i = userMapper.updateById(usertarge);
            return i ;
        }


        // 自动生成userinfo内容   参数id

        public void setuserinfo(@RequestParam Long id)
        {
            Userinfo userinfo = new Userinfo();
            userinfo.setId(id);
            userinfo.setAdress("insert your adress");
            userinfo.setTximg("/tximg/inittximg.jpg");
            userinfo.setAge(18);
            userinfoMapper.insert(userinfo);
        }
    }

