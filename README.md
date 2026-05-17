<p align="center">
  <h1 align="center">🏖️ 旅游之家 TravelFamilies</h1>
  <p align="center">个人及家庭旅游综合服务平台 —— 微信小程序 + Web 管理后台</p>
</p>

---

## 📌 项目简介

TravelFamilies 是一个专注于旅游场景的全栈服务平台，提供**景点浏览、酒店预订、评论评分、优惠券**等一站式旅游服务。

- 🛒 **客户端**：微信小程序，支持景点搜索、酒店预订、下单支付、评论晒图
- 🖥️ **管理端**：Web 后台，支持系统管理员（超级管理）和酒店管理员（商家）双角色
- ⚡ **高并发**：集成 Redis 缓存、防刷限流、原子评分、异步落库等工业级方案

---

## 🛠 技术栈

| 类别 | 技术 |
|------|------|
| 核心框架 | Spring Boot 4.0.5 · Java 17 |
| 安全鉴权 | Spring Security + JWT (java-jwt 4.4.0) |
| 数据库 | MySQL 8 |
| 缓存 | Redis（Token · 缓存 · 计数 · 限流） |
| 消息队列 | RabbitMQ（死信队列 · 订单超时取消） |
| ORM | MyBatis + PageHelper 分页 |
| 对象存储 | 阿里云 OSS |
| 工具 | Lombok · Hutool · Fastjson2 |
| 构建 | Maven |

---

## 📁 项目结构

```
src/main/java/com/travelfamilies/
├── TravelFamiliesApplication.java   # 启动类（@EnableCaching + @EnableScheduling）
├── config/                          # 配置
│   ├── SecurityConfig.java          # 权限规则（用户/管理员/酒店管理员）
│   ├── JwtAuthenticationFilter.java # JWT 过滤器（Redis 黑白名单）
│   ├── JwtUtils.java                # Token 生成与校验
│   ├── CorsConfig.java              # 跨域
│   ├── RabbitConfig.java            # 死信队列（15 分钟订单超时）
│   └── RedisConfig.java             # Redis 序列化
├── controller/                      # 控制器（9 个 · 59 个接口）
├── service/                         # 业务接口 + impl 实现
├── mapper/                          # MyBatis Mapper + XML
├── pojo/                            # 实体 & VO
├── request/                         # 请求 DTO（按模块分包）
├── response/                        # 响应 DTO · Result 统一返回体
├── exception/                       # 全局异常处理
├── task/                            # 定时任务（库存滚动 · 浏览量同步 · 评分同步）
└── tools/                           # OSS上传 · 天数计算 · 订单取消消费者 · Redis常量
```

---

## 👥 角色权限

| 角色 | roleID | 标识 | 端 | 说明 |
|------|--------|------|----|------|
| 普通用户 | 1 | ROLE_USER | 微信小程序 | 浏览、预订、评论 |
| 系统管理员 | 2 | ROLE_ADMIN | Web | 全站管理、审批酒店 |
| 酒店管理员 | 3 | ROLE_HOTEL | Web | 酒店CRUD、订单管理 |

**安全亮点**：
- 🔐 JWT + Redis 白名单：支持挤下线与强制注销，克服无状态令牌无法实时失效的缺陷
- 🚫 Redis 黑名单：封禁账号毫秒级拦截存量 JWT 请求
- 🔒 BCrypt 密码加密

---

## 🚀 核心亮点

### 景点高并发方案

| 机制 | 实现 |
|------|------|
| 缓存击穿防护 | `@Cacheable(sync = true)` 本地锁 |
| 智能防刷 | Redis SETNX 10 分钟冷却期 `spot:views:user:{userId}:{spotId}` |
| 异步落库 | Write-Back 策略，Redis 实时计数 + 每小时批量同步 MySQL |

### 原子评分系统

> 放弃"查询-计算-存储"，改用 `HINCRBY` / `INCR` 原子递增，彻底解决高并发下评分覆盖问题。

### 订单超时取消

```
提交订单 → TTL 队列(15min) → 超时 → 死信队列 → OrderCancelConsumer
                                                    ├── 更新状态为已取消
                                                    ├── 回滚房型库存
                                                    └── 退还优惠券
```

### 酒店库存滑动窗口

- 每日凌晨 2 点自动生成第 31 天库存，清理过期记录
- 始终保持 30 天可预订窗口
- Redis 实时扣减，保证并发安全

### 评论系统

- 🌐 跨业务评价：`targetId` + `targetType` 解耦景点/酒店评论
- 💬 二级嵌套回复：根评论 + 子评论独立分页
- 📷 多媒体评价：支持关联多张 OSS 实拍图
- ⚡ Anti-N+1：Stream groupingBy 内存分拣，图片查询 IO 恒为 1 次

---

## 📡 API 概览

> 统一返回：`{ code: 200, message: "操作成功", data: ... }`  
> 认证方式：`Authorization: Bearer <token>`

| 模块 | 路径 | 接口数 | 核心功能 |
|------|------|--------|----------|
| 用户 | `/user` | 6 | 注册/登录/微信登录/修改资料 |
| 管理员 | `/admin` | 7 | 管理员注册登录/用户管理 |
| 酒店 | `/hotel` | 19 | CRUD/房型/搜索/预订/审批/库存 |
| 订单 | `/order` | 9 | 下单/支付/入住/退房/分类查询 |
| 景点 | `/spot` | 7 | 热门Top10/搜索/CRUD/防刷 |
| 评论 | `/comment` | 4 | 发表/查询/回复/状态 |
| 优惠券 | `/coupon` | 5 | 发放/领取/状态管理 |
| 图片 | `/image` | 1 | 按类型查询图片 |
| 上传 | `/upload` | 1 | OSS 文件上传 |

> 完整接口文档见 [help.md](./help.md)

---

## 🏗️ 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.x

### 配置

修改 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/travel_families?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
    username: root
    password: your_password
  data:
    redis:
      host: 127.0.0.1
      port: 6379

aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    accessKeyId: your_key
    accessKeySecret: your_secret
    bucketName: your_bucket
```

### 启动

```bash
# 编译（修改常量类后必须 clean）
mvn clean install

# 启动
mvn spring-boot:run

# 服务运行在 http://localhost:8080
```

### ⚠️ 注意事项

- 修改 `RedisConstant` 等常量类后务必 `mvn clean` 刷新编译缓存
- 生产环境将 `CorsConfig` 的 `allowedOriginPatterns("*")` 改为具体域名
- 修改 `JwtUtils.java` 中的 `SECRET` 为生产密钥

---

## ✅ 项目完成度

- [x] 用户/管理员/酒店管理员三角色权限体系
- [x] JWT + Redis 黑白名单安全机制
- [x] 景点模块（缓存/防刷/定时同步）
- [x] 酒店模块（CRUD/房型/库存滑动窗口）
- [x] 订单模块（下单/支付/入住/退房/RabbitMQ 超时取消）
- [x] 评论系统（二级嵌套/图片/原子评分）
- [x] 优惠券模块
- [x] 图片上传（阿里云 OSS）