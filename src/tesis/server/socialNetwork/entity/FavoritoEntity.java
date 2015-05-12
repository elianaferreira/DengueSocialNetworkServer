package tesis.server.socialNetwork.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Clase que representa una marcación de favorito, ya sea de un post
 * o de una campanha
 * 
 * @author eFerreira
 *
 */

@Entity
@Table(name="FAVORITO")
public class FavoritoEntity {

	private Integer idFavorito;
	private VoluntarioEntity autor;
	
	//no se puede marcar como fav un comentario, al estilo Instagram
	//private ComentarioEntity comentario;
	private PostEntity post;
	private CampanhaEntity campanha;
	
	
	//getters y setters
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SN_FAVORITO_SEQ")
    @SequenceGenerator(name="SN_FAVORITO_SEQ",sequenceName="SN_FAVORITO_SEQ")
	@Column(name="ID_FAVORITO", nullable=false)
	public Integer getIdFavorito() {
		return idFavorito;
	}
	public void setIdFavorito(Integer idFavorito) {
		this.idFavorito = idFavorito;
	}
	
	@ManyToOne(optional=false, cascade=CascadeType.REMOVE)
	@JoinColumn(name="AUTOR", nullable=false)
	public VoluntarioEntity getAutor() {
		return autor;
	}
	public void setAutor(VoluntarioEntity autor) {
		this.autor = autor;
	}
	
	/*/REF: en.wikibooks.org/wiki/Java_Persistence/OneToMany
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="COMENTARIO", nullable=true)
	public ComentarioEntity getComentario() {
		return comentario;
	}
	public void setComentario(ComentarioEntity comentario) {
		this.comentario = comentario;
	}*/
	
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
	@JoinColumn(name="POST", nullable=true)
	public PostEntity getPost() {
		return post;
	}
	public void setPost(PostEntity post) {
		this.post = post;
	}
	
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
	@JoinColumn(name="CAMPANHA", nullable=true)
	public CampanhaEntity getCampanha() {
		return campanha;
	}
	public void setCampanha(CampanhaEntity campanha) {
		this.campanha = campanha;
	}
		
}
