// start.sh 已经完成了副本集的初始化，因此在 init-mongo-cluster.js 中不需要检查和初始化副本集。
// init-mongo.js 仅需要关注业务逻辑，比如创建用户和授予权限。
print("init-mongo.js started...");

db = db.getSiblingDB("admin");
var adminExists = db.getUser('admin');
if (!adminExists) {
    db.createUser({
        user: 'admin',
        pwd: '123456',
        roles: [{ role: 'root', db: 'admin' }]
    });
} else {
    print('User "admin" already exists. No action taken.');
}

var authSuccess = db.auth("admin", "123456");
if (!authSuccess) {
    print("Failed to authenticate as admin. Exiting...");
    quit(1);
}
print("Successfully authenticated as admin.");

// 切换到 demo 数据库
db = db.getSiblingDB('demo');

// 检查用户是否已存在
var userExists = db.getUser('demo');
if (userExists === null) {
    db.createUser({
        user: "demo",
        pwd: "demo123",
        roles: [
            { role: "dbOwner", db: "demo" }
        ]
    });
    print("User 'demo' created successfully with dbOwner role on 'demo' database.");
}else {
    print("User 'demo' already exists. No action taken.");
}

print("init-mongo.js completed.");
