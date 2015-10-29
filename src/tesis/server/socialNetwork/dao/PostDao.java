package tesis.server.socialNetwork.dao;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.hibernate.Query;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import tesis.server.socialNetwork.entity.FavoritoEntity;
import tesis.server.socialNetwork.entity.NoFavoritoEntity;
import tesis.server.socialNetwork.entity.PostEntity;
import tesis.server.socialNetwork.entity.VoluntarioEntity;
import tesis.server.socialNetwork.utils.Utiles;


@Controller
@LocalBean
public class PostDao extends GenericDao<PostEntity, Integer> {

	@Inject
	private VoluntarioDao voluntarioDao;
	
	@Inject
	private FavoritoDao favoritoDao;
	
	@Inject
	private NoFavoritoDao noFavoritoDao;
	
	@Override
	protected Class<PostEntity> getEntityBeanType() {
		return PostEntity.class;
	}

	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Integer guardar(PostEntity postEntity){
		//para un nuevo post es necesario agregar el campo 'solucionado' a FALSE
		postEntity.setSolucionado(false);
		postEntity.setRelevante(false);
		//agregamos la fecha en formato timestamp
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime()); 
		postEntity.setFechaPost(timestamp);
		
		//si es un reporte solucionado debemos setear la fecha de solucion como la misma de guardado
		if(postEntity.getSolucionado() == true){
			postEntity.setFechaSolucion(timestamp);
		}
		
		//this.save(postEntity);
		Integer idGen = this.saveAndReturnPost(postEntity);
		
		return idGen;
	}
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void modificar(PostEntity postEntity){
		//agregamos la fecha en formato timestamp
		Date date = new Date();
		postEntity.setFechaSolucion(new Timestamp(date.getTime()));
		this.update(postEntity);
	}
	
	/**
	 * Metodo que retorna todos los posts de los amigos de un usuario cuya fecha de publicacion sea mayor
	 * a la ultima vez que el usuario solicito una actualizacion de su timeline.
	 * 
	 * @param username
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<PostEntity> getPosts(String username, Timestamp ultimaActualizacion, Boolean nuevos){
		System.out.println("Uusario " + username + "; timestamp: " + ultimaActualizacion.toString() + "; son nuevos?: " + nuevos.toString());
		/* este seria el query ideal pero Hibernate no soporta UNION.
		 "from PostEntity p where p.voluntario in ("
				+ "select c.voluntario from ContactoEntity c where c.contacto='abstract' "
				+ "union "
				+ "select c1.contacto from ContactoEntity c1 where c1.voluntario='abstract' "
				+ "union "
				+ "select 'abstract') "
				+ "and p.fechaPost<current_timestamp";*/
		String condicionActualizacion = "";
		String condicionNuevos = " and p.fechaPost> :ultimaactualizacion order by p.fechaPost asc";
		String condicionViejos = " and p.fechaPost< :ultimaactualizacion order by p.fechaPost desc";
		if(nuevos){
			condicionActualizacion = condicionNuevos;
		} else {
			condicionActualizacion = condicionViejos;
		}
		
		String consulta = "from PostEntity p "
				+ "where (p.voluntario in "
				+ "(select c.voluntario from ContactoEntity c where c.contacto.userName= :username )"
				+ "or p.voluntario in "
				+ "(select c1.contacto from ContactoEntity c1 where c1.voluntario.userName= :username) "
				+ "or p.voluntario in "
				+ "(select v.userName from VoluntarioEntity v where v.userName= :username))"
				+ condicionActualizacion;
		Query query = this.getSession().createQuery(consulta);
		query.setParameter("username", username);
		query.setParameter("ultimaactualizacion", ultimaActualizacion);
		//limitar la cantidad de registros
		query.setMaxResults(5);
		List lista = query.list();
		
		
		return lista;
	}
	
	
	/**
	 * Metodo que retorna un JSON representando a un post.
	 * 
	 * @param postEntity
	 * @return
	 */
	public JSONObject getJSONFromPost(String usernameSolicitante, PostEntity postEntity){
		//buscamos la cantidad de marcaciones de fav y noFav para el post
		List<FavoritoEntity> listaFV = favoritoDao.listaFavoritosByPost(postEntity);
		List<NoFavoritoEntity> listaNFV = noFavoritoDao.listaNoFavoritosByPost(postEntity);
		
		JSONObject retorno = new JSONObject();
		//es necesario enviar el ID del post para poder identificarlo luego
		retorno.put("id", postEntity.getIdPost());
		retorno.put("mensaje", postEntity.getPost());
		retorno.put("latitud", postEntity.getLatitud());
		retorno.put("longitud", postEntity.getLongitud());
		retorno.put("fecha", postEntity.getFechaPost());
		retorno.put("solucionado", postEntity.getSolucionado());
		if(postEntity.getVoluntarioQueSoluciona() != null){
			retorno.put("autorSolucion", postEntity.getVoluntarioQueSoluciona().getUsernameString());
		}
		retorno.put("voluntario", voluntarioDao.getJSONFromVoluntario(postEntity.getVoluntario()));
		if(listaFV == null || listaFV.size() == 0){
			retorno.put("buenos", 0);
		} else {
			retorno.put("buenos", listaFV.size());
		}
		if(listaNFV == null || listaNFV.size() == 0){
			retorno.put("malos", 0);
		} else {
			retorno.put("malos", listaNFV.size());
		}
		//se debe indicar si es usuario que solicita marco como bueno o malo
		retorno.put("marcoComoBueno", marcoComoBueno(usernameSolicitante, listaFV));
		retorno.put("marcoComoMalo", marcoComoMalo(usernameSolicitante, listaNFV));
		
		return retorno;
	}
	
	
	
	
	/**
	 * Metodo que verifica si un usuario ya marco como FAV un post
	 * 
	 * @param usernameSolicitante
	 * @param listaFavs
	 * @return
	 */
	private boolean marcoComoBueno(String usernameSolicitante, List<FavoritoEntity> listaFavs){
		if(listaFavs == null || listaFavs.size() == 0){
			return false;
		} else {
			for(int i=0; i<listaFavs.size(); i++){
				if(listaFavs.get(i).getAutor().getUserName() == usernameSolicitante){
					return true;
				}
			}
			return false;
		}
	}
	

	
	
	/**
	 * Metodo que verifica si un usuario ya marco como NoFav un post
	 * 
	 * @param usernameSolicitante
	 * @param listaNoFavs
	 * @return
	 */
	private boolean marcoComoMalo(String usernameSolicitante, List<NoFavoritoEntity> listaNoFavs){
		if(listaNoFavs == null || listaNoFavs.size() == 0){
			return false;
		} else {
			for(int i=0; i<listaNoFavs.size(); i++){
				if(listaNoFavs.get(i).getAutor().getUserName() == usernameSolicitante){
					return true;
				}
			}
			return false;
		}
	}
	
	
	/**
	 * Metodo que retorna la lista de un JSONString de las marcaciones del post
	 * 
	 * @param buenos
	 * @param malos
	 * @return
	 */
	public String getJSONFromMarcaciones(Integer buenos, Integer malos, Boolean marcoBueno, Boolean desmarcoBueno, Boolean marcoMalo, Boolean desmarcoMalo){
		JSONObject jsonRetorno = new JSONObject();
		jsonRetorno.put("buenos", buenos);
		jsonRetorno.put("malos", malos);
		jsonRetorno.put("marcoBueno", marcoBueno);
		jsonRetorno.put("desmarcoBueno", desmarcoBueno);
		jsonRetorno.put("marcoMalo", marcoMalo);
		jsonRetorno.put("desmarcoMalo", desmarcoMalo);
		
		return jsonRetorno.toString();
	}
	
	
	/**
	 * Metodo que retorna la lista de posts del usuario
	 * @param voluntario
	 * @return
	 */
	public List<PostEntity> getHomeTimeline(VoluntarioEntity voluntario){
		
		String consulta = "from PostEntity p where p.voluntario = :usuario";
		Query query = this.getSession().createQuery(consulta);
		query.setEntity("usuario", voluntario);
		List lista = query.list();
		
		return lista;
	}
	
	
	/**
	 * Metodo que retorna la lista de posts relevantes, y hace una validacion previa de la fecha para descartar
	 * lo que ya no pueden ser considerados relevantes.
	 * 
	 * @return
	 */
	public List<PostEntity> getRelevantes(){
		String consulta = "from PostEntity p where p.relevante = true";
		Query query = this.getSession().createQuery(consulta);
		List<PostEntity> lista = query.list();
		List<PostEntity> retorno = new ArrayList<PostEntity>();
		//hacemos la validacion de las fechas, aquella que supere los X dias debe ser descartada de la lista de relevantes
		Date current = new Date();
		for(int i=0; i<lista.size(); i++){
			long diff = current.getTime() - lista.get(i).getFechaPost().getTime();
			long diasPasados = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			if(diasPasados > Utiles.DIAS_PASADOS_RELEVANTE){
				// ya no es relevante
				lista.get(i).setRelevante(false);
				this.update(lista.get(i));
			} else {
				//sigue siendo relevante y lo agregamos a la lista de retorno
				retorno.add(lista.get(i));
			}
		}
		return retorno;
	}
	
	
	/**
	 * Metodo que se encarga de setear si el post es relevante o no
	 * 
	 * @param posibleRelevante
	 */
	public void setPostRelevante(PostEntity posibleRelevante){
		Integer cantFavs = favoritoDao.cantidadFavoritosByPost(posibleRelevante);
		Integer cantidadTotalVoluntarios = voluntarioDao.cantidadVoluntariosTotal();
		Integer cantNoFavs = noFavoritoDao.cantidadNoFavoritosByPost(posibleRelevante);
		//solo si tiene mas favs que noFavs puede ser relevante
		if(cantFavs > cantNoFavs){
			if(Utiles.puedeSerUnPostRelevante(cantFavs, cantidadTotalVoluntarios)){
				//verificamos el tiempo transcurrido entre el fav actual y la publicacion del post
			    Date fechaPost = posibleRelevante.getFechaPost();
				System.out.println(fechaPost);
				
				//verificamos si es del mismo anho
				//cuantos dias han pasado
				Date date = new Date();
				long diff = date.getTime() - fechaPost.getTime();
				long diasPasados = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
				System.out.println ("Dias pasados: " + diasPasados);
				if(diasPasados > Utiles.DIAS_PASADOS_RELEVANTE){
					//no hacer nada
				} else {
					//es un post relevante
					posibleRelevante.setRelevante(true);
					this.update(posibleRelevante);
				}
			}
		}
	}
	
	
	/**
	 * Metodo que trae todos los reportes de la Base de Datos
	 * @return
	 */
	public List<PostEntity> getAll(){
		String consulta = "from PostEntity p";
		Query query = this.getSession().createQuery(consulta);
		List<PostEntity> lista = query.list();
		
		return lista;
	}
	
}
