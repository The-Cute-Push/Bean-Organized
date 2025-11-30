package cutepush.beanorganized.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "perfilUser")
public class UserProfileEntity {

    // Shared primary key with UserEntity (idusuario)
    @Id
    @Column(name = "idUser", nullable = false, updatable = false, unique = true)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "idUser", nullable = false, updatable = false, unique = true)
    private UserEntity user;

    @Column(name = "profilePhoto")
    private String profilePhoto;

    @Lob
    @Column(name = "biography")
    private String biography;

    @Column(name = "phone")
    private String phone;
}
