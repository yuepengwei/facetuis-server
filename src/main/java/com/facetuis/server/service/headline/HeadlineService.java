package com.facetuis.server.service.headline;

import com.facetuis.server.model.headline.Headline;
import com.facetuis.server.service.headline.vo.HeadlineVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HeadlineService {



    public PageImpl<HeadlineVO> list(){
        HeadlineVO<String> headline = new HeadlineVO();
        headline.setContent("2018年5月7日脸推App正式上线，前期致力于拼多多导购，" +
                "让更多的人加入到脸推的大家庭享受到推广的力量带来的乐趣及回报！" +
                "脸推，一个专心做产品的App，希望在接下来的日子里能够与脸推的家人一起共同成长和进步，即将对接更多的平台以追求我们不息的脚步！" +
                "我们的口号：奋斗吧青春，既省钱又赚钱，我们一起努力着" +
                "---一个有温度的App<脸推>");
        headline.setId("1");
        headline.setTitle("官方公告");
        headline.setType("TXT");
        headline.setSubTitle("SubTitle");

        HeadlineVO headline1 = new HeadlineVO();
        HeadlineVO.HeadlineContent hc1 = headline1.new HeadlineContent();
        hc1.setImg("http://xxxx/img");
        hc1.setTxt("描述");
        List< HeadlineVO.HeadlineContent> hcs1 = new ArrayList<>();
        hcs1.add(hc1);
        headline1.setContent(hcs1 );
        headline1.setId("1");
        headline1.setTitle("title");
        headline1.setType("TXT");
        headline1.setSubTitle("#2018的第一次#脸推App上线了！");



        List<HeadlineVO> list = new ArrayList<>();
        list.add(headline);
        list.add(headline1);
        return new PageImpl<HeadlineVO>(list);
    }

    public PageImpl<HeadlineVO> recommandList(){
        HeadlineVO headline = new HeadlineVO();
        HeadlineVO.HeadlineContent hc = headline.new HeadlineContent();
        hc.setImg("http://xxxx/img");
        hc.setTxt("描述");
        List< HeadlineVO.HeadlineContent> hcs = new ArrayList<>();
        hcs.add(hc);
        headline.setContent(hcs);
        headline.setId("1");
        headline.setTitle("title");
        headline.setType("TXT");


        HeadlineVO headline1 = new HeadlineVO();
        HeadlineVO.HeadlineContent hc1 = headline.new HeadlineContent();
        hc1.setImg("http://xxxx/img");
        hc1.setTxt("描述");
        List< HeadlineVO.HeadlineContent> hcs1 = new ArrayList<>();
        hcs1.add(hc1);
        headline1.setContent(hcs1 );
        headline1.setId("1");
        headline1.setTitle("title");
        headline1.setType("TXT");

        HeadlineVO headline2 = new HeadlineVO();
        HeadlineVO.HeadlineContent hc2 = headline.new HeadlineContent();
        hc2.setImg("http://xxxx/img");
        hc2.setTxt("描述");
        List< HeadlineVO.HeadlineContent> hcs2 = new ArrayList<>();
        hcs2.add(hc2);
        headline2.setContent(hcs2);
        headline2.setId("1");
        headline2.setTitle("title");
        headline2.setType("TXT");
        List<HeadlineVO> list = new ArrayList<>();
        list.add(headline);
        list.add(headline1);
        list.add(headline2);
        return new PageImpl<HeadlineVO>(list);
    }

}
