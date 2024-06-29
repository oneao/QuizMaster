package com.quizmaster.framework.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.quizmaster.common.core.domain.Result;
import com.quizmaster.common.enums.ResponseEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    // 放行路径
    private final String[] EXCLUDE_PATH_PATTERNS = {
            "/doc.html", "/test", "favicon.ico"
    };
    // 超时时间
    private final long timeout = 600;

    /**
     * 注册 Sa-Token 拦截器打开注解鉴权功能:主要用于在Spring MVC控制器之前和之后执行逻辑，与Spring框架集成紧密，使用方便。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器打开注解鉴权功能
        registry.addInterceptor(new SaInterceptor(handle -> {
            // SaManager.getLog().debug("----- 请求path={}  提交token={}", SaHolder.getRequest().getRequestPath(), StpUtil.getTokenValue());

            // 指定一条 match 规则
            SaRouter
                    .match("/user/**")    // 拦截的 path 列表，可以写多个
                    .notMatch("/user/login")     // 排除掉的 path 列表，可以写多个
                    .check(r -> StpUtil.checkLogin());        // 要执行的校验动作，可以写完整的 lambda 表达式

            // 权限校验 -- 不同模块认证不同权限
//            SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
//            SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
//            SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
//            SaRouter.match("/notice/**", r -> StpUtil.checkPermission("notice"));
//            SaRouter.match("/comment/**", r -> StpUtil.checkPermission("comment"));

            // 甚至你可以随意的写一个打印语句
            SaRouter.match("/router/print", r -> System.out.println("----啦啦啦----"));

            // 写一个完整的 lambda
            SaRouter.match("/router/print2", r -> {
                System.out.println("----啦啦啦2----");
                // ... 其它代码
            });

            /*
             * 相关路由都定义在 com.pj.cases.use.RouterCheckController 中
             */
        })).addPathPatterns("/**");

    }

    /**
     * 注册 [Sa-Token全局过滤器]:主要用于对请求和响应进行预处理和后处理，独立于Spring框架，由Servlet容器管理。
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 拦截路径
                .addInclude("/**")
                // 放行路由
                .addExclude(EXCLUDE_PATH_PATTERNS)
                // 前置函数：在每次认证函数之前执行（BeforeAuth 不受 includeList 与 excludeList 的限制，所有请求都会进入）
                .setBeforeAuth(r -> {
                    // ---------- 设置一些安全响应头 ----------
                    SaHolder.getResponse()
                            // 服务器名称
                            .setServer("sa-server")
                            // 是否可以在iframe显示视图： DENY=不可以 | SAMEORIGIN=同域下可以 | ALLOW-FROM uri=指定域名下可以
                            .setHeader("X-Frame-Options", "SAMEORIGIN")
                            // 是否启用浏览器默认XSS防护： 0=禁用 | 1=启用 | 1; mode=block 启用, 并在检查到XSS攻击时，停止渲染页面
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff")
                            /* ---------------------------------------------------------- */
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", "*")
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "*")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "*");
                    ;
                })
                // 认证函数: 每次请求执行
                .setAuth(obj -> {
                    // 检查是否登录
                    SaRouter.match("/**").check(r -> StpUtil.checkLogin());
                    // 刷新token有效期
                    if (StpUtil.getTokenTimeout() < timeout) {
                        StpUtil.renewTimeout(1800);
                    }
                    // 输出 API 请求日志，方便调试代码
                    SaManager.getLog().debug("请求path={}  提交token={}", SaHolder.getRequest().getRequestPath(), StpUtil.getTokenValue());
                })
                //  异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> {
                    // 设置响应头
                    SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
                    if (e instanceof NotLoginException) {
                        return JSONUtil.toJsonStr(Result.fail(ResponseEnum.UNAUTHORIZED));
                    }
                    return SaResult.error(e.getMessage());
                });
    }
}
