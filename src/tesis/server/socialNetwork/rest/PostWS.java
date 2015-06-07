package tesis.server.socialNetwork.rest;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.ResponseBody;

import tesis.server.socialNetwork.dao.PostDao;
import tesis.server.socialNetwork.dao.VoluntarioDao;
import tesis.server.socialNetwork.entity.PostEntity;
import tesis.server.socialNetwork.entity.VoluntarioEntity;
import tesis.server.socialNetwork.utils.Utiles;


@Stateless
@Path("statuses")
public class PostWS {
	
	//acceso a Base de Datos
	@Inject
	private VoluntarioDao voluntarioDao;
	
	@Inject
	private PostDao postDao;
	
	
	
	/**
	 * Metodo que recibe un nuevo reporte.
	 * 
	 * @param mensaje
	 * @param username
	 * @param latitud
	 * @param longitud
	 * @return
	 */
	@Path("/new")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@ResponseBody
	public String newStatus(@FormParam("mensaje") String mensaje,
							@FormParam("username") String username,
							@FormParam("latitud") String latitud,
							@FormParam("longitud") String longitud){
		
		//traemos el usuario de la Base de Datos
		VoluntarioEntity voluntarioEntity = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username.toLowerCase());
		//verificamos que el usuario exista
		if(voluntarioEntity == null){
			return Utiles.retornarSalida(true, "El usuario no existe");
		} else {
			//verificamos que haya iniciado sesion
			if(!Utiles.haIniciadoSesion(voluntarioEntity)){
				return Utiles.retornarSalida(true, "No has iniciado sesión");
			} else {
				//intentamos guardar en la Base de Datos
				try {
					PostEntity postEntity = new PostEntity();
					postEntity.setPost(mensaje);
					postEntity.setVoluntario(voluntarioEntity);
					if(latitud != null && longitud != null){
						System.out.print("latitud: " + String.valueOf(latitud));
						System.out.print("longitud: " + String.valueOf(longitud));
						postEntity.setLatitud(Double.parseDouble(latitud));
						postEntity.setLongitud(Double.parseDouble(longitud));
					}
					postDao.guardar(postEntity);
					return Utiles.retornarSalida(false, "Enviado");
				} catch(Exception ex){
					ex.printStackTrace();
					return Utiles.retornarSalida(true, "Error al enviar el mensaje");
				}
			}
		}
	}
	
	
	@Path("/resolve")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@ResponseBody
	public String resolver(@FormParam("id") Integer idPost,
						   @FormParam("username") String username){
		
		//verificamos que el post exista en la Base de Datos
		PostEntity postEntity = postDao.findByClassAndID(PostEntity.class, idPost);
		if(postEntity == null){
			return Utiles.retornarSalida(true, "El mensaje no existe");
		} else {
			//verificamos que quien intenta resolverlo es el duenho del post
			if(postEntity.getVoluntario().getUserName().equals(username)){
				try{
					postEntity.setSolucionado(true);
					postDao.modificar(postEntity);
					return Utiles.retornarSalida(false, "El caso ha sido resuelto");
				} catch(Exception ex){
					ex.printStackTrace();
					return Utiles.retornarSalida(true, "Error al resolver el caso");
				}
			} else {
				//el usuario no es el owner
				return Utiles.retornarSalida(true, "No tienes permiso para resolver el caso");
			}
		}
	}
	
	/**
	 * Servicio que retorna una actualizacion del timeline principal del usuario
	 * (posts de sus amigos y de el)
	 * 
	 * @param username
	 * @param timestamp
	 * @param rango: es la cantidad de posts que se quiere recuperar, tendra un valor por defecto en el servidor
	 * @return
	 */
	@GET
	@Path("/homeTimeline/{username}")
	@ResponseBody
	public String actualizarTimeline(@PathParam("username") String username,
									@QueryParam("ultimaactualizacion") Date ultimaActualizacion){
		
		/*/verificaciones del usuario
		VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "No existe el usuario");
		} else {
			//verificamos si ha iniciado sesion
			if(voluntario.getLogged() == false){
				//no ha iniciado sesion
				return Utiles.retornarSalida(true, "No has iniciado sesión");
			} else {
				//existe el usuario y ha iniciado sesion
				
			}
		}*/
		JSONArray retornoArray = new JSONArray();
		List<PostEntity> timeline = postDao.getHomeTimeline(username, ultimaActualizacion);
		for(int i=0; i<timeline.size(); i++){
			JSONObject postJSON = postDao.getJSONFromPost(timeline.get(i));
			retornoArray.put(postJSON);
		}
		return Utiles.retornarSalida(false, retornoArray.toString());
	}

}
