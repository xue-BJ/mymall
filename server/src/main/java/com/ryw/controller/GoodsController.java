package com.ryw.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryw.entity.*;
import com.ryw.mapper.*;
//import jdk.jpackage.internal.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
public class GoodsController {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsimgMapper goodsimgMapper;
    @Autowired
    private SizerestcountMapper sizerestcountMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserinfoMapper userinfoMapper;
    @Autowired
    private UserorderMapper userorderMapper;

    @CrossOrigin
    @RequestMapping("/allgoodsinfo")            //查询全部
    public String getallgoodsinfo() {
        List<Goods> goodsList = goodsMapper.selectList(null);
        for (int i = 0; i < goodsList.size(); i++) {
            Long goodsid = goodsList.get(i).getGoodsid();     //拿到goodsid查找到该goods的其他属性

            QueryWrapper<Goodsimg> wrappergoodsimg = new QueryWrapper<>();
            wrappergoodsimg.eq("goodsid", goodsid);
            List<Goodsimg> goodsimgList = goodsimgMapper.selectList(wrappergoodsimg);
            goodsList.get(i).setImglist(goodsimgList);

            QueryWrapper<Sizerestcount> wrappergoodssize = new QueryWrapper<>();
            wrappergoodssize.eq("goodsid", goodsid);
            List<Sizerestcount> sizerestcountList = sizerestcountMapper.selectList(wrappergoodssize);
            goodsList.get(i).setSizecountlist(sizerestcountList);

        }
        String userallinfo_json = JSON.toJSONString(goodsList);
        return userallinfo_json;
    }

    @RequestMapping("/getoutgoods")            //查询outgoods
    public String getoutgoods() {
        List<Goods> goodsList = goodsMapper.getoutgoods();
        for (int i = 0; i < goodsList.size(); i++) {
            Long goodsid = goodsList.get(i).getGoodsid();     //拿到goodsid查找到该goods的其他属性

            QueryWrapper<Goodsimg> wrappergoodsimg = new QueryWrapper<>();
            wrappergoodsimg.eq("goodsid", goodsid);
            List<Goodsimg> goodsimgList = goodsimgMapper.selectList(wrappergoodsimg);
            goodsList.get(i).setImglist(goodsimgList);

            QueryWrapper<Sizerestcount> wrappergoodssize = new QueryWrapper<>();
            wrappergoodssize.eq("goodsid", goodsid);
            List<Sizerestcount> sizerestcountList = sizerestcountMapper.selectList(wrappergoodssize);
            goodsList.get(i).setSizecountlist(sizerestcountList);

        }
        String userallinfo_json = JSON.toJSONString(goodsList);
        return userallinfo_json;
    }

    @RequestMapping("/allgoodspagequery")   // 分页查询  和全部数据条数
    public String allgoodspagequery(@RequestParam("pageNum") int pageNum,
                                    @RequestParam("pageSize") int pageSize) {   //接收传来的参数，这里了封装一个实体类
        Page<Goods> page = new Page<>(pageNum, pageSize);
        goodsMapper.selectPage(page, null);
        List<Goods> goodslist = page.getRecords();  //分页查询
        for (int i = 0; i < goodslist.size(); i++) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("goodsid", goodslist.get(i).getGoodsid());
            List<Sizerestcount> sizerestcounts = sizerestcountMapper.selectList(queryWrapper);
            goodslist.get(i).setSizecountlist(sizerestcounts);
        }
        HashMap<String, Object> res = new HashMap<>();
        long numbers = page.getTotal();// 总条数
        res.put("numbers", numbers);
        res.put("data", goodslist);
        String json = JSON.toJSONString(res);
        return json;
    }


    @RequestMapping("/querygoods")
    public List<Goods> querygoods(@RequestParam("goodsname") String goodsname) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.like(goodsname != null, "goodsname", goodsname);
        List<Goods> goodslist = goodsMapper.selectList(queryWrapper);
        return goodslist;
    }

    //查询单个图片请求，用于显示单个图片
    @RequestMapping("/querygoodById")
    public Goodsimg querygoodById(@RequestParam("goodsid") String goodsid) {
        QueryWrapper<Goodsimg> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goodsid",goodsid).last("LIMIT 1");
        return goodsimgMapper.selectOne(queryWrapper);
    }

    @RequestMapping("/buy")
    public String buy(@RequestParam("userid") Long userid,
                      @RequestParam("goodsid") Long goodsid,
                      @RequestParam("size") String size) {
        /*
         * 拿到user对象     userinfo对象     goods对象  goodsimg对象  取出需要的属性存入order对象中
         * */
        QueryWrapper wrapperuser = new QueryWrapper();
        wrapperuser.eq("id", userid);
        User user = userMapper.selectOne(wrapperuser);

        Userinfo userinfo = userinfoMapper.selectOne(wrapperuser);

        QueryWrapper wrappergoods = new QueryWrapper();
        wrappergoods.eq("goodsid", goodsid);
        Goods goods = goodsMapper.selectOne(wrappergoods);

        List<Goodsimg> goodsimgList = goodsimgMapper.selectList(wrappergoods);

        Userorder userorder = new Userorder();
        userorder.setId(userid);
        userorder.setGoodstitle(goods.getGoodstitle());
        userorder.setGoodsprice(goods.getPrice());

        userorder.setGoodsimg(goodsimgList.get(0).getGoodsimg());
        userorder.setAdress(userinfo.getAdress());
        userorder.setUsername(user.getUsername());
        userorder.setEmail(user.getEmail());
        userorder.setSize(size);
        userorderMapper.insert(userorder);

//       购买成功库存后续操作,库存减少,优惠券减少，销量加一等等
        countsub(goodsid, size);
        sellcount(goodsid);
        return "success";
    }

    @RequestMapping("/uploadgoodsimg")            //指定商品图片上传
    public String uploadgoodsimg(@RequestParam("file") MultipartFile file, String goodsid) {
        System.out.println("进入了上传图片方法中");
        Goodsimg goodsimg = new Goodsimg();
        System.out.println(goodsid);
        goodsimg.setGoodsid(Long.parseLong(goodsid));
//        //查找刚刚写入的数据，进行改写路径
        QueryWrapper<Goodsimg> wrapper = new QueryWrapper<>();
        wrapper.eq("goodsid", goodsid).last("LIMIT 1");
        Goodsimg goodsimg1 = goodsimgMapper.selectOne(wrapper);
        if (!goodsid.isEmpty()) {   //如果商品图片里面没有这个商品id则先添加再查询
            goodsimgMapper.insert(goodsimg);
            goodsimg1 = goodsimgMapper.selectOne(wrapper);
        }
//        Long random = goodsimg1.getImgtableid();
        String random = UUID.randomUUID().toString();
        //存放图片并更新数据库
        try {
            //获取项目根目录
            File f = new File("");
            String rootPath = f.getCanonicalPath();
            //上传文件
            File path = new File(rootPath + "\\src\\main\\resources\\public\\img\\goodsimg"); //图片地址
            if (!path.exists()) {
                path.mkdir();
            }
            String imgName = "goodsimg" + goodsid + "(" + random + ").jpg";
            File tofile = new File(path, imgName);
            System.out.println("tofile" + tofile.getPath());
            file.transferTo(tofile);
            //更新数据库中对应图片的地址
            goodsimg1.setGoodsimg("http://localhost:9000/img/goodsimg/" + imgName);
            goodsimgMapper.updateById(goodsimg1);
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
            return "false";
        }
    }

    //    商品上新
    @RequestMapping("/addgoods")
    public String addgoods(@RequestParam("goodsname") String goodsname,
                           @RequestParam("category") String category,
                           @RequestParam("price") String price,
                           @RequestParam("goodsdescribe") String goodsdescribe,
                           @RequestParam("goodstitle") String goodstitle) {
        /*
        拿到参数的第一步转换类型
         * */
        Goods goods = new Goods();
        goods.setPrice(Float.parseFloat(price));
        goods.setNowprice(Float.parseFloat(price));
        goods.setGoodsname(goodsname);
        goods.setCategory(category);
        goods.setGoodsdescribe(goodsdescribe);
        goods.setGoodstitle(goodstitle);
        goodsMapper.insert(goods);
        return "success";
    }

    //    商品修改
    @RequestMapping("/editgoods")
    public String editgoods(@RequestParam("goodsid") String goodsid,
                            @RequestParam("goodsname") String goodsname,
                            @RequestParam("category") String category,
                            @RequestParam("nowprice") String nowprice,
                            @RequestParam("goodsdescribe") String goodsdescribe,
                            @RequestParam("goodstitle") String goodstitle) {

        Goods goods = goodsMapper.selectById(Long.parseLong(goodsid));
        goods.setGoodsname(goodsname);
        goods.setCategory(category);
        double nowpriceold = goods.getNowprice();
        goods.setPrice(nowpriceold);
        goods.setNowprice(Float.parseFloat(nowprice));
        goods.setGoodsdescribe(goodsdescribe);
        goods.setGoodstitle(goodstitle);
        int result = goodsMapper.updateById(goods);

        if (result != 0) {
            return "success";
        }
        return "error";
    }

    @RequestMapping("/addsizecount")            //添加尺码
    public String addsizecount(@RequestParam("goodsid") String goodsid,
                               @RequestParam("size") String size,
                               @RequestParam("count") String count) {
        //检查数据库中字符是否重复，类型转换，放入数据库
        QueryWrapper<Sizerestcount> wrapper = new QueryWrapper<>();
        wrapper.eq("size", size).eq("goodsid", goodsid);
        List<Sizerestcount> sizerestcounts = sizerestcountMapper.selectList(wrapper);
        if (sizerestcounts.size() == 0) {
            Sizerestcount sizerestcount = new Sizerestcount();
            sizerestcount.setSize(size);
            sizerestcount.setGoodsid(Long.parseLong(goodsid));
            sizerestcount.setRestcount(Integer.parseInt(count));
            sizerestcountMapper.insert(sizerestcount);
            return "success";
        } else {
            return "error";//size尺寸重复，如需修改size尺寸请移步修改数据
        }

    }


    @RequestMapping("/editsizecount")            //添加尺码
    public String editsizecount(@RequestParam("goodsid") String goodsid,
                                @RequestParam("size") String size,
                                @RequestParam("count") String count) {
        //检查数据库中字符是否重复，类型转换，放入数据库
        QueryWrapper<Sizerestcount> wrapper = new QueryWrapper<>();
        wrapper.eq("size", size).eq("goodsid", goodsid);
        Sizerestcount sizerestcountupdata = sizerestcountMapper.selectOne(wrapper);
        sizerestcountupdata.setRestcount(Integer.parseInt(count));
        int result = sizerestcountMapper.updateById(sizerestcountupdata);
        if (result != 0) {
            return "success";
        }
        return "false";
    }

    public void countsub(Long goodsid, String size) {
        QueryWrapper wrapper = new QueryWrapper();
        Map<String, Object> params = new HashMap<>();
        params.put("goodsid", goodsid);
        params.put("size", size);
        wrapper.allEq(params, false);
        Sizerestcount sizerestcount = sizerestcountMapper.selectOne(wrapper);
        int count = sizerestcount.getRestcount();
        count--;
        sizerestcount.setRestcount(count);
        sizerestcountMapper.update(sizerestcount, wrapper);
    }

    public void sellcount(Long goodsid) {
        Goods goods = goodsMapper.selectById(goodsid);
        int sellcount = goods.getSellcount();
        sellcount++;
        goods.setSellcount(sellcount);
        goodsMapper.updateById(goods);
    }
}
