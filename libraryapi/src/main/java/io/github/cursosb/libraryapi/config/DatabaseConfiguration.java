package io.github.cursosb.libraryapi.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * CONFIGURAÇÃO PRINCIPAL DO BANCO DE DADOS
 * 
 * <p>Esta classe é responsável por criar e gerenciar o pool de conexões com o banco de dados,
 * utilizando o HikariCP (o pool de conexões mais rápido e eficiente do Java).</p>
 * 
 * <p><b>IMPORTANTE:</b> Todas as configurações são lidas do application.yml</p>
 */
@Configuration
@Slf4j
public class DatabaseConfiguration {

    // Configurações lidas do application.properties/yml
    @Value("${spring.datasource.url}")
    private String url;
    
    @Value("${spring.datasource.username}")
    private String username;
    
    @Value("${spring.datasource.password}")
    private String password;
    
    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    /**
     * MÉTODO ALTERNATIVO DE CONEXÃO (SEM POOL)
     * 
     * <p>Cria uma conexão básica sem pool de conexões. Não está em uso (@Bean comentado)
     * pois estamos usando o HikariCP, mas foi mantido como referência.</p>
     * 
     * @return DataSource simples sem pool de conexões
     * @deprecated Prefira usar o hikariDataSource() para melhor performance
     */
    //@Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driver);
        return ds;
    }

    /**
     * CONFIGURAÇÃO DO POOL DE CONEXÕES HIKARI CP
     * 
     * <p>Cria um pool de conexões otimizado com as seguintes características:</p>
     * <ul>
     *   <li><b>Performance:</b> O pool de conexões mais rápido do Java</li>
     *   <li><b>Resiliência:</b> Conexões são validadas e recicladas automaticamente</li>
     *   <li><b>Controle:</b> Limites máximos para evitar sobrecarga do banco</li>
     * </ul>
     * 
     * @return DataSource configurado com pool de conexões HikariCP
     * @see <a href="https://github.com/brettwooldridge/HikariCP">Documentação HikariCP</a>
     */
    @Bean
    public DataSource hikariDataSource() {
        log.info("Iniciando conexão com o banco na URL: {}", url);

        HikariConfig config = new HikariConfig();
        
        // CONFIGURAÇÕES BÁSICAS
        config.setUsername(username);       // Usuário do banco
        config.setPassword(password);       // Senha do banco
        config.setDriverClassName(driver);  // Driver JDBC
        config.setJdbcUrl(url);             // URL de conexão

        // OTIMIZAÇÕES DE PERFORMANCE
        config.setMaximumPoolSize(10);      // Máximo de conexões ativas (evita sobrecarga)
        config.setMinimumIdle(1);           // Conexões mantidas ociosas (para respostas rápidas)
        config.setPoolName("library-db-pool"); // Identificador para monitoramento

        // CONFIGURAÇÕES DE RESILIÊNCIA
        config.setMaxLifetime(600000);      // Tempo máximo de vida de uma conexão (10min)
        config.setConnectionTimeout(100000); // Tempo máximo para obter conexão (100s)
        config.setConnectionTestQuery("select 1"); // Query para testar conexões

        return new HikariDataSource(config);
    }
}