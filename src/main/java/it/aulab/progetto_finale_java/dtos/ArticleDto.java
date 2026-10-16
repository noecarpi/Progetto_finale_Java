package it.aulab.progetto_finale_java.dtos;

import java.time.LocalDateTime;

import it.aulab.progetto_finale_java.models.Category;
import it.aulab.progetto_finale_java.models.Image;
import it.aulab.progetto_finale_java.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
public class ArticleDto {
    private Long id;
    private String title;
    private String subtitle;
    private String body;
    private LocalDateTime publishDate;
    private Boolean isAccepted;
    private User user;
    private Category category;
    private Image image;
}
