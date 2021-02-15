package br.edu.ifpr.bsi.prefeiturainterativaweb.dao.sad;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

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

	@SuppressWarnings("unchecked")
	public List<Entidade> getAll() {
		List<Entidade> resultado = null;
		Session sessao = HibernateHelper.getFabricaDeSessoes().openSession();
		try {
			CriteriaBuilder builder = sessao.getCriteriaBuilder();
			CriteriaQuery<Entidade> criteria = builder.createQuery(classe);
			criteria.
			criteria.Criteria consulta = sessao.createCriteria(classe);
			resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			resultado = null;
		} finally {
			sessao.close();
		}
		return resultado;
	}

	@SuppressWarnings("unchecked")
	public Entidade get(String _ID) {
		Entidade resultado = null;
		Session sessao = HibernateHelper.getFabricaDeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(classe);
			consulta.add(Restrictions.idEq(_ID));
			resultado = (Entidade) consulta.uniqueResult();
			return resultado;
		} catch (RuntimeException erro) {
			resultado = null;
		} finally {
			sessao.close();
		}
		return resultado;
	}

	@SuppressWarnings("unchecked")
	public List<Entidade> getAll(String campoOrdenacao) {
		List<Entidade> resultado = null;
		Session sessao = HibernateHelper.getFabricaDeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(classe);
			consulta.addOrder(Order.asc(campoOrdenacao));
			resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			resultado = null;
		} finally {
			sessao.close();
		}
		return resultado;
	}

	@SuppressWarnings("unchecked")
	public List<Entidade> getAll(String campo, String ordem, Object valor) {
		List<Entidade> resultado = null;
		Session sessao = HibernateHelper.getFabricaDeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(classe);
			if (valor != null)
				consulta.add(Restrictions.eq(campo, valor));
			consulta.addOrder(Order.asc(ordem));
			resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			resultado = null;
		} finally {
			sessao.close();
		}
		return resultado;
	}
}