# mongodb-learning
Demo project for Spring Boot data mongodb
## mongodb集群
启用了 MongoDB 的 authorization 并设置了副本集（Replica Set）， MongoDB 要求所有节点使用相同的 keyFile 进行相互认证。
没有 keyFile，MongoDB 副本集节点将无法安全地通信，从而导致启动失败。
### 1、生成mongo-keyfile
```shell
openssl rand -base64 756 > mongo-keyfile
chmod 600 mongo-keyfile
````
生成一个名为 mongo-keyfile 的文件，并确保它的权限是 600，即只有拥有者可以读取和写入。

### 2、手动设置账号密码
使用mongosh：
```shell
// docker exec -it container_name mongosh
docker exec -it mongo1 mongosh
db = db.getSiblingDB('admin');
db.createUser({
user: 'admin',
pwd: '123456',
roles: [{ role: 'root', db: 'admin' }]
});
db.auth("admin","123456");
db = db.getSiblingDB('demo');
db.createUser({ user: "demo", pwd: "demo123", roles: [ { role: "dbOwner", db: "demo" }] });
````
或者使用eval函数：
第一步：创建admin用户
```shell
docker exec -it mongo1 mongosh --eval '
db = db.getSiblingDB('admin');
db.createUser({
user: 'admin',
pwd: '123456',
roles: [{ role: 'root', db: 'admin' }]
});
'
```
第二步：创建其它用户
```shell
docker exec -it mongo1 mongosh --eval '
db = db.getSiblingDB("admin");
db.auth("admin", "123456");
db = db.getSiblingDB("demo");
db.createUser({
user: "demo",
pwd: "demo123",
roles: [{ role: "dbOwner", db: "demo" }]
});
'
```
也可以合并成一步：
```shell
docker exec -it mongo1 mongosh --eval '
db = db.getSiblingDB('admin');
db.createUser({
user: 'admin',
pwd: '123456',
roles: [{ role: 'root', db: 'admin' }]
});
db.auth("admin", "123456");
db = db.getSiblingDB("demo");
db.createUser({
user: "demo",
pwd: "demo123",
roles: [{ role: "dbOwner", db: "demo" }]
});
'
```

### 查看集群状态
```shell
docker exec -it mongo1 mongosh
rs.status()
````
或者
```shell
docker exec -it mongo1 mongosh --eval 'rs.status()'
````
## docker命令行
### 1、启动mongodb容器
```shell
docker compose -f docker-compose-mongo-cluster.yml up -d
````
### 2、启动demo容器
```shell
docker compose -f docker-compose-mongo-cluster.yml -f docker-compose-cluster.yml up -d
````
### 3、日志
```shell
docker logs mongo1 -f --tail 100 | grep -E "Wait for MongoDB to start|MongoDB is ready!|Replica set initialized!"
docker logs mongo1 | grep -E "Wait for MongoDB to start|MongoDB is ready!|Replica set initialized!"
````
### 4、进入容器
docker exec -it mongo1 mongosh -u admin -p demo123 --authenticationDatabase admin
### 5、容器安装curl (Centos)
#### 进入容器安装
```shell
docker exec -it centos_container /bin/bash
yum install -y curl
curl --version
````
#### 也可以在Dockerfile中安装：
如果希望在创建 CentOS Docker 镜像时就安装 curl，可以在 Dockerfile 中添加以下行：
```shell
FROM centos:latest

# 安装 curl
RUN yum install -y curl && yum clean all

```
  


