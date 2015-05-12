package tesis.server.socialNetwork.entity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
 * Clase que representa la entidad para manejo de un Post 
 * creado por un usuario/voluntario
 * 
 * @author eliana
 *
 */

@Entity
@Table(name="POST")
public class PostEntity {
	
	private Integer idPost;
	private String post;
	private VoluntarioEntity voluntario;
	private Date fechaPost;
	//private List<FotografiaEntity> fotografias;
	//private GeolocalizacionEntity geolocalizacion;
	//private List<ComentarioEntity> comentarios;
	//private List<FavoritoEntity> favoritoDe;
	//inidca si un caso reportado ha sido solucionado.
	private Boolean solucionado;
	
	//getters y setters
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SN_POST_SEQ")
    @SequenceGenerator(name="SN_POST_SEQ",sequenceName="SN_POST_SEQ")
	@Column(name="ID_POST", nullable=false)
	public Integer getIdPost() {
		return idPost;
	}
	public void setIdPost(Integer idPost) {
		this.idPost = idPost;
	}
	
	@Column(name="POST", nullable=false)
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
	@JoinColumn(name="VOLUNTARIO", nullable=false)
	public VoluntarioEntity getVoluntario() {
		return voluntario;
	}
	public void setVoluntario(VoluntarioEntity voluntario) {
		this.voluntario = voluntario;
	}
	
	@Column(name="FECHA_POST", nullable=false, columnDefinition="TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaPost() {
		return fechaPost;
	}
	public void setFechaPost(Date fechaPost) {
		this.fechaPost = fechaPost;
	}
	
	/*/mappedBy es el campo que es dueña de la relacion   mappedBy="ID_POST"
	@OneToMany(cascade=CascadeType.ALL)
	public List<FotografiaEntity> getFotografias() {
		return fotografias;
	}
	public void setFotografias(List<FotografiaEntity> fotografias) {
		this.fotografias = fotografias;
	}/*
	
	//en gelocalizacion no es necesario el mapeo
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="GEOLOCALIZACION", nullable=true)
	public GeolocalizacionEntity getGeolocalizacion() {
		return geolocalizacion;
	}
	public void setGeolocalizacion(GeolocalizacionEntity geolocalizacion) {
		this.geolocalizacion = geolocalizacion;
	}*/
	
	/*/mappedBy="ID_POST"
	@OneToMany(cascade=CascadeType.ALL)
	public List<ComentarioEntity> getComentarios() {
		return comentarios;
	}
	public void setComentarios(List<ComentarioEntity> comentarios) {
		this.comentarios = comentarios;
	}
	
	//mappedBy="ID_POST"
	@OneToMany(cascade=CascadeType.ALL)
	public List<FavoritoEntity> getFavoritoDe() {
		return favoritoDe;
	}
	public void setFavoritoDe(List<FavoritoEntity> favoritoDe) {
		this.favoritoDe = favoritoDe;
	}*/
	
	@Column(name="SOLUCIONADO", nullable=false)
	public Boolean getSolucionado() {
		return solucionado;
	}
	public void setSolucionado(Boolean solucionado) {
		this.solucionado = solucionado;
	}
}
