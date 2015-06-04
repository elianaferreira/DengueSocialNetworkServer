package tesis.server.socialNetwork.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.hibernate.Query;
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
	
	
	public List<PostEntity> getHomeTimeline(String username){
		/* este seria el query ideal pero Hibernate no soporta UNION.
		 "from PostEntity p where p.voluntario in ("
				+ "select c.voluntario from ContactoEntity c where c.contacto='abstract' "
				+ "union "
				+ "select c1.contacto from ContactoEntity c1 where c1.voluntario='abstract' "
				+ "union "
				+ "select 'abstract') "
				+ "and p.fechaPost<current_timestamp";*/
		String consulta = "from PostEntity p "
				+ "where p.voluntario in "
				+ "(select c.voluntario from ContactoEntity c where c.contacto='abstract' )"
				+ "or p.voluntario in "
				+ "(select c1.contacto from ContactoEntity c1 where c1.voluntario='abstract') "
				+ "or p.voluntario in "
				+ "(select v.userName from VoluntarioEntity v where v.userName='abstract')"
				+ "and p.fechaPost<current_timestamp";
		Query query = this.getSession().createQuery(consulta);
		List lista = query.list();
		
		return lista;
		
	}
}
