package com.Social.application.DG2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "posts")
public class Posts {
    @Id
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    public Posts() {
        this.id = UUID.randomUUID().toString();
    }

    private String title;

    @Column
    @NotNull
    private String body;

    private String status;

    @Column(name = "total_like")
    private int totalLike;

        @Column(name = "total_comment")
        private int totalComment;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Users userId;

    @Column(name = "created_at")
    private Timestamp createAt;

    @Column(name = "total_share")
    private int totalShare;

    @JsonManagedReference
    @OneToMany(mappedBy = "postsId", cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Medias> medias;

    @JsonIgnore 
    @ManyToMany(mappedBy = "favoritesPost", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Users> favoritesUser;

    @Override
    public String toString() {
        return "Posts{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", status='" + status + '\'' +
                ", totalLike=" + totalLike +
                ", totalComment=" + totalComment +
                ", createAt=" + createAt +
                '}';
    }
}
