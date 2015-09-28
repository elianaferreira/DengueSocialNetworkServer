package tesis.server.socialNetwork.rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import tesis.server.socialNetwork.dao.AdministradorDao;
import tesis.server.socialNetwork.dao.VoluntarioDao;
import tesis.server.socialNetwork.entity.AdminEntity;
import tesis.server.socialNetwork.entity.VoluntarioEntity;
import tesis.server.socialNetwork.utils.Utiles;


@Stateless
@Path("admin")
public class AdministradorWS {
	
	@Inject
	private AdministradorDao administradorDao;
	
	@Inject
	private VoluntarioDao voluntarioDao;

	
	@POST
	@Path("/auth")
	@Consumes("application/x-www-form-urlencoded")
	@ResponseBody
	public String adminAuth(@FormParam("name") String adminName,
							@FormParam("password") String password){
		
		//verificamos si el administrador existe
		AdminEntity administrador = administradorDao.verificarAdministrador(adminName, password);
		if(administrador == null){
			return Utiles.retornarSalida(true, "El nombre o la contrasenha son invalidos");
		} else {
			//iniciamos sesion para el administrador
			if(administradorDao.iniciarSesionAdmin(administrador)){
				JSONObject admin = administradorDao.getJsonFromAdmin(administrador);
				return Utiles.retornarSalida(false, admin.toString());
			} else {
				return Utiles.retornarSalida(true, "Error al iniciar sesion");
			}
		}
	}
	
	
	
	@POST
	@Path("/volunteers")
	@Consumes("application/x-www-form-urlencoded")
	@ResponseBody
	public String addVoluntiersACategory(@FormParam("admin") String adminName,
										 @FormParam("password") String password,
										 @FormParam("voluntarios") String voluntarios){
		
		AdminEntity admin = administradorDao.verificarAdministrador(adminName, password);
		if(admin == null){
			return Utiles.retornarSalida(true, "El nombre o la contrasenha son invalidos");
		} else {
			//array de JSON
			JSONArray retornoNoAgregados = new JSONArray();
			
			//voluntarios es un array de JSON con los voluntarios a ser agregado con categoria A
			JSONArray arrayTemp = new JSONArray(voluntarios);
			for(int i=0; i<arrayTemp.length(); i++){
				JSONObject jsonVoluntario = arrayTemp.getJSONObject(i);
				if(voluntarioDao.findByClassAndID(VoluntarioEntity.class, jsonVoluntario.getString("username").toLowerCase()) != null){
					retornoNoAgregados.put(jsonVoluntario);
				} else{
					try{
						VoluntarioEntity voluntario = new VoluntarioEntity();
						voluntario.setUserName(jsonVoluntario.getString("username").toLowerCase());
						voluntario.setPassword(jsonVoluntario.getString("password"));
						voluntario.setUsernameString(jsonVoluntario.getString("username"));
						voluntario.setNombreReal(jsonVoluntario.getString("nombre"));
						voluntario.setCi(jsonVoluntario.getInt("ci"));
						voluntario.setDireccion(jsonVoluntario.getString("direccion"));
						voluntario.setTelefono(jsonVoluntario.getString("telefono"));
						voluntario.setEmail(jsonVoluntario.getString("email"));
						voluntario.setCategoria("A");
						voluntarioDao.guardar(voluntario);
					} catch(Exception ex){
						retornoNoAgregados.put(jsonVoluntario);
					}
				}
			}
			return Utiles.retornarSalida(false, retornoNoAgregados.toString());
		}
	}
}
