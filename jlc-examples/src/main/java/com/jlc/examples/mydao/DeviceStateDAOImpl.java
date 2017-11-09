package com.jlc.examples.mydao;

import com.jlc.examples.mymodel.DeviceState;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author lokesh
 */

@Repository
public class DeviceStateDAOImpl implements DeviceStateDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(DeviceState deviceState) {
        entityManager.merge(deviceState);
    }

    @Override
    public DeviceState get(String device) {
        return entityManager.find(DeviceState.class, device);
    }

    @Override
    public List<DeviceState> list() {
        CriteriaQuery<DeviceState> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(DeviceState.class);
        @SuppressWarnings("unused")
        Root<DeviceState> root = criteriaQuery.from(DeviceState.class);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public void delete(String device) {
        entityManager.remove(get(device));
    }
}
