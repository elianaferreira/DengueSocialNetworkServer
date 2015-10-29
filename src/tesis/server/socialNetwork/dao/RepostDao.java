package tesis.server.socialNetwork.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.Query;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import tesis.server.socialNetwork.entity.RepostEntity;

@Controller
@LocalBean
public class RepostDao extends GenericDao<RepostEntity, Integer> {

	@Override
	protected Class<RepostEntity> getEntityBeanType() {
		return RepostEntity.class;
	}
	
	@Inject
	private PostDao postDao;
	

	public void guardar(RepostEntity repost){
		Date date = new Date();
		repost.setFechaRepost(new Timestamp(date.getTime()));
		this.save(repost);
	}
	
	public void eliminar(RepostEntity repost){
		this.delete(repost);
	}
	
	
	/**
	 * Metodo que retorna los repost de un usuario a partir de una fecha dada
	 * 
	 * @param usernameAutor
	 * @param ultimaActualizacion
	 * @return
	 */
	public List<RepostEntity> getReposts(String username, Timestamp ultimaActualizacion, Boolean nuevos){
		/*String consulta = "from RepostEntity r where r.autorRepost.userName = :autor and "
				+ "r.fechaRepost > :ultimaactualizacion order by r.fechaRepost desc";
		
		Query query = this.getSession().createQuery(consulta);
		query.setParameter("autor", usernameAutor);
		query.setParameter("ultimaactualizacion", ultimaActualizacion);
		List lista = query.list();
		
		return lista;*/
		System.out.println("Uusario " + username + "; timestamp: " + ultimaActualizacion.toString() + "; son nuevos?: " + nuevos.toString());
		String condicionActualizacion = "";
		String condicionNuevos = " and rp.fechaRepost> :ultimaactualizacion order by rp.fechaRepost asc";
		String condicionViejos = " and rp.fechaRepost< :ultimaactualizacion order by rp.fechaRepost desc";
		if(nuevos){
			condicionActualizacion = condicionNuevos;
		} else {
			condicionActualizacion = condicionViejos;
		}
		
		String consulta = "from RepostEntity rp "
				+ "where (rp.autorRepost in "
				+ "(select c.voluntario from ContactoEntity c where c.contacto.userName= :username )"
				+ "or rp.autorRepost in "
				+ "(select c1.contacto from ContactoEntity c1 where c1.voluntario.userName= :username) "
				+ "or rp.autorRepost in "
				+ "(select v.userName from VoluntarioEntity v where v.userName= :username))"
				+ condicionActualizacion;
		Query query = this.getSession().createQuery(consulta);
		query.setParameter("username", username);
		query.setParameter("ultimaactualizacion", ultimaActualizacion);
		//limitar la cantidad de registros
		query.setMaxResults(5);
		List lista = query.list();
		
		return lista;
	}
	
	
	/**
	 * Metodo que retorna los reposts hechos por el usuario solicitante
	 * @param username
	 * @param ultimaActualizacion
	 * @param nuevos
	 * @return
	 */
	public List<RepostEntity> getOwnReposts(String username, Timestamp ultimaActualizacion, Boolean nuevos){
		System.out.println("Usario " + username + "; timestamp: " + ultimaActualizacion.toString() + "; son nuevos?: " + nuevos.toString());
		String condicionActualizacion = "";
		String condicionNuevos = " and rp.fechaRepost> :ultimaactualizacion order by rp.fechaRepost asc";
		String condicionViejos = " and rp.fechaRepost< :ultimaactualizacion order by rp.fechaRepost desc";
		if(nuevos){
			condicionActualizacion = condicionNuevos;
		} else {
			condicionActualizacion = condicionViejos;
		}
		
		String consulta = "from RepostEntity rp where rp.autorRepost.userName = :username"
				+ condicionActualizacion;
		Query query = this.getSession().createQuery(consulta);
		query.setParameter("username", username);
		query.setParameter("ultimaactualizacion", ultimaActualizacion);
		//limitar la cantidad de registros
		query.setMaxResults(5);
		List lista = query.list();
		
		return lista;
	}

	
	/**
	 * Metodo que retorna el JSON correspondiente de un repost
	 * 
	 * @param repost
	 * @return
	 */
	public JSONObject getJSONFromRepost(RepostEntity repost, String usernameSolicitante){
		JSONObject retorno = new JSONObject();
		retorno.put("idRepost", repost.getIdRepost());
		retorno.put("fecha", repost.getFechaRepost());
		retorno.put("autor", repost.getAutorRepost().getNombreReal());
		retorno.put("post", postDao.getJSONFromPost(usernameSolicitante, repost.getPost()));
		
		return retorno;
		
		
	}
}
