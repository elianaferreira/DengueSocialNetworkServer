package tesis.server.socialNetwork.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import tesis.server.socialNetwork.dao.AdministradorDao;
import tesis.server.socialNetwork.dao.VoluntarioDao;
import tesis.server.socialNetwork.entity.AdminEntity;
import tesis.server.socialNetwork.entity.VoluntarioEntity;


/**
 * Clase que contiene metodos y variables de utilidad
 * 
 * @author eliana
 *
 */
public class Utiles {
	
	public static final String PHOTOS_FOLDER = "C://tesisPhotos/";
	public static final long DIAS_PASADOS_RELEVANTE = 15;
	//variables de puntajes y reputacion
	public static final Integer PUNTAJE_POR_SOLUCIONAR = 5;
	public static final Integer PUNTAJE_POR_REPORTAR = 1;
	//por el momento tendremos que sea un tercio de la poblacion total de voluntarios
	public static final Integer PARTE_POBLACIONAL_PARA_MEDIR_RELEVANTES = 3;
	
	//tipos de notificacion
	public static final String NOTIF_NUEVA_SOLICITUD_AMISTAD = "NUEVA_SOLICITUD_AMISTAD";
	public static final String NOTIF_INVITADO_CAMPANHA = "INVITADO_CAMPANHA";
	
	
	
	
	//acceso a Base de Datos
	@Autowired
	private static VoluntarioDao voluntarioDao;
	
	@Autowired
	private static AdministradorDao administradorDao;
	
	
	/**
	 * Metodo que crea un JSON a ser retornado al cliente
	 * 
	 * @param errorStatus
	 * @param mensaje
	 * @return
	 */
	public static String retornarSalida(boolean errorStatus, String mensaje){
		JSONObject retorno = new JSONObject();
		retorno.put("error", errorStatus);
		retorno.put("msj", mensaje);
		
		//escribimos en la consola
		System.out.println("MENSAJE RETORNO: " + mensaje);
		return retorno.toString();
	}
	
	
	/**
	 * Metodo que verifica que un voluntario haya iniciado sesion
	 * 
	 * @param voluntarioEntity
	 * @return
	 */
	public static boolean haIniciadoSesion(VoluntarioEntity voluntarioEntity){
		if(voluntarioEntity.getLogged() == true){
			return true;
		} else{
			return false;
		}
	}
	
	
	/**
	 * Metodo que verifica que el administrador haya iniciado sesion
	 * 
	 * @param adminEntity
	 * @return
	 */
	public static boolean adminLogged(AdminEntity adminEntity){
		if(adminEntity.getLogged() == true){
			return true;
		} else{
			return false;
		}
	}
	
	
	/**
	 * Metodo que retorna un voluntario como String pero en formato JSON Object
	 * Ej.: {
			  "username": "username",
			  "nombre": "nombre",
			  "apellido": "apellido",
			  "ci": "ci",
			  "direccion": "direccion",
			  "telefono": "telefono",
			  "email": "email"
			}
	 * @param voluntario
	 * @return
	 */
	public static String voluntarioToStringJsonStile(VoluntarioEntity voluntario){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("error", false);
			jsonObject.put("username", voluntario.getUserName());
			jsonObject.put("nombre", voluntario.getNombreReal());
			//TODO foto de perfil
			//datos opcionales.
			if(voluntario.getCi() != null){
				jsonObject.put("ci", voluntario.getCi());
			}
			if(voluntario.getDireccion() != null){
				jsonObject.put("direccion", voluntario.getDireccion());
			}
			if(voluntario.getTelefono() != null){
				jsonObject.put("telefono", voluntario.getTelefono());
			}
			if(voluntario.getEmail() != null){
				jsonObject.put("email", voluntario.getEmail());
			}
			return jsonObject.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static void savePhoto(InputStream file, String fileName){
		try {
            OutputStream out = null;
            int read = 0;
            byte[] bytes = new byte[1024];
 
            out = new FileOutputStream(new File(PHOTOS_FOLDER + fileName));
            while ((read = file.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
 
            e.printStackTrace();
        }
	}
	
	
	/**
	 * Metodo que decide si un post es relevante de manera a ser mostrado a lo largo de toda la red de acuerdo a la cantidad
	 * de favoritos o de reposts en relacion a la cantidad de voluntarios de la red.
	 * 
	 * @param cantFavsOrRepost
	 * @param cantidadTotalVoluntarios
	 * @return
	 */
	public static Boolean puedeSerUnPostRelevante(Integer cantFavsOrRepost, Integer cantidadTotalVoluntarios){
		Integer parametro = cantidadTotalVoluntarios/PARTE_POBLACIONAL_PARA_MEDIR_RELEVANTES; //toma la parte entera del resultado
		if(cantFavsOrRepost >= parametro){
			return true;
		}
		return false;
	}
	
	/**
	 * Metodo que retorna la distancia entre dos coordenadas
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @param unit
	 * @return
	 */
	public static double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		//distancia en Kilometros
		dist = dist * 1.609344;
		return (dist);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts decimal degrees to radians             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}
	
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts radians to decimal degrees             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
	
	
	
	
}
