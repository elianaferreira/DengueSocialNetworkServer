package tesis.server.socialNetwork.dao;

import tesis.server.socialNetwork.entity.CampanhaEntity;

public class CampanhaDao extends GenericDao<CampanhaEntity, Integer> {

	@Override
	protected Class<CampanhaEntity> getEntityBeanType() {
		return CampanhaEntity.class;
	}

	
	
}
