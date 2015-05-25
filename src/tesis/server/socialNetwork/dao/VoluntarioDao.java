package tesis.server.socialNetwork.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import tesis.server.socialNetwork.entity.ContactoEntity;
import tesis.server.socialNetwork.entity.VoluntarioEntity;


//@Repository
@Controller
public class VoluntarioDao extends GenericDao<VoluntarioEntity, String> {

	@Override
	protected Class<VoluntarioEntity> getEntityBeanType() {
		return VoluntarioEntity.class;
	}
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void guardar(VoluntarioEntity voluntarioEntity){
		//ponemos el username (ID) todo a minuscula porque el find es case sensitive
		voluntarioEntity.setUserName(voluntarioEntity.getUserName().toLowerCase());
		//agregamos la fecha de inscripcion del objeto
		voluntarioEntity.setFechaIns(new Date());
		this.save(voluntarioEntity);
	}
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void modificar(VoluntarioEntity voluntarioEntity){
		//el username lo ponemos a minuscula
		voluntarioEntity.setUserName(voluntarioEntity.getUserName().toLowerCase());
		this.update(voluntarioEntity);
	}
	
	
	/**
	 * Metodo que verifica la autenticidad de un usuario,
	 * retorna el usuario en caso correcto, sino null.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public VoluntarioEntity verificarUsuario(String username, String password){
		VoluntarioEntity voluntario = this.findByClassAndID(VoluntarioEntity.class, username.toLowerCase());
		if(voluntario == null){
			//si el usuario no existe
			return null;
		} else{
			//si el usuario existe pero el password no es correcto
			if(!voluntario.getPassword().equals(password)){
				return null;
			} else{
				return voluntario;
			}
		}
	}
	
	
	//TODO metodo a ser utilizado directamente por el cliente android
	/**
	 * Metodo que verifica si un nombre de usuario ya esta en la Base de Datos
	 * @param newUsername
	 * @return
	 */
	public Boolean verificarUsernameRepetido(String newUsername){
		//traemos de la Base de Datos
		VoluntarioEntity entity = this.findByClassAndID(VoluntarioEntity.class, newUsername);
		if(entity == null){
			//quiere decir que no existe todavia en la BD, por ende NO es repetido
			return false;
		} else{
			return true;
		}
	}
	
	
	/**
	 * Metodo que pasa una entidad Voluntario a un objeto JSON
	 * 
	 * @param voluntarioEntity
	 * @return
	 */
	public JSONObject getJSONFromVoluntario(VoluntarioEntity voluntarioEntity){
		JSONObject retorno = new JSONObject();
		retorno.put("username", voluntarioEntity.getUserName());
		retorno.put("usernamestring", voluntarioEntity.getUsernameString());
		retorno.put("nombre", voluntarioEntity.getNombreReal());
		retorno.put("telefono", voluntarioEntity.getTelefono());
		retorno.put("email", voluntarioEntity.getEmail());
		retorno.put("ci", voluntarioEntity.getCi());
		
		return retorno;
	}
	
	
	/**
	 * Metodo que verifica si dos voluntarios ya son amigos entre si.
	 * 
	 * @param voluntario1
	 * @param voluntario2
	 * @return
	 */
	public boolean yaEsContacto(VoluntarioEntity voluntario1, VoluntarioEntity voluntario2){
		//verificamos que vountario2 se encuentre en la lista de contactos de voluntario1
		List<ContactoEntity> listaContactos = voluntario1.getContactos();
		if(listaContactos.isEmpty()){
			return false;
		} else {
			//por cada uno de los contactos vemos si el solicitante o el solicitado coincide con coluntario2
			for(ContactoEntity contacto : listaContactos){
				if((contacto.getVoluntario().equals(voluntario2)) || contacto.getContacto().equals(voluntario2)){
					//ya son amigos
					return true;
				}
			}
		}
		return false;
	}
	
	
	//Busqueda
	public List<VoluntarioEntity> buscarUsuarios(String criterio){
		List<VoluntarioEntity> listaResultado = new ArrayList<VoluntarioEntity>();
		JSONObject jsonRestricciones = new JSONObject();
		Boolean existeCriterio = true;
		JSONArray retorno = new JSONArray();
		
		//verificamos si cual de los parametros es vacio o si ambos estan cargados
		if(criterio.isEmpty() && criterio.isEmpty()){
			//no hacemos nada si el criterio es vacio (NO se va a retornar la lista completa de usuarios!)
			existeCriterio= false;
		} else {
			//quiere decir el criterio esta cargado
			Criteria criteria = getSession().createCriteria(VoluntarioEntity.class);
			criteria.add(Restrictions.or(Restrictions.like("userName", "%"+criterio+"%").ignoreCase(), Restrictions.like("nombreReal", "%"+criterio+"%").ignoreCase()));
			listaResultado = criteria.list();
		}
		if(listaResultado == null || listaResultado.isEmpty()){
			return null;
		} else {
			//cada elemento de la lista lo transformamos a un JSON
			/*for (int i = 0; i < listaResultado.size(); i++) {
				retorno.put(this.getJSONFromVoluntario(listaResultado.get(i)));
			}*/
			return listaResultado;
		}
	}
}


