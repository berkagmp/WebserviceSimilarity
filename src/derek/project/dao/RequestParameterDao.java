package derek.project.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import derek.project.model.RequestParameter;

@Repository
public class RequestParameterDao {
	@PersistenceContext
	private EntityManager em;

	public void add(RequestParameter requestParameter) {
		em.persist(requestParameter);
	}

	public List<RequestParameter> list() {
		CriteriaQuery<RequestParameter> criteriaQuery = em.getCriteriaBuilder().createQuery(RequestParameter.class);
		@SuppressWarnings("unused")
		Root<RequestParameter> root = criteriaQuery.from(RequestParameter.class);
		return em.createQuery(criteriaQuery).getResultList();
	}
}
