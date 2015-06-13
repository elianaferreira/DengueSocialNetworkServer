package tesis.server.socialNetwork.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.hibernate.Query;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import tesis.server.socialNetwork.entity.PostEntity;


@Controller
public class PostDao extends GenericDao<PostEntity, Integer> {

	@Inject
	private VoluntarioDao voluntarioDao;
	
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
	
	/**
	 * Metodo que retorna todos los posts de los amigos de un usuario cuya fecha de publicacion sea mayor
	 * a la ultima vez que el usuario solicito una actualizacion de su timeline.
	 * 
	 * @param username
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<PostEntity> getHomeTimeline(String username, Timestamp ultimaActualizacion){
		/* este seria el query ideal pero Hibernate no soporta UNION.
		 "from PostEntity p where p.voluntario in ("
				+ "select c.voluntario from ContactoEntity c where c.contacto='abstract' "
				+ "union "
				+ "select c1.contacto from ContactoEntity c1 where c1.voluntario='abstract' "
				+ "union "
				+ "select 'abstract') "
				+ "and p.fechaPost<current_timestamp";*/
		//TODO cambiar la condicio de que sea fechaPost mayor a un timestamp
		String consulta = "from PostEntity p "
				+ "where p.voluntario in "
				+ "(select c.voluntario from ContactoEntity c where c.contacto.userName= :username )"
				+ "or p.voluntario in "
				+ "(select c1.contacto from ContactoEntity c1 where c1.voluntario.userName= :username) "
				+ "or p.voluntario in "
				+ "(select v.userName from VoluntarioEntity v where v.userName= :username)"
				+ "and p.fechaPost> :ultimaactualizacion order by p.fechaPost desc";
		Query query = this.getSession().createQuery(consulta);
		query.setParameter("username", username);
		query.setParameter("ultimaactualizacion", ultimaActualizacion);
		List lista = query.list();
		
		return lista;
	}
	
	
	/**
	 * Metodo que retorna un JSON representando a un post.
	 * 
	 * @param postEntity
	 * @return
	 */
	public JSONObject getJSONFromPost(PostEntity postEntity){
		JSONObject retorno = new JSONObject();
		retorno.put("mensaje", postEntity.getPost());
		retorno.put("latitud", postEntity.getLatitud());
		retorno.put("longitud", postEntity.getLongitud());
		retorno.put("fecha", postEntity.getFechaPost());
		retorno.put("solucionado", postEntity.getSolucionado());
		retorno.put("voluntario", voluntarioDao.getJSONFromVoluntario(postEntity.getVoluntario()));
		
		return retorno;
	}
	
	
}
