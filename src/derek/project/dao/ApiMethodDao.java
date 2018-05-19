package derek.project.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import derek.project.model.ApiMethod;

@Repository
public class ApiMethodDao {
	@PersistenceContext
	private EntityManager em;

	public void add(ApiMethod apiMethod) {
		em.persist(apiMethod);
	}

	public List<ApiMethod> list() {
		CriteriaQuery<ApiMethod> criteriaQuery = em.getCriteriaBuilder().createQuery(ApiMethod.class);
		@SuppressWarnings("unused")
		Root<ApiMethod> root = criteriaQuery.from(ApiMethod.class);
		return em.createQuery(criteriaQuery).getResultList();
	}
}
