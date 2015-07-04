package tesis.server.socialNetwork.dao;

import java.util.List;

import org.hibernate.Query;

import tesis.server.socialNetwork.entity.FavoritoEntity;

public class FavoritoDao extends GenericDao<FavoritoEntity, Integer> {

	@Override
	protected Class<FavoritoEntity> getEntityBeanType() {
		return FavoritoEntity.class;
	}
	
	
	public void guardar(FavoritoEntity favoritoEntity){
		this.save(favoritoEntity);
	}
	
	
	public void eliminar(FavoritoEntity favoritoEntity){
		this.delete(favoritoEntity);
	}
	
	
	/**
	 * Metodo que retorna una marcacion de favorito de acuerdo al post y 
	 * al autor de la marcacion.
	 * 
	 * @param idPostMarcado
	 * @param usernameAutorMarcacion
	 * @return
	 */
	public FavoritoEntity buscarMarcacion(Integer idPostMarcado, String usernameAutorMarcacion){
		String consulta = "from FavoritoEntity f "
				+ "where f.post.idPost = :idPost "
				+ "and f.autor.userName = :autorUsername";
		Query query = this.getSession().createQuery(consulta);
		query.setInteger("idPost", idPostMarcado);
		query.setString("autorUsername", usernameAutorMarcacion);
		FavoritoEntity fav = (FavoritoEntity) query.uniqueResult();
		return fav;  
	}

}
