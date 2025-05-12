package bd.org.quantum.hrm.config;

import bd.org.quantum.authorizer.interceptor.AuthInterceptor;
import bd.org.quantum.common.configs.CommonBeanConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Profile("!dev")
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class WebConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    private final List<String> excludePathPatterns;

    @Autowired
    public WebConfig(@Value("#{'${app.auth.interceptor.exclude.paths}'.split(',')}") List<String> excludePathPatterns,
                     AuthInterceptor authInterceptor) {
        this.excludePathPatterns = excludePathPatterns;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .excludePathPatterns(excludePathPatterns)
                .order(1);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/resources/**")
                .addResourceLocations(
                        "classpath:/static/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        CommonBeanConfig.addArgumentResolvers(resolvers);
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }
}