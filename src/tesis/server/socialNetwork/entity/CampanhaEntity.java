package tesis.server.socialNetwork.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Clase que representa una campanha lanzada por el super usuario
 * 
 * @author eliana
 *
 */

@Entity
@Table(name="CAMPANHA")
public class CampanhaEntity {

	private Integer idCampanha;
	private String nombreCampanha;
	private String mensaje;
	//private List<FotografiaEntity> fotografias;
	//private GeolocalizacionEntity geolocalizacion;
	private EstadoEntity estado;
	private Date fechaLanzamiento;
	//private List<FavoritoEntity> favoritoDe;
	
	//getters y setters
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SN_CAMPANHA_SEQ")
    @SequenceGenerator(name="SN_CAMPANHA_SEQ",sequenceName="SN_CAMPANHA_SEQ")
	@Column(name="ID_CAMPANHA", nullable=false)
	public Integer getIdCampanha() {
		return idCampanha;
	}
	public void setIdCampanha(Integer idCampanha) {
		this.idCampanha = idCampanha;
	}
	
	@Column(name="NOMBRE_CAMPANHA", nullable=false)
	public String getNombreCampanha() {
		return nombreCampanha;
	}
	public void setNombreCampanha(String nombreCampanha) {
		this.nombreCampanha = nombreCampanha;
	}
	
	@Column(name="MENSAJE", nullable=false)
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	/*s/mappedBy es el campo que es dueña de la relacion
	@OneToMany(cascade=CascadeType.ALL, mappedBy="ID_CAMPANHA")
	public List<FotografiaEntity> getFotografias() {
		return fotografias;
	}
	public void setFotografias(List<FotografiaEntity> fotografias) {
		this.fotografias = fotografias;
	}*/
	
	/*/en gelocalizacion no es necesario el mapeo
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="GEOLOCALIZACION", nullable=true)
	public GeolocalizacionEntity getGeolocalizacion() {
		return geolocalizacion;
	}
	public void setGeolocalizacion(GeolocalizacionEntity geolocalizacion) {
		this.geolocalizacion = geolocalizacion;
	}*/
	
	@ManyToOne(optional=false)
	@JoinColumn(name="ESTADO", nullable=false)
	public EstadoEntity getEstado() {
		return estado;
	}
	public void setEstado(EstadoEntity estado) {
		this.estado = estado;
	}
	
	@Column(name="FECHA_LANZAMIENTO", nullable=false)
	@Temporal(TemporalType.DATE)
	public Date getFechaLanzamiento() {
		return fechaLanzamiento;
	}
	public void setFechaLanzamiento(Date fechaLanzamiento) {
		this.fechaLanzamiento = fechaLanzamiento;
	}
	
	/*@OneToMany(cascade=CascadeType.ALL, mappedBy="campanha")
	public List<FavoritoEntity> getFavoritoDe() {
		return favoritoDe;
	}
	public void setFavoritoDe(List<FavoritoEntity> favoritoDe) {
		this.favoritoDe = favoritoDe;
	}*/
}
