package tesis.server.socialNetwork.rest;

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

import org.hibernate.Criteria;
import org.hibernate.annotations.Generated;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.exceptionMappingType;

import tesis.server.socialNetwork.dao.ContactoDao;
import tesis.server.socialNetwork.dao.SolicitudAmistadDao;
import tesis.server.socialNetwork.dao.VoluntarioDao;
import tesis.server.socialNetwork.entity.ContactoEntity;
import tesis.server.socialNetwork.entity.SolicitudAmistadEntity;
import tesis.server.socialNetwork.entity.VoluntarioEntity;
import tesis.server.socialNetwork.utils.Utiles;

/**
 * Clase que se encargará de atender las peticiones REST para los voluntarios.
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
	@ResponseBody
	public String userCreation(@PathParam("username") String username,
							   @FormParam("password") String password,
							   @FormParam("nombre") String nombre,
							   @FormParam("ci") Integer ci,
							   @FormParam("direccion") String direccion,
							   @FormParam("telefono") String telefono,
							   @FormParam("email") String email){
		//verificar que ese nombre de usuario no exista ya en la Base de Datos
		//generamos un ejemplo
		VoluntarioEntity voluntario = new VoluntarioEntity();
		voluntario.setUserName(username);
		voluntario.setUsernameString(username);
		//el password ya viene encriptado
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
			return Utiles.retornarSalida(true, "El usuario ya existe");
		} else{
			try{
				voluntarioDao.guardar(voluntario);
				return Utiles.retornarSalida(false, "Voluntario registrado con éxito");
			}catch(Exception ex){
				return Utiles.retornarSalida(true, "Error al guardar los datos del voluntario");
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
	@ResponseBody
	public String userUpdate(@PathParam("username") String username,
							 @FormParam("newUserName") String newUserName,
							 @FormParam("password") String password,
							 @FormParam("nombre") String nombre,
							 @FormParam("apellido") String apellido,
							 @FormParam("ci") Integer ci,
							 @FormParam("direccion") String direccion,
							 @FormParam("telefono") String telefono,
							 @FormParam("email") String email){
		
		//verificar que ese nombre de usuario exista ya en la Base de Datos
		String usernameLower = username.toLowerCase();
		VoluntarioEntity voluntario = voluntarioDao.findByClassAndID(VoluntarioEntity.class, usernameLower);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario no existe");
		} else {
			//verificamos que el usuario haya iniciado sesion
			if(Utiles.haIniciadoSesion(voluntario)){
				//cargamos los cambios que envio el usuario
				if(newUserName != null){
					//verificamos que no exista ya alguien con ese nombre de usuario
					if(voluntarioDao.findByClassAndID(VoluntarioEntity.class, newUserName.toLowerCase()) != null){
						return Utiles.retornarSalida(true, "Este nombre de usuario ya está registrado");
					}
					//cambiamos el ID del usuario, que anteriormente se verifico que no exista ya
					voluntario.setUserName(newUserName.toLowerCase());
					voluntario.setUsernameString(newUserName);
				}
				//el password ya viene encriptado, si es que viene.
				if(password != null){
					voluntario.setPassword(password);
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
				try{
					voluntarioDao.modificar(voluntario);
					return Utiles.retornarSalida(false, "Datos actualizados con éxito");
				}catch(Exception ex){
					return Utiles.retornarSalida(true, "Error al actualizar los datos del voluntario");
				}
			} else{
				return Utiles.retornarSalida(true, "No has iniciado sesión");
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
	@ResponseBody
	public String userAuth(@FormParam("username") String username,
						   @FormParam("password") String password){		
		//se pondra a TRUE el campo logged del usuario si es que coincide con el username y pass
		//buscamos el usuario en la base de datos
		VoluntarioEntity voluntario = voluntarioDao.verificarUsuario(username, password);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario o el password no es válido");
		} else{
			//se inicia sesion para el usuario
			voluntario.setLogged(true);
			voluntarioDao.modificar(voluntario);
			return Utiles.retornarSalida(false, "Sesión iniciada");
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
	@ResponseBody
	public String userLoggout(@FormParam("username") String username,
							  @FormParam("password") String password){
		//traemos el usuario de la BD y cambiamos el campo logged
		VoluntarioEntity voluntario = voluntarioDao.verificarUsuario(username, password);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario o el password no es válido");
		}else{
			try{
				voluntario.setLogged(false);
				voluntarioDao.modificar(voluntario);
				return Utiles.retornarSalida(false, "Sesión cerrada");
			} catch (Exception ex){
				ex.printStackTrace();
				return Utiles.retornarSalida(true, "Error al cerrar la sesión");
			}
		}
	}
	
	/*
	public String changeUsername(@PathParam("username") String username,
									@FormParam("newUsername") String newUsername){
		
	}*/
	
	/**
	 * Metodo que agrega un nuevo contacto a la lista de amigos de un usuario.
	 * El metodo es bidireccional
	 * 
	 * @param username
	 * @param contact
	 * @return
	 */
	/*@POST
	@Path("/user/{username}/newcontact")
	@Produces("application/json")
	public HashMap<String, Object> usersAddContact(@PathParam("username") String username,
													@QueryParam("password") String password,
													@QueryParam("contact") String contactname){		
		//verificamos la validez del usuario
		VoluntarioEntity voluntarioEntity = Utiles.verificarUsuario(username, password);
		if(voluntarioEntity == null){
			return Utiles.retornarSalida(true, "El usuario o el password no es válido");
		} else{
			//verificamos que haya iniciado sesion
			if(Utiles.haIniciadoSesion(voluntarioEntity)){
				//buscamos a "contact"
				VoluntarioEntity contacto = voluntarioDao.getById(contactname);
				if(contacto == null){
					return Utiles.retornarSalida(true, "El contacto no existe");
				} else{
					voluntarioEntity.addContacto(contacto);
					voluntarioDao.modificar(voluntarioEntity);
					//tambien agregamos a la lista de contactos del otro usuario
					contacto.addContacto(voluntarioEntity);
					voluntarioDao.modificar(contacto);
					return Utiles.retornarSalida(false, "Contacto agregado");
				}
			} else{
				return Utiles.retornarSalida(true, "No has iniciado sesión");
			}
		}
	}*/
	
	
	/**
	 * Metodo que elimina un contato de la lista de amistades de un usuario.
	 * El metodo es bidireccional.
	 * 
	 * @param username
	 * @param password
	 * @param contactname
	 * @return
	 */
	/*@POST
	@Path("/user/{username}/deletecontact")
	@Produces("application/json")
	public HashMap<String, Object> deleteContact(@PathParam("username") String username,
												 @QueryParam("password") String password,
												 @QueryParam("contact") String contactname){
		//verificamos el username
		VoluntarioEntity voluntario = Utiles.verificarUsuario(username, password);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario o password no es válido");
		} else{
			//verificamos que haya iniciado sesion
			if(Utiles.haIniciadoSesion(voluntario)){
				//verificamos la exitencia del contacto
				VoluntarioEntity contacto = voluntarioDao.getById(contactname);
				if(contacto == null){
					return Utiles.retornarSalida(true, "El contacto no existe");
				} else{
					voluntario.getContactos().remove(contacto);
					voluntarioDao.modificar(voluntario);
					contacto.getContactos().remove(voluntario);
					voluntarioDao.modificar(contacto);
					return Utiles.retornarSalida(false, "Contacto eliminado");
				}
			} else{
				return Utiles.retornarSalida(true, "No has iniciado sesión");
			}
		}
	}*/
	
	
	/**
	 * Metodo que retorna el perfil de un usuario en base a su username,
	 * o error en todo caso.
	 *  
	 * @param username
	 * @return
	 */
	/*@GET
	@Path("/user/profile/{username}")
	@ResponseBody
	public String getUsuario(@PathParam("username") String username,
											  @QueryParam("usuario") String usuario,
											  @QueryParam("password") String password){
		
		//verificamos la validez del usuario que solicita el perfil
		VoluntarioEntity voluntario = voluntarioDao.verificarUsuario(usuario, password);
		if(voluntario == null){
			return Utiles.retornarSalida(true, "El usuario o password no es válido");
		} else{
			//buscamos el voluntrario por el username
			VoluntarioEntity perfilBuscado = voluntarioDao.getById(username);
			if(perfilBuscado == null){
				return Utiles.retornarSalida(true, "El usuario no existe");
			} else{
				//llamamos al metodo que escribe los datos del voluntario
				String jsonStringFromVoluntario = Utiles.voluntarioToStringJsonStile(voluntario);
				if(jsonStringFromVoluntario == null){
					return Utiles.retornarSalida(true, "No se ha podido retornar los datos del voluntario");
				} else{
					return Utiles.retornarSalida(false, jsonStringFromVoluntario);
				}
			}
		}
	}*/	
	
	
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
	@ResponseBody
	public String enviarSolicitudAmistad(@FormParam("solicitante") String usuarioQueEnvia,
										 @FormParam("solicitado") String usuarioSolicitado){
		
		//verificamos que el usuario que ha solicitado exista
		VoluntarioEntity voluntarioQueSolicita = voluntarioDao.findByClassAndID(VoluntarioEntity.class, usuarioQueEnvia);
		if(voluntarioQueSolicita == null){
			return Utiles.retornarSalida(true, "El usuario no existe");
		} else {
			//verificamos que el usuario que solicita haya iniciado sesion
			if(!Utiles.haIniciadoSesion(voluntarioQueSolicita)){
				return Utiles.retornarSalida(true, "No has iniciado sesión");
			} else {
				//verificamos que el contacto exista
				VoluntarioEntity contactoSolicitado = voluntarioDao.findByClassAndID(VoluntarioEntity.class, usuarioSolicitado);
				if(contactoSolicitado == null){
					return Utiles.retornarSalida(true, "El contacto no existe");
				} else {
					//verificamos que no sean ya amigos
					if(voluntarioDao.yaEsContacto(voluntarioQueSolicita, contactoSolicitado)){
						return Utiles.retornarSalida(true, "Ya son amigos");
					} else {
						SolicitudAmistadEntity nuevaSolicitud = new SolicitudAmistadEntity();
						nuevaSolicitud.setUsuarioSolicitante(voluntarioQueSolicita);
						nuevaSolicitud.setUsuarioSolicitado(contactoSolicitado);
						solicitudAmistadDao.guadar(nuevaSolicitud);
						//TODO enviarla en backgroud y que haga un push al usuario
						return Utiles.retornarSalida(true, "Solicitud de amistad enviada");
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
	@ResponseBody
	public String getSolicitudesPendientes(@PathParam("username") String username){
		
		//verificamos que el usuario exista en la Base de Datos
		VoluntarioEntity voluntarioEntity = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username.toLowerCase());
		if(voluntarioEntity == null){
			return Utiles.retornarSalida(true, "El usuario no existe");
		} else {
			//verificamos que haya iniciado sesion
			if(!Utiles.haIniciadoSesion(voluntarioEntity)){
				return Utiles.retornarSalida(true, "No has iniciado sesión");
			} else{
				List<SolicitudAmistadEntity> listaPendientes = solicitudAmistadDao.getListaSolicitudesPendientes(username);				
				if(listaPendientes.isEmpty()){
					return Utiles.retornarSalida(false, "No tenés solicitudes pendientes");
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
				return Utiles.retornarSalida(true, "No has iniciado sesión");
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
	@ResponseBody
	public String buscarUsuario(@PathParam("searchParam") String criterioBusqueda,
			@QueryParam("username") String username,
			@QueryParam("password") String password){
		
		//verificamos que no este vacio
		if(criterioBusqueda == null || criterioBusqueda.isEmpty()){
			//no hacemos nada
		} else {
			VoluntarioEntity voluntarioQueSolicita = voluntarioDao.findByClassAndID(VoluntarioEntity.class, username);
			if(voluntarioQueSolicita == null){
				return Utiles.retornarSalida(true, "El usuario no existe");
			} else {
				//verificamos que el usuario que solicita haya iniciado sesion
				if(!Utiles.haIniciadoSesion(voluntarioQueSolicita)){
					return Utiles.retornarSalida(true, "No has iniciado sesión");
				} else {
					//llamamos al dao que se encarga de la busqueda
					List<VoluntarioEntity> listaResultado = voluntarioDao.buscarUsuarios(criterioBusqueda);
					if(listaResultado == null){
						return Utiles.retornarSalida(true, "No hay usuario con ese nombre");
					} else {
						JSONArray retorno = new JSONArray();
						//a cada usuario le agregamos la cantidad de amigos que tiene y un boolean de si son amigos
						for(int j=0; j<listaResultado.size(); j++){
							//verificamos si ambos voluntarios ya son amigos, luego lo pasamos a JSON y agregamos los nuevos campos
							JSONObject jsonFromVoluntario = voluntarioDao.getJSONFromVoluntario(listaResultado.get(j));
							if(voluntarioDao.yaEsContacto(voluntarioQueSolicita, listaResultado.get(j))){
								jsonFromVoluntario.put("yaEsAmigo", true);
							} else {
								jsonFromVoluntario.put("yaEsAmigo", false);
							}
							jsonFromVoluntario.put("cantidadAmigos", listaResultado.get(j).getContactos().size());
							retorno.put(jsonFromVoluntario);
						}
						return Utiles.retornarSalida(false, retorno.toString());
					}
				}
			}
		}
		return "";
	}
}
