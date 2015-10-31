package tesis.server.socialNetwork.rest;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import tesis.server.socialNetwork.dao.AdministradorDao;
import tesis.server.socialNetwork.dao.PostDao;
import tesis.server.socialNetwork.dao.RepostDao;
import tesis.server.socialNetwork.dao.VoluntarioDao;
import tesis.server.socialNetwork.entity.AdminEntity;
import tesis.server.socialNetwork.entity.PostEntity;
import tesis.server.socialNetwork.entity.RepostEntity;
import tesis.server.socialNetwork.entity.VoluntarioEntity;
import tesis.server.socialNetwork.utils.SortedByDate;
import tesis.server.socialNetwork.utils.Utiles;


@Stateless
@Path("admin")
public class AdministradorWS {
	
	@Inject
	private AdministradorDao administradorDao;
	
	@Inject
	private VoluntarioDao voluntarioDao;
	
	@Inject
	private PostDao postDao;
	
	@Inject
	private RepostDao repostDao;

	
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
	
	
	/**
	 * Metodo que se encarga de buscar los voluntarios de acuerdo a un criterio
	 * @param adminName
	 * @param password
	 * @param criterioBusqueda
	 * @return
	 */
	@GET
	@Path("/search")
	@ResponseBody
	public String searchVoluntiers(@QueryParam("admin") String adminName,
			 @QueryParam("password") String password,
			 @QueryParam("criterio") String criterioBusqueda){
		
		AdminEntity admin = administradorDao.verificarAdministrador(adminName, password);
		if(admin == null){
			return Utiles.retornarSalida(true, "El nombre o la contrasenha son invalidos");
		} else {
			//llamamos al dao que se encarga de la busqueda
			List<VoluntarioEntity> listaResultado = voluntarioDao.buscarUsuarios(criterioBusqueda);
			if(listaResultado == null){
				return Utiles.retornarSalida(true, "No hay usuario con ese nombre");
			} else {
				JSONArray retorno = new JSONArray();
				//a cada usuario le agregamos la cantidad de amigos que tiene y un boolean de si son amigos
				for(int j=0; j<listaResultado.size(); j++){
					//lo agregamos a la lista solo si no se trata del mismo usuario que solicta la busqueda
					JSONObject jsonFromVoluntario = voluntarioDao.getJSONFromVoluntario(listaResultado.get(j));
					retorno.put(jsonFromVoluntario);
				}
				return Utiles.retornarSalida(false, retorno.toString());
			}
		}
	}
	
	@GET
	@Path("/reports")
	@ResponseBody
	public String getReportsOfVoluntiers(@QueryParam("admin") String adminName,
			 @QueryParam("password") String password,
			 @QueryParam("username") String username){
		
		AdminEntity admin = administradorDao.verificarAdministrador(adminName, password);
		if(admin == null){
			return Utiles.retornarSalida(true, "El nombre o la contrasenha son invalidos");
		} else {
			VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
			if(voluntario == null){
				return Utiles.retornarSalida(true, "El usuario no existe");
			} else {
				List<JSONObject> arrayRetorno = new ArrayList<JSONObject>();
				List<PostEntity> posts = postDao.getHomeTimeline(voluntario);
				for(int i=0; i<posts.size(); i++){
					JSONObject postJSON = postDao.getJSONFromPost(username, posts.get(i));
					arrayRetorno.add(postJSON);
				}
				try{
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
					String formattedDate = sdf.format(date);
					Timestamp timestamp;
				    Date parsedDate = sdf.parse(formattedDate);
				    timestamp = new java.sql.Timestamp(parsedDate.getTime());
					List<RepostEntity> reposts = repostDao.getOwnReposts(username, timestamp, false);
					for(int j=0; j<reposts.size(); j++){
						JSONObject repostJSON = repostDao.getJSONFromRepost(reposts.get(j), username);
						arrayRetorno.add(repostJSON);
					}
				} catch(Exception e){
					e.printStackTrace();
				}
				
				Collections.sort(arrayRetorno, new SortedByDate());
				return Utiles.retornarSalida(false, arrayRetorno.toString());
			}
		}
	}
	
	
	/**
	 * Metodo que retorna la lista completa de posts pero solo los datos relevantes para el mapa
	 * 
	 * @param adminName
	 * @param password
	 * @return
	 */
	@GET
	@Path("/allPosts")
	@ResponseBody
	public String getAllPosts(@QueryParam("admin") String adminName,
								@QueryParam("password") String password){
		
		AdminEntity admin = administradorDao.verificarAdministrador(adminName, password);
		if(admin == null){
			return Utiles.retornarSalida(true, "El nombre o la contrasenha son invalidos");
		} else {
			List<PostEntity> todosLosPosts = postDao.getAll();
			//vamos a enviar solo el nombre real, el id del post, la ubicacion y si ya fue solucionado
			JSONArray retorno = new JSONArray();
			for(PostEntity p: todosLosPosts){
				JSONObject postJSON = new JSONObject();
				postJSON.put("id", p.getIdPost());
				postJSON.put("solucionado", p.getSolucionado());
				postJSON.put("nombre", p.getVoluntario().getNombreReal());
				postJSON.put("latitud", p.getLatitud());
				postJSON.put("longitud", p.getLongitud());
				retorno.put(postJSON);
			}
			return Utiles.retornarSalida(false, retorno.toString());
		}
	}
}
