#!/bin/bash

# 更新包列表并安装 curl 和必要的工具
# ubantu用apt-get, centos用yum install -y curl
apt-get update && apt-get install -y curl

# mongodb低版本用mongo，高版本用mongosh
which mongosh
if [ $? -ne 0 ]; then
    echo "mongosh not found!"
    exit 1
fi

echo "mongosh found"


# 确保 脚本有执行权限
chmod +x /wait-for-it.sh

# 等待其他 MongoDB 实例启动
/wait-for-it.sh mongo2:27017 -- /wait-for-it.sh mongo3:27017 --

# 启动 MongoDB 服务
mongod --replSet rs0 --bind_ip_all --keyFile /etc/mongo-keyfile &

# 等待 MongoDB 启动
echo "Wait for MongoDB to start..."

# 等待 MongoDB 启动
wait_for_mongo() {
    while true; do
        if mongosh --quiet --eval 'db.runCommand({ ping: 1 })'; then
            echo "MongoDB is ready!"
            break
        fi
        sleep 2
        echo "Waiting for MongoDB to start..."
    done
}

wait_for_mongo

# 初始化副本集
echo "Initializing replica set..."
mongosh --eval 'rs.initiate({
    _id: "rs0",
    members: [
        { _id: 0, host: "mongo1:27017" },
        { _id: 1, host: "mongo2:27017" },
        { _id: 2, host: "mongo3:27017" }
    ]
})'
echo "Replica set initialized!"
# 查看集群状态：
# 1、docker exec -it mongo1 mongosh
# 2、rs.status()

# 等待后台的 MongoDB 进程完成
wait
