package br.edu.ifpr.bsi.prefeiturainterativaweb.helpers;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.jdbc.ReturningWork;

public class HibernateHelper {
	private static SessionFactory fabricaDeSessoes = criarFabricaDeSessoes();

	public static SessionFactory getFabricaDeSessoes() {
		return fabricaDeSessoes;
	}

	public static Connection getConexao() {
		Session sessao = fabricaDeSessoes.openSession();
		java.sql.Connection conexao = sessao.doReturningWork(new ReturningWork<Connection>() {
			@Override
			public Connection execute(java.sql.Connection conn) throws SQLException {
				// TODO Auto-generated method stub
				return conn;
			}
		});
		return conexao;
	}

	private static SessionFactory criarFabricaDeSessoes() {
		try {
			
            StandardServiceRegistry registro = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
            Metadata metadata = new MetadataSources(registro).getMetadataBuilder().build();

			SessionFactory fabrica = metadata.getSessionFactoryBuilder().build();
			return fabrica;
		} catch (Throwable ex) {
			System.err.println("A fábrica de sessões não pode ser criada." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
}
