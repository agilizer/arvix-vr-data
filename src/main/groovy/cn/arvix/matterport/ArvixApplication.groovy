package cn.arvix.matterport


import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.orm.jpa.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages = "cn.arvix.matterport.repository")
@EntityScan(basePackages = "cn.arvix.matterport.domain")
class ArvixApplication {

    static void main(String[] args) {
        SpringApplication.run ArvixApplication, args
    }
}
