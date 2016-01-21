package tesis.server.socialNetwork.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.hibernate.Query;

import tesis.server.socialNetwork.entity.NotificacionEntity;
import tesis.server.socialNetwork.entity.VoluntarioEntity;
import tesis.server.socialNetwork.utils.Utiles;

public class NotificacionDao extends GenericDao<NotificacionEntity, Integer> {

	@Override
	protected Class<NotificacionEntity> getEntityBeanType() {
		return NotificacionEntity.class;
	}
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void guardar(NotificacionEntity entity){
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		entity.setFechaCreacionNotificacion(timestamp);
		
		this.save(entity);
	}
	
	
	/**
	 * 
	 * @param username
	 * @param ultimaActualizacion puede ser null
	 * @param sinceId indica el ID de la notificacion que esta en el tope de la pila ---> https://dev.twitter.com/rest/reference/get/statuses/mentions_timeline
	 * @return
	 */
	public List<NotificacionEntity> getListaNotificacion(String username, Timestamp ultimaActualizacion){
		String consulta = "from NotificacionEntity n where n.voluntarioTarget.userName= :username";
		if(ultimaActualizacion != null){
			consulta += " and n.fechaCreacionNotificacion > :ultimaActualizacion";
		}
		Query query = this.getSession().createQuery(consulta);
		query.setParameter("username", username.toLowerCase());
		if(ultimaActualizacion != null){
			query.setParameter("ultimaActualizacion", ultimaActualizacion);
		}
		query.setMaxResults(70);
		List<NotificacionEntity> lista = query.list();
			
		return lista;
	}
	
	
	/**
	 * Metodo que crea y guarda una notificacion de solicitud de amistad
	 * 
	 * @param solicitante
	 * @param solicitado
	 */
	public void crearNotificacionSolicitudAmistad(VoluntarioEntity solicitante, VoluntarioEntity solicitado){
		try {
			NotificacionEntity notif = new NotificacionEntity();
			notif.setTipoNotificacion(Utiles.NOTIF_NUEVA_SOLICITUD_AMISTAD);
			notif.setMensaje("Te ha enviado una solicitud de amistad");
			notif.setVoluntarioCreadorNotificacion(solicitante);
			notif.setVoluntarioTarget(solicitado);
			this.guardar(notif);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
