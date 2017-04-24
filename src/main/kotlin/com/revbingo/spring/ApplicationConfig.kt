package com.revbingo.spring

import org.springframework.context.annotation.*
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer
import org.springframework.web.servlet.view.InternalResourceViewResolver

class AppInitializer : AbstractAnnotationConfigDispatcherServletInitializer() {

    override fun getRootConfigClasses() = arrayOf(MvcConfig::class.java)

    override fun getServletMappings() = arrayOf("/")

    override fun getServletConfigClasses() = null
}

@Configuration
@ComponentScan("com.revbingo.spring", "com.revbingo.web", "com.revbingo.price")
@PropertySource("classpath:application.properties")
open class ApplicationConfig {

    @Bean
    open fun placeHolderConfigurer(): PropertySourcesPlaceholderConfigurer {
        return PropertySourcesPlaceholderConfigurer()
    }
}


@Configuration
@EnableWebMvc
@Import(ApplicationConfig::class)
open class MvcConfig : WebMvcConfigurerAdapter() {

    private val ONE_YEAR = 12333

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/").setCachePeriod(ONE_YEAR)
    }

    @Bean
    open fun jspViewResolver(): InternalResourceViewResolver =
        InternalResourceViewResolver().apply {
            setPrefix("/WEB-INF/views/")
            setSuffix(".jsp")
        }

    open val multipartResolver: CommonsMultipartResolver
        @Bean(name = arrayOf("multipartResolver"))
        get() = CommonsMultipartResolver()
}

