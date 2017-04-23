package com.aws.codestar.projecttemplates.configuration;

import com.revbingo.price.PricingProvider;
import com.revbingo.web.ApiController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan({ "com.aws.codestar.projecttemplates.configuration", "com.revbingo.web", "com.revbingo.price" })
@PropertySource("classpath:application.properties")
public class ApplicationConfig {

    @Bean
    public ApiController apiController() { return new ApiController(pricingProvider()); }

    @Bean
    public PricingProvider pricingProvider() { return new PricingProvider("https://pricing.us-east-1.amazonaws.com/offers/v1.0/aws/AmazonEC2/current/index.json"); }

    /**
     * Required to inject properties using the 'Value' annotation.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
