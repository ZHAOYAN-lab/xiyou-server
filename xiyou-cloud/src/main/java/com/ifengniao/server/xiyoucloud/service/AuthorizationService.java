package com.ifengniao.server.xiyoucloud.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ifengniao.common.component.security.service.IAuthenticate;
import com.ifengniao.common.component.security.service.SecurityService;
import com.ifengniao.common.component.token.service.SessionTokenVerification;
import com.ifengniao.common.message.AuthorizationMessageCode;
import com.ifengniao.common.message.BaseException;
import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.JsonUtil;
import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.UserEntity;
import com.ifengniao.server.xiyoucloud.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AuthorizationService implements IAuthenticate {

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    @Autowired
    private UserService userService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private SessionTokenVerification sessionTokenVerification;

    @Override
    public ResultMsg authenticate(String userName, String userpass, String applicationCode, Map<String, Object> attach) {

        ResultMsg msg = new ResultMsg(AuthorizationMessageCode.invalid_password);

        // 检查用户名密码
        UserEntity user = userService.findByUserName(userName);
//        passwordEncoder.encode(userpass);
        if (user != null) {
            boolean pass = passwordEncoder.matches(userpass, user.getUserPass());
            if (pass) {
                msg = new ResultMsg();

                Map<String, Object> map = new HashMap<>();
                map.put("principal", JsonUtil.fromJson(JsonUtil.toJson(user)));
                map.put("from", "XiyouServer");
                /**
                 * 获取菜单权限
                 */
                List<Map<String, Object>> menuPermissions = new ArrayList<>();

                Map<String, Object> p1 = new HashMap<>();
                p1.put("action", "ALL");
                p1.put("url", "/**/*");
                menuPermissions.add(p1);

                /*Map<String, Object> p2 = new HashMap<>();
                p2.put("action", "ALL");
                p2.put("url", "/*");
                menuPermissions.add(p2);*/

                map.put("permissions", menuPermissions);

                msg.setDetail(map);
            }
        }
        return msg;
    }

    @Override
    @Transactional(readOnly = true)
    public void requestRefreshAuth() throws Exception {
        //这里可以加版本来判断是否需要更新权限
//        securityService.updateAuthorities(Collections.singletonList(new CustomGrantedAuthority(map)));
    }

    @Override
    @Transactional(readOnly = true)
    public ResultMsg checkUserAccessToken(String token) {
        try {
            String userName = sessionTokenVerification.verifyToken(token);
            if (StringUtils.isNotBlank(userName)) {
                UserEntity user = userService.findByUserName(userName);
                if (user != null) {
                    Map<String, Object> map = new HashMap<>();
                    UserDTO userDTO = new UserDTO(user);
                    map.put("principal", JsonUtil.fromJson(JsonUtil.toJson(userDTO)));
                    map.put("from", "XiyouServer");
                    /**
                     * 获取菜单权限
                     */
                    List<Map<String, Object>> menuPermissions = new ArrayList<>();

                    Map<String, Object> p1 = new HashMap<>();
                    p1.put("action", "ALL");
                    p1.put("url", "/**/*");
                    menuPermissions.add(p1);

                    /*Map<String, Object> p2 = new HashMap<>();
                    p2.put("action", "ALL");
                    p2.put("url", "/*");
                    menuPermissions.add(p2);*/

                    map.put("permissions", menuPermissions);

                    ResultMsg resultMsg = new ResultMsg();
                    resultMsg.setDetail(map);
                    return resultMsg;
                }
            }
        } catch (TokenExpiredException e) {
            return new ResultMsg(AuthorizationMessageCode.token_expired);
        } catch (Exception ignored) {
        }
        return new ResultMsg(AuthorizationMessageCode.invalid_token);
    }

    @Override
    public Map getDebugPrincipal(String name) {
        return null;
    }

    public String requestToken(String userName, String password) throws Exception {
        if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            UserEntity user = userService.findByUserName(userName);
            if (user != null) {
                boolean pass = passwordEncoder.matches(password, user.getUserPass());
                if (pass) {
                    return "Bearer " + sessionTokenVerification.createToken(user.getUserName());
                }
            }
        }
        throw new BaseException("账号/密码错误").setStrCode(Constants.ERR_F000);
    }

    public Boolean deleteToken(HttpServletRequest request) throws Exception {
        securityService.logout(request);
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isBlank(authorization)) {
            throw new BaseException("失败，未找到登录信息").setStrCode(Constants.ERR_F001);
        }
        String token = authorization.substring(7);
        return sessionTokenVerification.deleteToken(token);
    }

    public UserEntity getCurrentLogin() throws Exception {
        String userName = securityService.getUserName();
        if (StringUtils.isBlank(userName)) {
            throw new BaseException("权限校验失败").setStrCode(Constants.ERR_F002);
        }
        UserEntity self = userService.findByUserName(userName);
        if (self == null) {
            throw new BaseException("权限校验失败").setStrCode(Constants.ERR_F002);
        }
        return self;
    }

}
