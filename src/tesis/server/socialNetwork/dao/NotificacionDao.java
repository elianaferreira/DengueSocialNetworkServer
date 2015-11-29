package tesis.server.socialNetwork.dao;

import java.sql.Timestamp;
import java.util.Date;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import tesis.server.socialNetwork.entity.NotificacionEntity;

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

}
