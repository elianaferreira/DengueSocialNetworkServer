package tesis.server.socialNetwork.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import tesis.server.socialNetwork.entity.SolicitudAmistadEntity;


@Controller
public class SolicitudAmistadDao extends GenericDao<SolicitudAmistadEntity, Integer> {

	//acceso a Base de Datos
	@Inject
	private VoluntarioDao voluntarioDao;
	
	@Override
	protected Class<SolicitudAmistadEntity> getEntityBeanType() {
		return SolicitudAmistadEntity.class;
	}
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void guadar(SolicitudAmistadEntity solicitudAmistadEntity){
		//por defecto el valor de 'aceptada' sera FALSE
		solicitudAmistadEntity.setAceptada(false);
		this.save(solicitudAmistadEntity);
	}
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void modificar(SolicitudAmistadEntity solicitudAmistadEntity){
		this.update(solicitudAmistadEntity);
	}
	
	
	/**
	 * Metodo que trae la lista de solicitudes pendientes del voluntario solicitado
	 * 
	 * @param username
	 * @return
	 */
	public List<SolicitudAmistadEntity> getListaSolicitudesPendientes(String username){
		Criteria criteriaSolicitud = getSession().createCriteria(SolicitudAmistadEntity.class);
		criteriaSolicitud.createCriteria("usuarioSolicitado", "v");
		criteriaSolicitud.add(Restrictions.eq("v.userName", username));
		criteriaSolicitud.add(Restrictions.eq("aceptada", false));
		List<SolicitudAmistadEntity> listaPendientes = criteriaSolicitud.list();
		return listaPendientes;
	}
	
	
	/**
	 * Metodo que convierte una solicitud de amistad a un objeto JSON
	 * 
	 * @param solicitudAmistadEntity
	 * @return
	 */
	public JSONObject getJSONStringFromSolicitud(SolicitudAmistadEntity solicitudAmistadEntity){
		JSONObject retorno = new JSONObject();
		
		retorno.put("id", solicitudAmistadEntity.getIdSolicitudAmistad());
		retorno.put("voluntariosolicitante", voluntarioDao.getJSONFromVoluntario(solicitudAmistadEntity.getUsuarioSolicitante()));
		retorno.put("aceptada", solicitudAmistadEntity.getAceptada());
		
		return retorno;
	}
	
	
	
	/**
	 * Metodo que retorna la lista de solicitudes como un string.
	 * Cada solicitud es un objeto JSON.
	 * Es necesario quitar la informacion del solicitado del JSON, no es necesaria esta informacion
	 * 
	 * @param listaSolicitudes
	 * @return
	 */
	public String getListParsedFromSolicitudes(List<SolicitudAmistadEntity> listaSolicitudes){
		//creamos la lista a ser retornada
		List<JSONObject> retorno = new ArrayList<JSONObject>();
		//recorremos la lista del parametro
		for(int i=0; i<listaSolicitudes.size(); i++){
			JSONObject solicitud = getJSONStringFromSolicitud(listaSolicitudes.get(i));
			retorno.add(solicitud);
		}
		
		return retorno.toString();
	}
}
