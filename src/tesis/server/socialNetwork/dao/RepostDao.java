package tesis.server.socialNetwork.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Query;
import org.json.JSONObject;

import tesis.server.socialNetwork.entity.RepostEntity;

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
	public List<RepostEntity> getReposts(String usernameAutor, Timestamp ultimaActualizacion){
		String consulta = "from RepostEntity r where r.autorRepost.userName = :autor and "
				+ "r.fechaRepost > :ultimaactualizacion order by r.fechaRepost desc";
		
		Query query = this.getSession().createQuery(consulta);
		query.setParameter("autor", usernameAutor);
		query.setParameter("ultimaactualizacion", ultimaActualizacion);
		List lista = query.list();
		
		return lista;
	}

	
	/**
	 * Metodo que retorna el JSON correspondiente de un repost
	 * 
	 * @param repost
	 * @return
	 */
	public JSONObject getJSONFromRepost(RepostEntity repost){
		JSONObject retorno = new JSONObject();
		retorno.put("idRepost", repost.getIdRepost());
		retorno.put("autor", repost.getAutorRepost().getNombreReal());
		retorno.put("post", postDao.getJSONFromPost(repost.getAutorRepost().getUserName(), repost.getPost()));
		
		return retorno;
		
		
	}
}
