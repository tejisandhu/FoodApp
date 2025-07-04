package com.ribjet.FoodApp.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
    private final AuthInterceptor authInterceptor;

    @Autowired  // Inject the AuthInterceptor
    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

	  @Override
	    public void addInterceptors(InterceptorRegistry registry) {
	        registry.addInterceptor(authInterceptor)
	                .addPathPatterns("/customer/**", "/restaurant/**","/contactus"); // Secure these routes
	    }
	  
	  @Override
	  public void addResourceHandlers(ResourceHandlerRegistry registry) {
	      registry.addResourceHandler("/uploads/**")
	              .addResourceLocations("file:src/main/resources/static/uploads/");
	  }
}
