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
chmod 400 /etc/mongo-keyfile

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
# 等待其它 MongoDB 实例启动
/wait-for-it.sh mongo2:27017 -- /wait-for-it.sh mongo3:27017 --

# 注意：如果rs.initiate() 命令中指定了所有节点（mongo1, mongo2, mongo3），当这三个节点都启动时，MongoDB 会自动将它们加入到副本集中。这个过程是自动的，无需手动干预。
# 虽然通过设置 priority 可以大大增加 mongo1 成为 primary 的可能性，但在实际操作中，由于选举过程受到多个因素的影响，无法 100% 保证 mongo1 总是成为 primary
# 解决办法：先运行mongo1，等mongo1运行起立后再运行mongo2 mongo3，并将mongo2 mongo3添加到集群中，可以确保mongo1 作为primary
mongosh --eval 'rs.initiate({
    _id: "rs0",
    members: [
        { _id: 0, host: "mongo1:27017", priority: 2},
        { _id: 1, host: "mongo2:27017", priority: 1},
        { _id: 2, host: "mongo3:27017", priority: 0}
    ]
})'

# 等待副本集初始化完成
function checkReplicaSetStatus() {
    while true; do
        status=$(mongosh --quiet --eval "rs.status().ok")
        if [ "$status" == "1" ]; then
            echo "Replica set is initialized."
            break
        else
            echo "Replica set not yet initialized, waiting..."
            sleep 5 # 等待5秒后重新检查
        fi
    done
}

checkReplicaSetStatus

# next：手动创建用户。脚本或者js中创建用户不生效，这条路似乎走不通

# 查看集群状态：
# 1、docker exec -it mongo1 mongosh
# 2、rs.status()

# 等待后台的 MongoDB 进程完成
wait
