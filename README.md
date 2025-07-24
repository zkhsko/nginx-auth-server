


修改Idea的Gradle使用的JVM版本
``` text
Idea -> Settings -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JVM 
```


# 功能列表
- [x] 注册
- [x] 登录
- [ ] 重置License
- [x] 下单
- [x] 发起退款
- [x] 管理员主动退款


# TODO
- [ ] 注册缺少验证码,防止恶意请求发送邮件
- [ ] 商品页面美化
- [ ] 下单时订单信息确认页面信息完善和美化

- [x] 路由管理
- [x] 商品管理
- [ ] 支付
- [x] 用户管理(暂时只有封禁解封)
- [ ] 计算到期时间
- [ ] 查看订单(管理员/用户)
- [ ] 添加测试用例
- [ ] 优惠券
- [ ] 支付页面美化
- [ ] 支付页支付结果获取
- [ ] 防止重复支付




# 使用教程

``` nginx.conf
server {
    listen       80;
    server_name  localhost;

    # auth_request    /api/v1.0/auth/access;
    location = /xxx/api/v1.0/auth/access {
        proxy_pass              http://127.0.0.1:8081/xxx/api/v1.0/auth/access;
        # 如果proxy_pass用的是https协议,需要开启以下两行
        # 并且proxy_ssl_name需要设置为https的域名
        # proxy_ssl_server_name   on;
        # proxy_ssl_name          xxx.domain.com;
        proxy_http_version      1.1;
        proxy_set_header        Connection keep-alive;
        proxy_set_header        Host $http_host;
        proxy_set_header        X-Original-URI $request_uri;
        proxy_set_header        X-Real-IP $remote_addr;
        proxy_set_header        Content-Length "";
        proxy_read_timeout      3600;
    }

    location /xxx/ {
        proxy_pass      http://127.0.0.1:8081/xxx/;
        proxy_redirect  http://127.0.0.1:8081/ /;
    }

    location /another_xxx/ {
        auth_request    /xxx/api/v1.0/auth/access;
        proxy_pass      http://127.0.0.1:8000/another_xxx/;
    }

    error_page  401  =302  http://localhost:8080/xxx/user/login.html;
    #error_page  404              /404.html;

    # redirect server error pages to the static page /50x.html
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   html;
    }

}

```



