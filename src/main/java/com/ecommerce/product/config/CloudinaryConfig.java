package com.ecommerce.product.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary getCloudinary(){
        Map map=new HashMap();
        map.put("cloud_name","dzsm5ypo2");
        map.put("api_key","448695951633917");
        map.put("api_secret","ZpHTOfoVJA5kuUyH4RLdUtx8udo");
        map.put("secure",true);
        return new Cloudinary(map);
    }

}
