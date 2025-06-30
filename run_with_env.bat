@echo off
setlocal enabledelayedexpansion

REM 加载 .env 文件中的环境变量（忽略注释和空行）
if exist .env (
    for /f "usebackq delims=" %%A in (".env") do (
        set "line=%%A"
        REM 跳过注释和空行
        if not "!line!"=="" if not "!line:~0,1!"=="#" (
            for /f "tokens=1,* delims==" %%B in ("!line!") do set "%%B=%%C"
        )
    )
    echo .env 环境变量已加载
) else (
    echo .env 文件不存在，未加载环境变量
)

echo 健康管理系统启动脚本

echo SPRING_MAIL_USERNAME=!SPRING_MAIL_USERNAME!
echo SPRING_MAIL_PASSWORD=***已设置***
echo.

if "!SPRING_MAIL_USERNAME!"=="your_qq_email@qq.com" (
    echo 警告：请先修改 .env 文件中的邮箱配置！
    echo 请编辑 .env 文件，填写您的实际QQ邮箱和授权码
    echo.
    pause
    exit /b 1
)

echo 正在启动应用程序...
echo.

REM 运行应用程序
./gradlew run

pause