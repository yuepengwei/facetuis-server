package com.facetuis.server.utils;

import com.alipay.api.domain.GoodsDetail;
import com.facetuis.server.service.pinduoduo.response.GoodsDetails;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class GoodsImageUtils {

    public static byte[] createImage(String backgroundImgPath, GoodsDetails goodsDetail,String link){
        String goodsImageUrl = goodsDetail.getGoods_image_url();
        // 获取产品主图
        BufferedImage goodsImage = null;
        try {
            goodsImage = ImageIO.read(new URL(goodsImageUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 根据推广链接生成二维码图片
        BufferedImage rqImageBuffered = RQCodeUtils.encode(link);

        BufferedImage resultImage = null;
        try {
            resultImage = ImageIO.read(new File(backgroundImgPath));
            Graphics graphics = resultImage.createGraphics();
            // 商品图片
            graphics.drawImage(goodsImage,0,0,750,760,null);
            // 卷后价格
            graphics.setFont( new Font("幼圆", Font.BOLD, 45));
            graphics.setColor(new Color(255, 71, 89));
            graphics.drawString("￥" + CommisionUtils.divide ((goodsDetail.getMin_group_price() - goodsDetail.getCoupon_discount())/1.0,100.00,2) ,10,827);
            // 优惠券价格
            graphics.setFont( new Font("幼圆", Font.BOLD, 30));
            graphics.setColor(new Color(255, 255, 255));
            graphics.drawString("￥" + CommisionUtils.divide ((goodsDetail.getCoupon_discount())/1.0,100.00,0) ,632,865);
            // 原价
            graphics.setFont( new Font("幼圆", Font.BOLD, 22));
            graphics.setColor(new Color(136, 136, 136));
            graphics.drawString("原价 ￥" + CommisionUtils.divide ((goodsDetail.getMin_group_price())/1.0,100.00,2) ,10,870);

            // 标题
            graphics.setFont( new Font("幼圆", Font.BOLD, 28));
            graphics.setColor(new Color(0, 0, 0));
            String goods_name = goodsDetail.getGoods_name();
            String name1 = "";
            String name2 = "";
            if(goods_name.length() > 20){
                name1 = goods_name.substring(0,19);
                name2 = goods_name.substring(20,goods_name.length() - 1);
            }else{
                name1 = goods_name;
            }
            graphics.drawString(name1 ,135,923);
            if(!StringUtils.isEmpty(name2)){
                graphics.drawString(name2 ,10,980);
            }

            //二维码
            graphics.drawImage(rqImageBuffered,60*2,538*2,180,180,null);

            graphics.dispose();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            boolean flag = ImageIO.write(resultImage, "jpg", out);
            if(flag){
                return out.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}