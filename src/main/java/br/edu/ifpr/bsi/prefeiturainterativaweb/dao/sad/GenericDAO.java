package br.edu.ifpr.bsi.prefeiturainterativaweb.dao.sad;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.edu.ifpr.bsi.prefeiturainterativaweb.helpers.HibernateHelper;

public class GenericDAO<Entidade> {
	private Class<Entidade> classe;

	@SuppressWarnings("unchecked")
	public GenericDAO() {
		this.classe = (Class<Entidade>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

	public boolean merge(Entidade entidade) {
		boolean success = true;
		Session sessao = HibernateHelper.getFabricaDeSessoes().openSession();
		Transaction transacao = null;
		try {
			transacao = sessao.beginTransaction();
			sessao.merge(entidade);
			transacao.commit();
			success = true;
		} catch (RuntimeException erro) {
			if (transacao != null) {
				transacao.rollback();
			}
			success = false;
			throw erro;
		} finally {
			sessao.close();
		}
		return success;
	}

	public boolean merge(List<Entidade> entidades) {
		boolean success = true;
		Session sessao = HibernateHelper.getFabricaDeSessoes().openSession();
		Transaction transacao = null;
		try {
			transacao = sessao.beginTransaction();
			entidades.forEach((entidade) -> {
				sessao.merge(entidade);
			});
			transacao.commit();
			success = true;
		} catch (RuntimeException erro) {
			if (transacao != null) {
				transacao.rollback();
			}
			success = false;
			throw erro;
		}
		return success;
	}

	public boolean remove(Entidade entidade) {
		boolean success = true;
		Session sessao = HibernateHelper.getFabricaDeSessoes().openSession();
		Transaction transacao = null;
		try {
			transacao = sessao.beginTransaction();
			sessao.delete(entidade);
			transacao.commit();
			success = true;
		} catch (RuntimeException erro) {
			if (transacao != null) {
				transacao.rollback();
			}
			success = false;
		} finally {
			sessao.close();
		}
		return success;
	}

	public boolean removeAll(List<Entidade> entidades) {
		boolean success = true;
		Session sessao = HibernateHelper.getFabricaDeSessoes().openSession();
		Transaction transacao = null;
		try {
			transacao = sessao.beginTransaction();
			entidades.forEach((entidade) -> {
				sessao.delete(entidade);
			});
			transacao.commit();
			success = true;
		} catch (RuntimeException erro) {
			if (transacao != null) {
				transacao.rollback();
			}
			success = false;
		} finally {
			sessao.close();
		}
		return success;
	}

	public List<Entidade> getAll() {
		List<Entidade> resultado = null;
		Session sessao = HibernateHelper.getFabricaDeSessoes().openSession();
		try {
			CriteriaBuilder builder = sessao.getCriteriaBuilder();
			CriteriaQuery<Entidade> criteria = builder.createQuery(classe);
			Root<Entidade> root = criteria.from(classe);
			criteria.select(root);
			return sessao.createQuery(criteria).getResultList();
		} catch (RuntimeException erro) {
			resultado = null;
		} finally {
			sessao.close();
		}
		return resultado;
	}

}