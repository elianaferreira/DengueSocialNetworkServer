package tesis.server.socialNetwork.dao;


import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import tesis.server.socialNetwork.entity.AdminEntity;

@Controller
@LocalBean
public class AdministradorDao extends GenericDao<AdminEntity, Integer> {

	@Override
	protected Class<AdminEntity> getEntityBeanType() {
		return AdminEntity.class;
	}
	

	
	public AdminEntity verificarAdministrador(String adminName, String password){
		String consulta = "from AdminEntity ad where ad.adminName = :adminName and ad.password = :password";
		
		
		//creamos el JSON de restricciones que sera en base al username
		JSONObject restriccion = new JSONObject();
		restriccion.put("adminName", adminName);
		List<AdminEntity> lista = this.getListOfEntitiesWithRestrictionsLike(AdminEntity.class, restriccion);
		//la lista en teoria seria de un solo elemento
		if(lista == null || lista.size() == 0){
			return null;
		} else{
			AdminEntity admin = lista.get(0);
			//verificamos el password
			if(admin.getPassword().equals(password)){
				return admin;
			} else {
				return null;
			}
		}
	}
	
	
	
	/**
	 * Metodo que se encarga de iniciar la sesion del administrador
	 * 
	 * @param admin
	 * @return
	 */
	public Boolean iniciarSesionAdmin(AdminEntity admin){
		//cambiamos el estado del atributo logged a TRUE
		admin.setLogged(true);
		//hacemos el update
		try{
			this.update(admin);
			return true;
		} catch (Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Metodo que cierra la sesion del administrador
	 * @param admin
	 * @return
	 */
	public Boolean cerrarSesionAdmin(AdminEntity admin){
		//cambiamos el estado del atributo logged a FALSE
		admin.setLogged(false);
		//hacemos el update
		try{
			this.update(admin);
			return true;
		} catch (Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	
	
	
	/**
	 * Metodo que retorna un JSON de la entidad del Administrador
	 * @param admin
	 * @return
	 */
	public JSONObject getJsonFromAdmin(AdminEntity admin){
		JSONObject retorno = new JSONObject();
		retorno.put("adminname", admin.getAdminName());
		retorno.put("nombre", admin.getNombre());
		retorno.put("apellido", admin.getApellido());
		retorno.put("password", admin.getPassword());
		
		return retorno;	
	}
}
