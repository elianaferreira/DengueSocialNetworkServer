package tesis.server.socialNetwork.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Query;
import org.json.JSONObject;

import tesis.server.socialNetwork.entity.CampanhaEntity;
import tesis.server.socialNetwork.entity.NotificacionEntity;
import tesis.server.socialNetwork.entity.VoluntarioEntity;
import tesis.server.socialNetwork.utils.Utiles;

public class CampanhaDao extends GenericDao<CampanhaEntity, Integer> {
	
	@Inject
	NotificacionDao notificacionDao;

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
		jsonRetorno.put("cantInvitados", c.getVoluntariosInvitados().size());
		
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
	
	
	/**
	 * Metodo que guarda una entidad notificacion para el voluntario
	 * @param listaInvitados
	 */
	public void guardarNotificacionParaVoluntarios(CampanhaEntity campanha, List<VoluntarioEntity> listaInvitados){
		for(int i=0; i<listaInvitados.size(); i++){
			try{
				NotificacionEntity notificacion = new NotificacionEntity();
				notificacion.setTipoNotificacion(Utiles.NOTIF_INVITADO_CAMPANHA);
				notificacion.setMensaje("El Administrador te ha invitado a unirte.");
				notificacion.setVoluntarioTarget(listaInvitados.get(i));
				notificacion.setCampanha(campanha);
				notificacionDao.guardar(notificacion);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Metodo que retorna todas la campanhas de forma paginada en base a la fecha de lanzamiento
	 * 
	 * @param ultimaFecha
	 * @return
	 */
	public List<CampanhaEntity> listaCampanhas(Date ultimaFecha){
		
		//"from PostEntity p where p.quienDebeSolucionar = :ente and p.fechaPost < :ultimaActualizacion order by p.fechaPost desc
		String consulta = "from CampanhaEntity c where c.fechaLanzamiento < :ultimaActualizacion order by c.fechaLanzamiento desc";
		Query query = this.getSession().createQuery(consulta);
		query.setParameter("ultimaActualizacion", ultimaFecha);
		query.setMaxResults(2);
		List lista = query.list();
		
		return lista;
	}
}
