package com.facetuis.server.app.web;

import com.facetuis.server.app.web.basic.BaseResponse;
import com.facetuis.server.app.web.basic.FacetuisController;
import com.facetuis.server.model.order.Order;
import com.facetuis.server.model.user.User;
import com.facetuis.server.service.pinduoduo.OrderService;
import com.facetuis.server.utils.NeedLogin;
import com.facetuis.server.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/1.0/order")
public class OrderController extends FacetuisController {


    @Autowired
    private OrderService orderService;

    @RequestMapping("/team/{orderStatus}")
    @NeedLogin(needLogin = true)
    public BaseResponse getTeamOrder(@PathVariable int orderStatus , String n, String m){
        User user = getUser();

        return successResult();
    }
}