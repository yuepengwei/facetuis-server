package com.facetuis.server.app.web;

import com.facetuis.server.app.web.basic.BaseResponse;
import com.facetuis.server.app.web.basic.FacetuisController;
import com.facetuis.server.app.web.request.UpgradedRequest;
import com.facetuis.server.app.web.response.UserRecommanderResponse;
import com.facetuis.server.model.user.User;
import com.facetuis.server.model.user.UserLevel;
import com.facetuis.server.service.user.UserService;
import com.facetuis.server.utils.NeedLogin;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/1.0/user")
public class UserController extends FacetuisController{

    @Autowired
    private UserService userService;

    /**
     * 获取推荐自己的用户
     * @return
     */
    @RequestMapping(value = "/recommender",method = RequestMethod.GET)
    @NeedLogin(needLogin = true)
    public BaseResponse getCommander() {
        User user = getUser();
        UserRecommanderResponse response = new UserRecommanderResponse();
        User recommander = userService.findRecommander(user.getUuid());
        if(recommander == null){
            return successResult(response);
        }
        BeanUtils.copyProperties(recommander, response);
        return successResult(new UserRecommanderResponse());
    }

    /**
     * 用户升级
     * @return
     */
    @RequestMapping(value = "/upgraded",method = RequestMethod.POST)
    @NeedLogin(needLogin = true)
    public BaseResponse upgraded(@RequestBody UpgradedRequest request, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return setErrorResult(bindingResult,400,"请求参数错误");
        }
        User user = getUser();
        if(user.getLevel().equals(UserLevel.LEVEL2)){
            return setErrorResult(600,"已经是最高级");
        }
        userService.upload(user.getUuid());

        return null;

    }




}
