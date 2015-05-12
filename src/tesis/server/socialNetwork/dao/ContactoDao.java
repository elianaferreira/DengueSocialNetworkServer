package tesis.server.socialNetwork.dao;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.springframework.stereotype.Controller;

import tesis.server.socialNetwork.entity.ContactoEntity;

@Controller
public class ContactoDao extends GenericDao<ContactoEntity, Integer> {

	@Override
	protected Class<ContactoEntity> getEntityBeanType() {
		return ContactoEntity.class;
	}
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void guardar(ContactoEntity contactoEntity){
		this.save(contactoEntity);
	}

}
