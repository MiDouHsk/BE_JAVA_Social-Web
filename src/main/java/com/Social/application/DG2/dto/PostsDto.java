package com.Social.application.DG2.dto;

import com.Social.application.DG2.entity.Medias;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostsDto {
    private String title;
    private String body;
    private String status;

    private List<Medias> mediasId;

    public List<String> getMediasId() {
        List<String> mediasIds = new ArrayList<>();
        if (mediasId != null && !mediasId.isEmpty()) {
            for (Medias media : mediasId) {
                mediasIds.add(media.getId());
            }
        }
        return mediasIds;
    }



}
