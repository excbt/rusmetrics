package ru.excbt.datafuse.nmk.config.jpa;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Properties;

import org.springframework.core.env.Environment;

import ru.excbt.datafuse.hibernate.dialect.ExPostgreSQLDialect;

public class HibernateProps {

	public static final String DIALECT = "hibernate.dialect";
	public static final String SHOW_SQL = "hibernate.show_sql";
	public static final String FORMAT_SQL = "hibernate.format_sql";
	public static final String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";

	private HibernateProps() {

	}

	/**
	 * 
	 * @param env
	 * @param prefix
	 * @return
	 */
	public static Properties readEnvProps(Environment env, String prefix) {

		checkNotNull(env);
		checkNotNull(prefix);

		Properties properties = new Properties();
		properties.put(HibernateProps.DIALECT,
				ExPostgreSQLDialect.class.getName());
		properties.put(HibernateProps.SHOW_SQL,
				env.getProperty(String.format("%s.show_sql", prefix)));
		properties.put(HibernateProps.FORMAT_SQL,
				env.getProperty(String.format("%s.format_sql", prefix)));
		properties.put(HibernateProps.HBM2DDL_AUTO,
				env.getProperty(String.format("%s.hbm2ddl.auto", prefix)));
		return properties;

	}
}
