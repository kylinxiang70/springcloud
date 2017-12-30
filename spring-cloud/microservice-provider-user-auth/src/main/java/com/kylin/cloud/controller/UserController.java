package com.kylin.cloud.controller;

import com.kylin.cloud.entity.User;
import com.kylin.cloud.repository.UserRepository;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EurekaClient eurekaClient;

    @Autowired
    //注意导包
    private DiscoveryClient discoveryClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user/{id}")
    public User findById(@PathVariable Long id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            Collection<? extends GrantedAuthority> collection = userDetails.getAuthorities();
            for (GrantedAuthority c : collection) {
                UserController.LOGGER.info("当前用户是{}，角色是{}", userDetails.getUsername(), c.getAuthority());
            }
        } else {
            //do other things
        }
        return this.userRepository.findOne(id);
    }

    @GetMapping("/eureka-instance")
    public String serviceUrl() {
        // 默认情况下，MICROSERVICE-PROVIDER-USER为虚拟化的主机名，就是应用的名称
        InstanceInfo instance = eurekaClient.getNextServerFromEureka("microservice-provider-user", false);
        return instance.getHomePageUrl();
    }

    @GetMapping("/instance-info")
    public ServiceInstance showInfo() {
        ServiceInstance localServiceInstance = this.discoveryClient.getLocalServiceInstance();
        return localServiceInstance;
    }

}
