package com.Social.application.DG2.entity;

import com.Social.application.DG2.entity.Enum.EnableType;
import com.Social.application.DG2.entity.Enum.RoleType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "favorites")
public class Favorites {

    @Id
    private String id;

    @MapsId
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Posts postsID;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users userID;

    @Column(name = "created_at")
    private Timestamp createAt;
}
