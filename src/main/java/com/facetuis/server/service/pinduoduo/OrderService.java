package com.facetuis.server.service.pinduoduo;

import com.alibaba.fastjson.JSONObject;
import com.facetuis.server.dao.order.OrderRepository;
import com.facetuis.server.dao.user.UserRelationRepository;
import com.facetuis.server.dao.user.UserRepository;
import com.facetuis.server.model.order.Order;
import com.facetuis.server.model.user.User;
import com.facetuis.server.model.user.UserRelation;
import com.facetuis.server.service.basic.BaseResult;
import com.facetuis.server.service.pinduoduo.response.OrderDetail;
import com.facetuis.server.service.pinduoduo.response.OrderListResponse;
import com.facetuis.server.service.pinduoduo.response.OrderVO;
import com.facetuis.server.service.pinduoduo.utils.PRequestUtils;
import com.facetuis.server.utils.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.logging.Logger;

@Service
public class OrderService {

    private static final Logger logger = Logger.getLogger("订单业务逻辑");

    @Autowired
    private PRequestUtils pRequestUtils;

    private String API_ORDER_RANGE = "pdd.ddk.order.list.range.get";
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRelationRepository userRelationRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * 查询订单
     * @param startTime
     * @param endTime
     * @param pid
     * @param page
     * @return
     */
    public OrderListResponse getOrder(String startTime,String endTime,String pid,int page) {
        SortedMap<String, String> map = new TreeMap<>();
        map.put("start_time", startTime);
        map.put("end_time", endTime);
        map.put("p_id", pid);
        map.put("page", page + "");
        map.put("page_size", "50");
        BaseResult<String> send = pRequestUtils.send(API_ORDER_RANGE, map);
        OrderListResponse response = JSONObject.parseObject(send.getResult(), OrderListResponse.class);
        return response;
    }

    /**
     * 批量更新订单
     * @param orderList
     */
    @Transactional
    public void updateOrder(OrderListResponse orderList){
        if(orderList.getOrder_list_get_response() == null){
            return;
        }
        List<OrderDetail> order_list = orderList.getOrder_list_get_response().getOrder_list();
        if(order_list != null && order_list.size() > 0){
            List<String> orderSnList = new ArrayList<>();
            for(OrderDetail order : order_list){
                orderSnList.add(order.getOrderSn());
            }
            orderRepository.deleteByOrderSnIn(orderSnList);
            orderRepository.flush();
            List<Order> orders = new ArrayList<>();
            for(OrderDetail detail : order_list){
                Order order = new Order();
                order.setUuid(UUID.randomUUID().toString());
                order.setCreateTime(new Date());
                BeanUtils.copyProperties(detail,order);
                orders.add(order);
            }
            orderRepository.saveAll(orders);
        }
    }

    /**
     * 获取团队所有订单
     * @param userId
     * @return
     */
    public Page<Order> findTeamOrders(String userId,Pageable pageable){
        String startTime = TimeUtils.date2String(TimeUtils.getDateBefore(new Date(), 60)) + " 00:00:00";
        String endTime = TimeUtils.date2String(new Date()) + " 23:59:59";
        return findByDate(startTime,endTime,userId,pageable);
    }


    /**
     * 根据指定日期获取团队订单
     * @param startTime
     * @param endTime
     * @param userId
     * @return
     */
    public Page<Order> findByDate(String startTime, String endTime, String userId, Pageable pageable){
         long star =  TimeUtils.stringToDateTime(startTime).getTime()/1000;
         long end = TimeUtils.stringToDateTime(endTime).getTime()/1000;
        List<String> pids = getTeamPids(userId);
        if(pids.size() == 0){
            logger.info("未找到推广位");
            return new PageImpl<>(Collections.EMPTY_LIST);
        }
        Page<Order> orders = orderRepository.findOrder(pids, star, end,pageable);
        return orders;
    }


    /**
     * 根据指定日期获取团队订单
     * @param startTime
     * @param endTime
     * @param userId
     * @return
     */
    public Page<Order> findByDateAndStatus(String startTime, String endTime, String userId,int status ,Pageable pageable){
        long star =  TimeUtils.stringToDateTime(startTime).getTime()/1000;
        long end = TimeUtils.stringToDateTime(endTime).getTime()/1000;
        List<String> pids = getTeamPids(userId);
        if(pids.size() == 0){
            logger.info("未找到推广位");
            return new PageImpl<>(Collections.EMPTY_LIST);
        }
        Page<Order> orders = orderRepository.findOrderByStatus(pids, star, end,status,pageable);
        return orders;
    }



    /**
     * 根据订单状态查询
     * @param userId
     * @param status
     * @param pageable
     * @return
     */
    public Page<OrderVO> findByStatus(String userId, int status, Pageable pageable){
        Page<Order> orders = null;
        if(status == 0){
            orders = findTeamOrders(userId, pageable);
        }else{
            String startTime = TimeUtils.date2String(TimeUtils.getDateBefore(new Date(), 60)) + " 00:00:00";
            String endTime = TimeUtils.date2String(new Date()) + " 23:59:59";
            orders = findByDateAndStatus(startTime,endTime,userId,status,pageable);
        }
        if(orders == null){
            return new PageImpl<OrderVO>(Collections.EMPTY_LIST);
        }
        return getOrderVo(orders.getContent());
    }


    /**
     * 查询团队订单
     * @param userId
     * @param mobile
     * @param nickName
     * @param status
     * @param pageable
     * @return
     */
    public Page<OrderVO> findByMobileOrNickName(String userId, String mobile,String nickName,int status,Pageable pageable){
        UserRelation userRelation = userRelationRepository.findByUserId(userId);// 当前用户的所有团队成员
        String user3Ids = userRelation.getUser3Ids();
        String user2Ids = userRelation.getUser2Ids();
        String user1Ids = userRelation.getUser1Ids();
        String pid = "";
        if(!StringUtils.isEmpty(mobile)){
            User user = userRepository.findByMobileNumber(mobile);
            pid = user.getPid();
        }else if(!StringUtils.isEmpty(nickName)){
            List<User> byNickNameLike = userRepository.findByNickName("%" + nickName + "%");
            if(byNickNameLike.size() >= 1){
                pid = byNickNameLike.get(0).getPid();
            }
        }
        if(StringUtils.isEmpty(pid)){
            return new PageImpl<OrderVO>(Collections.EMPTY_LIST) ;
        }
        if( user1Ids.contains(pid) || user2Ids.contains(pid) || user3Ids.contains(pid) ){
            String startTime = TimeUtils.date2String(TimeUtils.getDateBefore(new Date(), 60)) + " 00:00:00";
            String endTime = TimeUtils.date2String(new Date()) + " 23:59:59";
            List<String> pids = new ArrayList<>();
            pids.add(pid);
            long star =  TimeUtils.stringToDateTime(startTime).getTime()/1000;
            long end = TimeUtils.stringToDateTime(endTime).getTime()/1000;
            Page<Order> orders = orderRepository.findOrderByStatus(pids, star, end, status, pageable);
            return getOrderVo(orders.getContent());
        }
        return  new PageImpl<OrderVO>(Collections.EMPTY_LIST) ;
    }

    /**
     * 获取团队所有推广位
     * @param userId
     * @return
     */
    private List<String> getTeamPids(String userId){
        UserRelation userRelation = userRelationRepository.findByUserId(userId);// 当前用户的所有团队成员
        if(userRelation == null){
            logger.info("未找到团队");
            return Collections.EMPTY_LIST;
        }
        String user1Ids = userRelation.getUser1Ids();// 所属一级用户
        List<String> user1s = new ArrayList<>();
        if(!StringUtils.isEmpty(user1Ids)){
            String[] split = user1Ids.split(",");
            user1s = Arrays.asList(split);
        }
        String user2Ids = userRelation.getUser2Ids();// 所属二级用户
        List<String> user2s = new ArrayList<>();
        if(!StringUtils.isEmpty(user2Ids)){
            String[] split = user2Ids.split(",");
            user2s = Arrays.asList(split);
        }
        String user3Ids = userRelation.getUser3Ids();// 所属三级用户
        List<String> user3s = new ArrayList<>();
        if(!StringUtils.isEmpty(user3Ids)){
            String[] split = user3Ids.split(",");
            user3s = Arrays.asList(split);
        }
        user1s.addAll(user2s);
        user1s.addAll(user3s);
        List<User> allUsers = userRepository.findAllById(user1s);
        List<String> pids = new ArrayList<>();
        for(User user : allUsers){
            pids.add(user.getPid());
        }
        return pids;
    }

    private Page<OrderVO> getOrderVo(List<Order> content){
        if(content.size() > 0){
            List<OrderVO> list = new ArrayList<>();
            for(Order order : content){
                OrderVO vo = new OrderVO();
                User user = userRepository.findByPid(order.getpId());
                if(user != null){
                    BeanUtils.copyProperties(user,vo);
                    vo.setUserId(user.getUuid());
                }
                BeanUtils.copyProperties(order,vo);
                list.add(vo);
            }
            return new PageImpl<OrderVO>(list);
        }
        return new PageImpl<OrderVO>(Collections.EMPTY_LIST);
    }
    /* 根据pid获取所有订单

     */

    public Page<Order> getOrdersByPid(String pid)
    {
        return orderRepository.findByPId(pid);
    }

}
