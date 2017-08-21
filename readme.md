# Readme

![Mou icon](file:////Users/danding1207/Desktop/Ganghuazhifu/app/src/main/res/drawable-xhdpi/ic_launcher.png)

## Overview

**港华交易宝**生产环境，测试环境切换 操作：

### 防盗版

目录 **com.mqt.ganghuazhifu.activity** 类名 **WelcomeActivity**， **onResume()** 方法第三行：生产环境下开放； 测试环境下注释掉；

### manifests注册文件

**application** 标签下 **meta-data** 标签， 名称：**com.baidu.push.apikey**， 值依照注释修改。

#### 网络ip和端口号

目录 **com.mqt.ganghuazhifu.http** 类名 **HttpURLS**， 依照注释修改。

#### UI布局

1. **res** 资源文件夹下， **layout** 布局文件夹下， **fragment_home_page.xml** 文件， 189行 **CardView** 标签的 **visibility** 属性：生产环境修改为： **gone** ； 测试环境修改为： **visible** ；
2. **res** 资源文件夹下， **values** 文件夹下， **dimens.xml** 文件，名称： **home_menu_height** ，生产环境值为： 220dp ； 测试环境值为： 330dp ；
3. **res** 资源文件夹下， **layout** 布局文件夹下， **payment_way_dialog.xml** 文件， **LinearLayout**标签，**id**为：**ll_ganghua**，**visibility** 属性：生产环境修改为： **gone** ； 测试环境修改为： **visible** ；

#### leakcanary内存优化辅助插件

1. **Module：app** 的 **build.gradle** 文件， **dependencies** 项中 **leakcanary** 相关的3个引用包：生产环境下注释掉，不进行编译； 测试环境下开放引用；
2. 目录 **com.mqt.ganghuazhifu** 类名 **App**， **import** 声明中关于 **leakcanary** 的1行 和 **onCreate()** 初始化方法中关于初始化leakcanary的语句：生产环境下注释掉； 测试环境下开放；

#### 模拟器测试

**Module：app** 的 **build.gradle** 文件， **android** 项中 **defaultConfig** 项中 **ndk** 项中 **abiFilters** 项 的值，其中 **'x86'** 是为了方便使用x86内核的模拟器， 打包时可以注释掉以减少包大小

#### 暂未开通的功能

1. 短信校验：类 **VerifyPhoneActivity**， **VerificationQuestionActivity**， **ChangePhoneActivity**， **NewRegistrationActivity**中 **checkEmpty()** 方法的 关于 短信验证码校验的3行代码：生产环境下开放； 测试环境下注释掉；
            **HttpRequestParams** 类中 关于 **VerificationKey** 和 **VerificationCode** 的请求参数代码：生产环境下注释掉； 测试环境下开放。
