# 汽车修理厂管理系统 Docker 部署说明

本文档说明如何将当前项目使用 Docker 方式部署到服务器。

项目结构：

* 后端：Spring Boot，模块入口为 `ruoyi-admin`
* 前端：Vue 2，目录为 `ruoyi-ui`
* 依赖服务：MySQL、Redis

## 1. 部署思路

推荐采用 4 个容器：

* `mysql`：数据库
* `redis`：缓存
* `garage-admin`：后端 Java 服务
* `garage-ui`：前端静态页面，由 Nginx 提供访问

访问关系：

* 浏览器 -> `garage-ui`
* `garage-ui` -> `/prod-api` -> `garage-admin`
* `garage-admin` -> `mysql`
* `garage-admin` -> `redis`

## 2. 部署前准备

服务器需要先安装：

* Docker
* Docker Compose

建议版本：

* Docker 24+
* Docker Compose v2+

## 3. 当前项目需要先注意的配置

当前生产配置文件是：

* [ruoyi-admin/src/main/resources/application-prod.yml](C:/Code/Java/fix/ruoyi-admin/src/main/resources/application-prod.yml)

里面现在的 MySQL 和 Redis 地址是：

```yml
spring:
  data:
    redis:
      host: 127.0.0.1
  datasource:
    druid:
      master:
        url: jdbc:mysql://127.0.0.1:3306/auto_garage_manager...
```

如果后端也运行在 Docker 容器里，`127.0.0.1` 指向的是后端容器自己，不是 MySQL/Redis 容器。

所以 Docker 部署时需要改成容器服务名，例如：

```yml
spring:
  data:
    redis:
      host: redis
      port: 6379
      database: 2
      password: change_me
  datasource:
    druid:
      master:
        url: jdbc:mysql://mysql:3306/auto_garage_manager?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8
        username: root
        password: change_me
```

如果 MySQL、Redis 不放在 Docker 内，而是外部服务器，则把 `mysql`、`redis` 改成实际 IP 或域名。

## 4. 后端打包

在项目根目录执行：

```bash
mvn clean package -DskipTests
```

打包完成后，后端 Jar 一般位于：

```bash
ruoyi-admin/target/ruoyi-admin.jar
```

## 5. 前端打包

进入前端目录：

```bash
cd ruoyi-ui
npm install
npm run build:prod
```

构建完成后，静态文件位于：

```bash
ruoyi-ui/dist
```

前端生产环境接口前缀来自：

* [ruoyi-ui/.env.production](C:/Code/Java/fix/ruoyi-ui/.env.production)

当前为：

```bash
VUE_APP_BASE_API = '/prod-api'
```

这意味着 Nginx 需要把 `/prod-api` 代理到后端服务。

## 6. 推荐目录结构

建议在服务器上准备一个单独部署目录，例如：

```bash
deploy/
├── docker-compose.yml
├── Dockerfile.admin
├── Dockerfile.ui
├── nginx.conf
├── sql/
│   └── ry_20260417.sql
└── data/
    ├── mysql/
    └── redis/
```

SQL 初始化文件可以使用项目中的：

* [sql/ry_20260417.sql](C:/Code/Java/fix/sql/ry_20260417.sql)

## 7. 后端 Dockerfile

文件名：`Dockerfile.admin`

```dockerfile
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY ruoyi-admin/target/ruoyi-admin.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
```

## 8. 前端 Dockerfile

文件名：`Dockerfile.ui`

```dockerfile
FROM nginx:1.27-alpine

COPY ruoyi-ui/dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
```

## 9. 前端 Nginx 配置

文件名：`nginx.conf`

```nginx
server {
    listen 80;
    server_name _;

    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /prod-api/ {
        proxy_pass http://garage-admin:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /v3/api-docs/ {
        proxy_pass http://garage-admin:8080/v3/api-docs/;
        proxy_set_header Host $host;
    }
}
```

说明：

* `/prod-api/xxx` 会被转发到 `garage-admin:8080/xxx`
* `try_files` 用来支持 Vue history 路由

## 10. docker-compose.yml 示例

```yml
version: "3.9"

services:
  mysql:
    image: mysql:8.0
    container_name: garage-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: change_me
      MYSQL_DATABASE: auto_garage_manager
      TZ: Asia/Shanghai
    ports:
      - "3306:3306"
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_general_ci
      - --default-authentication-plugin=mysql_native_password
    volumes:
      - ./data/mysql:/var/lib/mysql
      - ./sql:/docker-entrypoint-initdb.d

  redis:
    image: redis:7-alpine
    container_name: garage-redis
    restart: always
    command: redis-server --appendonly yes --requirepass change_me
    ports:
      - "6379:6379"
    volumes:
      - ./data/redis:/data

  garage-admin:
    build:
      context: ..
      dockerfile: deploy/Dockerfile.admin
    container_name: garage-admin
    restart: always
    depends_on:
      - mysql
      - redis
    ports:
      - "8080:8080"

  garage-ui:
    build:
      context: ..
      dockerfile: deploy/Dockerfile.ui
    container_name: garage-ui
    restart: always
    depends_on:
      - garage-admin
    ports:
      - "80:80"
```

如果你不使用 `deploy` 子目录，而是把这些文件直接放到项目根目录，需要把 `dockerfile` 路径改成：

```yml
dockerfile: Dockerfile.admin
dockerfile: Dockerfile.ui
```

## 11. 部署步骤

### 11.1 修改生产配置

先把 [ruoyi-admin/src/main/resources/application-prod.yml](C:/Code/Java/fix/ruoyi-admin/src/main/resources/application-prod.yml) 中：

* MySQL 地址改为 `mysql`
* Redis 地址改为 `redis`
* 用户名、密码改为真实值

### 11.2 构建后端和前端

在项目根目录执行：

```bash
mvn clean package -DskipTests
cd ruoyi-ui
npm install
npm run build:prod
cd ..
```

### 11.3 启动容器

```bash
docker compose up -d --build
```

### 11.4 查看运行状态

```bash
docker compose ps
docker compose logs -f garage-admin
docker compose logs -f garage-ui
```

## 12. 访问地址

启动成功后通常访问：

* 前端页面：`http://服务器IP/`
* 后端接口：`http://服务器IP:8080/`
* Swagger：`http://服务器IP:8080/swagger-ui.html`

注意：

* 当前 `application-prod.yml` 里 `springdoc.swagger-ui.enabled: false`
* 如果要在生产环境打开 Swagger，需要改成 `true`

## 13. 常见问题

### 13.1 前端能打开，但接口请求失败

优先检查：

* `ruoyi-ui/.env.production` 中接口前缀是不是 `/prod-api`
* `nginx.conf` 是否配置了 `/prod-api/` 代理
* 后端容器是否正常启动

### 13.2 后端连不上数据库

优先检查：

* `application-prod.yml` 是否还在使用 `127.0.0.1`
* Docker Compose 里的 MySQL 服务名是否为 `mysql`
* 数据库密码、库名是否一致

### 13.3 后端连不上 Redis

优先检查：

* Redis host 是否配置为 `redis`
* Redis 是否设置了密码
* `application-prod.yml` 的 Redis 密码是否和容器启动参数一致

### 13.4 页面刷新后 404

这是 Vue history 路由常见问题，检查 Nginx 是否配置了：

```nginx
try_files $uri $uri/ /index.html;
```

## 14. 更稳妥的生产建议

正式上线建议再补这几项：

* 给 MySQL、Redis、后端都设置强密码
* 不要把敏感信息硬编码进 Git 仓库
* 使用 `.env` 或服务器环境变量管理密码
* 前端通过 80/443 对外暴露，后端 8080 只在内网开放
* 配置 HTTPS
* 给 MySQL 和上传目录做持久化备份

## 15. 最小可用结论

这个项目可以用 Docker 部署，推荐方式是：

1. 后端打包成 `ruoyi-admin.jar`
2. 前端打包成 `ruoyi-ui/dist`
3. 使用 Nginx 承载前端静态资源
4. 使用 Docker Compose 同时编排 MySQL、Redis、后端、前端
5. 把生产配置中的 `127.0.0.1` 改成容器服务名

