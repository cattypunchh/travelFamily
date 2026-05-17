package com.travelfamilies;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.travelfamilies.exception.BusinessException;
import com.travelfamilies.mapper.UserMapper;
import com.travelfamilies.request.hotelRequest.HotelRequest;
import com.travelfamilies.request.hotelRequest.RoomRequest;
import com.travelfamilies.request.spotRequest.SpotRequest;
import com.travelfamilies.request.userRequest.RegisterRequest;
import com.travelfamilies.response.Result;
import com.travelfamilies.response.UserResponse;
import com.travelfamilies.service.AdminService;
import com.travelfamilies.service.HotelService;
import com.travelfamilies.service.SpotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SpringBootTest
class TravelFamiliesApplicationTests {

    @Autowired
    private SpotService spotService;
    @Autowired
    private HotelService hotelService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RestTemplate restTemplate;

    private final String AMAP_KEY = "3702909104b2c6f22ae18732e975fd63";

    // OSS 配置
    private static final String OSS_ENDPOINT = "oss-cn-hangzhou.aliyuncs.com";
    private static final String OSS_ACCESS_KEY = "LTAI5t7z2kAJh1CmUw1PDMko";
    private static final String OSS_SECRET_KEY = "1RQLHiXAPpVipQlfdWJ7QeDbMdvY7y";
    private static final String OSS_BUCKET = "travel-families-img";
    private static final String OSS_URL_PREFIX = "https://travel-families-img.oss-cn-hangzhou.aliyuncs.com/";

    private OSS ossClient;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

    private OSS getOssClient() {
        if (ossClient == null) {
            ossClient = new OSSClientBuilder().build(OSS_ENDPOINT, OSS_ACCESS_KEY, OSS_SECRET_KEY);
        }
        return ossClient;
    }

    /**
     * 下载高德图片并上传到 OSS，返回 OSS 链接
     */
    private String uploadToOss(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            InputStream inputStream = conn.getInputStream();

            String fileName = "hotel/" + UUID.randomUUID() + ".jpg";
            getOssClient().putObject(OSS_BUCKET, fileName, inputStream);
            inputStream.close();
            conn.disconnect();

            return OSS_URL_PREFIX + fileName;
        } catch (Exception e) {
            System.err.println("上传图片失败: " + imageUrl + ", " + e.getMessage());
            return imageUrl;
        }
    }

    @Test
    void fetchHotelAndInventory() {
        String[] cities = {
                "110000", "310000", "440100", "440300", "510100", "330100", "610100", "500000",
                "420100", "320100", "370200", "350200", "530100", "430100", "340100", "320500",
                "210100", "220100", "230100", "350100", "360100", "370100", "410100", "450100",
                "460100", "520100", "540100", "620100", "630100", "640100", "650100"
        };

        for (String adcode : cities) {
            String url = "https://restapi.amap.com/v3/place/text?keywords=酒店&city=" + adcode + "&offset=10&page=1&key=" + AMAP_KEY;

            try {
                String response = restTemplate.getForObject(url, String.class);
                JSONObject json = JSON.parseObject(response);
                JSONArray pois = Objects.requireNonNull(json).getJSONArray("pois");

                if (pois == null) continue;

                for (int i = 0; i < pois.size(); i++) {
                    JSONObject poi = pois.getJSONObject(i);

                    RegisterRequest adminRequest = generateRandomAdminRequest();
                    Long adminId = registerRandomAdmin(adminRequest);

                    if (adminId == null) {
                        System.err.println("管理员注册失败，跳过酒店: " + poi.getString("name"));
                        continue;
                    }

                    HotelRequest hotelReq = new HotelRequest();
                    hotelReq.setName(poi.getString("name"));
                    hotelReq.setCity(poi.getString("cityname"));
                    hotelReq.setDistrict(poi.getString("adname"));

                    String bizArea = poi.getString("business_area");
                    hotelReq.setBusinessArea((bizArea == null || bizArea.equals("[]")) ? "热门商圈" : bizArea);
                    hotelReq.setAddress(poi.getString("address"));
                    hotelReq.setDescription(poi.getString("name") + "为您提供舒适的住宿体验。");
                    hotelReq.setStarLevel(4);
                    hotelReq.setBasePrice(299.0);
                    hotelReq.setStatus(1);
                    hotelReq.setAdminId(adminId);

                    // 处理图片 - 上传到 OSS
                    List<String> hotelImages = new ArrayList<>();
                    JSONArray photos = poi.getJSONArray("photos");
                    if (photos != null && !photos.isEmpty()) {
                        for (int j = 0; j < photos.size(); j++) {
                            String photoUrl = photos.getJSONObject(j).getString("url");
                            String ossUrl = uploadToOss(photoUrl);
                            hotelImages.add(ossUrl);
                        }
                    } else {
                        hotelImages.add("https://your-default-hotel-image.jpg");
                    }
                    hotelReq.setImages(hotelImages);

                    Result<?> hotelResult = hotelService.addHotel(hotelReq);

                    if (hotelResult.getCode() != 200) {
                        System.err.println("酒店已存在或添加失败: " + hotelReq.getName());
                        continue;
                    }

                    Long hotelId = (Long) hotelResult.getData();

                    String[][] roomTemplates = {
                            {"标准双人间", "双人床 1.5m", "1", "25", "299.0"},
                            {"高级大床房", "大床 1.8m", "1", "30", "399.0"}
                    };

                    for (String[] template : roomTemplates) {
                        RoomRequest roomReq = new RoomRequest(
                                null,
                                String.valueOf(hotelId),
                                template[0],
                                template[1],
                                Integer.parseInt(template[2]),
                                Integer.parseInt(template[3]),
                                Double.parseDouble(template[4]),
                                20,
                                hotelImages,
                                1
                        );

                        hotelService.addRoom(roomReq);
                    }
                    System.out.println("成功导入酒店及30天库存: " + hotelReq.getName());
                }
                Thread.sleep(500);
            } catch (Exception e) {
                System.err.println("处理城市 " + adcode + " 酒店数据时异常: " + e.getMessage());
            }
        }
        // 关闭 OSS 客户端
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    private RegisterRequest generateRandomAdminRequest() {
        String randomSuffix = String.valueOf(System.currentTimeMillis()).substring(7);
        String username = "admin_" + RandomUtil.randomString(8);
        String nickname = "酒店管理员" + randomSuffix;
        String password = "123456";
        String email = "admin" + randomSuffix + "@hotel.com";
        String avatar = "https://your-default-avatar.jpg";
        return new RegisterRequest(username, nickname, password, email, 3, avatar);
    }

    private Long registerRandomAdmin(RegisterRequest registerRequest) {
        try {
            adminService.registerAdmin(registerRequest);
            UserResponse registerUser = userMapper.getRegisterUser(registerRequest.username());
            if (registerUser != null) {
                return registerUser.getId();
            }
        } catch (BusinessException e) {
            System.err.println("用户名已存在，重新生成: " + e.getMessage());
            return registerRandomAdmin(generateRandomAdminRequest());
        } catch (Exception e) {
            System.err.println("注册管理员异常: " + e.getMessage());
        }
        return null;
    }

    @Test
    void fetchAndImportAllCities() {
        String[] cities = {
                "110000", "310000", "440100", "440300", "510100", "330100", "610100", "500000",
                "420100", "320100", "370200", "350200", "530100", "430100", "340100", "320500",
                "210100", "220100", "230100", "350100", "360100", "370100", "410100", "450100",
                "460100", "520100", "540100", "620100", "630100", "640100", "650100"
        };

        for (String adcode : cities) {
            System.out.println(">>>>>> 正在抓取城市编码: " + adcode + " <<<<<<");

            String url = "https://restapi.amap.com/v3/place/text?keywords=景点&city=" + adcode + "&offset=20&page=1&key=" + AMAP_KEY;

            try {
                String response = restTemplate.getForObject(url, String.class);
                JSONObject json = JSON.parseObject(response);

                if (!"1".equals(Objects.requireNonNull(json).getString("status"))) {
                    System.err.println("接口返回错误: " + json.getString("info"));
                    continue;
                }

                JSONArray pois = json.getJSONArray("pois");
                if (pois == null || pois.isEmpty()) {
                    System.out.println("城市 " + adcode + " 未查询到景点数据。");
                    continue;
                }

                for (int i = 0; i < pois.size(); i++) {
                    JSONObject poi = pois.getJSONObject(i);

                    SpotRequest request = new SpotRequest();
                    request.setName(poi.getString("name"));
                    request.setCity(poi.getString("cityname"));
                    request.setAddress(poi.getString("address"));

                    String typeStr = poi.getString("type");
                    request.setType((typeStr != null && typeStr.contains(";")) ? typeStr.split(";")[0] : typeStr);

                    JSONObject bizExt = poi.getJSONObject("biz_ext");
                    String cost = "50.00";
                    if (bizExt != null) {
                        String apiCost = bizExt.getString("cost");
                        if (apiCost != null && !apiCost.isEmpty() && !apiCost.equals("[]")) {
                            cost = apiCost;
                        }
                    }
                    request.setPrice(new BigDecimal(cost));
                    request.setOpenTime("09:00-17:00");
                    request.setDescription(poi.getString("name") + "，欢迎来到" + poi.getString("cityname") + "体验独特风光。");
                    request.setStatus(1);
                    request.setViews(0);

                    // 图片处理 - 上传到 OSS
                    List<String> images = new ArrayList<>();
                    JSONArray photos = poi.getJSONArray("photos");
                    if (photos != null && !photos.isEmpty()) {
                        for (int j = 0; j < photos.size(); j++) {
                            String photoUrl = photos.getJSONObject(j).getString("url");
                            String ossUrl = uploadToOss(photoUrl);
                            images.add(ossUrl);
                        }
                    } else {
                        images.add("https://images.unsplash.com/photo-1506744038136-46273834b3fb");
                    }
                    request.setImageUrls(images);

                    try {
                        spotService.addSpot(request);
                        System.out.println("成功保存: " + request.getName());
                    } catch (Exception e) {
                        System.err.println("跳过景点 [" + request.getName() + "]: " + e.getMessage());
                    }
                }

                Thread.sleep(300);
            } catch (Exception e) {
                System.err.println("处理城市 " + adcode + " 时发生严重异常: " + e.getMessage());
            }
        }
        if (ossClient != null) {
            ossClient.shutdown();
        }
        System.out.println("####### 所有城市数据导入任务完成！ #######");
    }
}