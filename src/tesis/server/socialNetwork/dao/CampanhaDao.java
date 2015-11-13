package tesis.server.socialNetwork.dao;

import org.hibernate.Query;

import tesis.server.socialNetwork.entity.CampanhaEntity;

public class CampanhaDao extends GenericDao<CampanhaEntity, Integer> {

	@Override
	protected Class<CampanhaEntity> getEntityBeanType() {
		return CampanhaEntity.class;
	}

	public void guardar(CampanhaEntity entity){
		entity.setActiva(true);
		this.save(entity);
	}
	
	
	public CampanhaEntity buscarPorNombre(String nombreBuscar){
		String consulta = "from CampanhaEntity c where c=:nombre";
		Query query = this.getSession().createQuery(consulta);
		query.setString("nombre", nombreBuscar);
		 CampanhaEntity c =  (CampanhaEntity) query.uniqueResult();
		 
		 return c;
		
		
	}
}
