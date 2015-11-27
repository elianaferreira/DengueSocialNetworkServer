package tesis.server.socialNetwork.dao;

import java.util.List;

import org.hibernate.Query;
import org.json.JSONObject;

import tesis.server.socialNetwork.entity.CampanhaEntity;
import tesis.server.socialNetwork.entity.VoluntarioEntity;

public class CampanhaDao extends GenericDao<CampanhaEntity, Integer> {

	@Override
	protected Class<CampanhaEntity> getEntityBeanType() {
		return CampanhaEntity.class;
	}

	public void guardar(CampanhaEntity entity){
		entity.setActiva(true);
		this.save(entity);
	}
	
	
	public void modificar(CampanhaEntity entity){
		this.update(entity);
	}
	
	public CampanhaEntity buscarPorNombre(String nombreBuscar){
		String consulta = "from CampanhaEntity c where c.nombreCampanha=:nombre";
		Query query = this.getSession().createQuery(consulta);
		query.setString("nombre", nombreBuscar);
		 CampanhaEntity c =  (CampanhaEntity) query.uniqueResult();
		 
		 return c;
		
	}
	
	
	public List<CampanhaEntity> getAll(){
		String consulta = "from CampanhaEntity c";
		Query query = this.getSession().createQuery(consulta);
		
		List lista = query.list();
		return lista;
	}
	
	
	public JSONObject getJSONFromCampanha(CampanhaEntity c, String username){
		JSONObject jsonRetorno = new JSONObject();
		jsonRetorno.put("id", c.getIdCampanha());
		jsonRetorno.put("nombre", c.getNombreCampanha());
		jsonRetorno.put("mensaje", c.getMensaje());
		jsonRetorno.put("fechaInicio", c.getFechaLanzamiento());
		jsonRetorno.put("fechaFin", c.getFechaFinalizacion());
		jsonRetorno.put("cantAdheridos", c.getVoluntariosAdheridos().size());
		
		//verificamos si es un usuario el que lo solicita
		if(username != ""){
			//buscamos en la lista de adheridos
			List<VoluntarioEntity> listaAdheridos = c.getVoluntariosAdheridos();
			for(int j=0; j<listaAdheridos.size(); j++){
				if(listaAdheridos.get(j).getUserName().equals(username)){
					jsonRetorno.put("adherido", true);
					break;
				}
			}
		}
		
		return jsonRetorno;
	}

}
