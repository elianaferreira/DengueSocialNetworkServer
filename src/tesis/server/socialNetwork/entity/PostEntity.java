package tesis.server.socialNetwork.entity;

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
	//datos de geolocalizacion
	private Double latitud;
	private Double longitud;
	//fotos guardadas como un array de bytes
	private byte[] fotoAntes;
	private byte[] fotoDespues;
	private List<ComentarioEntity> comentarios;
	private List<FavoritoEntity> likeList;
	private List<NoFavoritoEntity> noLikeList;
	//indica si un caso reportado ha sido solucionado.
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
	
	@Column(name="SOLUCIONADO", nullable=false)
	public Boolean getSolucionado() {
		return solucionado;
	}
	public void setSolucionado(Boolean solucionado) {
		this.solucionado = solucionado;
	}
	
	@Column(name="LATITUD", nullable=true)
	public Double getLatitud() {
		return latitud;
	}
	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}
	
	@Column(name="LONGITUD", nullable=true)
	public Double getLongitud() {
		return longitud;
	}
	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}
	
	@Column(name="FOTO_ANTES_BYTES", nullable=true)
	public byte[] getFotoAntes() {
		return fotoAntes;
	}
	public void setFotoAntes(byte[] fotoAntes) {
		this.fotoAntes = fotoAntes;
	}
	
	@Column(name="FOTO_DESPUES_BYTES", nullable=true)
	public byte[] getFotoDespues() {
		return fotoDespues;
	}
	public void setFotoDespues(byte[] fotoDespues) {
		this.fotoDespues = fotoDespues;
	}
	
	@OneToMany
	@JoinColumn(name="COMENTARIO")
	public List<ComentarioEntity> getComentarios() {
		return comentarios;
	}
	public void setComentarios(List<ComentarioEntity> comentarios) {
		this.comentarios = comentarios;
	}
	
	@OneToMany
	@JoinColumn(name="LIKE")
	public List<FavoritoEntity> getLikeList() {
		return likeList;
	}
	public void setLikeList(List<FavoritoEntity> likeList) {
		this.likeList = likeList;
	}
	
	@OneToMany
	@JoinColumn(name="NO_LIKE")
	public List<NoFavoritoEntity> getNoLikeList() {
		return noLikeList;
	}
	public void setNoLikeList(List<NoFavoritoEntity> noLikeList) {
		this.noLikeList = noLikeList;
	}
	
	
	
}
