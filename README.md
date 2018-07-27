
## 用途

1. 代理后端dubbo服务，转换为http接口
2. 统一的用户登陆/鉴权入口
3. 用户登录信息存入redis

## 数据库

docs/gateway.sql

## 技术选型

1. SpringBoot
2. MySQL
3. dubbo & zookeeper
4. redis

## 打包  & docker

1. mvn clean package -Dmaven.test.skip=true -Ptest
2. docker build -t adzuki-gateway .
3. docker run -d -p 8080:8080 --name adzuki-gateway adzuki-gateway
