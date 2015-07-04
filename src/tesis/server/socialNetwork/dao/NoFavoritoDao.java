package tesis.server.socialNetwork.dao;

import org.hibernate.Query;

import tesis.server.socialNetwork.entity.NoFavoritoEntity;

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

}
