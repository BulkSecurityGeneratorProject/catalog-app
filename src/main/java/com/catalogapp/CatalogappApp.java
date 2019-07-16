package com.catalogapp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.catalogapp.config.ApplicationProperties;
import com.catalogapp.config.DefaultProfileUtil;
import com.catalogapp.domain.Authority;
import com.catalogapp.domain.User;
import com.catalogapp.repository.AuthorityRepository;
import com.catalogapp.repository.UserRepository;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.github.jhipster.config.JHipsterConstants;

@SpringBootApplication
@EnableConfigurationProperties({ LiquibaseProperties.class, ApplicationProperties.class })
public class CatalogappApp implements InitializingBean, CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CatalogappApp.class);

    private final Environment env;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder encoder;

    public CatalogappApp(Environment env) {
        this.env = env;
    }

    /**
     * Initializes catalogapp.
     * <p>
     * Spring profiles can be configured with a program argument
     * --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href=
     * "https://www.jhipster.tech/profiles/">https://www.jhipster.tech/profiles/</a>.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
                && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run "
                    + "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
                && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not "
                    + "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CatalogappApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info(
                "\n----------------------------------------------------------\n\t"
                        + "Application '{}' is running! Access URLs:\n\t" + "Local: \t\t{}://localhost:{}{}\n\t"
                        + "External: \t{}://{}:{}{}\n\t"
                        + "Profile(s): \t{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"), protocol, serverPort, contextPath, protocol, hostAddress,
                serverPort, contextPath, env.getActiveProfiles());
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            Authority adminAuthority = new Authority();
            adminAuthority.setName("ROLE_ADMIN");
            Authority userAuthority = new Authority();
            userAuthority.setName("ROLE_USER");
            adminAuthority = authorityRepository.save(adminAuthority);
            userAuthority = authorityRepository.save(userAuthority);
            
            User user = new User();
            user.setActivated(true);
            Set<Authority> authorities = new HashSet<>();
            authorities.add(adminAuthority);
            authorities.add(userAuthority);
            user.setAuthorities(authorities);
            user.setEmail("zakaria.kaoukab@gmail.com");
            user.setLogin("admin");
            user.setPassword(encoder.encode("admin"));
            userRepository.save(user);
        }
    }
}
