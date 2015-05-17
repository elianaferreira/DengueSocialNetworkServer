package tesis.server.socialNetwork.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

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

}
