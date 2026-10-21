package com.bbva.migracionstandingoffer.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    /**
     * Datasource principal: Base de Datos Oracle de Enterprise (Origen).
     * Se usa para consultar los casos ya aprobados en STF_CASE.
     */
    @Bean(destroyMethod = "close")
    @Primary
    public HikariDataSource enterpriseDataSource(
            @Value("${enterprise.datasource.url:${DB_HOST_BI:jdbc:oracle:thin:@//exbbvascan-de.co.igrupobbva:15535/DCARBON}}") String url,
            @Value("${enterprise.datasource.username:${DB_USER_BI:BBVA_BI}}") String user,
            @Value("${enterprise.datasource.password:${DB_PASSWORD_BI:BBVA_BI}}") String password,
            @Value("${enterprise.datasource.driver-class-name:oracle.jdbc.OracleDriver}") String driver) {
        return crearPool("enterprise-bi", url, user, password, driver);
    }

    @Bean
    @Primary
    public JdbcTemplate enterpriseJdbcTemplate(@Qualifier("enterpriseDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    /**
     * Datasource para consultar el BDM de Bonita Enterprise (BONITA7BDM.STANDINGOFFERDM).
     * Permite consultar directamente los casos de negocio que inician por 3000xxxx.
     */
    @Bean(destroyMethod = "close")
    public HikariDataSource bonitaBdmDataSource(
            @Value("${bonita-bdm.datasource.url:${DB_HOST_B7BDM:jdbc:oracle:thin:@//exbbvascan-de.co.igrupobbva:15535/DBONITA}}") String url,
            @Value("${bonita-bdm.datasource.username:${DB_USER_B7BDM:BONITA7BDM}}") String user,
            @Value("${bonita-bdm.datasource.password:${DB_PASSWORD_B7BDM:BONITA7BDM}}") String password,
            @Value("${bonita-bdm.datasource.driver-class-name:oracle.jdbc.OracleDriver}") String driver) {
        return crearPool("bonita-bdm", url, user, password, driver);
    }

    @Bean
    public JdbcTemplate bonitaBdmJdbcTemplate(@Qualifier("bonitaBdmDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    /**
     * Datasource secundario: Base de Datos Oracle de Community (Destino).
     * Disponible para consultar o validar tablas BDM/BI en el entorno Community.
     */
    @Bean(destroyMethod = "close")
    public HikariDataSource communityDataSource(
            @Value("${community.datasource.url:${DB_HOST_BI_COM:jdbc:oracle:thin:@//exbbvascan-de.co.igrupobbva:15535/DCARBONPC}}") String url,
            @Value("${community.datasource.username:${DB_USER_BI_COM:${DB_USER_BI:BBVA_BI}}}") String user,
            @Value("${community.datasource.password:${DB_PASSWORD_BI_COM:${DB_PASSWORD_BI:BBVA_BI}}}") String password,
            @Value("${community.datasource.driver-class-name:oracle.jdbc.OracleDriver}") String driver) {
        return crearPool("community-bi", url, user, password, driver);
    }

    @Bean
    public JdbcTemplate communityJdbcTemplate(@Qualifier("communityDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    private static HikariDataSource crearPool(String nombre, String url, String user, String password, String driver) {
        HikariDataSource ds = new HikariDataSource();
        ds.setPoolName("pool-" + nombre);
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setDriverClassName(driver);
        ds.setMaximumPoolSize(5);
        ds.setMinimumIdle(1);
        ds.setConnectionTimeout(30000L);
        // Evita que la aplicación falle al iniciar si la BD no está disponible inmediatamente
        ds.setInitializationFailTimeout(-1L);
        return ds;
    }
}
