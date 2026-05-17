# travelFamilies（旅游之家）开发手册

## 项目简介

旅游之家是一个专注于个人及家庭旅游场景的综合服务平台，提供景点浏览、酒店预订、评论评分、优惠券等功能。

- **前端**：客户端（微信小程序）+ 管理端（Web 网页）
- **后端**：Spring Boot 3.x RESTful API

---

## 技术栈

| 类别 | 技术 |
|------|------|
| 核心框架 | Spring Boot 4.0.5（Java 17） |
| 安全鉴权 | Spring Security + JWT（java-jwt 4.4.0） |
| 数据库 | MySQL 8（业务数据） |
| 缓存 | Redis（Token 管理、缓存、限流、计数） |
| 消息队列 | RabbitMQ（订单超时取消） |
| ORM | MyBatis + PageHelper 分页 |
| 对象存储 | 阿里云 OSS（图片上传） |
| 工具库 | Lombok、Hutool 5.8.16、Fastjson2 |

---

## 项目结构

```
src/main/java/com/travelfamilies/
├── TravelFamiliesApplication.java    # 启动类（启用缓存、定时任务）
├── config/                           # 配置层
│   ├── SecurityConfig.java           # Spring Security 配置（角色权限）
│   ├── JwtAuthenticationFilter.java  # JWT 认证过滤器（黑白名单）
│   ├── JwtUtils.java                 # JWT 生成与校验
│   ├── CorsConfig.java               # 跨域配置
│   ├── AccessDeniedHandler.java      # 403 处理器
│   ├── AuthenticationHandler.java    # 401 处理器
│   ├── RabbitConfig.java             # RabbitMQ 死信队列配置
│   └── RedisConfig.java              # Redis 配置
├── controller/                       # 控制器层（9 个 Controller，59 个接口）
│   ├── UserController.java           # 用户端：注册/登录/微信登录/修改资料
│   ├── AdminController.java          # 管理端：管理员注册/登录/用户管理
│   ├── HotelController.java          # 酒店管理：酒店CRUD/房型/库存/审批
│   ├── OrderController.java          # 订单：下单/支付/入住/退房
│   ├── SpotController.java           # 景点：热门/搜索/CRUD
│   ├── CommentController.java        # 评论：发表/查询/回复/状态
│   ├── CouponController.java         # 优惠券：发放/领取/查询
│   ├── ImageController.java          # 图片：按类型查询
│   └── UploadController.java         # 上传：文件上传至阿里云OSS
├── service/                          # 服务接口 + impl 实现
├── mapper/                           # MyBatis Mapper 接口
├── pojo/                             # 实体类
│   ├── User.java / Hotel.java / Room.java / Spot.java
│   ├── HotelOrder.java / Comment.java / Coupon.java
│   ├── Image.java / CheckInRecord.java / LocateRoomInfo.java
│   └── VO 类：HotelVO / SpotVO / CommentVO / UserCouponVO
├── request/                          # 请求 DTO（按模块分包）
│   ├── userRequest/ hotelRequest/ orderRequest/
│   ├── spotRequest/ commentRequest/ couponRequest/
│   └── GetDataRequest.java           # 通用分页请求
├── response/                         # 响应 DTO + Result 统一返回体
├── exception/                        # 全局异常处理
├── task/                             # 定时任务
│   ├── HotelTask.java                # 每日自动生成未来第31天库存
│   └── SpotTask.java                 # 浏览量/评分/评论数定时同步
└── tools/                            # 工具类
    ├── AliOSSUtils.java              # OSS 文件上传
    ├── CalculateDays.java            # 日期计算与库存回滚
    ├── OrderCancelConsumer.java      # RabbitMQ 订单超时取消消费者
    └── RedisConstant.java            # Redis Key 常量定义
```

---

## 角色权限体系

系统共有 **3 种角色**，通过 Spring Security + JWT 实现权限控制：

| 角色 | roleID | 权限标识 | 说明 |
|------|--------|----------|------|
| 普通用户 | 1 | ROLE_USER | 微信小程序端用户 |
| 系统管理员 | 2 | ROLE_ADMIN | Web 管理端超级管理员 |
| 酒店管理员 | 3 | ROLE_HOTEL | Web 管理端酒店商家 |

**权限矩阵**：

| 接口路径 | 允许角色 |
|----------|----------|
| `/user/login`, `/user`, `/user/wx-login`, `/admin/login`, `/admin`, `/admin/resetPassword`, `/upload`, `/error` | 全部放行 |
| `/order/checkIn`, `/order/checkOut`, `/order/getOrderByGuest`, `/hotel/*`（增删改）, `/coupon/add`, `/hotel/getOwner` | ROLE_HOTEL |
| `/spot`（增删改）, `/coupon/add`, `/hotel/list`, `/hotel/all` | ROLE_ADMIN |
| `/hotel/status/**` | ROLE_ADMIN 或 ROLE_HOTEL |
| 其余接口 | 认证即可 |

**安全机制**：
- JWT Token 有效期 24 小时
- Redis 存储活跃 Token（白名单），支持挤下线与强制注销
- 黑名单机制：封禁账号实时拦截存量 JWT 请求
- BCrypt 密码加密

---

## API 接口文档

> 所有接口返回统一格式 `Result<T>`：`{ code: 200, message: "操作成功", data: ... }`
> 除放行接口外，请求头需携带 `Authorization: Bearer <token>`

### 1. 用户模块 `/user`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/user` | 用户注册 | 否 |
| POST | `/user/login` | 用户登录 | 否 |
| POST | `/user/wx-login` | 微信小程序登录（code 换 token） | 否 |
| PUT | `/user/{id}/password` | 修改用户密码 | 是 |
| PUT | `/user/{id}` | 修改用户详细资料 | 是 |
| PUT | `/user/wx-profile` | 更新微信用户资料 | 是 |

### 2. 管理员模块 `/admin`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/admin` | 管理员注册 | 否 |
| POST | `/admin/login` | 管理员登录 | 否 |
| POST | `/admin/resetPassword` | 重置密码 | 否 |
| PUT | `/admin/status` | 更新管理员状态 | 是 |
| PUT | `/admin/{id}/password` | 修改管理员密码 | 是 |
| POST | `/admin/all` | 分页查询所有用户 | 是 |
| POST | `/admin/query` | 按条件查询用户 | 是 |

### 3. 酒店模块 `/hotel`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/hotel` | 添加酒店 | 是 |
| POST | `/hotel/room` | 添加房型 | 是 |
| PUT | `/hotel/{id}` | 修改酒店信息 | 是 |
| PUT | `/hotel/room` | 更新房型信息 | 是 |
| PUT | `/hotel/dayMess` | 更新每日信息 | 是 |
| PUT | `/hotel/status/{id}` | 审批酒店（通过/拒绝） | 是 |
| GET | `/hotel` | 查询酒店信息 | 是 |
| GET | `/hotel/search` | 搜索酒店 | 是 |
| GET | `/hotel/{id}` | 根据酒店ID查房型列表 | 是 |
| GET | `/hotel/{hotelId}/room/{roomId}/{date}` | 查询指定日期库存 | 是 |
| GET | `/hotel/getHotel` | 根据酒店ID查酒店及订单 | 是 |
| POST | `/hotel/getOwner` | 获取当前管理员的酒店列表 | 是 |
| POST | `/hotel/getRoom` | 根据房间ID查房型详情 | 是 |
| POST | `/hotel/reserve` | 预订房间 | 是 |
| POST | `/hotel/locate` | 定位酒店 | 是 |
| POST | `/hotel/list` | 按状态查询酒店列表 | 是 |
| POST | `/hotel/all` | 分页查询所有酒店 | 是 |
| DELETE | `/hotel/{id}` | 删除酒店 | 是 |
| DELETE | `/hotel/room/{id}` | 删除房型 | 是 |

### 4. 订单模块 `/order`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/order/orderInfo` | 获取订单房间详情 | 是 |
| POST | `/order/submit` | 提交订单 | 是 |
| GET | `/order/{id}` | 根据订单ID查询详情 | 是 |
| POST | `/order/surePay` | 确认支付 | 是 |
| GET | `/order/getOrderSort` | 按分类获取订单列表 | 是 |
| POST | `/order/getOrderByGuest` | 根据住客姓名查订单 | 是 |
| POST | `/order/checkIn` | 办理入住 | 是 |
| POST | `/order/checkOut` | 办理退房 | 是 |
| POST | `/order/getOrder` | 分页查询订单 | 是 |

### 5. 景点模块 `/spot`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/spot/hot` | 首页热门景点 Top10 | 是 |
| GET | `/spot` | 动态搜索景点 | 是 |
| GET | `/spot/{id}` | 查询景点详情 | 是 |
| POST | `/spot/all` | 分页查询所有景点 | 是 |
| POST | `/spot` | 添加景点 | 是 |
| PUT | `/spot/{id}` | 更新景点信息 | 是 |
| DELETE | `/spot/{id}` | 删除景点 | 是 |

### 6. 评论模块 `/comment`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/comment/add` | 添加评论（支持图片） | 是 |
| POST | `/comment/get` | 查询评论列表 | 是 |
| POST | `/comment/getReply` | 查询回复评论 | 是 |
| GET | `/comment/getCommentStatus` | 查询订单评论状态 | 是 |

### 7. 优惠券模块 `/coupon`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/coupon` | 根据优惠券ID查详情 | 是 |
| GET | `/coupon/list` | 按酒店ID和类型查优惠券 | 是 |
| POST | `/coupon` | 添加优惠券 | 是 |
| POST | `/coupon/coupons` | 分页查询优惠券列表 | 是 |
| PUT | `/coupon/{couponId}` | 更新优惠券状态 | 是 |

### 8. 图片模块 `/image`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/image` | 根据关联ID和类型查图片 | 是 |

### 9. 上传模块 `/upload`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/upload` | 上传文件到阿里云OSS | 否 |

---

## 核心机制说明

### JWT 认证与黑白名单

- 登录成功后生成 JWT，Token 同时存入 Redis
- 每次请求通过 `JwtAuthenticationFilter` 校验 Token 有效性
- **白名单**：Redis Key `login:{role}:token:{userId}` 存当前有效 Token，保证一处登录
- **黑名单**：Redis Key `blacklist:{role}:` 存在即拦截，实现实时封禁

### 景点浏览量防刷

- Redis SETNX 实现用户级 10 分钟冷却期（Key: `spot:views:user:{userId}:{spotId}`）
- 浏览量实时写入 Redis，每小时通过 `SpotTask` 定时批量同步到 MySQL
- 热门景点 Top10 使用 `@Cacheable(sync = true)` 防缓存击穿

### 评分与评论计数

- 使用 Redis `HINCRBY` / `INCR` 原子操作记录总评分与总人数，避免高并发下评分覆盖
- 每小时定时任务 `SpotTask.dynamicCountAndStarRating()` 将 Redis 数据批量同步到数据库

### 酒店库存管理

- 每日凌晨 2 点 `HotelTask.dynamicDayStock()` 自动生成未来第 31 天的房型库存
- 同时清理过期库存记录，保持滑动窗口始终为 30 天
- 预订时通过 Redis 实时扣减库存，保证并发安全

### 订单超时取消（RabbitMQ 死信队列）

- 订单提交后发送消息到 TTL 队列（15 分钟过期）
- 过期后自动转入死信队列，由 `OrderCancelConsumer` 消费
- 取消逻辑：更新订单状态为已取消 → 回滚库存 → 退还优惠券

### 评论系统

- 支持跨业务评价（景点/酒店），通过 `targetId` + `targetType` 解耦
- 二级嵌套回复：根评论 + 子评论分层展示，支持二级独立分页
- 评论支持关联多张 OSS 图片

---

## 环境搭建

### 前置依赖

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.x

### 配置步骤

1. **数据库**：创建 MySQL 数据库，执行建表 SQL

2. **修改 `application.yml`**：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/你的数据库名?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
    username: 你的用户名
    password: 你的密码
  data:
    redis:
      host: 127.0.0.1
      port: 6379

aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    accessKeyId: 你的AccessKey
    accessKeySecret: 你的AccessKeySecret
    bucketName: 你的Bucket名
```

3. **修改 `JwtUtils.java`** 中的 `SECRET` 密钥

4. **启动**：
```bash
mvn clean install
mvn spring-boot:run
```
服务默认运行在 `http://localhost:8080`

### 注意事项

- 修改 `RedisConstant` 等常量类后必须执行 `mvn clean` 刷新编译缓存
- 生产环境需将 `CorsConfig` 中的 `allowedOriginPatterns("*")` 改为具体域名
- 需要在 `JwtAuthenticationFilter` 中配置正确的 JWT SECRET

---

## 项目状态

- [x] 用户与管理员权限体系
- [x] 景点模块（多级缓存、防刷、定时同步）
- [x] 酒店模块（CRUD、房型管理、库存同步）
- [x] 订单模块（下单、支付、入住、退房、超时取消）
- [x] 评论系统（二级嵌套回复、图片关联、原子评分）
- [x] 优惠券模块
- [x] 图片上传（阿里云 OSS）