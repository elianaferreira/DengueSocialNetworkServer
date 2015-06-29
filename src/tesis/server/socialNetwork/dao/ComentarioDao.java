package tesis.server.socialNetwork.dao;

import java.sql.Timestamp;
import java.util.Date;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.springframework.stereotype.Controller;

import tesis.server.socialNetwork.entity.ComentarioEntity;


@Controller
public class ComentarioDao extends GenericDao {

	@Override
	protected Class<ComentarioEntity> getEntityBeanType() {
		return ComentarioEntity.class;
	}
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void guardar(ComentarioEntity comentarioEntity){
		Date date = new Date();
		comentarioEntity.setFecha(new Timestamp(date.getTime()));
		this.save(comentarioEntity);
	}

	
}
