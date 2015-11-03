package tesis.server.socialNetwork.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
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
import tesis.server.socialNetwork.utils.Base64;
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
	
	
	
	/**
	 * Metodo que retorna los datos de un post especifico en base a si ID
	 * 
	 * @param adminName
	 * @param password
	 * @param idPost
	 * @return
	 */
	@GET
	@Path("/post")
	@ResponseBody
	public String getPost(@QueryParam("admin") String adminName,
							@QueryParam("password") String password,
							@QueryParam("idPost") Integer idPost){
		
		AdminEntity admin = administradorDao.verificarAdministrador(adminName, password);
		if(admin == null){
			return Utiles.retornarSalida(true, "El nombre o la contrasenha son invalidos");
		} else {
			PostEntity postSolicitado = postDao.findByClassAndID(PostEntity.class, idPost);
			if(postSolicitado == null){
				return Utiles.retornarSalida(true, "El reporte no existe");
			} else {
				JSONObject postJSON = postDao.getJSONFromPost("", postSolicitado);
				return Utiles.retornarSalida(false, postJSON.toString());
			}
		}
	}
	
	
	
	@GET
	@Path("/photos")
	@ResponseBody
	public String getPhotos(@QueryParam("admin") String adminName,
							@QueryParam("password") String password,
							@QueryParam("idPost") Integer idPost){
		
		AdminEntity admin = administradorDao.verificarAdministrador(adminName, password);
		if(admin == null){
			return Utiles.retornarSalida(true, "El nombre o la contrasenha son invalidos");
		} else {
			String retornoAntes = null;
			String retornoDespues = null;
		
			//foto de antes
			BufferedImage img = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] imageInByte = null;
			
			try {
				img = ImageIO.read(new File(Utiles.PHOTOS_FOLDER + String.valueOf(idPost) + "antes_image.png"));
				ImageIO.write(img, "png", baos);
				baos.flush();
				imageInByte = baos.toByteArray();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
				img = null;
			}
			
			if(img != null){
				retornoAntes = Base64.encodeToString(imageInByte, Base64.DEFAULT);
			}
			
			BufferedImage img2 = null;
			ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
			byte[] imageInByte2 = null;
			
			try {
				img2 = ImageIO.read(new File(Utiles.PHOTOS_FOLDER + String.valueOf(idPost) + "despues_image.png"));
				ImageIO.write(img2, "png", baos2);
				baos2.flush();
				imageInByte2 = baos2.toByteArray();
				baos2.close();
			} catch (IOException e) {
				e.printStackTrace();
				img2 = null;
			}
			
			if(img2 != null){
				retornoDespues = Base64.encodeToString(imageInByte2, Base64.DEFAULT);
			}
			
			JSONObject retorno = new JSONObject();
			retorno.put("antes", retornoAntes);
			retorno.put("despues", retornoDespues);
			
			return Utiles.retornarSalida(false, retorno.toString());
		}
	}
	
	
	@POST
	@Path("/invalidateUser")
	@Consumes("application/x-www-form-urlencoded")
	@ResponseBody
	public String desactivarCuentaVoluntario(@FormParam("adminName") String adminName,
												@FormParam("password") String password,
												@FormParam("username") String username){
		
		AdminEntity admin = administradorDao.verificarAdministrador(adminName, password);
		if(admin == null){
			return Utiles.retornarSalida(true, "El nombre o la contrasenha son invalidos");
		} else {
			//buscamos el voluntario
			VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
			if(voluntario == null){
				return Utiles.retornarSalida(true, "No existe un voluntario con use nombre de usuario");
			} else {
				try{
					voluntario.setActivo(false);
					voluntarioDao.modificar(voluntario);
					return Utiles.retornarSalida(false, "El voluntario ha sido dado de baja");
				}catch(Exception e){
					e.printStackTrace();
					return Utiles.retornarSalida(true, "Ha ocurrido un error");
				}
			}
		}
	}
	
	
	@POST
	@Path("/activateUser")
	@Consumes("application/x-www-form-urlencoded")
	@ResponseBody
	public String activarCuentaUsuario(@FormParam("adminName") String adminName,
										@FormParam("password") String password,
										@FormParam("username") String username){
		
		AdminEntity admin = administradorDao.verificarAdministrador(adminName, password);
		if(admin == null){
			return Utiles.retornarSalida(true, "El nombre o la contrasenha son invalidos.");
		} else {
			//buscamos el voluntario
			VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
			if(voluntario == null){
				return Utiles.retornarSalida(true, "No existe un voluntario con use nombre de usuario.");
			} else {
				try{
					voluntario.setActivo(true);
					voluntarioDao.modificar(voluntario);
					return Utiles.retornarSalida(false, "La cuenta del voluntario ha sido activada.");
				}catch(Exception e){
					e.printStackTrace();
					return Utiles.retornarSalida(true, "Ha ocurrido un error.");
				}
			}
		}
	}
}
