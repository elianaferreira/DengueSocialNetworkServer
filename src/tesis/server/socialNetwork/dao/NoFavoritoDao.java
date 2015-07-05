package tesis.server.socialNetwork.dao;

import java.util.List;

import org.hibernate.Query;
import org.json.JSONObject;

import tesis.server.socialNetwork.entity.FavoritoEntity;
import tesis.server.socialNetwork.entity.NoFavoritoEntity;
import tesis.server.socialNetwork.entity.PostEntity;

public class NoFavoritoDao extends GenericDao<NoFavoritoEntity, Integer> {

	@Override
	protected Class<NoFavoritoEntity> getEntityBeanType() {
		return NoFavoritoEntity.class;
	}

	
	public void guardar(NoFavoritoEntity noFavoritoEntity){
		this.save(noFavoritoEntity);
	}
	
	
	public void eliminar(NoFavoritoEntity noFavoritoEntity){
		this.delete(noFavoritoEntity);
	}
	
	/**
	 * Metodo que retorna una marcacion de noFavorito de acuerdo al post y 
	 * al autor de la marcacion.
	 * 
	 * @param idPostMarcado
	 * @param usernameAutorMarcacion
	 * @return
	 */
	public NoFavoritoEntity buscarMarcacion(Integer idPostMarcado, String usernameAutorMarcacion){
		String consulta = "from NoFavoritoEntity f "
				+ "where f.post.idPost = :idPost "
				+ "and f.autor.userName = :autorUsername";
		Query query = this.getSession().createQuery(consulta);
		query.setInteger("idPost", idPostMarcado);
		query.setString("autorUsername", usernameAutorMarcacion);
		NoFavoritoEntity noFav = (NoFavoritoEntity) query.uniqueResult();
		return noFav;  
	}
	
	
	/**
	 * Metodo que retorna la lista de no favoritos para un post dado.
	 * 
	 * @param postEntity
	 * @return
	 */
	public List<NoFavoritoEntity> listaNoFavoritosByPost(PostEntity postEntity){
		JSONObject restriccion = new JSONObject();
		restriccion.put("post", postEntity.getIdPost());
		
		List<NoFavoritoEntity> listaRetorno = this.getListOfEntitiesWithRestrictionsLike(NoFavoritoEntity.class, restriccion);
		
		return listaRetorno;
	}

}
