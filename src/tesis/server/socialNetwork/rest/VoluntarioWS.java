package tesis.server.socialNetwork.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.hibernate.Criteria;
import org.hibernate.annotations.Generated;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.sun.el.parser.ParseException;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.json.impl.writer.JsonEncoder;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.exceptionMappingType;

import tesis.server.socialNetwork.dao.CampanhaDao;
import tesis.server.socialNetwork.dao.ContactoDao;
import tesis.server.socialNetwork.dao.NotificacionDao;
import tesis.server.socialNetwork.dao.PostDao;
import tesis.server.socialNetwork.dao.RepostDao;
import tesis.server.socialNetwork.dao.SolicitudAmistadDao;
import tesis.server.socialNetwork.dao.VoluntarioDao;
import tesis.server.socialNetwork.entity.CampanhaEntity;
import tesis.server.socialNetwork.entity.ContactoEntity;
import tesis.server.socialNetwork.entity.NotificacionEntity;
import tesis.server.socialNetwork.entity.PostEntity;
import tesis.server.socialNetwork.entity.RepostEntity;
import tesis.server.socialNetwork.entity.SolicitudAmistadEntity;
import tesis.server.socialNetwork.entity.VoluntarioEntity;
import tesis.server.socialNetwork.utils.Base64;
import tesis.server.socialNetwork.utils.SortedByDate;
import tesis.server.socialNetwork.utils.Utiles;

/**
 * Clase que se encargar� de atender las peticiones REST para los voluntarios.
 * 
 * @author eliana
 *
 */

//el PATH especifica el URI al cual se haran las peticiones
@Stateless
@Path("users")
public class VoluntarioWS {
	
	//acceso a Base de Datos
	@Inject
	private VoluntarioDao voluntarioDao;
	
	@Inject
	private ContactoDao contactoDao;
	
	@Inject
	private SolicitudAmistadDao solicitudAmistadDao;
	
	@Inject
	private PostDao postDao;

	@Inject
	private RepostDao repostDao;
	
	@Inject
	private CampanhaDao campanhaDao;
	
	@Inject
	private NotificacionDao notificacionDao;
	
	/**
	 * Metodo que  agrega un nuevo usuario a la BD
	 * 
	 * @param username
	 * @param password
	 * @param nombre
	 * @param ci
	 * @param direccion
	 * @param telefono
	 * @param email
	 * @return
	 */
	@POST
	@Path("/user/{username}")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String userCreation(@PathParam("username") String username,
							   @FormParam("password") String password,
							   @FormParam("nombre") String nombre,
							   @FormParam("ci") Integer ci,
							   @FormParam("direccion") String direccion,
							   @FormParam("telefono") String telefono,
							   @FormParam("email") String email,
							   @FormParam("fotoPerfil") String fotoPerfil){
		/*
		 * @FormDataParam("fotoPerfil") InputStream fileInputStream
		 */
		//verificar que ese nombre de usuario no exista ya en la Base de Datos
		//generamos un ejemplo
		VoluntarioEntity voluntario = new VoluntarioEntity();
		voluntario.setUserName(username);
		voluntario.setUsernameString(username);
		//el password ya viene encriptado //la validacion se debe hacer en el cliente
		voluntario.setPassword(password);
		voluntario.setNombreReal(nombre);
		if(ci != null){
			voluntario.setCi(ci);
		}
		if(direccion != null){
			voluntario.setDireccion(direccion);
		}
		if(telefono != null){
			voluntario.setTelefono(telefono);
		}
		if(email != null){
			voluntario.setEmail(email);
		}
		
		// lo pasamos a minuscula y verificamos si no existe ya
		String usernameLower = username.toLowerCase();
		if(voluntarioDao.findByClassAndID(VoluntarioEntity.class, usernameLower) != null){
			return Utiles.retornarSalida(true, "El usuario ya existe.");
		} else{
			try{
				if(fotoPerfil != null){
					byte[] aByteArray = Base64.decode(fotoPerfil, Base64.DEFAULT);
					voluntario.setFotoDePerfil(aByteArray);
					/*BufferedImage img = ImageIO.read(new ByteArrayInputStream(aByteArray));

					ImageIO.write(img, "png", new File(Utiles.PHOTOS_FOLDER + usernameLower + "_profile.png"));*/
				}
				//los de categoria A son agregados por el administrador
				voluntario.setCategoria("B");
				voluntarioDao.guardar(voluntario);
				return Utiles.retornarSalida(false, "Voluntario registrado con �xito.");
			}catch(Exception ex){
				return Utiles.retornarSalida(true, "Error al guardar los datos del voluntario.");
			}
		}		
	}
	
	
	/**
	 * Metodo que actualiza un usuario a la BD
	 * 
	 * @param username
	 * @param password
	 * @param nombre
	 * @param apellido
	 * @param ci
	 * @param direccion
	 * @param telefono
	 * @param email
	 * @return
	 */
	@POST
	@Path("/user/update/{username}")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String userUpdate(@PathParam("username") String username,
							 @FormParam("newUserName") String newUserName,
							 @FormParam("nombre") String nombre,
							 @FormParam("apellido") String apellido,
							 @FormParam("ci") Integer ci,
							 @FormParam("direccion") String direccion,
							 @FormParam("telefono") String telefono,
							 @FormParam("email") String email,
							 @FormParam("fotoPerfil") String fotoPerfil){
		
		//verificar que ese nombre de usuario exista ya en la Base de Datos
		String usernameLower = username.toLowerCase();
		VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, usernameLower);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario no existe");
		} else {
			//verificamos que el usuario haya iniciado sesion
			if(Utiles.haIniciadoSesion(voluntario)){
				//cargamos los cambios que envio el usuario
				//verificamos que newUsername sea distinto de nulo y solo si es distinto del actual se valida
				if(newUserName != null && !newUserName.toLowerCase().equals(usernameLower)){
					//verificamos que no exista ya alguien con ese nombre de usuario
					if(voluntarioDao.findByClassAndID(VoluntarioEntity.class, newUserName.toLowerCase()) != null){
						return Utiles.retornarSalida(true, "Este nombre de usuario ya est� registrado");
					}
					//cambiamos el ID del usuario, que anteriormente se verifico que no exista ya
					voluntario.setUserName(newUserName.toLowerCase());
					voluntario.setUsernameString(newUserName);
				}
				if(nombre != null){
					voluntario.setNombreReal(nombre);
				}
				if(ci != null){
					voluntario.setCi(ci);
				}
				if(direccion != null){
					voluntario.setDireccion(direccion);
				}
				if(telefono != null){
					voluntario.setTelefono(telefono);
				}
				if(email != null){
					voluntario.setEmail(email);
				}
				if(fotoPerfil != null){
					byte[] aByteArray = Base64.decode(fotoPerfil, Base64.DEFAULT);
					voluntario.setFotoDePerfil(aByteArray);
					/*BufferedImage img;
					try {
						img = ImageIO.read(new ByteArrayInputStream(aByteArray));
						ImageIO.write(img, "png", new File(Utiles.PHOTOS_FOLDER + usernameLower + "_profile.png"));
					} catch (IOException e) {
						e.printStackTrace();
					}*/
				}
				try{
					voluntarioDao.modificar(voluntario);
					return Utiles.retornarSalida(false, "Datos actualizados con �xito");
				}catch(Exception ex){
					return Utiles.retornarSalida(true, "Error al actualizar los datos del voluntario");
				}
			} else{
				return Utiles.retornarSalida(true, "No has iniciado sesi�n");
			}
			
		}
	}
	
	
	/**
	 * Metodo que autentica (inicia sesion) a un usuario
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@POST
	@Path("/user/auth")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String userAuth(@FormParam("username") String username,
						   @FormParam("password") String password){		
		//se pondra a TRUE el campo logged del usuario si es que coincide con el username y pass
		//buscamos el usuario en la base de datos
		VoluntarioEntity voluntario = voluntarioDao.verificarUsuario(username, password);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario o la contrase�a no es v�lida.");
		} else{
			if(voluntario.getActivo() == false){
				return Utiles.retornarSalida(true, "El Administrador ha dado de baja tu cuenta.");
			} else {
				//se inicia sesion para el usuario
				voluntario.setLogged(true);
				voluntarioDao.modificar(voluntario);
				JSONObject retorno = voluntarioDao.getJSONFromVoluntario(voluntario);
				retorno.put("password", voluntario.getPassword());
				if(voluntario.getMsjAlerta() != null){
					retorno.put("alerta", voluntario.getMsjAlerta());
					try{
						voluntario.setMsjAlerta(null);
						voluntarioDao.modificar(voluntario);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
				return Utiles.retornarSalida(false, retorno.toString());
			}
		}
	}
	
	
	/**
	 * Metodo que cierra sesion para un usuario dado
	 * 
	 * @param username
	 * @return
	 */
	@POST
	@Path("/user/loggout")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String userLoggout(@FormParam("username") String username,
							  @FormParam("password") String password){
		//traemos el usuario de la BD y cambiamos el campo logged
		VoluntarioEntity voluntario = voluntarioDao.verificarUsuario(username, password);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario o el password no es v�lido.");
		}else{
			try{
				voluntario.setLogged(false);
				voluntarioDao.modificar(voluntario);
				return Utiles.retornarSalida(false, "Sesi�n cerrada.");
			} catch (Exception ex){
				ex.printStackTrace();
				return Utiles.retornarSalida(true, "Error al cerrar la sesi�n.");
			}
		}
	}
		
	
	/**
	 * Servicio que permite a un usuario enviar una solicitud de amistad a otro usuario
	 * 
	 * @param usuarioQueEnvia
	 * @param usuarioSolicitado
	 * @return
	 */
	@POST
	@Path("/user/contacts/new")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String enviarSolicitudAmistad(@FormParam("solicitante") String usuarioQueEnvia,
										 @FormParam("solicitado") String usuarioSolicitado){
		
		//verificamos que el usuario que ha solicitado exista
		VoluntarioEntity voluntarioQueSolicita = voluntarioDao.findByClassAndID(VoluntarioEntity.class, usuarioQueEnvia.toLowerCase());
		if(voluntarioQueSolicita == null){
			return Utiles.retornarSalida(true, "El usuario no existe.");
		} else {
			//verificamos que el usuario que solicita haya iniciado sesion
			if(!Utiles.haIniciadoSesion(voluntarioQueSolicita)){
				return Utiles.retornarSalida(true, "No has iniciado sesi�n.");
			} else {
				//verificamos que el contacto exista
				VoluntarioEntity contactoSolicitado = voluntarioDao.findByClassAndID(VoluntarioEntity.class, usuarioSolicitado.toLowerCase());
				if(contactoSolicitado == null){
					return Utiles.retornarSalida(true, "El contacto no existe.");
				} else {
					//verificamos que no sean ya amigos
					if(voluntarioDao.yaEsContacto(voluntarioQueSolicita, contactoSolicitado)){
						return Utiles.retornarSalida(true, "Ya son amigos.");
					} else {
						try{
							SolicitudAmistadEntity nuevaSolicitud = new SolicitudAmistadEntity();
							nuevaSolicitud.setUsuarioSolicitante(voluntarioQueSolicita);
							nuevaSolicitud.setUsuarioSolicitado(contactoSolicitado);
							solicitudAmistadDao.guardar(nuevaSolicitud);
							notificacionDao.crearNotificacionSolicitudAmistad(voluntarioQueSolicita, contactoSolicitado);
							return Utiles.retornarSalida(false, "Solicitud de amistad enviada.");
						}catch(Exception e){
							e.printStackTrace();
							return Utiles.retornarSalida(true, "Ha ocurrido un error al enviar la solicitud de amistad.");
						}
					}
				}
			}
		}
	}
	
	
	
	/**
	 * Metodo que retorna la lista de solicitudes de amistad hechas al usuario identificado con {username}
	 * @param username
	 * @return
	 */
	@GET
	@Path("/user/pendingFriendships/{username}")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String getSolicitudesPendientes(@PathParam("username") String username){
		
		//verificamos que el usuario exista en la Base de Datos
		VoluntarioEntity voluntarioEntity = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username.toLowerCase());
		if(voluntarioEntity == null){
			return Utiles.retornarSalida(true, "El usuario no existe");
		} else {
			//verificamos que haya iniciado sesion
			if(!Utiles.haIniciadoSesion(voluntarioEntity)){
				return Utiles.retornarSalida(true, "No has iniciado sesi�n");
			} else{
				List<SolicitudAmistadEntity> listaPendientes = solicitudAmistadDao.getListaSolicitudesPendientes(username);				
				if(listaPendientes.isEmpty()){
					List<SolicitudAmistadEntity> listaVacia = new ArrayList<SolicitudAmistadEntity>();
					return Utiles.retornarSalida(false, solicitudAmistadDao.getListParsedFromSolicitudes(listaVacia));
				} else {
					return Utiles.retornarSalida(false, solicitudAmistadDao.getListParsedFromSolicitudes(listaPendientes));
				}
			}
		}
	}
	
	
	
	
	/**
	 * Servicio que permite aceptar o rechazar una solicitud de amistad
	 * 
	 * @param idSolicitud
	 * @param aceptar
	 * @param rechazar
	 * @return
	 */
	@POST
	@Path("/user/contacts/newFriendships/{idsolicitud}")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String aceptarRechazarSolicitud(@PathParam("idsolicitud") Integer idSolicitud,
										   @FormParam("aceptar") Boolean aceptar,
										   @FormParam("rechazar") Boolean rechazar){
		
		//buscamos la solicitud en la Base de Datos
		SolicitudAmistadEntity solicitud = solicitudAmistadDao.findByClassAndID(SolicitudAmistadEntity.class, idSolicitud);
		if(solicitud == null){
			return Utiles.retornarSalida(true, "La solicitud no existe");
		} else {
			//verificamos que el usuario solicitado haya iniciado sesion
			if(!Utiles.haIniciadoSesion(solicitud.getUsuarioSolicitado())){
				return Utiles.retornarSalida(true, "No has iniciado sesi�n");
			} else {
				//vemos si la solicitud es aceptada o rechazada
				if(aceptar == true){
					//se agrega al usuario a la lista de contactos
					try{
						ContactoEntity nuevoContacto = new ContactoEntity();
						nuevoContacto.setVoluntario(solicitud.getUsuarioSolicitante());
						nuevoContacto.setContacto(solicitud.getUsuarioSolicitado());
						contactoDao.guardar(nuevoContacto);
						//ponemos la solicitud de amistad a aceptada
						solicitud.setAceptada(true);
						//la actualizamos
						solicitudAmistadDao.modificar(solicitud);
						//TODO enviar notificacion al solicitante de que se ha aceptado la solicitud de amistad
						return Utiles.retornarSalida(false, "Solicitud de amistad aceptada");
					} catch(Exception ex){
						ex.printStackTrace();
						return Utiles.retornarSalida(true, "Error al aceptar la solicitud de amistad");
					}
				} else if(rechazar == true){
					//se elimina la solicitud de amistad cuando esta es rechazada
					try{
						solicitudAmistadDao.delete(solicitud);
						//solicitud.setAceptada(false);
						//solicitudAmistadDao.modificar(solicitud);
						return Utiles.retornarSalida(false, "Se ha eliminado la solicitud de amistad");
					}catch(Exception ex){
						ex.printStackTrace();
						return Utiles.retornarSalida(true, "Error al eliminar la solicitud de amistad");
					}
				}
			}
		}
		return "";
	}
	
	
	@GET
	@Path("/user/search/{searchParam}")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String buscarUsuario(@PathParam("searchParam") String criterioBusqueda,
			@QueryParam("username") String username){
		
		//verificamos que no este vacio
		if(criterioBusqueda == null || criterioBusqueda.isEmpty()){
			//no hacemos nada
		} else {
			VoluntarioEntity voluntarioQueSolicita = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username.toLowerCase());
			if(voluntarioQueSolicita == null){
				return Utiles.retornarSalida(true, "El usuario no existe");
			} else {
				//verificamos que el usuario que solicita haya iniciado sesion
				if(!Utiles.haIniciadoSesion(voluntarioQueSolicita)){
					return Utiles.retornarSalida(true, "No has iniciado sesi�n");
				} else {
					//llamamos al dao que se encarga de la busqueda
					List<VoluntarioEntity> listaResultado = voluntarioDao.buscarUsuarios(criterioBusqueda);
					if(listaResultado == null){
						return Utiles.retornarSalida(true, "No hay usuario con ese nombre");
					} else {
						JSONArray retorno = new JSONArray();
						//a cada usuario le agregamos la cantidad de amigos que tiene y un boolean de si son amigos
						for(int j=0; j<listaResultado.size(); j++){
							VoluntarioEntity voluntario1 = listaResultado.get(j);
							//lo agregamos a la lista solo si no se trata del mismo usuario que solicta la busqueda
							if(voluntarioQueSolicita.getUserName().toLowerCase() != voluntario1.getUserName().toLowerCase()){
								//verificamos si ambos voluntarios ya son amigos, luego lo pasamos a JSON y agregamos el nuevo campo
								JSONObject jsonFromVoluntario = voluntarioDao.getJSONFromVoluntario(listaResultado.get(j));
								/*if(voluntarioDao.yaEsContacto(voluntarioQueSolicita, listaResultado.get(j))){
									jsonFromVoluntario.put("yaEsAmigo", true);
								} else {
									jsonFromVoluntario.put("yaEsAmigo", false);
								}*/
								retorno.put(jsonFromVoluntario);
							}
						}
						return Utiles.retornarSalida(false, retorno.toString());
					}
				}
			}
		}
		return "";
	}
	
	
	
	@GET
	@Path("/contacts/{username}")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String getContacts(@PathParam("username") String username){
		//verificaciones del usuario
		VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "No existe el usuario");
		} else {
				//obtenemos la lista de contactos
				List<VoluntarioEntity> listaContactos = voluntarioDao.getListaContactos(voluntario);
				if(listaContactos == null){
					List<VoluntarioEntity> listaVacia = new ArrayList<VoluntarioEntity>();
					return Utiles.retornarSalida(false, listaVacia.toString());
				} else {
					List<JSONObject> listaRetorno = new ArrayList<JSONObject>();
					for(VoluntarioEntity contacto: listaContactos){
						JSONObject contactoJSON = voluntarioDao.getJSONFromVoluntario(contacto);
						listaRetorno.add(contactoJSON);
					}
					return Utiles.retornarSalida(false, listaRetorno.toString());
				}
				
			}
		
	}
	
	
	//esto es exclusivo para el administrador
	@GET
	@Path("/user/profilePhoto/{username}")
	@ResponseBody
	public String photoProfile(@PathParam("username") String usernameFoto){
		
		//no verificamos el usuario solicitante
		//verificamos si existe un usuario con ese username
		VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, usernameFoto);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario no existe");
		} else {
			//verificamos si tiene foto de perfil
			if(voluntario.getFotoDePerfil() == null){
				//enviamos un array vacio
				return Utiles.retornarSalida(true, "No tiene foto de perfil");
			} else {
				return Utiles.retornarImagen(false,Base64.encodeToString(voluntario.getFotoDePerfil(), Base64.DEFAULT));
			}
		}
	}
	
	
	@GET
	@Path("/user/homeTimeline/{username}")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String homeTimeline(@PathParam("username") String username,
							@QueryParam("ultimaactualizacion") String ultimaActualizacionString){
		//no verificamos el usuario solicitante
		//verificamos si existe un usuario con ese username
		VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario no existe");
		} else {
			JSONArray arrayRetorno = new JSONArray();
			List<PostEntity> postsPropios = postDao.getHomeTimeline(voluntario);
			for(int i=0; i<postsPropios.size(); i++){
				JSONObject postJSON = postDao.getJSONFromPost(username, postsPropios.get(i));
				arrayRetorno.put(postJSON);
			}
			try{
				Timestamp timestamp;
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			    Date parsedDate = dateFormat.parse(ultimaActualizacionString);
			    timestamp = new java.sql.Timestamp(parsedDate.getTime());
				List<RepostEntity> reposts = repostDao.getOwnReposts(username, timestamp, true);
				for(int j=0; j<reposts.size(); j++){
					JSONObject repostJSON = repostDao.getJSONFromRepost(reposts.get(j), username);
					arrayRetorno.put(repostJSON);
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			return Utiles.retornarSalida(false, arrayRetorno.toString());
		}
	}
	
	
	@GET
	@Path("/user/profile/{username}")
	@ResponseBody
	@Produces("text/html; charset=UTF-8")
	public String getProfileData(@PathParam("username") String username, 
			@QueryParam("usernameSolicitante") String usernameSolicitante){
		
		VoluntarioEntity solicitante = voluntarioDao.findByClassAndID(VoluntarioEntity.class, usernameSolicitante);
		if(solicitante == null){
			return Utiles.retornarSalida(true, "El usuario no existe.");
		} else {
			//verificamos si ha iniciado sesion
			if(solicitante.getLogged() == false){
				//no ha iniciado sesion
				return Utiles.retornarSalida(true, "No has iniciado sesi�n.");
			} else {
				VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
				if(voluntario == null){
					return Utiles.retornarSalida(true, "El usuario no existe");
				} else {
					JSONObject retorno = voluntarioDao.getJSONFromVoluntario(voluntario);
					//retorno.put("password", voluntario.getPassword());
					if(voluntario.getFotoDePerfil() != null){
						retorno.put("fotoPerfil", Base64.encodeToString(voluntario.getFotoDePerfil(), Base64.DEFAULT));
					}
					//verificamos si son amigos
					if(voluntarioDao.yaEsContacto(solicitante, voluntario)){
						retorno.put("sonAmigos", true);
					} else {
						retorno.put("sonAmigos", false);
					}
					return Utiles.retornarSalida(false, retorno.toString());
				}
			}
		}
	}
	
	
	@GET
	@Path("/user/myProfileToEdit/{username}")
	@ResponseBody
	@Produces("text/html; charset=UTF-8")
	public String getMyProfileDataToEdit(@PathParam("username") String username){
		
		VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario no existe.");
		} else {
			//verificamos si ha iniciado sesion
			if(voluntario.getLogged() == false){
				//no ha iniciado sesion
				return Utiles.retornarSalida(true, "No has iniciado sesi�n.");
			} else {
					JSONObject retorno = voluntarioDao.getJSONFromVoluntario(voluntario);
					if(voluntario.getFotoDePerfil() != null){
						retorno.put("fotoPerfil", Base64.encodeToString(voluntario.getFotoDePerfil(), Base64.DEFAULT));
					}
					return Utiles.retornarSalida(false, retorno.toString());
			}
		}
	}
	
	
	@POST
	@Path("/user/newPassword/{username}")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String changePassword(@PathParam("username") String username,
								@FormParam("password") String password, 
								@FormParam("newPassword") String newPassword){
		
		VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario no existe.");
		} else {
			//verificamos si ha iniciado sesion
			if(voluntario.getLogged() == false){
				//no ha iniciado sesion
				return Utiles.retornarSalida(true, "No has iniciado sesi�n.");
			} else {
				//verificamos que el password sea el mismo que el actual
				if(!password.equals(voluntario.getPassword())){
					return Utiles.retornarSalida(true, "La contrase�a no coincide.");
				} else {
					if(newPassword.equals(password)){
						return Utiles.retornarSalida(true, "La nueva contrase�a es igual a la anterior.");
					} else {
						try{
							voluntario.setPassword(newPassword);
							voluntarioDao.modificar(voluntario);
							return Utiles.retornarSalida(false, "La contrase�a ha sido cambiado con �xito.");
						} catch(Exception e){
							e.printStackTrace();
							return Utiles.retornarSalida(true, "Ha ocurrido un error al actualizar la contrase�a.");
						}
					}
				}
			}
		}
	}
	
	
	@GET
	@Path("/user/campaigns")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String getCampanhasActivas(@QueryParam("username") String username){
		
		VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario no existe");
		} else {
			try{
				//buscamos todas las campanhas
				List<CampanhaEntity> lista = campanhaDao.getAll();
				JSONArray retorno = new JSONArray();
				for(int i=0; i<lista.size(); i++){
					retorno.put(campanhaDao.getJSONFromCampanha(lista.get(i), username));
				}
				return Utiles.retornarSalida(false, retorno.toString());
			} catch(Exception e){
				e.printStackTrace();
				return Utiles.retornarSalida(true, "Ha ocurrido un error al obtener las campa�as lanzadas.");
			}
		}
	}
	
	@GET
	@Path("/user/campaign/adheridos/{id}")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String getAdheridosCampanha(@PathParam("id") Integer idCampanha,
			@QueryParam("username") String username){
		
		CampanhaEntity campanha = campanhaDao.findByClassAndID(CampanhaEntity.class, idCampanha);
		if(campanha == null){
			return Utiles.retornarSalida(true, "La campanha no existe.");
		} else {
			VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
			if(voluntario == null){
				return Utiles.retornarSalida(true, "El usuario no existe");
			} else {
				List<VoluntarioEntity> adheridos = campanha.getVoluntariosAdheridos();
				JSONArray retorno = new JSONArray();
				for(int i=0; i<adheridos.size(); i++){
					JSONObject v = voluntarioDao.getJSONFromVoluntario(adheridos.get(i));
					if(voluntarioDao.yaEsContacto(voluntario, adheridos.get(i))){
						v.put("yaEsAmigo", true);
					} else {
						v.put("yaEsAmigo", false);
					}
					retorno.put(v);
				}
				
				return Utiles.retornarSalida(false, retorno.toString());
			}
		}
	}
	
	
	
	@POST
	@Path("/user/campaign/adherirse")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String adherirme(@FormParam("campaign") Integer idCampanha, @FormParam("username") String username){
		CampanhaEntity campanha = campanhaDao.findByClassAndID(CampanhaEntity.class, idCampanha);
		if(campanha == null){
			return Utiles.retornarSalida(true, "La campanha no existe.");
		} else {
			VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
			if(voluntario == null){
				return Utiles.retornarSalida(true, "El usuario no existe.");
			} else {
				if(!voluntario.getLogged()){
					return Utiles.retornarSalida(true, "No has iniciado sesi�n.");
				} else {
					try{
						campanha.getVoluntariosAdheridos().add(voluntario);
						campanhaDao.modificar(campanha);
						return Utiles.retornarSalida(false, "Voluntario adherido.");
					} catch(Exception e){
						e.printStackTrace();
						return Utiles.retornarSalida(true, "Ha ocurrido un error al adherirse a la campa�a.");
					}
				}
			}
		}
	}
	
	
	
	@POST
	@Path("/user/contacts/delete")
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String eliminarContacto(@FormParam("username") String username, @FormParam("eliminar") String usernameAEliminar){
		VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario no existe.");
		} else {
			VoluntarioEntity vEliminar = voluntarioDao.findByClassAndID(VoluntarioEntity.class, usernameAEliminar);
			if(vEliminar == null){
				return Utiles.retornarSalida(true, "El usuario no existe.");
			} else {
				ContactoEntity cEliminar = contactoDao.getContact(voluntario, vEliminar);
				if(cEliminar == null){
					return Utiles.retornarSalida(true, "No son amigos.");
				} else {
					try{
						contactoDao.delete(cEliminar);
						return Utiles.retornarSalida(false, "Ya no son amigos.");
					} catch(Exception e){
						e.printStackTrace();
						return Utiles.retornarSalida(true, "Ha ocurrido un error al borrar la amistad.");
					}
				}
			}
		}
	}
	
	
	@GET
	@Path("/user/notifications/{username}")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String getNotificaciones(@PathParam("username") String username, @QueryParam("ultimaActualizacion") String ultimaActualizacionString){
		
		VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario no existe.");
		} else {
			if(!voluntario.getLogged()){
				return Utiles.retornarSalida(true, "No has iniciado sesi�n.");
			} else {
				List<NotificacionEntity> lista = new ArrayList<NotificacionEntity>();
				if(ultimaActualizacionString != null && !ultimaActualizacionString.equals("")){
					Timestamp timestamp;
					try{
					    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
					    Date parsedDate = dateFormat.parse(ultimaActualizacionString);
					    timestamp = new java.sql.Timestamp(parsedDate.getTime());
						lista = notificacionDao.getListaNotificacion(username, timestamp);
					}catch(Exception e){
						e.printStackTrace();
						lista = notificacionDao.getListaNotificacion(username, null);
					}
				} else {
					lista = notificacionDao.getListaNotificacion(username, null);
				}
				
				//pasamos cada notificacion a JSON
				JSONArray retorno = new JSONArray();
				for(int k=0; k<lista.size(); k++){
					JSONObject temp = new JSONObject();
					temp.put("id", lista.get(k).getIdNotificacion());
					temp.put("tipo", lista.get(k).getTipoNotificacion());
					temp.put("mensaje", lista.get(k).getMensaje());
					temp.put("fecha", lista.get(k).getFechaCreacionNotificacion());
					if(lista.get(k).getCampanha() != null){
						temp.put("campanha", campanhaDao.getJSONFromCampanha(lista.get(k).getCampanha(), username));
					}
					if(lista.get(k).getVoluntarioCreadorNotificacion() != null){
						//TODO se puede pasar solo el usernameString y el nombre?
						temp.put("creador", voluntarioDao.getJSONFromVoluntario(lista.get(k).getVoluntarioCreadorNotificacion()));
					}
					retorno.put(temp);
				}
				return Utiles.retornarSalida(false, retorno.toString());
			}
		}
	}
	
	
	@GET
	@Path("/user/statusDetails/{username}")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String getStatusDetail(@PathParam("username") String username){
		
		VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario no existe.");
		} else {
			if(!voluntario.getLogged()){
				return Utiles.retornarSalida(true, "No has iniciado sesi�n.");
			} else {
				JSONObject retorno = new JSONObject();
				//reportes
				Integer cantidadPosts = voluntarioDao.cantidadPosts(voluntario);
				JSONObject reportes = new JSONObject();
				reportes.put("cantidad", cantidadPosts);
				reportes.put("puntaje", Utiles.PUNTAJE_POR_REPORTAR);
				
				//solucionados
				Integer cantidadSolucionados = voluntarioDao.cantidadSolucionadosPorVoluntario(voluntario);
				JSONObject solucionados = new JSONObject();
				solucionados.put("cantidad", cantidadSolucionados);
				solucionados.put("puntaje", Utiles.PUNTAJE_POR_SOLUCIONAR);
				
				//favoritos
				Integer cantidadFavoritos = voluntarioDao.cantidadFavoritosParaVoluntario(voluntario);
				JSONObject favoritos = new JSONObject();
				favoritos.put("cantidad", cantidadFavoritos);
				favoritos.put("puntaje", Utiles.PUNTAJE_FAVORITO);
				
				//noFavoritos
				Integer cantidadNoFavoritos = voluntarioDao.cantidadNoFavoritosParaVoluntario(voluntario);
				JSONObject noFavoritos = new JSONObject();
				noFavoritos.put("cantidad", cantidadNoFavoritos);
				noFavoritos.put("puntaje", Utiles.PUNTAJE_NO_FAVORITO);
				
				retorno.put("activo", 1);
				retorno.put("reportes", reportes);
				retorno.put("solucionados", solucionados);
				retorno.put("favoritos", favoritos);
				retorno.put("noFavoritos", noFavoritos);
				return Utiles.retornarSalida(false, retorno.toString());
			}
		}
	}
	
	
	@GET
	@Path("/user/recommendations")
	@Produces("text/html; charset=UTF-8")
	@ResponseBody
	public String getListOfPrincipalsOrFriendsOfFriends(@QueryParam("username") String username){
		
		VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
		if(voluntario != null){
			JSONObject retorno = new JSONObject();
			JSONArray arrayRetorno = new JSONArray();
			//verificamos si tiene amigos
			List<VoluntarioEntity> listaContactos = voluntarioDao.getListaContactos(voluntario);
			if(listaContactos == null || listaContactos.size() == 0){
				//enviar los sobresalientes
				List<VoluntarioEntity> listaPorReputacion = voluntarioDao.getListUsersByRanking();
				int tempCantidad;
				//maximo se enviaran 10 resultados
				if(listaPorReputacion.size() > 10){
					tempCantidad = 10;
				} else {
					tempCantidad = listaPorReputacion.size();
				}
				for(int j=0; j<tempCantidad; j++){
					if(listaPorReputacion.get(j).getUserName() != voluntario.getUserName()){
						JSONObject vTemp = voluntarioDao.getJSONFromVoluntario(listaPorReputacion.get(j));
						arrayRetorno.put(vTemp);
					}
				}
				retorno.put("destacados", true);
				retorno.put("lista", arrayRetorno);
				return Utiles.retornarSalida(false, retorno.toString());
			} else {
				//enviar los amigos de amigos
				//obtenemos la lista de contactos
				List<VoluntarioEntity> amigosDeAmigos = contactoDao.getListOfFriendOfFriend(voluntario);
				for(int k=0; k<amigosDeAmigos.size(); k++){
					if(amigosDeAmigos.get(k).getUserName() != voluntario.getUserName()){
						JSONObject aTemp = voluntarioDao.getJSONFromVoluntario(amigosDeAmigos.get(k));
						arrayRetorno.put(aTemp);
					}
				}
				retorno.put("destacados", false);
				retorno.put("lista", arrayRetorno);
				return Utiles.retornarSalida(false, retorno.toString());				
			}
		}
		//en teoria no deberia llegar aca
		return Utiles.retornarSalida(true, "Error");
	}
}
