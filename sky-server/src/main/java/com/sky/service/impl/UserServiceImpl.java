package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private static final String WX_LOGIN ="https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String openid = getOpenid(userLoginDTO.getCode());

        //判断openid是否为空如果为空就抛异常
        if (openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //不为空就检查user表中是否有这样的用户
        User user = userMapper.selectByOpenId(openid);
        //如果说为空 那么我们就手动将这个用户信息封装成一个User并注册到用户表中
        if (user == null){
            //这里就暂时就知道一个openid 后续信息可以根据个人中心去完善
            user = User.builder().openid(openid).build();
            userMapper.register(user);
        }
        return user;
    }

    //将获取openid的代码抽取为一个方法
    private String getOpenid(String code){
        //创建HttpClient对象发送请求到微信服务器 获取到openid
        Map<String, String> params = new HashMap<>();
        params.put("appid", weChatProperties.getAppid());
        params.put("secret", weChatProperties.getSecret());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");
        //发送请求并获取响应体中的结果
        String response = HttpClientUtil.doGet(WX_LOGIN, params);
        log.info("响应体中的结果为: {}", response);
        //将response转为json 再通key去获取openId
        JSONObject jsonObject = JSON.parseObject(response);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
