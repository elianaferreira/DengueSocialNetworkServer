package tesis.server.socialNetwork.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import tesis.server.socialNetwork.dao.AdministradorDao;
import tesis.server.socialNetwork.dao.VoluntarioDao;
import tesis.server.socialNetwork.entity.AdminEntity;
import tesis.server.socialNetwork.entity.VoluntarioEntity;


/**
 * Clase que contiene metodos de utilidad
 * 
 * @author eliana
 *
 */
public class Utiles {
	
	public static String PHOTOS_FOLDER = "C://tesisPhotos/";
	public static long DIAS_PASADOS_RELEVANTE = 15;
	
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
	 * Metodo que retorna un voluntario como String pero en formato JOSN Object
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
		//por el momento tendremos que sea un terciod e la poblacion total de voluntarios
		Integer parametro = cantidadTotalVoluntarios/3; //toma la parte entrera del resultado
		if(cantFavsOrRepost >= parametro){
			return true;
		}
		return false;
	}
	
}
