package tesis.server.socialNetwork.dao;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.hibernate.Query;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import tesis.server.socialNetwork.entity.ContactoEntity;
import tesis.server.socialNetwork.entity.VoluntarioEntity;

@Controller
@LocalBean
public class ContactoDao extends GenericDao<ContactoEntity, Integer> {

	@Override
	protected Class<ContactoEntity> getEntityBeanType() {
		return ContactoEntity.class;
	}
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void guardar(ContactoEntity contactoEntity){
		this.save(contactoEntity);
	}
	
	
	/**
	 * Metodo que trae los entities de contacto correspondientes a un voluntario
	 * @param voluntario
	 * @return
	 */
	public List<ContactoEntity> getListaContactsEntity(VoluntarioEntity voluntario){
		String consulta = "from ContactoEntity c "
				+ "where c.voluntario = :voluntario1 "
				+ "or c.contacto = :voluntario2";
		Query query = getSession().createQuery(consulta);
		query.setEntity("voluntario1", voluntario);
		query.setEntity("voluntario2", voluntario);
		List lista = query.list();
		return lista;
	}
	
	
	/**
	 * Metodo que retorna la cantidad de contactos de un voluntario.
	 * el getContats del entity de voluntario no funciona cuando el usuario no es el owner de la relacion
	 * @param voluntario
	 * @return
	 */
	public Integer getCantidadContactos(VoluntarioEntity voluntario){
		String consulta = "select count(*) from ContactoEntity c "
				+ "where c.voluntario = :voluntario1 "
				+ "or c.contacto = :voluntario2";
		Query query = getSession().createQuery(consulta);
		query.setEntity("voluntario1", voluntario);
		query.setEntity("voluntario2", voluntario);
		Long cantidadLong = (Long) query.uniqueResult();
		Integer cantidad = cantidadLong.intValue();
		return cantidad;
	}
	
	
	
	/**
	 * Metodo que lista completa de contactos
	 * 
	 * @return
	 */
	public List<ContactoEntity> listarTodosLosContactos(){
		String consulta = "from ContactoEntity c ";
		Query query = getSession().createQuery(consulta);
		List lista = query.list();
		return lista;
	}
	

	
}
