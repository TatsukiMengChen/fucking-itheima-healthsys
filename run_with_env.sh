#!/bin/bash

# 加载.env文件中的环境变量（忽略注释和空行）
if [ -f .env ]; then
    export $(grep -v '^#' .env | grep -v '^$' | xargs)
    echo ".env 环境变量已加载"
else
    echo ".env 文件不存在，未加载环境变量"
fi

echo "健康管理系统启动脚本"
echo "========================"

echo "当前邮箱配置："
echo "SPRING_MAIL_USERNAME=$SPRING_MAIL_USERNAME"
echo "SPRING_MAIL_PASSWORD=***已设置***"
echo

if [ "$SPRING_MAIL_USERNAME" = "your_qq_email@qq.com" ]; then
    echo "警告：请先修改.env文件中的邮箱配置！"
    echo "请编辑 .env 文件，填写您的实际QQ邮箱和授权码"
    echo
    exit 1
fi

echo "正在启动应用程序..."
echo

# 运行应用程序
./gradlew run