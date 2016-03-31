package cn.arvix.vrdata


import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.orm.jpa.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages = "cn.arvix.vrdata.repository")
@EntityScan(basePackages = "cn.arvix.vrdata.domain")
class VrDataApplication {

    static void main(String[] args) {
        SpringApplication.run VrDataApplication, args
    }
}
