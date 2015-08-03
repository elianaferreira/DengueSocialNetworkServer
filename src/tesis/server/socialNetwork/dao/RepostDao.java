package tesis.server.socialNetwork.dao;

import java.sql.Timestamp;
import java.util.Date;

import tesis.server.socialNetwork.entity.RepostEntity;

public class RepostDao extends GenericDao<RepostEntity, Integer> {

	@Override
	protected Class<RepostEntity> getEntityBeanType() {
		return RepostEntity.class;
	}
	

	public void guardar(RepostEntity repost){
		Date date = new Date();
		repost.setFechaRepost(new Timestamp(date.getTime()));
		this.save(repost);
	}
	
	public void eliminar(RepostEntity repost){
		this.delete(repost);
	}

}
