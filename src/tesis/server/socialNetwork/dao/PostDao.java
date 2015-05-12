package tesis.server.socialNetwork.dao;

import java.sql.Timestamp;
import java.util.Date;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.springframework.stereotype.Controller;

import tesis.server.socialNetwork.entity.PostEntity;


@Controller
public class PostDao extends GenericDao<PostEntity, Integer> {

	@Override
	protected Class<PostEntity> getEntityBeanType() {
		return PostEntity.class;
	}

	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void guardar(PostEntity postEntity){
		//para un nuevo post es necesario agregar el campo 'solucionado' a FALSE
		postEntity.setSolucionado(false);
		//agregamos la fecha en formato timestamp
		Date date = new Date();
		postEntity.setFechaPost(new Timestamp(date.getTime()));
		this.save(postEntity);
	}
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void modificar(PostEntity postEntity){
		this.update(postEntity);
	}
}
